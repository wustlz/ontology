package pri.lz.relation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConceptRealtionUtil {
	
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
}
