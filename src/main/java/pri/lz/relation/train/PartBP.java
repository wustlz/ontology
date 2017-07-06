package pri.lz.relation.train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		int inputVectorSize = 200;	//BP网络输入向量的维数
		//1、读取训练数据集
		List<String[]> listTrainConcepts = conceptUtil.loadTrain(ConstantValue.MODEL_PATH+"C19-Computer_train_relation.txt");
		Map<String, List<String[]>> mapTrainConcepts = conceptUtil.segConcepts(listTrainConcepts);	//按关系名拆分成map集合
		//2、顺序读取概念，对应概念特征向量
		List<String> listConcepts = conceptUtil.loadConcepts(ConstantValue.MODEL_PATH+"C19-Computer_train.txt");
		//3、顺序读取概念特征向量
		List<double[]> listVectors = conceptUtil.loadMatrix(ConstantValue.MODEL_PATH+"C19-Computer_1_origin.txt");
		
		//4、构建训练组和测试组
		Map<String, List<String[]>> mapTrains = new HashMap<>();	//训练组
		Map<String, List<String[]>> mapTests = new HashMap<>();		//测试组
		Set<String> relations = new HashSet<>();	//关系名，用作确定BP网络个数
		
		for(Entry<String, List<String[]>> trains : mapTrainConcepts.entrySet()){
			relations.add(trains.getKey().trim());
			int train_size = (int) Math.round(trains.getValue().size()*train_scale);
			if(train_size == trains.getValue().size()){
				train_size--;
			}
			mapTrains.put(trains.getKey().trim(), trains.getValue().subList(0, train_size));
			mapTests.put(trains.getKey().trim(), trains.getValue().subList(train_size,trains.getValue().size()));
//			System.out.println(trains.getKey() + " --> mapTrains :" + mapTrains.get(trains.getKey().trim()).size() +
//					" , mapTests : " + mapTests.get(trains.getKey().trim()).size());
		}
		
		//5、构建输入向量
		Map<String, double[]> mapInputVectors = conceptUtil.loadInputVector(listTrainConcepts, listConcepts, listVectors, inputVectorSize);
		
		//5、根据关系名逐次训练对应的BP网络
		for (String relation : relations) {
			
		}
		
	}
	
	
	
}
