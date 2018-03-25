package pri.lz.relation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pri.lz.relation.service.TermService;
import pri.lz.relation.service.impl.TermServiceImpl;

public class TermUtilTest {

	TermUtil termUtil = new TermUtil();
	FileUtil fileUtil = new FileUtil();
	
	@Test
	public void testCountTerm() throws IOException {
		TermService termService = new TermServiceImpl();
		//1、加载所有领域名称
		List<String> domains = termUtil.getDomains();
		//2、分领域进行原子词过滤
		for (String domain : domains) {
			// 加载该领域的原始文档
			List<String> listCorpus = new ArrayList<>();
			listCorpus.addAll(fileUtil.readTxt(ConstantValue.PREDEAL_PATH+"answer/"+domain+".txt", "UTF-8"));
			listCorpus.addAll(fileUtil.readTxt(ConstantValue.PREDEAL_PATH+"train/"+domain+".txt", "UTF-8"));
			// 加载该领域的原子词串
//			Map<String, String> mapAws = fileUtil.readInfo(ConstantValue.TERM_AWS_PATH+domain+".txt");
			Map<String, String> mapAws = new HashMap<>();
			mapAws.put("无产阶级_革命_运动_", "8");
			System.out.println(domain + " , " + listCorpus.size() + " , " + mapAws.size());
			termService.countTerms(mapAws, listCorpus, ConstantValue.TERM_COUNT_PATH+domain+".txt");
			break;
		}
	}
	
	@Test
	public void testCountDoc() {
//		System.out.println(termUtil.countDoc(ConstantValue.PREDEAL_PATH));
		String s = "asdasd";
		System.out.println(ChineseCharUtil.isChinese(s));
	}
	
	@Test
	public void testMath(){
		System.out.println((double)3/5/2);
		System.out.println(Math.log(2));
		System.out.println(Math.log(5));
		System.out.println(Math.log(10/9));
		System.out.println(Math.pow(Math.E, 0.69));
		System.out.println("人_".length());
		
	}
	
	@Test
	public void testAws() {
		String aws = "毛泽东_内心_世界_根深蒂固_";
		String[] aw = aws.split("_");	//将原子词串拆分成原子词，以便构建起子串
		String temp = "";
		for(int i=0; i<aw.length; i++) {
			for (int j = aw.length-1; j >= i; j--) {
				temp = aw[i];
				for (int k = i+1; k <= j; k++) {
					temp += aw[k];
				}
				System.out.println(temp);
			}
		}
	}

}
