package pri.lz.ontology.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pri.lz.ontology.util.FileUtil;
import pri.lz.ontology.util.SegmentUtil;
import pri.lz.ontology.util.StaticValue;

public class TermService {
	FileUtil fileUtil = new FileUtil();
	
	/**
	* @Title: segmentSingle
	* @Description: 对单文件或单文本进行分词处理
	* @param @throws IOException
	*/
	public String segmentSingle(String txt) throws IOException {
		SegmentUtil segmentUtil = new SegmentUtil();
		return segmentUtil.segTxtSingle(txt);
	}
	
	/**
	* @Title: delWordByNature
	* @Description: 根据停用词性删除原子词
	* @param @param segTxt
	*/
	public List<String> delWordByNature(String segTxt) {
		Set<String> setNatures = fileUtil.readDicUTF8(StaticValue.STOPNATURE_PATH);
		String[] segs = segTxt.split(" ");
		List<String> list = new ArrayList<>();
		String natures = "";
		for (String seg : segs) {
			String[] words = seg.split("/");
			if(setNatures.contains(words[1].substring(0, 1))) {
				if(!natures.equals("")) {
					list.add(natures.trim());
					natures = "";
				}
			} else {
				natures += words[0] + " ";				
			}
		}
		return list;
	}
	
	public List<String> delWordByStopWords(List<String> natureList) {
		Set<String> stopWords = fileUtil.readDicUTF8(StaticValue.STOPWORD_PATH);
		List<String> list = new ArrayList<>();
		Map<String, Integer> map = new HashMap<>();
		String temp = "";
		for (String natureStr : natureList) {
			String[] words = natureStr.split(" ");
			for (String word : words) {
				if(stopWords.contains(word.trim())) {
					if(!temp.equals("")) {
						if(map.get(temp)==null) {
							map.put(temp, 1);
							list.add(temp);
						} else {
							map.put(temp, map.get(temp)+1);						
						}
						temp = "";					
					}
				} else if(!word.trim().equals("")){
					temp += word.trim() + " ";
				}
			}
			if(!temp.equals("")) {
				if(map.get(temp)==null) {
					map.put(temp, 1);
					list.add(temp);
				} else {
					map.put(temp, map.get(temp)+1);						
				}
				temp = "";
			}
		}
		return list;
	}

	/**
	* @Title: atomWord
	* @Description: 通过原子词步长法提取原子词串
	* @param @param wordList
	* @param @param txt
	*/
	public Map<String, Integer> atomWord(List<String> wordList, String txt) {
		Map<String, Integer> map = new HashMap<>();
		String temp = "";
		for (String aws : wordList) {
			// 首先验证整个词串是否符合要求
			temp = aws.replaceAll("\\s+", "");
			if(map.get(temp)==null) {
				int count = countWord(temp, txt);
				if(count > 0) {
					map.put(temp, count);
				}
			}
			// 再验证子串
			int start = 0;
			while((start=txt.indexOf(" ", start))>=0){
	            temp = aws.substring(start+1).replaceAll("\\s+", "");
	            if(map.get(temp)==null) {
					int count = countWord(temp, txt);
					if(count > 0) {
						map.put(temp, count);
					}
				}
	        }
			
		}
		return map;
	}
	
	private int countWord(String word, String txt) {
		int count = 0;
		int start =0;
        while((start=txt.indexOf(word, start))>=0){
            start += word.length();
            count++;
        }
		return count;
	}

	/**
	* @Title: entropy
	* @Description: 计算候选词语左右信息熵
	* @param @param atomMap
	* @param @param txt
	*/
	public Map<String, int[]> entropy(Map<String, Integer> atomMap, String txt) {
		Map<String, int[]> map = new HashMap<>();
		for(Entry<String, Integer> atom : atomMap.entrySet()) {
			// 首先找出所有的左右邻接字
			
		}
		return map;
	}
}
