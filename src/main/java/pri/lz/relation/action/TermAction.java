package pri.lz.relation.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
	
	public static void main(String[] args) {
		TermAction termAction = new TermAction();
		termAction.loadAtomStr();
	}
	
	private void loadAtomStr() {
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		//3、按照分好词的文件读入，并进行原子词分词
		Map<String, Integer> map_aws = new HashMap();
		List<String> list_aws = new ArrayList<>();
		
		//读取所有文件
		String domainName = "C19-Computer";
		List<File> listFiles = fileUtil.getAllFiles(ConstantValue.SEGMENT_PATH + "train/" + domainName);
		System.out.println(listFiles.size());
	}
}