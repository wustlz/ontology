package pri.lz.relation.action;

import java.io.IOException;

import pri.lz.relation.service.RelationService;
import pri.lz.relation.service.impl.RelationServiceImpl;
import pri.lz.relation.util.ConstantValue;

/**
* @ClassName: ConceptAction
* @Description: 通过神经网络训练分类模型
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
		
		// 通过BP神经网络训练
//		action.trainByBP();
		
		long end = System.currentTimeMillis();
		System.out.println("--end: " + (end-start) + " ms--");
	}
	
	// 计算概念向量
	public void conceptVector(){
		RelationService relationService = new RelationServiceImpl();
		relationService.featureVector("C19-Computer", "train", ConstantValue.CONCEPT_PATH + "train\\C19-Computer.txt");
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