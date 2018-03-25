package pri.lz.relation.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
* @ClassName: TermService
* @Description: 术语操作 接口类
* @author 廖劲为
* @date 2016年11月19日 上午10:40:22
* 
*/
public interface TermService {
	
	
	/**
	* @Title: delUnAtom
	* @Description: 根据停用词性，停用词删除无用原子词，是delUnuseWord的替换版本
	* @param @param segFiles-原子词txt文件
	* @param @param resultpath-处理后的文件存放路径
	* @param @param stopNatures-停用词性集合
	* @param @param stopWords-停用词集合
	* @param @throws IOException
	* @return void
	*/
	public void delUnAtom(List<File> segFiles, String resultpath, HashSet<String> stopNatures, HashSet<String> stopWords, int limit) throws IOException;

	/**
	* @Title: delUnuseWord
	* @Description: 根据停用词性，停用词删除无用原子词
	* @param segmentpath-原子词txt文件夹路径
	* @param resultpath-处理后的文件夹路径
	* @param stopNatures-停用词性txt文件
	* @param stopWords-停用词txt文件
	* @throws IOException
	*/
	public void delUnuseWord(String segmentpath, String resultpath, HashSet<String> stopNatures, HashSet<String> stopWords) throws IOException;

	/**
	* @Title: atomTerm
	* @Description: 对每个领域根据原子词步长法获取术语集合
	* @param wordpath-有效原子词文件夹路径
	* @param fileDir——预处理后的语料库路径
	* @param minfrequency——阈值
	* @return Map<String,Integer>——string对应术语，integer对应出现频率
	* @throws IOException
	*/
	public Map<String, Integer> atomTerm(String wordpath, String fileDir, int minfrequency) throws IOException;
	
	/**
	* @Title: dealTerm
	* @Description: 处理初步得到的术语集合
	* @throws IOException
	*/
	public void dealTerm(String termpath, String resultpath, HashSet<String> stopwords) throws IOException;
	
	/**
	* @Title: checkTermOnline
	* @Description: 通过“术语在线”在线监测是否为术语
	* @param termpath
	* @throws IOException
	*/
	public void checkTermOnline(String basepath, String termpath) throws IOException;

	/**
	* @Title: storeTermByxks
	* @Description: 将术语按照xks进行归类存储
	* @param @param tERM_3RD_PATH
	* @param @param online_term_info
	* @return void
	*/
	public void storeTermByxks(String dirpath, Map<String, String> online_term_info);

	/**
	* @Title: countAws
	* @Description: 按领域原子词串统计原子词串及其子串在各个语料库中的出现频率，并存入文件
	* @param @param mapDomainAws：key-domain,value-(原子词串,词频)
	* @param @param domains
	* @param @param savepath
	*/
	public void countAws(Map<String, Map<String, String>> mapDomainAws, List<String> domains,  String savepath);

	/**
	* @Title: countTerms
	* @Description: 对原子词串及其子串进行各领域文档词频、左右信息熵统计
	* @param @param mapAws
	* @param @param listCorpus
	* @param @param string
	* @return void
	*/
	public void countTerms(Map<String, String> mapAws, List<String> listCorpus, String savepath);

	public void filterAws(int limit) throws IOException;
}
