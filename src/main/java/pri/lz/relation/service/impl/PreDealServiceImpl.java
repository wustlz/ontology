package pri.lz.relation.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;

import pri.lz.relation.service.PreDealService;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

/**
* @ClassName: PreDealServiceImpl
* @Description: 语料预处理接口实现类
* @author 廖劲为
* @date 2016年10月11日 上午9:49:32
* 
*/
public class PreDealServiceImpl implements PreDealService {
	
	//初始化fileutil
	FileUtil fileUtil = new FileUtil();

	//将所有全角字符转换为半角字符
	@Override
	public String fullWidth2halfWidth(String fullWidthStr) {
		if (null == fullWidthStr || fullWidthStr.length() <= 0) {
    		return "";
    	}
    	char[] charArray = fullWidthStr.toCharArray();
    	//对全角字符转换的char数组遍历
    	for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间减掉偏移量65248;如果是空格的话,直接做转换
            if (charIntValue >= 65281 && charIntValue <= 65374) {
            	charArray[i] = (char) (charIntValue - 65248);
            } else if (charIntValue == 12288) {
            	charArray[i] = (char) 32;
            }
    	}
    	return new String(charArray);
	}

	//文件保存格式为UTF8
	@Override
	public void writeTxtUTF8(String text, String filePath, String fileName, boolean continueWrite) throws IOException {
		//将所有全角字符转换为半角字符
		text = fullWidth2halfWidth(text);
		File file=new File(filePath);
		//文件目录不存在
		if(!file.exists()){
			file.mkdirs();
		}
		//将内容写入到指定文件
		fileUtil.writeTxt(text, filePath+fileName, continueWrite);
	}

	//将file集合中的txt文件，转换成句子存储
	@Override
	public void txt2sentence(List<File> listFiles, String writepath) throws IOException {
		//句子结束正则表达
		String endStr = "\\.|。|!|！|\\?|？";
		//创建一个read对象
		InputStreamReader read = null;//考虑到编码格式
		BufferedReader bufferedReader = null;
		
		//创建输出流对象
		FileOutputStream out = null;
		StringBuffer sb = null;
		// 依次读取file集合中的数据
		String lineTxt = null;
		for (File file : listFiles) {
			String txt = "";	//写入文件的txt变量
			read = new InputStreamReader(new FileInputStream(file),"UTF-8");
			bufferedReader = new BufferedReader(read);
			//按行循环读取
			while((lineTxt = bufferedReader.readLine()) != null){
				//按照句子结束符号切割
				String[] sentences = lineTxt.split(endStr);
				for (String sentence : sentences) {
					if(sentence.length()>4){
						txt += sentence + "\n";
					}
				}
			}
			// 判断文件夹是否创建
			File tempDir = new File(writepath);
			if(!tempDir.exists()){
				tempDir.mkdirs();
			}
			//写入到指定文件中
			if(!txt.equals("")){
				File wFile = new File(writepath+"\\"+file.getName());
				//文件不存在
		        if(!wFile.exists()){
		        	wFile.createNewFile();
		        } else {	//清空文件内容
					FileWriter fw = new FileWriter(wFile);
					fw.write("");
					fw.close();
		        }
				out = new FileOutputStream(wFile,true);
				sb = new StringBuffer();
				sb.append(txt);
		    	out.write(sb.toString().getBytes("utf-8"));
			}
		}
		//关闭输入输出
		if(read!=null)
			read.close();
		if(out!=null)
			out.close();
		
	}
	
	//通过ICTCLAS对预处理后的文件进行分词，每个txt作为一个单位进行处理，并将分词结果存储到结果文件夹中
	@Override
	public void seg2TXTByICTCLAS(String fileDir, String resultDir, String NLPIR) throws IOException {
		
		//1、初始化ICTCLAS分词
		int charset_type = 1;
		int init_flag = CLibrary.Instance.NLPIR_Init(NLPIR, charset_type, "0");
		String nativeBytes = null;
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return;
		}
		
		//2、首先获取待处理文件集合
		FileUtil fileUtil = new FileUtil();
		List<File> listFiles = fileUtil.getAllFiles(fileDir);
		
		//3、遍历所有文件，进行相应操作
		for (File file : listFiles) {
			//3.1、读取对应的TXT文件内容
			String txt = fileUtil.readTxt(file, "UTF-8");
			//3.2、分词, nativeBytes为分词结果，形如: 据悉/v ，/wd 质检/vn 总局/n 已/d 将/d 最新/a 有关/vn
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(txt, 1);
			//3.3、将分词结果写入指定文件
			writeSeg2TXT(nativeBytes, resultDir, file.getName());
		}
		
		//4、关闭分词器
		CLibrary.Instance.NLPIR_Exit();
		
	}
	
	/**
	* @Title: writeSeg2TXT
	* @Description: 将分词结果写入指定文件
	* @param nativeBytes——分词结果，形如：据悉/v ，/wd 质检/vn 总局/n 已/d 将/d 最新/a 有关/vn
	* @param resultDir——存储结果文件夹路径
	* @param fileName——存储结果文件名
	* @throws IOException 
	*/
	private void writeSeg2TXT(String nativeBytes, String resultDir, String fileName) throws IOException  {
		//1、将所有的\n替换为空
		nativeBytes = nativeBytes.replaceAll("\n", "");
		String txt = "";
		//2、将分词结果按空格拆分
		String[] terms = nativeBytes.split(" ");
		//3、遍历
		for (String term : terms) {
			//3.1、获取词性与词的间隔位置索引
			int index = term.lastIndexOf('/');
			//3.2、将词与词性以tab符间隔开，并每个词换行
			if(index>0)
				txt += term.substring(0, index) + "\t" + term.substring(index+1) + "\n";
		}
		//4、写入文件
		File file = new File(resultDir);
		if(!file.exists()){
			file.mkdirs();
		}
		fileUtil.writeTxt(txt, resultDir+"//"+fileName, false);
	}

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(ConstantValue.NLPIR+"NLPIR", CLibrary.class);
		
		public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
				
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

//			public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
//			public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
//			public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
//			public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}

}
