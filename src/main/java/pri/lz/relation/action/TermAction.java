package pri.lz.relation.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import pri.lz.relation.service.TermService;
import pri.lz.relation.service.impl.TermServiceImpl;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.DomainConcept;
import pri.lz.relation.util.FileUtil;
import pri.lz.relation.util.MapUtil;

/**
* @ClassName: TermAction
* @Description: 系统第2步，完成对原子词的处理，最终得到概念集合和特征词集合，主要过程如下：
* 				1）对原子词集合进行术语提取，首先通过停用词性和停用词筛选，然后利用原子词步长法，初步筛选出术语集合2-term/1st；
* 				2）对初步获取的术语集合进行停用词筛选、词频筛选、去重，得到候选概念词集合2-term/2nd；
* 				3）将分词结果与术语词典相比对，得到特征词典集合2_segment/tfIdf_segment.txt
* 				4）将候选概念词集合与术语词典相比对，获取概念词集合2-concept/
* @author 廖劲为
* @date 2017年6月13日 下午8:29:20
* 
*/
public class TermAction {
	
	FileUtil fileUtil = new FileUtil();
	DomainConcept domainConcept = new DomainConcept();
	public static void main(String[] args) throws IOException {
		TermAction termAction = new TermAction();
		
		//0、根据停用词性及停用词筛选原子词
//		termAction.filterAtom();
		
		//1、根据原子词步长法，初步获取术语集合
//		int minfrequency = 2;	//阈值
//		termAction.getTerm(minfrequency);
		
		//2、处理初步获取的术语集合，得到候选概念词集合
//		termAction.dealTerm();
		
		//3、将分词结果集合通过TF-IDF，获取特征词典集合
//		termAction.buildFetureDic();
		
		//4、将候选概念词集合与术语词典相比对，获取概念词集合
		termAction.checkTerms();
		
		//5、根据术语词典，提取各领域概念词
//		termAction.getTermByDomain();
		
		System.out.println("--over--");
	}
	
	/**
	* @Title: getTermByDomain
	* @Description: 根据术语词典的相关信息提取不同领域的术语作为概念词
	* @return void
	*/
	private void getTermByDomain() {
		//1、加载术语info
		Map<String, String> online_term_info = fileUtil.readInfo(ConstantValue.ONLINE_INFO_OK);
		//2、解析术语info，并按照领域存储
		TermService termService = new TermServiceImpl();
		termService.storeTermByxks(ConstantValue.TERM_3RD_PATH, online_term_info);
	}

	/**
	* @Title: filterAtom
	* @Description: 根据停用词性及停用词筛选原子词
	* @return void
	* @throws IOException 
	*/
	public void filterAtom() throws IOException {
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		TermService termService = new TermServiceImpl();
		//3、根据不同文件夹进行筛选
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			termService.delUnuseWord(ConstantValue.SEGMENT_PATH+dir, ConstantValue.WORD_PATH+dir, stopNatures, stopWords);
		}
	}

	/**
	* @Title: buildFetureDic
	* @Description: 构建特征词典，通过TF-IDF
	*/
	public void buildFetureDic() throws IOException{
		// 获取预处理语料库下的所有文档
		List<File> listFiles = fileUtil.traverseFile(ConstantValue.PREDEAL_PATH);
		// 统计语料库中的文档总数
		int doc_count = listFiles.size();
		// 获取汇总后的分词结果
		Map<String, String> map_words = fileUtil.readInfo(ConstantValue.WORD_LIST_FILE);
		
		//统计总词频数
		int total_word = 0;
		for(Entry<String, String> word : map_words.entrySet()){
			total_word += Integer.parseInt(word.getValue().trim());
		}
		System.out.println("文档总数：" + doc_count + " , 词频总数：" + total_word + " , 词个数：" + map_words.size());
		
		//遍历计算TF-IDF
		Map<String, Object> map_word_tfidf = new HashMap<>();	//存储对应词的IDF值
		DecimalFormat df = new DecimalFormat("#.00000");	//保留小数位
		for(Entry<String, String> word : map_words.entrySet()){
			//统计包含当前词的文档数
			int contain_count = 0;
			for (File file : listFiles) {
				String text = fileUtil.readTxt(file, "UTF-8");
				if(text.indexOf(word.getKey().trim()) >= 0){
					contain_count++;
				}
			}
			System.out.println(word.getKey() + " -> " + contain_count);
			//根据公式计算IDF值
			double idf = Math.log(doc_count/(contain_count+1));
			
			//计算TF值
			double tf = (double) Integer.parseInt(word.getValue().trim())/total_word ;
			
			//计算tf-idf，并存储按照“词频 tfidf tf idf”存储到map集合中
			map_word_tfidf.put(word.getKey().trim(), word.getValue() + "\t" + df.format(tf*idf) + "\t" 
								+ df.format(tf) + "\t" + df.format(idf));
			// 为了避免内存过大，设置一定长度后就写入本地文件
			if(map_word_tfidf.size()>300){
				fileUtil.writeMap2Txt(map_word_tfidf, ConstantValue.WORD_TFIDF_FILE, true);
				map_word_tfidf.clear();
			}
		}
		
		//将计算结果写入到本地文件
		fileUtil.writeMap2Txt(map_word_tfidf, ConstantValue.WORD_TFIDF_FILE, true);
	}

	/**
	* @Title: getConcepts
	* @Description: 将候选概念词集合与术语词典相比对，获取概念词集合
	*/
	public void checkTerms() throws IOException{
		//1、加载术语词典
		HashSet<String> online_term_ok = fileUtil.readDicUTF8(ConstantValue.ONLINE_TERM_OK);
		Map<String, String> online_term_info = fileUtil.readInfo(ConstantValue.ONLINE_INFO_OK);
		//2、加载分词结果的合并，构造特征词典
//		HashSet<String> term_wait = fileUtil.readMap(ConstantValue.SEGMENT_PATH+"answer_total_segment.txt");
//		term_wait.addAll(fileUtil.readMap(ConstantValue.SEGMENT_PATH+"train_total_segment.txt"));
//		HashSet<String> term_dic = new HashSet<>();
//		for (String term : term_wait) {
//			if(online_term_ok.contains(term)){
//				term_dic.add(term);
//			}
//		}
//		fileUtil.writeSet2Txt(term_dic, ConstantValue.TERM_DIC_PATH, false);
		//3、构造领域概念集合
		// 实例化InputStreamReader
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
				
		String[] dirs = {"train","answer"};
		
		for (String dir : dirs) {
			System.out.println("cur dir: " + dir);
			// 获取当前领域文件夹所有的初处理得到的术语文件
			List<File> listFiles = fileUtil.getAllFiles(ConstantValue.TERM_2ND_PATH+dir);
			for (File file : listFiles) {
				String fileName = file.getName();
				read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
				bufferedReader = new BufferedReader(read);
				Map<String, Integer> map_term = new HashMap<>();
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					String[] words = lineTxt.split("\t");
					if(words.length==2){
						String word = words[0].trim().replaceAll("_", "");
						if(online_term_ok.contains(word) && checkDomain(word,fileName.replaceAll(".txt", ""), online_term_info.get(word))){
							int w_count = Integer.parseInt(words[1]);
							Integer count = map_term.get(word);
							map_term.put(word, count==null ? w_count : w_count+count);
						}
					}
				}
				// 写入文件
				fileUtil.writeTxt(map_term, ConstantValue.CONCEPT_PATH+dir+"/"+fileName, false);
			}
		}
	}
	
	private boolean checkDomain(String word, String domain, String online_info) {
		// 根据领域对应的概念学科
		String[] xuekes = domainConcept.getDomain_ke().get(domain).split(",");
		// 将online_info转换为json格式
		JSONObject jsonobjec = JSON.parseObject(online_info);
		String[] checks = jsonobjec.get("xks").toString().split("，");
		// 验证学科是否正确
		for (String check_term : checks) {
			for (String xueke : xuekes) {
				if(xueke.equals(check_term))
					return true;
			}
		}
		return false;
	}

	/**
	* @Title: dealTerm
	* @Description: 处理初步获取的术语集合，得到候选概念词集合
	* @throws IOException
	*/
	public void dealTerm() throws IOException{
		TermService termService = new TermServiceImpl();
		//加载停用词
		HashSet<String> stopwords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			// 获取当前领域文件夹所有的初处理得到的术语文件
			List<File> listFiles = fileUtil.getAllFiles(ConstantValue.TERM_1ST_PATH+dir);
			for (File file : listFiles) {
				termService.dealTerm(file.getPath(), ConstantValue.TERM_2ND_PATH+dir+"/"+file.getName(), stopwords);
			}
		}
		// 将所有语料中的各个领域的候选概念词汇总
		// 实例化InputStreamReader
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		for (String dir : dirs) {
			Map<String, Integer> map_term = new HashMap<>();
			List<File> listFiles = fileUtil.getAllFiles(ConstantValue.TERM_2ND_PATH+dir+"/");
			for (File file : listFiles) {
				read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
				bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					String[] words = lineTxt.split("\t");
					if(words.length==2){
						String word = words[0].trim().replaceAll("_", "");
						int w_count = Integer.parseInt(words[1]);
						Integer count = map_term.get(word);
						map_term.put(word, count==null ? w_count : w_count+count);
					}
				}
			}
			// 写入txt文件
			fileUtil.writeTxt(map_term, ConstantValue.TERM_2ND_PATH + dir + "_total_term.txt", false);
		}
	}	
	
	/**
	* @Title: getTerm
	* @Description: 根据原子词步长法提取术语集合
	* @param minfrequency-原子词步长法对应的阈值
	* @throws IOException
	*/
	public void getTerm(int minfrequency) throws IOException{
		
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			//新建文件夹
			File temp = new File(ConstantValue.TERM_1ST_PATH+dir);
			if(!temp.exists()){
				temp.mkdirs();
			}
			
			// 获取每个语料库下的所有领域文件夹
			List<File> listDomains = fileUtil.getAllFileDirs(ConstantValue.WORD_PATH+dir);
			Num i = new Num(0); // 新建对象，准备传递给线程
			new AtomThread(i, listDomains, minfrequency, dir).start(); // 新建线程1，并启动
			new AtomThread(i, listDomains, minfrequency, dir).start(); // 新建线程2，并启动
		}
	}
}

/**
* @ClassName: AtomThread
* @Description: 多线程类，用于处理原子词步长法
* @author 廖劲为
* @date 2017年11月28日 下午3:29:02
*/
class AtomThread extends Thread {
	Num id; // 申明对象，默认null，就是没有指向任何实体
	int sno; // 申明int变量。因为系统默认初始化为0，所以应该是定义一个int变量
	int minfrequency;	//最低阈值
	String dir;	//train或answer，对应语料类别
	
	List<File> listDomains;	//领域文件夹

	AtomThread(Num id, List<File> listDomains, int minfrequency, String dir) {
		this.id = id;
		this.listDomains = listDomains;
		this.minfrequency = minfrequency;
		this.dir = dir;
	}

	public void run() {
		TermService termService = new TermServiceImpl();
		FileUtil fileUtil = new FileUtil();
		while (true) {
			synchronized (this) {
				sno = id.i; // 保存id.i的数值，到线程私有变量sno
				if(sno>=listDomains.size()) {
					break;
				}
				id.i++;
				
				File domain = listDomains.get(sno);	//获取当前需要操作的文件
				
				//根据原子词步长法获取相应术语集合
				try {
					Map<String, Integer> terms = termService.atomTerm(ConstantValue.WORD_PATH+dir+"/"+domain.getName(),	//有效原子词文件夹路径
							ConstantValue.PREDEAL_PATH+dir+"/"+domain.getName(),	//语料库预处理后的路径
							minfrequency);	//阈值
					System.out.println(this.getName() + " : dir = " + dir + " -> name = " + domain.getName() + " -> " + terms.size());
					//将term map集合按频率降序排列
					MapUtil mapUtil = new MapUtil();
					Map<String, Integer> termsOrderDesc = mapUtil.sortMapByValueDesc(terms, true);
					//将排序后的术语集合写入到txt
					String txt = "";
					for(Entry<String, Integer> term : termsOrderDesc.entrySet()){
						txt += term.getKey() + "\t" + term.getValue() + "\n";
					}
					fileUtil.writeTxt(txt, ConstantValue.TERM_1ST_PATH+dir+"/"+domain.getName()+".txt", false);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}
}

class Num{ // 定义一个类

	int i;

	Num(int i) {
		this.i = i;
	}
}