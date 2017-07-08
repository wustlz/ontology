package pri.lz.relation.train;

import java.io.IOException;
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
* @ClassName: PartBP
* @Description: 按照关系名训练多个二元输出神经网络
* @author 廖劲为
* @date 2017年7月5日 下午8:11:27
* 
*/
public class PartBP {
	
	FileUtil fileUtil = new FileUtil();
	ConceptRealtionUtil conceptUtil = new ConceptRealtionUtil();
	
	public static void main(String[] args) {
		PartBP partBP = new PartBP();
		try {
			partBP.train();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 训练BP网络
	public void train() throws IOException{
		double train_scale = 0.8;	//训练数据集比例占总训练数据的比例
		int inputVectorSize = 200;	//BP网络输入向量的维数/2
		//1、读取训练数据集
		List<String[]> listTrainConcepts = conceptUtil.loadTrain(ConstantValue.MODEL_PATH+"C19-Computer_train_relation.txt");
		//2、按关系名拆分成map集合，key-关系名，value-对应关系的概念对集合
		Map<String, List<String[]>> mapTrainConcepts = conceptUtil.segConcepts(listTrainConcepts);
		//3、顺序读取概念，对应概念特征向量
		List<String> listConcepts = conceptUtil.loadConcepts(ConstantValue.MODEL_PATH+"C19-Computer.txt");
		//4、顺序读取概念特征向量
		List<double[]> listVectors = conceptUtil.loadMatrix(ConstantValue.MODEL_PATH+"C19-Computer_1_origin.txt");
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
		//6.3、根据测试组的概念对确定对应的输入向量
		double[][] inputs_test = new double[listTrainConcepts.size()-all_train_size][];
		tempIndex = 0;
		for(Entry<String, List<String[]>> trains : mapTests.entrySet()){
			for (String[] concepts : trains.getValue()) {
				inputs_test[tempIndex++] = mapInputVectors.get(concepts[0]+"_"+concepts[1]);
			}
		}
		
//		double[][] targts_test = new double[train_size-train_part][];
		
		//7、根据关系名逐次训练对应的BP网络,每个BP网络的输出为[0,1]或[1,0]
		int hdn_size = (int) Math.round(Math.pow(inputVectorSize*2+2, 0.5)+5);	//隐藏层节点数
		int maxTrain = 5000;
		double eta = 0.25;
		double momentum = 0.3;
		double limitErr = 0.01;
		
		for (String relation : relations) {
			// 构建当前BP网络的输出向量
			double[][] targts = new double[all_train_size][2];
			tempIndex = 0;
			for(Entry<String, List<String[]>> trains : mapTrains.entrySet()){
				for (String[] concepts : trains.getValue()) {
					if(mapTrainConcepts.get(relation).contains(concepts)){	//表示当前概念对的关系为relation，输出向量为[1,0]
						targts[tempIndex][0] = 1;
						targts[tempIndex][1] = 0;
					} else {	//否则为[0,1]
						targts[tempIndex][0] = 0;
						targts[tempIndex][1] = 1;
					}
//					System.out.println(concepts[0] + "_" + concepts[1] + " : [" + targts[tempIndex][0] + " , " + targts[tempIndex][1] + "]");
					tempIndex++;
				}
			}
			
			// 训练: 输入向量维数ipt_size，隐藏层节点数hdn_size，输出向量维数opt_size，学习率eta， 学习动量momentum， 最大误差limitErr
			BP bp = new BP(inputVectorSize*2, hdn_size, 2, maxTrain, eta, momentum, limitErr);
			bp.train(inputs, targts);
			bp.writeModel(relation);
			break;	//测试使用，仅训练1次，没问题需要删除
		}
		
	}
}
