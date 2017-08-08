package pri.lz.relation.action;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pri.lz.relation.service.PreDealService;
import pri.lz.relation.service.impl.PreDealServiceImpl;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

/**
* @ClassName: PreDealAction
* @Description: 系统第1步，完成对语料库的预处理，最终得到原子词集合，主要过程如下：
* 				1）原始语料pre_corpus的文件编码转换，全角-半角转换，得到预处理后的语料predeal/corpus；
* 				2）通过中科院分词工具NLPIR对corpus分词处理，得到原子词集合predeal/segment；
* 				3）对corpus进行切分成句处理，得到句子集合predeal/sentence；
* @author 廖劲为
* @date 2017年6月13日 下午7:55:35
* 
*/
public class PreDealAction {
	
	//初始化
	FileUtil fileUtil = new FileUtil();
	PreDealService preDealService = new PreDealServiceImpl();

	public static void main(String[] args) throws IOException {
		System.out.println("-------start-------");
		
		PreDealAction main = new PreDealAction();
		long startTime = System.currentTimeMillis();
		
		//1、预处理语料数据
//		main.preDealCorpus(ConstantValue.CORPUS_PATH, ConstantValue.PREDEAL_PATH);
		
		//2、对预处理的语料通过ICTCLAS进行分词，得到原子词集合
		main.segment(ConstantValue.PREDEAL_PATH, ConstantValue.SEGMENT_PATH, ConstantValue.NLPIR);
		
		//3、将预处理后的语料进行分句
		main.getSentence(ConstantValue.PREDEAL_PATH);
		
		long endTime = System.currentTimeMillis();
		System.out.println("over, Time: " + (endTime-startTime) + "ms");

	}
	
	/**
	* @Title: segment
	* @Description: 对预处理后的文件，每个txt作为一个单位进行分词处理，并将分词结果存储到txt文件
	* @param fileDir——待分词的文本文件夹路径
	* @param resultDir——分词后的存储文件夹路径
	* @throws IOException
	*/
	public void segment(String corpusDir, String resultDir, String NLPIR) throws IOException{
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			//1、根据语料库路径读取所有领域语料文件夹
			List<File> listFileDirs = fileUtil.getAllFileDirs(corpusDir+dir+"/");
			//2、遍历所有文件夹，进行分词
			for (File fileDir : listFileDirs) {
				preDealService.seg2TXTByICTCLAS(fileDir.getPath(), resultDir+dir+"/"+fileDir.getName(), NLPIR);
			}
		}
	}
	
	/**
	* @Title: getSentence
	* @Description: 将预处理后的语料库文本，切分成句
	* @throws IOException
	*/
	public void getSentence(String corpusDir) throws IOException{
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			//1、根据语料库路径读取所有领域语料文件
			List<File> listFileDirs = fileUtil.getAllFileDirs(corpusDir+dir+"/");
			//2、遍历所有文件夹，进行预处理
			for (File fileDir : listFileDirs) {
				List<File> listFiles = fileUtil.getAllFiles(fileDir.getPath());
				//2、切分成句并写入到文件
				preDealService.txt2sentence(listFiles, ConstantValue.SENTENCE_PATH + dir + "/" + fileDir.getName());
			}
		}
	}
	
	/**
	* @Title: preDealCorpus
	* @Description: 对语料库进行预处理，按照预处理规则进行预处理转换，全角改半角，编码改UTF-8，并重新写入txt文件
	* @param corpusDir——语料库路径
	* @param fileDir——处理后的文件路径
	* @throws IOException
	*/
	public void preDealCorpus(String corpusDir, String predealDir) throws IOException{
		String[] dirs = {"train","answer"};
		for (String dir : dirs) {
			//1、根据语料库路径读取所有领域语料文件
			List<File> listFileDirs = fileUtil.getAllFileDirs(corpusDir+dir+"/");
			//2、遍历所有文件夹，进行预处理
			for (File fileDir : listFileDirs) {
				List<File> listFiles = fileUtil.getAllFiles(fileDir.getPath());
				for (File file : listFiles) {
					//2.1、以GBK编码读取当前文件内容
					String txt = fileUtil.readTxt(file,"GBK");
					//2.2、按照预处理规则进行预处理转换，全角改半角，编码改UTF-8，并重新写入txt文件
					preDealService.writeTxtUTF8(txt, predealDir+dir+"/"+fileDir.getName()+"/", file.getName(), false);
				}
			}
		}
	}

}
