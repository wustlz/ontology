package pri.lz.relation.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;

import pri.lz.relation.service.TermService;
import pri.lz.relation.service.impl.TermServiceImpl;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;
import pri.lz.relation.util.MapUtil;

/**
* @ClassName: TermAction
* @Description: 系统第2步，完成对原子词的处理，最终得到概念集合和特征词集合，主要过程如下：
* 				1）对原子词集合进行术语提取，首先通过停用词性和停用词筛选，然后利用原子词步长法，初步筛选出术语集合term\\1st；
* 				2）对初步获取的术语集合进行停用词筛选、词频筛选、去重，得到候选概念词集合term\\2nd；
* 				3）将候选概念词集合与术语词典相比对，获取概念词集合
* @author 廖劲为
* @date 2017年6月13日 下午8:29:20
* 
*/
public class TermAction {
	
	FileUtil fileUtil = new FileUtil();
	public static void main(String[] args) throws IOException {
		TermAction termAction = new TermAction();
		
		//1、根据原子词步长法，初步获取术语集合
//		int minfrequency = 2;	//阈值
//		termAction.getTerm(minfrequency);
		
		//2、处理初步获取的术语集合，得到候选概念词集合
		termAction.dealTerm();
		
		//3、将候选概念词集合与术语词典相比对，获取概念词集合
		termAction.getConcepts();
		
		//1、统计在线术语比对结果，分别存储到指定文件夹
//		termAction.mergeTermOk();
		
		System.out.println("--over--");
	}
	
	/**
	* @Title: getConcepts
	* @Description: 将候选概念词集合与术语词典相比对，获取概念词集合
	*/
	public void getConcepts(){
		//
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
				termService.dealTerm(file.getPath(), ConstantValue.TERM_2ND_PATH+dir+"\\"+file.getName(), stopwords);
			}
		}
		// 将所有语料中的各个领域的候选概念词汇总
		// 实例化InputStreamReader
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		for (String dir : dirs) {
			Map<String, Integer> map_term = new HashMap<>();
			List<File> listFiles = fileUtil.getAllFiles(ConstantValue.TERM_2ND_PATH+dir+"\\");
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
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		TermService termService = new TermServiceImpl();
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			//1、删除无效原子词
			termService.delUnuseWord(ConstantValue.SEGMENT_PATH+dir, ConstantValue.WORD_PATH+dir, stopNatures, stopWords);
			
			// 获取每个语料库下的所有领域文件夹
			List<File> listDomains = fileUtil.getAllFileDirs(ConstantValue.WORD_PATH+dir);
			for (File domain : listDomains) {
				//2、根据原子词步长法获取相应术语集合
				Map<String, Integer> terms = termService.atomTerm(ConstantValue.WORD_PATH+dir+"\\"+domain.getName(),	//有效原子词文件夹路径
						ConstantValue.PREDEAL_PATH+dir+"\\"+domain.getName(),	//语料库预处理后的路径
						minfrequency);	//阈值
				System.out.println(terms.size());
				//3、将term map集合按频率降序排列
				MapUtil mapUtil = new MapUtil();
				Map<String, Integer> termsOrderDesc = mapUtil.sortMapByValueDesc(terms, true);
				//4、将排序后的术语集合写入到txt
				String txt = "";
				for(Entry<String, Integer> term : termsOrderDesc.entrySet()){
					txt += term.getKey() + "\t" + term.getValue() + "\n";
				}
				fileUtil.writeTxt(txt, ConstantValue.TERM_1ST_PATH+dir+"\\"+domain.getName()+".txt", false);
			}
			
		}
	}	
	
	// 统计在线术语比对结果，分别存储到指定文件夹
	public void mergeTermOk() throws IOException{
		String path = ConstantValue.DATA_ROOT_PATH + "online\\agg_online\\";
		List<File> listFiles = fileUtil.getAllFiles(path);
		Map<String, String> map_term_ok = new HashMap<>();
		Map<String, String> map_term_no = new HashMap<>();
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		for (File file : listFiles) {
			read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				String[] temp = lineTxt.split("\t");
				if(temp.length==2){
					// 根据术语在线的统计信息判断术语
					JSONObject jsonResult = JSONObject.parseObject(temp[1]);
					if(jsonResult!=null && jsonResult.getString("count")!=null && !jsonResult.getString("count").equals("0")){
						map_term_ok.put(temp[0], temp[1]);
						if(writeMap(map_term_ok, "term_online_ok.txt", false)){
							map_term_ok.clear();
						}
					} else {
						map_term_no.put(temp[0], temp[1]);
						if(writeMap(map_term_no, "term_online_no.txt", false)){
							map_term_no.clear();
						}
					}
				}
			}
			read.close();
		}
		writeMap(map_term_ok, "term_online_ok.txt", true);
		writeMap(map_term_no, "term_online_no.txt", true);
	}
	
	public boolean writeMap(Map<String, String> map_term, String txtName, boolean write) throws IOException{
		if(map_term.size()>5000 || write){
			String txt = "";
			String txt_online = "";
			for(Entry<String, String> term : map_term.entrySet()){
				txt_online += term.getKey() + "\t" + term.getValue() + "\n";
				txt += term.getKey() + "\n";
			}
			fileUtil.writeTxt(txt_online, ConstantValue.DATA_ROOT_PATH + "term\\" + txtName, true);
			fileUtil.writeTxt(txt, ConstantValue.DATA_ROOT_PATH + "term\\" + txtName.replaceAll("online_", ""), true);
			return true;
		} else {
			return false;
		}
	}
}