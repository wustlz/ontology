package pri.lz.relation.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
* @ClassName: PreDealService
* @Description: 语料预处理 接口，实现对语料的编码转换、分词
* @author 廖劲为
* @date 2016年10月11日 上午9:47:51
* 
*/
public interface PreDealService {

	/**
	* @Title: fullWidth2halfWidth
	* @Description: 将所有全角字符转换为半角字符
	* @param @param text
	* @return String
	*/
	public String fullWidth2halfWidth(String fullWidthStr);
	
	/**
	* @Title: writeTxtUTF8
	* @Description: 文件保存格式为UTF8
	* @param @param text
	* @param @param filePath-文件所在路径，结尾处一定有\\
	* @param @param fileName-文件名，自定义后缀名，但实际上仍是txt文件
	* @param @param continueWrite
	* @return void
	*/
	public void writeTxtUTF8(String text, String filePath, String fileName, boolean continueWrite) throws IOException;
	
	/**
	* @Title: txt2sentence
	* @Description: 将file集合中的txt文件，转换成句子存储
	* @param listFiles-预处理后的语料库txt
	* @param writepath-分割成语句后的存储路径
	* @throws IOException
	*/
	public void txt2sentence(List<File> listFiles, String writepath) throws IOException;
	
	/**
	* @Title: seg2TXT
	* @Description: 通过ICTCLAS对预处理后的文件进行分词，每个txt作为一个单位进行处理，并将分词结果存储到结果文件夹中
	* @param fileDir——待分词的文本文件夹路径
	* @param resultDir——分词后的存储文件夹路径
	* @param NLPIR——ICTCLAS分词的相应的配置文件路径
	* @throws IOException
	*/
	public void seg2TXTByICTCLAS(String fileDir, String resultDir, String NLPIR) throws IOException;
}