package pri.lz.relation.action;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import pri.lz.relation.service.RelationService;
import pri.lz.relation.service.impl.RelationServiceImpl;
import pri.lz.relation.util.BP;
import pri.lz.relation.util.ConceptRealtionUtil;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

/**
* @ClassName: ConceptAction
* @Description: 系统第3步，完成对候选术语的处理，最终得到候选概念集合，主要过程如下：
* @author 廖劲为
* @date 2017年4月12日 下午3:28:16
* 
*/
public class ConceptAction {

	public static void main(String[] args){
		System.out.println("--start--");
		ConceptAction action = new ConceptAction();
		long start = System.currentTimeMillis();
		
		// 计算概念特征向量并写入文件
		action.conceptVector();
		
		// 统计指定领域的概念的特征向量矩阵
//		action.countIndexMatrix();
		
		// 计算概念相关度
//		action.computeRelated();
		
		// 通过BP神经网络训练
//		action.trainByBP();
		
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

	// 计算概念向量
	public void conceptVector(){
		int feauterSize = 50;
		RelationService relationService = new RelationServiceImpl();
		relationService.featureVector("C19-Computer", "train", ConstantValue.CONCEPT_PATH + "train/C19-Computer.txt", feauterSize);
	}
	
	// 统计指定领域的概念的特征向量矩阵
	public void countIndexMatrix(){
		RelationService relationService = new RelationServiceImpl();
		try {
			relationService.countIndexMatrix("train", "C19-Computer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 计算概念之间的相关度
	public void computeRelated(){
		double limit = 0.3;
		String type = "train";
		String domainName = "C19-Computer";
		RelationService relationService = new RelationServiceImpl();
		try {
			relationService.conceptRelated(type, domainName, limit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 通过BP神经网络训练
	public void trainByBP(){
		String domainName = "C19-Computer";
		RelationService relationService = new RelationServiceImpl();
		try {
			relationService.trainByBP(domainName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}