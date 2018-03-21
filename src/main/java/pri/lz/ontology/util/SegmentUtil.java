package pri.lz.ontology.util;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class SegmentUtil {
	
	public String segTxtSingle(String txt) {
		
		//1、初始化ICTCLAS分词
		int charset_type = 1;
		int init_flag = CLibrary.Instance.NLPIR_Init(StaticValue.NLPIR, charset_type, "0");
		String nativeBytes = null;
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return null;
		}
		
		nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(txt, 1);
		
		return nativeBytes;
	}

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(StaticValue.NLPIR+"/NLPIR", CLibrary.class);
		
		public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
				
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

//				public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
//				public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
//				public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
//				public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
		// 导入用户自定义词典：自定义词典路径，bOverwrite=true表示替代当前的自定义词典，false表示添加到当前自定义词典后    
        public int NLPIR_ImportUserDict(String sFilename, boolean bOverwrite);
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}
}
