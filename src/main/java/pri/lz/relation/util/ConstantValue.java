package pri.lz.relation.util;

import java.util.HashMap;
import java.util.Map;

/**
* @ClassName: ConstantValue
* @Description: 自定义的常量值类
* @author 廖劲为
* @date 2017年3月3日 上午10:52:16
* 
*/
public class ConstantValue {

	public static String DATA_ROOT_PATH = "D:\\ontology\\data\\";	// 数据处理的根文件目录
	public static String CORPUS_PATH = DATA_ROOT_PATH + "corpus\\";	// 原始语料库路径
	public static String PREDEAL_PATH = DATA_ROOT_PATH + "predeal\\corpus\\";	// 预处理后的语料库路径
	public static String SEGMENT_PATH = DATA_ROOT_PATH + "predeal\\segment\\";	// 预处理后的分词结果路径
	public static String SENTENCE_PATH = DATA_ROOT_PATH + "predeal\\sentence\\";	// 预处理后的分词结果路径
	public static String WORD_PATH = DATA_ROOT_PATH + "predeal\\word\\";	// 对分词结果进行停用词(性)过滤后的原子词存放路径
	public static String TERM_1ST_PATH = DATA_ROOT_PATH + "term\\1st\\";	// 对分词结果进行原子词步长法初步提取的术语存放路径
	public static String TERM_2ND_PATH = DATA_ROOT_PATH + "term\\2nd\\";	// 对原子词步长法初步提取的术语进行筛选后的术语存放路径
	
	public static String ONLINE_TERM_OK = DATA_ROOT_PATH + "term\\online\\online_term_ok.txt";	//在线比对术语结果，术语成立
	public static String ONLINE_TERM_NO = DATA_ROOT_PATH + "term\\online\\online_term_no.txt";	//在线比对术语结果，术语不成立
	public static String ONLINE_INFO_OK = DATA_ROOT_PATH + "term\\online\\online_info_ok.txt";	//在线比对术语结果，术语成立，包括查询结果
	public static String ONLINE_INFO_NO = DATA_ROOT_PATH + "term\\online\\online_info_no.txt";	//在线比对术语结果，术语不成立，包括查询结果
	
	public static String TERM_DIC_PATH = DATA_ROOT_PATH + "term\\term_dic.txt";	// 经过术语在线比对，作为特征词典
	public static String CONCEPT_PATH = DATA_ROOT_PATH + "concept\\";	//概念词集合文件夹
	public static String CONCEPT_VECTOR_PATH = CONCEPT_PATH + "vector\\";	//概念向量集合文件夹
	public static String CONCEPT_EXIST_FILE = CONCEPT_PATH + "concept_exist.txt";	// 已经计算过特征向量的概念
	
	public static int RELATION_SIZE = 10;	//关系种类数
	
	public static String NLPIR = "D:\\NLPIR";
	public static String UTIL_PATH = DATA_ROOT_PATH + "util\\";
	
	public static String RELATION_PATH = DATA_ROOT_PATH + "relation\\";	//relation，包括训练数据集
	public static String MATRIX_PATH = RELATION_PATH + "matrix\\";	//矩阵存放
	public static String MODEL_PATH = RELATION_PATH + "result\\";	//矩阵存放
	public static String MODEL_IPTHIDWEIGHTS = RELATION_PATH + "result\\iptHidWeights.txt";	//模型1-输入隐藏层
	public static String MODEL_HIDOPTWEIGHTS = RELATION_PATH + "result\\hidOptWeights.txt";	//模型2-隐藏输出层
	
	public static String LOG_PATH = "D:\\ontology\\data\\log.txt";	//日志文件存放
	
	public static Map<String, String> DOMAINONLINE = new HashMap<>();
	
	public ConstantValue() {
		
		DOMAINONLINE.put("C3-Art", "C3-Art");
		DOMAINONLINE.put("C4-Literature", "C4-Literature");
		DOMAINONLINE.put("C5-Education", "C5-Education");
		DOMAINONLINE.put("C6-Philosophy", "C6-Philosophy");
		DOMAINONLINE.put("C7-History", "C7-History");
		DOMAINONLINE.put("C11-Space", "C11-Space");
		DOMAINONLINE.put("C15-Energy", "C15-Energy");
		DOMAINONLINE.put("C16-Electronics", "C16-Electronics");
		DOMAINONLINE.put("C17-Communication", "C17-Communication");
		DOMAINONLINE.put("C19-Computer", "C19-Computer");
		DOMAINONLINE.put("C23-Mine", "C23-Mine");
		DOMAINONLINE.put("C29-Transport", "C29-Transport");
		DOMAINONLINE.put("C31-Enviornment", "C31-Enviornment");
		DOMAINONLINE.put("C32-Agriculture", "C32-Agriculture");
		DOMAINONLINE.put("C34-Economy", "C34-Economy");
		DOMAINONLINE.put("C35-Law", "C35-Law");
		DOMAINONLINE.put("C36-Medical", "C36-Medical");
		DOMAINONLINE.put("C37-Military", "C37-Military");
		DOMAINONLINE.put("C38-Politics", "C38-Politics");
		DOMAINONLINE.put("C39-Sports", "C39-Sports");
		
	}
	
	// 根据关系名称返回对应的值
	public static Integer checkRelationType(String relationType){
		switch (relationType) {
		case "is-a":
			return 0;
		case "component-of":
			return 1;
		case "member-of":
			return 2;
		case "substance-of":
			return 3;
		case "cause-to":
			return 4;
		case "similar":
			return 5;
		case "opposite":
			return 6;
		case "attribute":
			return 7;
		case "TimeOrSpace":
			return 8;
		case "arithmetic":
			return 9;
		default:
			return 10;
		}
	}
	
	public static String relationType(int idx){
		switch (idx) {
		case 0:
			return "is-a";
		case 1:
			return "component-of";
		case 2:
			return "member-of";
		case 3:
			return "substance-of";
		case 4:
			return "cause-to";
		case 5:
			return "similar";
		case 6:
			return "opposite";
		case 7:
			return "attribute";
		case 8:
			return "TimeOrSpace";
		case 9:
			return "arithmetic";
		default:
			return "other";
		}
	}	
}