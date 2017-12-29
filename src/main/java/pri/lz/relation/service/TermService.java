package pri.lz.relation.service;

import java.io.IOException;
import java.util.HashSet;
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
}
