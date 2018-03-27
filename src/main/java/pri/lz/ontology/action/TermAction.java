package pri.lz.ontology.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pri.lz.ontology.service.TermService;
import pri.lz.ontology.util.StaticValue;

public class TermAction {
	
	TermService termService = new TermService();

	public static void main(String[] args) throws IOException {
		TermAction termAction = new TermAction();
		termAction.test();
	}
	
	/**
	* @Title: test
	* @Description: 测试指定文本分词无用原子词删除
	*/
	private void test() throws IOException {
//		String txt = StaticValue.test_txt;
		String txt = "@①原字艹下加秋@②原字木加巳@③原字叹繁体@④原字穴下加洼";
		String segStr = termService.segmentSingle(txt);
		System.out.println(segStr);
		List<String> natureList = termService.delWordByNature(segStr);
//		printList(natureList);
		List<String> wordList = termService.delWordByStopWords(natureList);
//		printList(wordList);
		//原子词步长法，<原子词串, 频率>
		Map<String, Integer> atomMap = termService.atomWord(wordList, txt);
//		printMap(atomMap);
//		System.out.println("----entropy---");
		//信息熵计算
		Map<String, double[]> entropy = termService.entropy(atomMap, txt);
//		printMap2(entropy);
	}
	
	
	@SuppressWarnings("rawtypes")
	public void printList(List list) {
		for (Object object : list) {
			System.out.println(object.toString());
		}
	}
	
	public void printMap(Map<String, Integer> map) {
		for(Entry<String, Integer> word: map.entrySet()) {
			System.out.println(word.getKey() + "\t" + word.getValue());
		}
	}
	
	public void printMap2(Map<String, double[]> map) {
		for(Entry<String, double[]> word: map.entrySet()) {
			System.out.println(word.getKey() + "\t" + word.getValue()[0] + "\t" + word.getValue()[1]);
		}
	}

}
