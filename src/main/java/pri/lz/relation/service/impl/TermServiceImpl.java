package pri.lz.relation.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.util.ParserException;

import com.alibaba.fastjson.JSONObject;

import pri.lz.relation.service.TermService;
import pri.lz.relation.util.ChineseCharUtil;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;
import pri.lz.relation.util.TermUtil;

/**
* @ClassName: TermServiceImpl
* @Description: 术语操作 接口实现类
* @author 廖劲为
* @date 2016年11月19日 上午10:41:04
* 
*/
public class TermServiceImpl implements TermService {
	
	FileUtil fileUtil = new FileUtil();
	
	Map<String,String> mapOnlineInfo_agg;
	Map<String,String> mapOnlineInfo_all;
	int count_onlineInfo_all = 1;
	int count_onlineInfo_agg = 1;
	
	public TermServiceImpl() {
		mapOnlineInfo_agg = new HashMap<>();
		mapOnlineInfo_all = new HashMap<>();
	}
	
	//根据停用词性，停用词删除无用原子词
	@Override
	public void delUnuseWord(String segmentpath, String resultpath, HashSet<String> stopNatures, HashSet<String> stopWords) throws IOException {
		//1、获取领域文件夹集合
		List<File> listFileDirs = fileUtil.getAllFileDirs(segmentpath);
		//2、遍历领域文件夹，对应子文件夹中的原子词中的删除无效原子词
		for (File fileDir : listFileDirs) {
			//3、获取文件夹下的所有文件
			List<File> listFiles = fileUtil.getAllFiles(fileDir.getPath());
			//4、遍历所有文件，删除对应的无效原子词
			for (File file : listFiles) {
				// 若没有当前领域的文件夹，则创建
				File temp = new File(resultpath+"/"+fileDir.getName());
				if(!temp.exists()){
					temp.mkdirs();
				}
				//4.1、按行读取内容，并返回相应集合
				List<String> listWords = fileUtil.readTxTLine(file, "UTF-8");
				String txt = "";	//保留的原子词重写txt
				//4.2、遍历当前文件中对应的分词结果
				for (int i = 0; i<listWords.size(); i++) {
					//4.2.1、按tab符分割
					String[] words = listWords.get(i).split("\t");
					//4.2.2、删除非中文字符
					if(!ChineseCharUtil.isChinese(words[0])){
						continue;
					}//4.2.3、根据停用词性删除
					else if(stopNatures.contains(words[1].substring(0, 1).trim())){	//含有停用词性，继续下一个循环
						continue;
					}//4.2.4、根据停用词删除
					else if(stopWords.contains(words[0])){	//含有停用词，继续下一个循环
						continue;
					} else {
						//4.2.5、将有效词及其索引加入到txt中
						txt += i + "\t" + listWords.get(i) + "\n";
					}
				}
				//4.3、将有效原子词写入文件
				fileUtil.writeTxt(txt, resultpath+"/"+fileDir.getName()+"/"+file.getName(), false);
			}
		}
	}

	//对每个领域根据原子词步长法获取术语集合
	@Override
	public Map<String, Integer> atomTerm(String wordpath, String fileDir, int minfrequency) throws IOException {
		//1、获取预处理后的语料库内容
		//1.1、首先获取相应的语料库文件集合
		List<File> listCorpusFiles = fileUtil.getAllFiles(fileDir);
		//1.2、遍历文件，加载到listCorpus集合
		List<String> listCorpus = new ArrayList<>();
		for (File file : listCorpusFiles) {
			//读取文件相应内容并加载到listCorpus
			listCorpus.add(fileUtil.readTxtAll(file, "UTF-8"));
		}
		
		//2、按照txt文件为单位获取术语集合
		Map<String, Integer> terms = new HashMap<>();
		List<File> listWordFiles = fileUtil.getAllFiles(wordpath);//获取有效原子词对应的文件集合
		for (File file : listWordFiles) {
			List<String> listTerms = new ArrayList<>();
			//2.1、按行读取有效原子词内容
			List<String> words = fileUtil.readTxTLine(file, "UTF-8");
			//2.2、根据每个word索引得到候选术语集合
			int preIndex = 0;	//当前索引
			String txt = "";//当前原子词串
			for (int i=0; i<words.size(); i++) {	//word形如0	软件	n		
				String[] word = words.get(i).split("\t");
				int curIndex = Integer.parseInt(word[0].trim());
				if(i==0){	//当前第一个，特殊处理
					preIndex = curIndex;
					txt += word[1].trim();
				} else if(curIndex-preIndex == 1){	//表示连续
					preIndex = curIndex;
					txt += "_"+word[1].trim();
				} else {
					if(txt.length()>1){	//不连续，且txt长度>1，将其加载到listTerms
						listTerms.add(txt);	//添加到listTerms
					}
					preIndex = curIndex;
					txt = word[1].trim();
				}
			}
			//2.3、按照原子词步长法对候选连续原子词串筛选
			terms = filterTerms(listTerms, listCorpus, minfrequency, terms);
		}
		
		return terms;
	}

	/**
	* @Title: filterTerms
	* @Description: 对单个文件得到的候选原子词串按原子词步长法进行筛选
	* @param listTerms——候选原子词串
	* @param listCorpus——预处理后的语料库内容
	* @param minfrequency——阈值
	* @param terms——已验证的术语集合
	* @return Map<String,Integer>
	*/
	private Map<String, Integer> filterTerms(List<String> listTerms, List<String> listCorpus, int minfrequency, Map<String, Integer> terms) {
		//遍历所有候选原子词串
		for (String candidateStr : listTerms) {
			//表示验证通过的词串
			List<String> listOkStrs = new ArrayList<>();
			//1、获取每个候选词串的所有原子词
			String[] words = candidateStr.split("_");
			//2、遍历，得到所有以该原子词为首的子串
			for (int i = 0; i < words.length; i++) {
				for (int j = words.length-1; j >= i; j--) {
					//2.1、依次获取子串
					String txt = words[i];
					for (int k = i+1; k <= j; k++) {
						txt += "_" + words[k];
					}
//					System.out.println(txt);
					//2.3、验证当前子串是否已经包含在已验证过的词串中
					if(!containChildStr(listOkStrs, txt)){	//不包含已验证词串中，控制为不验证
						//2.4、是否已经验证当前词串
						if(terms.get(txt)==null){	//表示未验证当前词串
							//2.5、对当前子串进行阈值验证
							int count = strCount(listCorpus, txt);
							if(count >= minfrequency){	//词串频率>阈值
								listOkStrs.add(txt);	//添加待选集合
								terms.put(txt, count);	//添加到已验证集合
							}
						}
					}
				}
			}
		}
		return terms;
	}

	/**
	* @Title: strCount
	* @Description: 统计词串在语料库中的出现次数
	* @param listCorpus
	* @param str
	* @return int
	*/
	private int strCount(List<String> listCorpus, String str) {
		int count = 0;
		//1、将str中的_去掉
		str = str.replaceAll("_", "");
		//2、遍历，统计词串频率
		for (String corpus : listCorpus) {
			int start =0;
	        while((start=corpus.indexOf(str,start))>=0){
	            start += str.length();
	            count++;
	        }
		}
		return count;
	}

	/**
	* @Title: containChildStr
	* @Description: 验证集合词串中是否含有子串
	* @param listOkStrs
	* @param childStr
	* @return boolean
	*/
	private boolean containChildStr(List<String> listOkStrs, String childStr) {
//		for (String okStr : listOkStrs) {
//			if(okStr.indexOf(childStr)>=0){	//表示含有子串
//				return true;
//			}
//		}
		for (String okStr : listOkStrs) {
			if(okStr.replaceAll("_", "").equals(childStr.replaceAll("_", ""))){	//表示含有相同子串
				return true;
			}
		}
		return false;
	}

	//处理初步得到的术语集合
	@Override
	public void dealTerm(String termpath, String resultpath, HashSet<String> stopwords) throws IOException {
		List<String> listTermNew = new ArrayList<>();
		HashSet<String> mapTerm = new HashSet<>();
		//1、获取初步得到的术语集合
		List<String> listTermsOld = fileUtil.readTxt(termpath, "UTF-8");
		System.out.println("listTermsOld: " + listTermsOld.size());
		//2、遍历，筛选不符合要求的术语集合
		for (String termOld : listTermsOld) {
			String[] terms = termOld.split("\t");
			//2.1、根据词长删除
			if(terms[0].length()<2){
				continue;
			}//2.2、去除重复的词
			else if(mapTerm.contains(terms[0].replaceAll("_", ""))){
				continue;
			}//2.3、去除停用词
			else if (stopwords.contains(terms[0].replaceAll("_", ""))) {
				continue;
			}
			listTermNew.add(termOld);
			mapTerm.add(terms[0].replaceAll("_", ""));
		}
		
		//3、写入txt文件
		String txt = "";
		for (String term : listTermNew) {
			txt += term + "\n";
		}
		fileUtil.writeTxt(txt, resultpath, false);
	}

	//通过“术语在线”在线监测是否为术语
	@Override
	public void checkTermOnline(String basepath, String termpath) throws IOException{
		//获取指定文件夹下的所有文件
		List<File> listFiles = fileUtil.getAllFiles(basepath);
		for (File file : listFiles) {
			System.out.println(file.getName());
			List<String> listTerms = new ArrayList<>();
			List<String> listUnKonw = new ArrayList<>();
			List<String> listUnword = new ArrayList<>();
			//1、获取含分隔符_的术语集合
			List<String> listDealTerms = fileUtil.readTxTLine(file, "UTF-8");
			//2、依次检验，将符合要求的提取出来
			int count_term = 1;
			int count_unknow = 1;
			int count_unword = 1;
			int pageSize = 1000;
			for (String dealTerm : listDealTerms) {
				String[] temp = dealTerm.split("\t");
				int rst = checkTerm(temp[0].replaceAll("_", ""));
				//保存在线查询的信息
				if(mapOnlineInfo_all.size() == pageSize){
//					String filepath = basepath+"/online_info/all_"+(count_onlineInfo_all++)+".txt";
					String filepath = basepath+"/online_info/all_"+file.getName();
					write(mapOnlineInfo_all, filepath);
					mapOnlineInfo_all.clear();
				}
				if(mapOnlineInfo_agg.size() == pageSize){
//					String filepath = basepath+"/online_info/agg_"+(count_onlineInfo_agg++)+".txt";
					String filepath = basepath+"/online_info/agg_"+file.getName();
					write(mapOnlineInfo_agg, filepath);
					mapOnlineInfo_agg.clear();
				}
				if(rst==1){
					if(listTerms.size()==pageSize){
						write(listTerms, basepath+"/online/"+file.getName());
						listTerms.clear();
						System.out.println("listTerm 第" + (count_term++) + "次");
					}
					listTerms.add(dealTerm);
				} else if(rst==3){
					if(listUnKonw.size()==pageSize){
						write(listUnKonw, basepath+"/unknow/"+file.getName());
						listUnKonw.clear();
						System.out.println("listUnKonw 第" + (count_unknow++) + "次");
					}
					listUnKonw.add(dealTerm);
				} else {
					if(listUnword.size()==pageSize){
						write(listUnword, basepath+"/unword/"+file.getName());
						listUnword.clear();
						System.out.println("listUnword 第" + (count_unword++) + "次");
					}
					listUnword.add(dealTerm);
				}
			}
			//最后不管有多少，均写入
			write(listTerms, basepath+"/online/"+file.getName());
			System.out.println("listTerm 第" + count_term + "次");
			write(listUnKonw, basepath+"/unknow/"+file.getName());
			System.out.println("listUnKonw 第" + count_unknow + "次");
			write(listUnword, basepath+"/unword/"+file.getName());
			System.out.println("listUnword 第" + count_unword + "次");
			// 写入mapOnlineInfo_all
			String filepath = basepath+"/online_info/all_"+file.getName();
			write(mapOnlineInfo_all, filepath);
			mapOnlineInfo_all.clear();
			// 写入mapOnlineInfo_agg
			filepath = basepath+"/online_info/agg_"+file.getName();
			write(mapOnlineInfo_agg, filepath);
			mapOnlineInfo_agg.clear();
		}
	}
	
	/**
	* @Title: write
	* @Description: 将list集合数据写入txt文件
	* @param list
	* @param filepath
	* @throws IOException
	*/
	private void write(Map<String,String> mapOnlineInfo, String filepath) throws IOException{
		String txt = "";
		for (Entry<String, String> info : mapOnlineInfo.entrySet()) {
			txt += info.getKey() + "\t" + info.getValue() + "\n";
		}
		fileUtil.writeTxt(txt, filepath, true);
	}
	
	/**
	 * @Title: write
	 * @Description: 将list集合数据写入txt文件
	 * @param list
	 * @param filepath
	 * @throws IOException
	 */
	private void write(List<String> list, String filepath) throws IOException{
		String txt = "";
		for (String term : list) {
			txt += term + "\n";
		}
		fileUtil.writeTxt(txt, filepath, true);
	}
	
	/**
	* @Title: checkTerm
	* @Description: 检测是否为术语
	* @param term-待检测术语
	* @throws IOException
	* @throws ParserException
	* @return int
	*/
	private int checkTerm(String term){
		String webSite = "http://www.termonline.cn/list.jhtm?op=query&k=${term}"
				+ "&start=0&pageSize=15&sort=&resultType=0&conds%5B0%5D.key=all"
				+ "&conds%5B0%5D.match=1&conds%5B1%5D.val=&conds%5B1%5D.key=category"
				+ "&conds%5B1%5D.match=1&conds%5B2%5D.val=&conds%5B2%5D.key=subject_code"
				+ "&conds%5B2%5D.match=3&conds%5B3%5D.val=&conds%5B3%5D.key=publish_year"
				+ "&conds%5B3%5D.match=1&conds%5B0%5D.val=${term}";
		
//		Parser parser = new Parser(webSite.replace("${term}", URLEncoder.encode(term, "UTF-8")));
//		parser.setEncoding("utf-8");//设置编码机
//		TextExtractingVisitor visitor = new TextExtractingVisitor();  
//        parser.visitAllNodesWith(visitor);  
//        String textInPage = visitor.getExtractedText();
		String textInPage;
		try {
			URL url = new URL(webSite.replace("${term}", term)); 
			InputStream is = url.openStream(); 
			InputStreamReader isr = new InputStreamReader(is); 
			BufferedReader br = new BufferedReader(isr); 
			textInPage = br.readLine();
			mapOnlineInfo_all.put(term,textInPage);
	        //将textInPage转换为Json格式，获取aggStr
	        JSONObject jsonObject = JSONObject.parseObject(textInPage);
	        String aggStr = jsonObject.getString("agg");
	        mapOnlineInfo_agg.put(term,aggStr);
	        //将aggStr转换为Json，获取count
	        JSONObject jsonResult = JSONObject.parseObject(aggStr);
	        if(jsonResult!=null && jsonResult.getString("count")!=null && !jsonResult.getString("count").equals("0")){
	        	return 1;
	        } else if(jsonResult==null || jsonResult.getString("count")==null){
	        	System.out.println(term);
	        	return 2;
	        } else {
	        	return 3;
	        }
		} catch (IOException e) {
			e.printStackTrace();
			return 3;
		}
	}

	//将术语按照xks进行归类存储
	@Override
	public void storeTermByxks(String dirpath, Map<String, String> online_term_info) {
		Map<String, List<String>> map_term = new HashMap<>();	//key-xk，value-对应xk的术语
		JSONObject jsonResult = null;
		//遍历术语info
		for(Entry<String, String> term_info : online_term_info.entrySet()) {
			jsonResult = JSONObject.parseObject(term_info.getValue());	//转换为json对象
			String[] xks = jsonResult.getString("xks").trim().split("，");	//获取xks
			for(String xk : xks) {
				List<String> list = map_term.get(xk);
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(term_info.getKey());
				map_term.put(xk, list);
			}
		}
		//将分类后的term写入文件
		for(Entry<String, List<String>> xk_term : map_term.entrySet()) {
			try {
				String txt = "";
				for(String t : xk_term.getValue()) {
					txt += t + "\n";
				}
				fileUtil.writeTxt(txt, dirpath+xk_term.getKey()+".txt", false);
			} catch (Exception e) {
				continue;
			}
		} 
	}

	@Override
	public void delUnAtom(List<File> segFiles, String resultpath, HashSet<String> stopNatures,
			HashSet<String> stopWords, int limit) throws IOException {
		// 定义一个map集合存放原子词串，string为原子词和_组成，integer为对应的领域内的出现频率
		Map<String, Integer> map = new HashMap<>();
		String temp = "";
		// 遍历所有的分词文件，进行相应的原子词删除操作
		for (File file : segFiles) {
			// 1、读取原子词及其词性，形如：文献	n
			List<String> segs = fileUtil.readTxTLine(file, "UTF-8");
			// 2、遍历分词结果，进行原子词过滤
			for (String seg : segs) {
				String[] words = seg.split("\t");	//words[0]=文献，words[1]=n
				if(words.length!=2) {
					continue;
				}
				if(!stopNatures.contains(words[1].trim().substring(0, 1)) && !stopWords.contains(words[0].trim())
						&& ChineseCharUtil.isChinese(words[0].trim())) {
					temp += words[0].trim() + "/" + words[1].trim() + "_";
				} else {
					if(!temp.equals("")) {
						if(temp.split("/")[0].length()>1) {
							if(map.get(temp)==null) {
								map.put(temp, 1);
							} else {
								map.put(temp, map.get(temp)+1);						
							}
						}
						temp = "";
					}
				}
			}
			if(!temp.equals("")) {
				if(temp.length()>2) {
					if(map.get(temp)==null) {
						map.put(temp, 1);
					} else {
						map.put(temp, map.get(temp)+1);						
					}
				}
				temp = "";
			}
		}
		Map<String, Integer> fMap = new HashMap<>();
		for(Entry<String, Integer> m : map.entrySet()) {
			if(m.getValue()>limit) {
				fMap.put(m.getKey(), m.getValue());
			}
		}
		// 将map集合写入文件
		fileUtil.writeTxt(fMap, resultpath, true);
	}

	@Override
	public void countAws(Map<String, Map<String, String>> mapDomainAws, List<String> domains,  String savepath) {
		// 定义map集合存放统计的原子词串
		Map<String, Object> map = new HashMap<>();
		// 根据领域名称依次遍历所有aws，进行词频统计，包括其子串
		String temp = "";
		for (String domain : domains) {
			System.out.println(domain);
			Map<String, String> mapAws = mapDomainAws.get(domain);
			// 遍历所有原子词串
			for (Entry<String, String> aws : mapAws.entrySet()) {				
				// 统计子串对应词频
				int[] count = new int[domains.size()];
				String[] aw = aws.getKey().split("_");	//将原子词串拆分成原子词，以便构建起子串
				for(int i=0; i<aw.length; i++) {
					for (int j = aw.length-1; j >= i; j--) {
						temp = aw[i];
						for (int k = i+1; k <= j; k++) {
							temp += aw[k];
						}
						if(temp.length()<2) {
							continue;
						}
						if(map.get(temp)==null) {
							count = countWord(temp, mapDomainAws, domains);
							map.put(temp, int2String(count));
						}
					}
				}
			}
			// 将map写入txt文件
			fileUtil.writeMap2Txt(map, savepath+domain+".txt", true);
		}
	}
	
	private String int2String(int[] nums) {
		String s = "";
		for (int i : nums) {
			s += "" + i + "\t";
		}
		return s;
	}

	/**
	* @Title: countWord
	* @Description: 统计词频
	*/
	private int[] countWord(String aws, Map<String, Map<String, String>> mapDomainAws, List<String> domains) {
		int[] counts = new int[domains.size()];
		
		// 依照领域统计词频
		for (int i=0; i<domains.size(); i++) {
			Map<String, String> mapAws = mapDomainAws.get(domains.get(i));
			int count = 0;
			for(Entry<String, String> aw : mapAws.entrySet()) {
				if(aw.getKey().replaceAll("_", "").indexOf(aws)>=0) {
					count += Integer.parseInt(aw.getValue());
				}
			}
			counts[i] = count;
		}
		return counts;
	}
	
	// 获取aws的子串
	public List<String[]> getSubAws(String aws, HashSet<String> stopNatures, HashSet<String> stopWords){
		List<String[]> list = new ArrayList<>();
		
		String[] aw = aws.split("_");	//将原子词串拆分成原子词，以便构建起子串
		String temp = "";
		String tmpn = "";
		String nature = "";
		for(int i=0; i<aw.length; i++) {
			for (int j = aw.length-1; j >= i; j--) {
				int count_ = 1;
				tmpn = aw[i];
				String[] atmp = aw[i].split("/");
				if(atmp.length!=2) {
					System.out.println(aw[i]);
					continue;
				}
				temp = atmp[0];
				nature = atmp[1];
				for (int k = i+1; k <= j; k++) {
					tmpn += "_"+aw[k];
					temp += aw[k].split("/")[0];
					count_++;
				}
				if(temp.length()>1) {
//					if(temp.equals("做到")) {
//						System.out.println(tmpn + stopNatures.contains(nature) + stopWords.contains(temp) + count_);
//					}
					if(count_==1 && stopNatures.contains(nature)) {	//单个原子词串的词性不符合要求
						continue;
					} else if(count_==1 && stopWords.contains(temp)){	//单个原子词串是停用词v2
						continue;
					} else {
						String[] t = {temp,tmpn};
						list.add(t);
					}
				}
			}
		}
		
		return list;
	}

	@Override
	public void countTerms(Map<String, String> mapAws, List<String> listCorpus, List<String> listCounted, HashSet<String> stopNatures, HashSet<String> stopWords, String savepath) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> mapExist = new HashMap<>();
		// 遍历所有的原子词串，进行相关统计
		for (Entry<String, String> aws : mapAws.entrySet()) {
			List<String[]> subAws = getSubAws(aws.getKey(), stopNatures, stopWords);
			for(String[] aw : subAws) {
				if(mapExist.get(aw[0])!=null) {
					continue;
				}
				// 检测是否已经统计过
				String v = checkCounted(aw[0], listCounted);
				if (!v.equals("no")) {
					map.put(aw[1], v);
					mapExist.put(aw[0], "");
					continue;
				}
				//统计词频及左右邻接字
				int[] wf = new int[listCorpus.size()];
				Map<String, Integer> leftWord = new HashMap<>();
				Map<String, Integer> rightWord = new HashMap<>();
				for(int i=0; i<listCorpus.size(); i++) {
					int start = 0;
					int count = 0;
					while((start=listCorpus.get(i).indexOf(aw[0], start))>=0) {
						count++;
						if(start>0) {	//统计左邻接字
							String ltmp = listCorpus.get(i).substring(start-1, start);
							Integer c = leftWord.get(ltmp);
							leftWord.put(ltmp, c==null?1:c+1);
						} else {
							leftWord.put("", 1);
						}
						if((start+aw[0].length())<listCorpus.get(i).length()) {	//统计右邻接字
							String rtmp = listCorpus.get(i).substring(start+aw[0].length(), start+aw[0].length()+1);
							Integer c = leftWord.get(rtmp);
							rightWord.put(rtmp, c==null?1:c+1);
						} else {
							rightWord.put("", 1);
						}
						start += aw[0].length();
					}
					wf[i] = count;
				}
				// 统计总词频
				int sum = 0;
				for (int i : wf) {
					sum += i;
				}
				// 计算左右信息熵
				double le = computEntropy(leftWord, sum);
				double re = computEntropy(rightWord, sum);
				v = "" + sum + "\t" + le + "\t" + re;
				for(int i : wf) {
					v += "\t" + i;
				}
				map.put(aw[1], v);
				mapExist.put(aw[0], "");
				if(map.size()>1000) {
					fileUtil.writeMap2Txt(map, savepath, true);
					map.clear();
				}
			}
		}
		// 写入txt
		fileUtil.writeMap2Txt(map, savepath, true);
	}
	
	public double computEntropy(Map<String, Integer> word, int total_count) {
		double lre = 0.0;
		for(Entry<String, Integer> lr : word.entrySet()) {
			double p = (double) lr.getValue() / total_count;
			lre += -p*Math.log(p);
		}
		return lre;
	}
	
	public String checkCounted(String aw, List<String> listCounted) {
		String rst = "no";
		
		for(String doneAw : listCounted) {
			String[] aws = doneAw.split("\t");
			if(aw.equals(aws[0])) {
				rst = aws[1];
				for(int i=2; i<aws.length; i++) {
					rst += "\t" + aws[i];
				}
				break;
			}
		}
		
		return rst;
	}

	@Override
	public void filterAws(int limit) throws IOException {
		// 读取统计好的词频文件
		TermUtil termUtil = new TermUtil();
		List<String> domains = termUtil.getDomains();
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.applyPattern("#.0000");	//四位小数
		
		// 对每个领域的词频文件分开统计
		for (String domain : domains) {
			System.out.println(domain);
			// 考虑到部分文件过大，需要更换读取方式
			File file = new File(ConstantValue.TERM_COUNT_PATH+domain+".txt");
			List<String> list = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			if(!file.exists()) {
				continue;
			} else if (file.length()<1024*1024*20) {
				list = fileUtil.readTxTLine(file, "UTF-8");
				for (String aws : list) {
					String[] aw = aws.split("\t");
					String v = computeCW(aw, limit, decimalFormat);
					if(v!=null) {
						map.put(aw[0], v);
					}
				}
			} else {
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));    
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"), 10*1024*1024);// 用5M的缓冲读取文本文件  
				  
				String aws = "";
				while((aws = reader.readLine()) != null){
					String[] aw = aws.split("\t");
					String v = computeCW(aw, limit, decimalFormat);
					if(v!=null) {
						map.put(aw[0], v);
					}
				}
				reader.close();
			}
			fileUtil.writeMap2Txt(map, ConstantValue.TERM_CW_PATH+domain+".txt", true);
		}
	}
	
	public String computeCW(String[] aw,int limit, DecimalFormat decimalFormat) {
		int total = Integer.parseInt(aw[1]);
		if(aw.length<5 || aw[0].length()<2 || total<limit) {
			return null;
		}
		//统计次数
		int n = 0;	//含有w的文档数
		int m = 0;	//总文档数
		double[] tf = new double[aw.length-3];	//统计含有w的文档的词频
		int idx = 0;
		int a = 0;	//临时
		for(int i=4; i<aw.length; i++) {
			m++;
			a = Integer.parseInt(aw[i]);
			if(a>0) {
				n++;
				tf[idx++] = a;
			}
		}
		tf[idx] = (double) total/m;
		// 计算信息熵
		double le = Double.parseDouble(aw[2]);
		double re = Double.parseDouble(aw[3]);
		double entropy = le*re;
		// 计算平均词频
		double tfa = (double) (m+1)*total/m/(n+1);
		// 计算加权平均词频
		double sum = 0.0;
		for (int i=0; i<=idx; i++) {
			sum += Math.pow(tf[i]-tfa, 2);
		}
		sum = Math.pow(sum/n, 0.5);
		// 计算信息熵*平均词频
		double rst = entropy * sum;
		String v = aw[1] + "\t" + decimalFormat.format(rst) + "\t" + decimalFormat.format(entropy)
				+ "\t" + decimalFormat.format(sum) + "\t" + decimalFormat.format(le)
				+ "\t" + decimalFormat.format(re);
		
		return v;
	}

	@Override
	public void filterCW(String domain, int limitFrequency, double limitEntropy,
			HashSet<String> stopNatures, HashSet<String> stopWords) throws IOException {
		List<String> listAws = fileUtil.readTxt(ConstantValue.TERM_CW_PATH+domain+".txt", "UTF-8");
		Map<String, Object> map = new HashMap<>();
		for (String aws : listAws) {
			String[] aw = aws.split("\t");
			if(aw.length==7) {
				String[] tmp = aw[0].trim().split("_");
				// 首先再次根据停用词及停用词性筛选，由于数量较小，效率在可接受范围
				if(tmp.length==1) {
					String[] t = tmp[0].split("/");
					if(stopWords.contains(t[0].trim()) || stopNatures.contains(t[1])) {
						continue;
					}
				} else {
					String s = "";
					for(int i=0; i<tmp.length; i++) {
						String[] t = tmp[0].split("/");
						s += t[0];
					}
					if(stopWords.contains(s)) {
						continue;
					}
				}
				// 根据词频和信息熵过滤
				if(Integer.parseInt(aw[1]) < limitFrequency) {
					continue;
				}
				if(Double.parseDouble(aw[3]) < limitEntropy) {
					continue;
				}
				String v = aw[1] + "\t" + aw[2] + "\t" + aw[3] + "\t" + aw[4] + "\t" + aw[5] + "\t" + aw[6];
				map.put(aw[0], v);
			}
		}
		fileUtil.writeMap2Txt(map, ConstantValue.TERM_TERM_PATH+domain+".txt", false);
	}
}
