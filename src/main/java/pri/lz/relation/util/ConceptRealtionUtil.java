package pri.lz.relation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptRealtionUtil {
	
	
	/**
	* @Title: segConcepts
	* @Description: 拆分训练数据集
	* @return Map<String,List<String[]>>-Key对应关系名，value对应概念对集合
	*/
	public Map<String, List<String[]>> segConcepts(List<String[]> listConcepts){
		Map<String, List<String[]>> mapTrainConcepts = new HashMap<>();
		for (String[] trains : listConcepts) {
			List<String[]> lists = mapTrainConcepts.get(trains[2].trim());
			if(lists==null){
				lists = new ArrayList<>();
			}
			String[] concepts = {trains[0].trim(), trains[1].trim()};
			lists.add(concepts);
			mapTrainConcepts.put(trains[2].trim(), lists);
		}
		return mapTrainConcepts;
	}
	
	// 根据领域加载概念向量集合（压缩后）
	public List<double[]> loadMatrix(String fileName) throws IOException{
		File file = new File(fileName);
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		List<double[]> listMatrix = new ArrayList<>();
		while ((lineTxt = bufferedReader.readLine()) != null) {
			String[] conceptVector = lineTxt.split("\t");
			int leg = conceptVector.length;
			if(leg>0){
				double[] vector = new double[leg];
				for (int i=0; i<leg; i++) {
					vector[i] = Double.parseDouble(conceptVector[i]);
				}
				listMatrix.add(vector);
			}
		}
		read.close();
		return listMatrix;
	}

	// 顺序读取概念名称
	public List<String> loadConcepts(String fileName) throws IOException{
		File file = new File(fileName);
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		List<String> listConcepts = new ArrayList<>();
		while ((lineTxt = bufferedReader.readLine()) != null) {
			listConcepts.add(lineTxt);
		}
		read.close();
		return listConcepts;
	}
	
	// 根据领域有序加载对应的概念名词
	public List<String[]> loadTrain(String fileName) throws IOException{
		File file = new File(fileName);
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		
		List<String[]> listTrains = new ArrayList<>();
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			String[] temp = lineTxt.split("\t");
			if(temp.length>0){
				listTrains.add(temp);
			}
		}
		read.close();
		return listTrains;
	}

	// 读取训练好的BP网络模型
	public double[][] loadBPModel(String fileName) throws IOException{
		File file = new File(fileName);
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		List<double[]> listMatrix = new ArrayList<>();
		int col = 0;
		
		while ((lineTxt = bufferedReader.readLine()) != null) {
			String[] conceptVector = lineTxt.split("\t");
			col = conceptVector.length;
			if(col>0){
				double[] vector = new double[col];
				for (int i=0; i<col; i++) {
					vector[i] = Double.parseDouble(conceptVector[i]);
				}
				listMatrix.add(vector);
			}
		}
		read.close();
		int row = listMatrix.size();
		double[][] model = new double[row][col];
		for (int i=0; i<row; i++) {
			for (int j=0; j<col; j++) {
				model[i][j] = listMatrix.get(i)[j];
			}
		}
		return model;
	}

	/**
	* @Title: loadInputVector
	* @Description: 根据训练概念对的特征向量构建BP网络的输入向量
	* @param @param listTrainConcepts——训练集中的概念对
	* @param @param listConcepts——所有概念，有序
	* @param @param listVectors——对应概念的特征向量，有序
	* @param @param inputVectorSize——BP网络的输入向量维数
	* @return Map<String,double[]>——key用概念对表示，形如：concept1_concept2，value对应当前概念对的输入向量
	*/
	public Map<String, double[]> loadInputVector(List<String[]> listTrainConcepts, List<String> listConcepts,
			List<double[]> listVectors, int inputVectorSize) {
		Map<String, double[]> inputVectors = new HashMap<>();
		
		// 初步压缩概念对的特征向量（将同位置均为0的去除）
		Map<String, double[][]> firstReduce = new HashMap<>();
		for (String[] concepts : listTrainConcepts) {
			double[] vector1 = listVectors.get(listConcepts.indexOf(concepts[0]));	//概念1的特征向量
			double[] vector2 = listVectors.get(listConcepts.indexOf(concepts[1]));	//概念2的特征向量
			double[][] vector = new double[2][vector1.length];
			int v_size = 0;	//合并后的非0向量为长度
			for (int i = 0; i < vector1.length; i++) {
				if(vector1[i]!=0 || vector2[i]!=0){
					vector[0][v_size] = vector1[i];
					vector[1][v_size++] = vector2[i];
				}
			}
		}
		return null;
	}
	
	private double[] mergeVetor(double[][] vector){
		return null;
	}
}
