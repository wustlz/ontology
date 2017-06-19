package pri.lz.relation.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pri.lz.relation.service.RelationService;
import pri.lz.relation.util.BP;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

public class RelationServiceImpl implements RelationService {

	FileUtil fileUtil = new FileUtil();

	// 计算概念特征向量
	@Override
	public void featureVector(String domainName, String typeName, String conceptPath) {
		String vector_path = ConstantValue.CONCEPT_VECTOR_PATH + typeName + "\\" + domainName;
		// 添加文件夹
		File file = new File(vector_path);
		if(!file.exists()){
			file.mkdirs();
		}
		try {
			// 读取概念
			List<String> listConcepts = loadConcept(conceptPath);
			List<String> listComputed = new ArrayList<>();	//已计算的概念特征向量
			System.out.println("listConcepts size: " + listConcepts.size());
			// 读取特征术语
			List<String> listFeatures = fileUtil.readTxt(ConstantValue.TERM_DIC_PATH, "UTF-8");
			System.out.println("listFeatures size: " + listFeatures.size());
			// 读取指定领域语料库中的句子
			List<String> listSentences = loadDomainSentence(domainName, typeName);
			System.out.println("listSentences size: " + listSentences.size());
			int sentence_size = listSentences.size();	//语料中的句子总数
			//定义一个存储map，string表示对应的特征词，list<Integer>表示对应的句子下标
			Map<String, List<Integer>> map_feature_sentence = countTermSentence(listFeatures, listSentences);
			//统计每个概念词出现的句子数
			Map<String, List<Integer>> map_concept_sentence = countTermSentence(listConcepts, listSentences);
			
			//遍历每个概念词，计算该概念词的向量表达
//			List<Map<Integer, Double>> listConceptVectors = new ArrayList<>();	//与概念词的长度一致，相同索引位置表示相对应的概念向量表达式
			int features_size = listFeatures.size();
			for (String concept : listConcepts) {
				Map<Integer, Double> conceptVector = new HashMap<>();
				//遍历特征词
				for (int i=0; i<features_size; i++) {
					String feature = listFeatures.get(i);
					if (feature.equals(concept) || concept.indexOf(feature)>=0) {	//碰到相同情况获取feature是concept的子串，直接赋值为1
						conceptVector.put(i, 1.0);
						continue;
					}
					//统计两个词的共现句子
					List<Integer> listConceptFeatureIndex = countConceptFeatureSentence(feature, listSentences, map_concept_sentence.get(concept));
					if(listConceptFeatureIndex!=null && listConceptFeatureIndex.size()>0){
						//计算互信息
						double a_b = (double) listConceptFeatureIndex.size() / sentence_size;	// 概念与特征词共现的概率
						double a = (double) map_feature_sentence.get(feature).size() / sentence_size;	// 特征词共现的概率;
						double b = (double) map_concept_sentence.get(concept).size() / sentence_size;	// 概念词共现的概率;
						BigDecimal bd = new BigDecimal(Math.log(a_b/(a*b)));
						double p = bd.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
						if(p>0){
//							System.out.println(a_b + " , " + a + " , " + b);
//							System.out.println(listConceptFeatureIndex.size() + " , " + map_feature_sentence.get(feature).size()
//									+ " , " + map_concept_sentence.get(concept).size());
							conceptVector.put(i,p);
						}
//						if(concept.equals("系统") && i==0)
//						System.out.println("a: " + a + " | b: " + b + " | p: " + p + " | log: " + Math.log(p));
					}
				}
				listComputed.add(concept);
				//写入文件
				writeConceptVectorToTxt(conceptVector, vector_path + "\\" + concept + ".txt");
				System.out.println(concept + " vector compute!");
//				listConceptVectors.add(conceptVector);
			}
			// 将listComputed写入文件
			String txt = "";
			for (String conpt : listComputed) {
				txt += conpt + "\n";
			}
			fileUtil.writeTxt(txt, ConstantValue.CONCEPT_EXIST_FILE, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* @Title: countConceptFeatureSentence
	* @Description: 计算概念词、特征词在句子集合中共现的句子
	* @param @param feature
	* @param @param listSentences
	* @param @param listConceptIndex——语料库中包含概念词的句子索引集合
	* @return List<Integer>
	*/
	private List<Integer> countConceptFeatureSentence(String feature, List<String> listSentences, List<Integer> listConceptIndex){
		List<Integer> listIndex = new ArrayList<>();
		//遍历出现概念词句子索引
		for (Integer conceptIndex : listConceptIndex) {
			//表示概念词的句子中同时含有特征词
			if(listSentences.get(conceptIndex).indexOf(feature)>=0){
				listIndex.add(conceptIndex);
			}
		}
		return listIndex;
	}
	
	/**
	* @Title: countTermSentence
	* @Description: 统计每个特征词出现的句子数
	* @param @param listTerms——特征词集合/概念词集合
	* @param @param listSentences——语料库中的所有句子集合
	* @return Map<String,List<Integer>>
	*/
	private Map<String, List<Integer>> countTermSentence(List<String> listTerms, List<String> listSentences){
		//定义一个存储map，string表示对应的特征词，list<Integer>表示对应的句子下标
		Map<String, List<Integer>> map_term_sentence = new HashMap<>();
		//遍历每个特征词
		for (String term : listTerms) {
			//定义一个存储句子索引的list集合
			List<Integer> listIndex = new ArrayList<>();
			for (int i = 0; i < listSentences.size(); i++) {
				//如果句子含有该特征词，添加
				if(listSentences.get(i).indexOf(term)>=0){
					listIndex.add(i);
				}
			}
			//添加到list集合
			map_term_sentence.put(term, listIndex);
		}
		return map_term_sentence;
	}
	
	/**
	* @Title: writeConceptVectorToTxt
	* @Description: 将概念向量集合写入txt文件
	* @param @param listConceptVectors
	*/
	private void writeConceptVectorToTxt(Map<Integer, Double> conceptVectors, String fileName){
		String txt = "";
		for (Entry<Integer, Double> conceptVector : conceptVectors.entrySet()) {
			txt += conceptVector.getKey() + "\t" + conceptVector.getValue() + "\n";
		}
		try {
			fileUtil.writeTxt(txt, fileName, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* @Title: loadDomainSentence
	* @Description: 加载指定领域的语料-句子
	* @param domainName
	* @param typeName-仅train或者answer
	* @return List<String>
	*/
	private List<String> loadDomainSentence(String domainName, String typeName) throws IOException{
		List<String> listSentence = new ArrayList<>();
		List<File> listFiles = fileUtil.getAllFiles(ConstantValue.PREDEAL_PATH+typeName+"\\"+domainName);
		InputStreamReader read = null;// 考虑到编码格式
		BufferedReader bufferedReader = null;
		for (File file : listFiles) {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				if(lineTxt.length()>5)
					listSentence.add(lineTxt);
			}
			read.close();
		}
		return listSentence;
	}
	
	//读取概念，不重复
	private List<String> loadConcept(String conceptPath){
		// 读取概念
		HashSet<String> listConcepts = fileUtil.readMap(conceptPath);
		HashSet<String> concepts_exist = fileUtil.readDicUTF8(ConstantValue.CONCEPT_EXIST_FILE);
		List<String> listConcepts_unique = new ArrayList<>();
		for (String concept : listConcepts) {
			if(concepts_exist.contains(concept)){
				continue;
			} else {
				listConcepts_unique.add(concept);
			}
		}
		return listConcepts_unique;
	}

	//计算概念之间的相关性
	@Override
	public double computeRelated(String concept1, String concept2, String domainName) {
		// 从概念特征向量文件夹读取对应的概念特征向量文件
		List<File> listFiles = fileUtil.getAllFiles(ConstantValue.CONCEPT_PATH + domainName);
		Map<Integer, Double> vector1 = new HashMap<>();
		Map<Integer, Double> vector2 = new HashMap<>();
		// 读取对应文件的概念特征向量
		for (File file : listFiles) {
			if(file.getName().equals(concept1+".txt")){
				vector1 = loadVector(file, concept1);
			} else if(file.getName().equals(concept2+".txt")){
				vector2 = loadVector(file, concept2);
			}
		}
		System.out.println("vector1: " + vector1.size());
		System.out.println("vector2: " + vector2.size());
		// 根据夹角余弦计算概念相似度
		double norm1 = 0.0;	//概念1的模
		double norm2 = 0.0;	//概念2的模
		double cosin = 0.0;	//概念1,2的积
		for(int i=0; i<ConstantValue.FEATURE_SIZE; i++){
			Double c1 = vector1.get(i);
			Double c2 = vector2.get(i);
			if(c1!=null){
				norm1 += c1*c1;
			}
			if(c2!=null){
				norm2 += c2*c2;
			}
			if(c1!=null && c2!=null){
				cosin += c1*c2;
			}
		}
		
		double similarity = cosin/(norm1*norm2);
		return similarity;
	}

	// 读取指定文件名的概念特征向量
	private Map<Integer, Double> loadVector(File file, String concept1) {
		Map<Integer, Double> vector = new HashMap<>();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				String[] term = lineTxt.split("\t");
				if(term.length==2){
					vector.put(Integer.parseInt(term[0]), Double.parseDouble(term[1]));
				}
			}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vector;
	}

	// 统计当前领域概念包括所有特征向量不为0的特征词索引
	@Override
	public void countIndexMatrix(String domainName) throws IOException {
		List<String> listConcepts = new ArrayList<>();
		// 读取指定领域的概念特征向量所在的文件夹下的所有文件
		List<File> listFiles = fileUtil.getAllFiles(ConstantValue.CONCEPT_PATH+domainName);
		// 首先统计在所有概念中都起作用的特征词索引
		HashSet<Integer> term_indexs = new HashSet<>();
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		for (File file : listFiles) {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				String[] term = lineTxt.split("\t");
				if(term.length==2){
					term_indexs.add(Integer.parseInt(term[0]));
				}
			}
			read.close();
		}
		// 按升序排列  
        LinkedList<Integer> term_indexs_order = new LinkedList<Integer>(term_indexs);         
//        Comparator<Integer> setComp = Collections.reverseOrder();  //降序
//        Collections.sort(term_indexs_order, setComp); 
        Collections.sort(term_indexs_order);	// 升序
        // 获取特征向量对应的初始矩阵
        int matrix_size = term_indexs_order.size();
        int file_size = listFiles.size();
        double[][] primaryArray = new double[file_size][matrix_size];	//原始特征矩阵
        for (int i = 0; i<file_size; i++) {
        	listConcepts.add(listFiles.get(i).getName().replace(".txt", ""));
			read = new InputStreamReader(new FileInputStream(listFiles.get(i)), "UTF-8");// 考虑到编码格式
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				String[] term = lineTxt.split("\t");
				if(term.length==2){
					primaryArray[i][term_indexs_order.indexOf(Integer.parseInt(term[0]))] = Double.parseDouble(term[1]);
				}
			}
			read.close();
		}
        writeMatrix(primaryArray, ConstantValue.MATRIX_PATH+domainName+"_1_origin.txt");
        // 按当前读取顺序写入概念名
        String txt = "";
        for (String concept : listConcepts) {
			txt += concept + "\n";
		}
        fileUtil.writeTxt(txt, ConstantValue.MATRIX_PATH+domainName+".txt", false);
	}
	
	private void writeMatrix(double[][] matrix, String fileName) throws IOException{
		// 将原始矩阵写入到txt文件
        String txt = "";
        for (double[] ds : matrix) {
        	for (double d : ds) {
        		txt += d + "\t";
			}
        	txt += "\n";
        	if(txt.length()>10000){
        		fileUtil.writeTxt(txt, fileName, true);
        		txt = "";
        	}
		}
        fileUtil.writeTxt(txt, fileName, true);
	}

	// 利用BP训练当前领域的概念关系分类器
	@Override
	public void trainByBP(String domainName) throws IOException {
		// 根据领域加载概念向量集合（压缩后）
		List<double[]> listConceptVector = loadMatrix(domainName);
		// 顺序读取概念名称
		List<String> listConcepts = loadConcepts(domainName);
		// 读取手工训练数据集
		List<String[]> listTrains = loadTrain(domainName);
		int train_size = listTrains.size();
		double[][] datas = new double[train_size][];	//全部数据集
		double[][] targets = new double[train_size][];	//对应的目标向量
		int[] targets_int = new int[train_size];	//对应的int
		String[] concepts = new String[train_size];	//对应的概念关系集合
		Map<String, List<Integer>> relationTypeIndex = new HashMap<>();	//对应关系类型的对应下标值
		for (int i=0; i<train_size; i++) {
			String[] trains = listTrains.get(i);	//当前的概念关系集，算法	遗传算法	is-a
			// 输入向量
			double[] vector1 = listConceptVector.get(listConcepts.indexOf(trains[0]));
			double[] vector2 = listConceptVector.get(listConcepts.indexOf(trains[1]));
			double[] data = new double[vector1.length+vector2.length];	//向量合并作为输入向量
			int k=0;
			for(double d : vector1){
				data[k++] = d;
			}
			for(double d : vector2){
				data[k++] = d;
			}
			datas[i] = data;
			// 目标向量
			targets[i] = new double[ConstantValue.RELATION_SIZE];
			targets[i][ConstantValue.checkRelationType(trains[2])] = 1;
			targets_int[i] = ConstantValue.checkRelationType(trains[2]);
			
			// 添加索引值
			List<Integer> listIndexs = relationTypeIndex.get(trains[2]);
			if(listIndexs==null){
				listIndexs = new ArrayList<>();
			}
			listIndexs.add(i);
			
			//概念关系集
			concepts[i] = trains[0] + "\t" + trains[1];
		}
		/* is-a:[0-78]-79 component-of:[79-108]-30 member-of:[109-138]-30 substance-of:[139-158]-20
		 * cause-to:[159-171]-13 similar:[172-189]-18 opposite:[190-203]-14 attribute:[204-227]-24
		 * TimeOrSpace:[228-249]-22 arithmetic:[250-253]-4
		*/
		int train_part = 200;
		double d = (double)train_part/train_size;
		int[] indexs = {0,79,109,139,159,172,190,204,228,250,254};
		double[][] inputs = new double[train_part][];
		double[][] targts = new double[train_part][];
		double[][] inputs_test = new double[train_size-train_part][];
		double[][] targts_test = new double[train_size-train_part][];
		int[] targets_int_test = new int[train_size-train_part];
		String[] concept_test = new String[train_size-train_part];
		int count = 0;
		int count_test = 0;
		for(int i=0; i<indexs.length-1; i++){
			int need = (int) Math.round((indexs[i+1]-indexs[i])*d);
			for(int k=indexs[i]; k<indexs[i+1]; k++){
				if(k-indexs[i]<need){
					inputs[count] = datas[k];
					targts[count++] = targets[k];
				} else {
					inputs_test[count_test] = datas[k];
					targts_test[count_test] = targets[k];
					targets_int_test[count_test] = targets_int[k];
					concept_test[count_test++] = concepts[k];
				}
			}
		}
		int ipt_size = inputs[0].length;
		int opt_size = targts[0].length;
		int hdn_size = (int) Math.round(Math.pow(ipt_size+opt_size, 0.5)+5);
		int maxTrain = 50000;
		double eta = 0.25;
		double momentum = 0.3;
		double limitErr = 0.1;
		// 训练: 输入向量维数ipt_size，隐藏层节点数hdn_size，输出向量维数opt_size，学习率eta， 学习动量momentum， 最大误差limitErr
		BP bp = new BP(ipt_size, hdn_size, opt_size, maxTrain, eta, momentum, limitErr);
		bp.train(inputs, targts);
		// 检验
		DecimalFormat df = new DecimalFormat("#.000000");
		for (int k=0; k<inputs_test.length; k++) {
			double[] result = bp.getResult(inputs_test[k]);
			double max = -Integer.MIN_VALUE;
			int idx = -1;
			String txt = "";
			for (int i = 0; i != result.length; i++) {
				txt += df.format(result[i]) + "\t";
				if (result[i] > max) {
					max = result[i];
					idx = i;
				}
			}
			System.out.println(concept_test[k] + "\t" + idx + "\t"
			+ ConstantValue.relationType(idx) + "\t" + ConstantValue.relationType(targets_int_test[k])
			+ "\t" + txt);
		}
	}
	
	// 根据领域有序加载对应的概念名词
	private List<String[]> loadTrain(String domainName) throws IOException{
		File file = new File(ConstantValue.RELATION_PATH + domainName + "_train_relation.txt");
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
		
		List<String[]> listTrains = new ArrayList<>();
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			String[] temp = lineTxt.split("\t");
			if(temp.length==3){
				listTrains.add(temp);
			}
		}
		read.close();
		return listTrains;
	}

	// 根据领域加载概念向量集合（压缩后）
	private List<double[]> loadMatrix(String domainName) throws IOException{
		File file = new File(ConstantValue.MATRIX_PATH + domainName + "_2_lle.txt");
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
	private List<String> loadConcepts(String domainName) throws IOException{
		File file = new File(ConstantValue.MATRIX_PATH + domainName + ".txt");
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
}