package pri.lz.relation.train;

import java.io.IOException;
import java.util.List;

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
		
	}
	
	// 训练BP网络
	public void train() throws IOException{
		//1、读取训练数据集
		List<String[]> listTrainConcepts = conceptUtil.loadTrain(ConstantValue.MODEL_PATH+"C19-Computer_train_relation.txt");
		//2、顺序读取概念，对应概念特征向量
		List<String> listConcepts = conceptUtil.loadConcepts(ConstantValue.MODEL_PATH+"C19-Computer_train.txt");
		//3、顺序读取概念特征向量
		List<double[]> listVectors = conceptUtil.loadMatrix(ConstantValue.MODEL_PATH+"C19-Computer_1_origin.txt");
		//4、构建输入向量
		
	}
}
