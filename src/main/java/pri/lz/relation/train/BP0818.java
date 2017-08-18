package pri.lz.relation.train;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pri.lz.relation.util.BP;
import pri.lz.relation.util.ConceptRealtionUtil;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

/**
* @ClassName: ConceptAction
* @Description: 通过神经网络训练分类模型，训练1个，输出为8元神经网络，输入向量为原始向量（未降维）
* 		其中特征词典为TFIDF＞0.003，计算出的概念互信息不全为0共524个
* @author 廖劲为
* 
*/
public class BP0818 {
	
	FileUtil fileUtil = new FileUtil();
	ConceptRealtionUtil conceptUtil = new ConceptRealtionUtil();

	public static void main(String[] args) throws IOException{
		System.out.println("--start--");
		BP0818 action = new BP0818();
		long start = System.currentTimeMillis();
		
		// 通过BP神经网络训练
		action.train();
		
		// 根据BP神经网络模型对具有相关度的概念对进行关系分类
//		action.clssifyRelation();
		
		long end = System.currentTimeMillis();
		System.out.println("--end: " + (end-start) + " ms--");
	}
	
	// 根据BP神经网络模型对具有相关度的概念对进行关系分类
	public void clssifyRelation() {
		String domainName = "C19-Computer";
		ConceptRealtionUtil util = new ConceptRealtionUtil();
		try {
			// 读取候选概念关系对
			List<String[]> listCandidateConcepts = util.loadTrain(ConstantValue.MODEL_PATH+domainName+"_test.txt");
			// 顺序读取概念，对应概念特征向量
			List<String> listConcepts = util.loadConcepts(ConstantValue.MODEL_PATH+domainName+".txt");
			// 顺序读取概念特征向量
			List<double[]> listVectors = util.loadMatrix(ConstantValue.MODEL_PATH+domainName+"_2_lle.txt");
			System.out.println("listCandidateConcepts: " + listCandidateConcepts.size());
			System.out.println("listConcepts: " + listConcepts.size());
			System.out.println("listVectors: " + listVectors.size());
			// 读取训练好的BP模型
			double[][] iptHids = util.loadBPModel(ConstantValue.MODEL_IPTHIDWEIGHTS);
			double[][] hidOpts = util.loadBPModel(ConstantValue.MODEL_HIDOPTWEIGHTS);
			// 实例化训练好的BP
			BP bp = new BP(iptHids.length-1, iptHids[0].length-1, hidOpts[0].length-1, iptHids, hidOpts);
			for(String[] candidates : listCandidateConcepts){
				// 输入向量
				double[] vector1 = listVectors.get(listConcepts.indexOf(candidates[0]));
				double[] vector2 = listVectors.get(listConcepts.indexOf(candidates[1]));
				double[] data = new double[vector1.length+vector2.length];	//向量合并作为输入向量
				int k=0;
				for(double d : vector1){
					data[k++] = d;
				}
				for(double d : vector2){
					data[k++] = d;
				}
				// BP计算得出输出向量
				double[] result = bp.getResult(data);
				printBPResult(result, candidates[0], candidates[1], domainName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// 输出BP测试结果
	private void printBPResult(double[] result, String concept1, String concept2, String domainName) throws IOException{
		double max = -Integer.MIN_VALUE;
		int idx = -1;
		String txt = "";
		DecimalFormat df = new DecimalFormat("#.000000");
		for (int i = 0; i != result.length; i++) {
			txt += df.format(result[i]) + "\t";
			if (result[i] > max) {
				max = result[i];
				idx = i;
			}
		}
		String wtxt = concept1 + "\t" + concept2 + "\t" + ConstantValue.relationType(idx) + "\t" + txt + "\n";
		FileUtil fileUtil = new FileUtil();
		fileUtil.writeTxt(wtxt, ConstantValue.MODEL_PATH+domainName+"_result.txt", true);
	}

	// 通过BP神经网络训练
	public void train() throws IOException{
		double train_scale = 0.8;	//训练数据集比例占总训练数据的比例
		//1、读取训练数据集
		List<String[]> listTrainConcepts = conceptUtil.loadTrain(ConstantValue.MODEL_PATH+"C19-Computer_train_relation.txt");
		//2、按关系名拆分成map集合，key-关系名，value-对应关系的概念对集合
		Map<String, List<String[]>> mapTrainConcepts = conceptUtil.segConcepts(listTrainConcepts);
		//3、顺序读取概念，对应概念特征向量
		List<String> listConcepts = conceptUtil.loadConcepts(ConstantValue.MODEL_PATH+"C19-Computer.txt");
		//4、顺序读取概念特征向量
		List<double[]> listVectors = conceptUtil.loadMatrix(ConstantValue.MODEL_PATH+"C19-Computer_1_origin.txt");
		int inputVectorSize = listVectors.get(0).length;	//BP网络输入向量的维数/2
		//5、构建输入向量，key-概念对(concept1_concept2)，value-概念对的合并向量，用作输入向量
		Map<String, double[]> mapInputVectors = conceptUtil.loadInputVector(listTrainConcepts, listConcepts, listVectors, inputVectorSize);
		
		//6、构建训练组和测试组
		//6.1、获取训练组和测试组的概念对名称
		Map<String, List<String[]>> mapTrains = new HashMap<>();	//训练组，key-关系名，value-对应关系的概念对集合
		Map<String, List<String[]>> mapTests = new HashMap<>();		//测试组，key-关系名，value-对应关系的概念对集合
		Set<String> relations = new HashSet<>();	//关系名，用作确定BP网络个数
		int all_train_size = 0;
		for(Entry<String, List<String[]>> trains : mapTrainConcepts.entrySet()){
			relations.add(trains.getKey().trim());
			int train_size = (int) Math.round(trains.getValue().size()*train_scale);
			if(train_size == trains.getValue().size()){
				train_size--;
			}
			mapTrains.put(trains.getKey().trim(), trains.getValue().subList(0, train_size));
			all_train_size += mapTrains.get(trains.getKey().trim()).size();
			mapTests.put(trains.getKey().trim(), trains.getValue().subList(train_size,trains.getValue().size()));
//			System.out.println(trains.getKey() + " --> mapTrains :" + mapTrains.get(trains.getKey().trim()).size() +
//					" , mapTests : " + mapTests.get(trains.getKey().trim()).size());
		}
		//6.2、根据训练组的概念对确定对应的输入向量
		double[][] inputs = new double[all_train_size][];
		int tempIndex = 0;
		for(Entry<String, List<String[]>> trains : mapTrains.entrySet()){
			for (String[] concepts : trains.getValue()) {
				inputs[tempIndex++] = mapInputVectors.get(concepts[0]+"_"+concepts[1]);
			}
		}
		
		//7、构建BP网络模型,每个BP网络的输出为[0,0,0,...1,0](relations.size 大小)
		int hdn_size = (int) Math.round(Math.pow(inputVectorSize*2+relations.size(), 0.5)+5);	//隐藏层节点数
		int maxTrain = 100000;
		double eta = 0.25;
		double momentum = 0.3;
		double limitErr = 0.01;
		
		// 构建当前BP网络的输出向量
		double[][] targts = new double[all_train_size][relations.size()];
		tempIndex = 0;
		for(Entry<String, List<String[]>> trains : mapTrains.entrySet()){
			int idx = ConstantValue.checkRelationType(trains.getKey().trim());
//			System.out.println("idx = " + idx + " , relation = " + trains.getKey());
			if(idx>=0){
				for(int i=0; i<trains.getValue().size(); i++){
					targts[tempIndex][idx] = 1;
//					System.out.println(trains.getValue().get(i)[0] + "_" + trains.getValue().get(i)[1] + " = "
//					+ print(targts[tempIndex]));
					tempIndex++;
				}
			}
		}
		
		// 训练: 输入向量维数ipt_size，隐藏层节点数hdn_size，输出向量维数opt_size，学习率eta， 学习动量momentum， 最大误差limitErr
		BP bp = new BP(inputVectorSize*2, hdn_size, relations.size(), maxTrain, eta, momentum, limitErr);
		bp.train(inputs, targts);
		bp.writeModel();
		
	}
	
}