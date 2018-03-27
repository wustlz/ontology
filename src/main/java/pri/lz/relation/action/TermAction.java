package pri.lz.relation.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import pri.lz.relation.service.TermService;
import pri.lz.relation.service.impl.TermServiceImpl;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;
import pri.lz.relation.util.TermUtil;

/**
* @ClassName: TermAction
* @Description: 系统第2步，完成对原子词的处理，最终得到候选术语集合，主要过程如下：
* 				1）对原子词集合进行术语提取，首先通过停用词性和停用词筛选，初步得到原子词串集合2-term/1_AWS；
* 				2）对初步获取的术语集合进行词频统计，2-term/2_count；过滤词频，得到候选词语集合2_term/3_CW；
* 				3）将候选词语集合经过信息熵过滤，得到候选术语集合2_term/5_term
* @author 廖劲为
* @date 2017年6月13日 下午8:29:20
* 
*/
public class TermAction {
	
	FileUtil fileUtil = new FileUtil();
	
	public static void main(String[] args) throws IOException {
		System.out.println("--start--");
		TermAction termAction = new TermAction();
//		termAction.loadAtomStr();
//		termAction.mergeAtomStr();
//		termAction.countAtomStr();
//		termAction.filterAtomStr();
		termAction.filterCW();
		System.out.println("--over--");
	}
	
	// 再次根据停用词过滤
	public void filterCW() throws IOException {
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature_v2.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword_v2.txt");
		//3、加载所有领域名称
		TermUtil termUtil = new TermUtil();
		List<String> domains = termUtil.getDomains();
		TermService termService = new TermServiceImpl();
		//4、分领域进行信息熵过滤
		int limitFrequency = 5;
		double limitEntropy = 0.1;
		for (String domain : domains) {
			termService.filterCW(domain, limitFrequency, limitEntropy, stopNatures, stopWords);
		}
	}

	/**
	* @Title: countAtomStr
	* @Description: 对提取的原子词串通过原子词步长法筛选，并进行词频统计
	*/
	public void countAtomStr() throws IOException {
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature_v2.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword_v2.txt");
		TermService termService = new TermServiceImpl();
		//3、加载所有领域名称
		TermUtil termUtil = new TermUtil();
		List<String> domains = termUtil.getDomains();
		//4、分领域进行原子词过滤
		for (String domain : domains) {
			// 加载该领域的原始文档
			List<String> listCorpus = new ArrayList<>();
			listCorpus.addAll(fileUtil.readTxt(ConstantValue.PREDEAL_PATH+"answer/"+domain+".txt", "UTF-8"));
			listCorpus.addAll(fileUtil.readTxt(ConstantValue.PREDEAL_PATH+"train/"+domain+".txt", "UTF-8"));
			// 加载已统计过的文档信息
			String countedpath = ConstantValue.DATA_ROOT_PATH + "2_term_V1_aws-no-nature/2_count/";	//词频统计算法未修改，以此为准
			List<String> listCounted = new ArrayList<>();
			listCounted = fileUtil.readTxt(countedpath, "UTF-8");
			// 加载该领域的原子词串
			Map<String, String> mapAws = fileUtil.readInfo(ConstantValue.TERM_AWS_PATH+domain+".txt");
			System.out.println(domain + " , " + listCorpus.size() + " , " + mapAws.size());
			termService.countTerms(mapAws, listCorpus, listCounted, stopNatures, stopWords, ConstantValue.TERM_COUNT_PATH+domain+".txt");
		}
	}
	
	// 筛选得到候选术语
	public void filterAtomStr() throws IOException{
		TermService termService = new TermServiceImpl();
		//过滤并计算词频
		int limit = 5;
		termService.filterAws(limit);
	}

	/**
	* @Title: mergeAtomStr
	* @Description: 合并原子词串
	*/
	public void mergeAtomStr() throws IOException {
		//1、获取指定的领域名称
		TermUtil termUtil = new TermUtil();
		List<String> domains = termUtil.getDomains();
		Map<String, Integer> map = new HashMap<>();
		//读取对应领域的原子词串
		for (String domain : domains) {
			List<String> list = fileUtil.readTxt(ConstantValue.TERM_AWS_PATH+domain+".txt", "UTF-8");
			// 将list文件合并到map中
			for (String aws : list) {
				String[] aw = aws.split("\t");
				if(aw.length==2) {
					Integer count = map.get(aw[0]);
					int c = Integer.parseInt(aw[1]);
					map.put(aw[0], count==null ? c : count + c);
				}
			}
		}
		//将map集合写入TXT文件
		fileUtil.writeTxt(map, ConstantValue.TERM_PATH+"aws_total.txt", true);
	}
	
	/**
	* @Title: loadAtomStr
	* @Description: 根据停用词及停用词性筛选得到原子词串
	*/
	public void loadAtomStr() throws IOException {
		int limit = 0;
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		//3、获取指定的领域名称
		TermUtil termUtil = new TermUtil();
		List<String> domains = termUtil.getDomains();
		
		//3、根据domains合并answer和train，并进行原子词删除
		TermService termService = new TermServiceImpl();
		for (String domain : domains) {
			System.out.println(domain);
			//3.1、合并answer和train中的分词文件，便于整体的原子词删除
			List<File> listFiles = new ArrayList<>();
			listFiles.addAll(fileUtil.getAllFiles(ConstantValue.SEGMENT_PATH + "answer/" + domain));
			listFiles.addAll(fileUtil.getAllFiles(ConstantValue.SEGMENT_PATH + "train/" + domain));
			//3.1 调用指定接口进行原子词删除
			termService.delUnAtom(listFiles, ConstantValue.TERM_AWS_PATH+domain+".txt", stopNatures, stopWords, limit);
		}
	}
	
	/**
	* @Title: filterAtomStr-旧方法，效率太低
	* @Description: 对提取的原子词串通过原子词步长法筛选，得到候选词语集合
	*/
	public void filterAtomStrold() throws IOException {
		//1、加载所有领域名称
//		TermUtil termUtil = new TermUtil();
//		List<String> domains = termUtil.getDomains();
//		
//		//2、根据领域读取原子词串文件
//		Map<String, Map<String, String>> mapDomainAws = new HashMap<>();	//key-domain,value-(原子词串,词频)
//		for (String domain : domains) {
//			// 原子词串集合,k-原子词串，v-词频
//			Map<String, String> mapAws = fileUtil.readInfo(ConstantValue.TERM_AWS_PATH+domain+".txt");
//			mapDomainAws.put(domain, mapAws);
//		}
//		TermService termService = new TermServiceImpl();
		//计算词频
//		termService.countAws(mapDomainAws, domains, ConstantValue.TERM_COUNT_PATH);
	}
}