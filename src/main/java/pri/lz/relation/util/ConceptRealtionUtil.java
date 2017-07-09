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
		
		for (String[] concepts : listTrainConcepts) {
			double[] vector1 = listVectors.get(listConcepts.indexOf(concepts[0]));	//概念1的特征向量
			double[] vector2 = listVectors.get(listConcepts.indexOf(concepts[1]));	//概念2的特征向量
			inputVectors.put(concepts[0].trim()+"_"+concepts[1].trim(), mergeVetor(vector1, vector2, inputVectorSize));
		}
		
		return inputVectors;
	}
	
	// 首先提取>0的索引位置，不够size个，就继续提取vecotr1中的降序排列中的数，使索引位置的个数为size，并将其对应数值降序排列
	private double[] mergeVetor(double[] vector1, double[] vector2, int size){
		int[] bothShowIndex = new int[vector1.length];	//vector1和vector2同样索引位置必须全部>0,存储对应的索引
		double[][] bothShowVector = new double[2][vector1.length];	//vector1和vector2同样索引位置必须全部>0
		int index = 0;
		for(int i=0; i<vector1.length; i++){
			if(vector1[i]>0 && vector2[i]>0){
				bothShowVector[0][index] = vector1[i];
				bothShowVector[1][index] = vector2[i];
				bothShowIndex[index++] = i;
			}
		}
		if(index<size){
			//将vector1和vector2整合到一个二维数组中
			double[][][] temp = new double[2][vector1.length][2];
			for(int i=0; i<vector1.length; i++){
				temp[0][i][0] = vector1[i];
				temp[0][i][1] = i;
				temp[1][i][0] = vector2[i];
				temp[1][i][1] = vector2[i];
			}
			
			for (int i=0;i<size; i++) {
				for (int j = i+1; j < vector1.length; j++) {
					if(temp[0][j][0]>temp[0][i][0]){
						double d = temp[0][i][0];
						double a = temp[0][i][1];
						temp[0][i][0] = temp[0][j][0];
						temp[0][j][0] = d;
						temp[0][i][1] = temp[0][j][1];
						temp[0][j][1] = a;
						// 同步修改对应temp[1]
						d = temp[0][i][0];
						a = temp[0][i][1];
						temp[1][i][0] = temp[1][j][0];
						temp[1][j][0] = d;
						temp[1][i][1] = temp[1][j][1];
						temp[1][j][1] = a;
					}
				}
				// 判断当前的index是否已经提取
				boolean flag = true;	//true表示未提取
				for(int idx : bothShowIndex){
					if(idx==temp[0][i][1]){
						flag = false;
						break;
					}
				}
				if(flag){
					bothShowVector[0][index] = temp[0][i][0];
					bothShowVector[1][index++] = temp[1][i][0];
				}
				if(index==size){	//已经提取了指定size的数据，跳出循环
					break;
				}
			}
		}
		for (int i=0;i<size-1; i++) {
			for (int j = i+1; j < index; j++) {
				if(bothShowVector[0][j]>bothShowVector[0][i]){
					double d = bothShowVector[0][i];
					bothShowVector[0][i] = bothShowVector[0][j];
					bothShowVector[0][j] = d;
					// 同步修改对应vector[1]
					d = bothShowVector[1][i];
					bothShowVector[1][i] = bothShowVector[1][j];
					bothShowVector[1][j] = d;
				}
			}
		}
		
		double[] iptVector = new double[size*2];
		for(int i=0; i<size; i++){
			iptVector[i] = bothShowVector[0][i];
			iptVector[i+size] = bothShowVector[1][i];
		}
		return iptVector;
	} 
	
	/**
	* @Title: mergeVetor
	* @Description: 硬降维，取第1行的前size个最大值的索引，将对应位置的向量重新组合成2*size大小的一维向量
	* @param vector1
	* @param vector2
	* @param size
	* @return double[]
	*/
	public double[] mergeVetor1(double[] vector1, double[] vector2, int size){
		int leg = vector1.length;
		for (int i=0;i<size; i++) {
			for (int j = i+1; j < leg; j++) {
				if(vector1[j]>vector1[i]){
					double d = vector1[i];
					vector1[i] = vector1[j];
					vector1[j] = d;
					// 同步修改对应vector[1]
					d = vector2[i];
					vector2[i] = vector2[j];
					vector2[j] = d;
				}
			}
		}
		double[] iptVector = new double[size*2];
		for(int i=0; i<size; i++){
			iptVector[i] = vector1[i];
			iptVector[i+size] = vector2[i];
		}
		return iptVector;
	}

	/**
	* @Title: loadInputVector
	* @Description: 根据训练概念对的特征向量构建BP网络的输入向量
	* @param @param listTrainConcepts——训练集中的概念对
	* @param @param listConcepts——所有概念，有序
	* @param @param listVectors——对应概念的特征向量，有序
	* @return Map<String,double[]>——key用概念对表示，形如：concept1_concept2，value对应当前概念对的输入向量
	*/
	public Map<String, double[]> loadInputVector(List<String[]> listTrainConcepts, List<String> listConcepts,
			List<double[]> listVectors) {
		Map<String, double[]> inputVectors = new HashMap<>();
		
		for (String[] concepts : listTrainConcepts) {
			double[] vector1 = listVectors.get(listConcepts.indexOf(concepts[0]));	//概念1的特征向量
			double[] vector2 = listVectors.get(listConcepts.indexOf(concepts[1]));	//概念2的特征向量
			inputVectors.put(concepts[0].trim()+"_"+concepts[1].trim(), mergeVetor(vector1, vector2));
		}
		
		return inputVectors;
	}

	// 将两个向量直接连在一起
	private double[] mergeVetor(double[] vector1, double[] vector2) {
		double[] iptVector = new double[vector1.length+vector2.length];
		for(int i=0; i<vector1.length; i++){
			iptVector[i] = vector1[i];
			iptVector[i+vector1.length] = vector2[i];
		}
		return iptVector;
	}
}
