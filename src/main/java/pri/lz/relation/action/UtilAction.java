package pri.lz.relation.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import pri.lz.relation.util.ChineseCharUtil;
import pri.lz.relation.util.ConstantValue;
import pri.lz.relation.util.FileUtil;

/**
* @ClassName: UtilAction
* @Description: 单独处理一些文件的工具Action
* @author 廖劲为
* @date 2017年1月16日 上午11:09:48
* 
*/
public class UtilAction {
	
	FileUtil fileUtil = new FileUtil();
	
	/**
	* @Title: findUncheckTerm
	* @throws IOException 
	* @Description: 筛选出未进行online check的term集合，必须执行的
	*/
	@Test
	public void findUncheckTerm() throws IOException{
		
		// 读取已经经过术语比对后的术语结果
		HashSet<String> term_no = fileUtil.readDicUTF8(ConstantValue.ONLINE_TERM_OK);
		HashSet<String> term_ok = fileUtil.readDicUTF8(ConstantValue.ONLINE_TERM_NO);

		// 待检测的term文件
		String[] termpaths = {
				ConstantValue.TERM_2ND_PATH + "answer_total_term.txt",
				ConstantValue.TERM_2ND_PATH + "train_total_term.txt",
				ConstantValue.SEGMENT_PATH+"answer_total_segment.txt",
				ConstantValue.SEGMENT_PATH + "train_total_segment.txt"
				};
		
		// 将待检测的term文件术语合并成一个文件
		Map<String, Integer> term_map = new HashMap<>();
		int termpath_size = termpaths.length;
		for (int i=0; i<termpath_size; i++) {
			Map<String, Integer> term_temp = loadTerm(termpaths[i]);
			if(i==0){
				term_map.putAll(term_temp);
			} else {
				for (Entry<String, Integer> term : term_temp.entrySet()) {
					Integer count = term_map.get(term.getKey());
					term_map.put(term.getKey(), (count==null || count>=term.getValue()) ? term.getValue() : count);
				}
			}
		}
		System.out.println("term_map size: " + term_map.size());
		
		// 依次读取文件中的term并进行检测是否经过对比，将对比文件按照词频分开存储
		Map<String, Object> term_uncheck_2 = new HashMap<>();
		Map<String, Object> term_uncheck_3 = new HashMap<>();
		Map<String, Object> term_uncheck_4 = new HashMap<>();
		Map<String, Object> term_uncheck_5 = new HashMap<>();
		Map<String, Object> term_uncheck_6 = new HashMap<>();
		Map<String, Object> term_uncheck_7 = new HashMap<>();
		Map<String, Object> term_uncheck_8 = new HashMap<>();
		Map<String, Integer> term_uncheck_0 = new HashMap<>();

		boolean wait = true;
		int count_term_check = 0;
		int count_term_ok_no = 0;
		int count_term_child = 0;
		// 遍历term_map结合，检测
		for (Entry<String, Integer> term : term_map.entrySet()) {
			// 通过hashset自带的contains方法比较，速度较快，直接对比是否是同一个词，若不是，则比较是否是其中的子串
			if(term_ok.contains(term.getKey()) || term_no.contains(term.getKey())){	//再与term_ok、term_no对比，有，wait-false
				wait = false;
				count_term_ok_no++;
			} else {	//没有，则比对子串
				// 首先与term_ok比对
				for (String ok_term : term_ok) {
					if(ok_term.indexOf(term.getKey())>=0){
						wait = false;
						count_term_child++;
						break;
					}
				}
			}
			if(wait){
				switch (term.getValue()) {
				case 2:
					term_uncheck_2.put(term.getKey(), term.getValue());
					if(term_uncheck_2.size()>10000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_2, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-2.txt", true);
						term_uncheck_2.clear();
						System.out.println("--term_uncheck_2 write--");
					}
					break;
				case 3:
					term_uncheck_3.put(term.getKey(), term.getValue());
					if(term_uncheck_3.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_3, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-3.txt", true);
						term_uncheck_3.clear();
						System.out.println("--term_uncheck_3 write--");
					}
					break;
				case 4:
					term_uncheck_4.put(term.getKey(), term.getValue());
					if(term_uncheck_4.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_4, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-4.txt", true);
						term_uncheck_4.clear();
						System.out.println("--term_uncheck_4 write--");
					}
					break;
				case 5:
					term_uncheck_5.put(term.getKey(), term.getValue());
					if(term_uncheck_5.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_5, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-5.txt", true);
						term_uncheck_5.clear();
						System.out.println("--term_uncheck_5 write--");
					}
					break;
				case 6:
					term_uncheck_6.put(term.getKey(), term.getValue());
					if(term_uncheck_6.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_6, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-6.txt", true);
						term_uncheck_6.clear();
						System.out.println("--term_uncheck_6 write--");
					}
					break;
				case 7:
					term_uncheck_7.put(term.getKey(), term.getValue());
					if(term_uncheck_7.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_7, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-7.txt", true);
						term_uncheck_7.clear();
						System.out.println("--term_uncheck_7 write--");
					}
					break;
				case 8:
					term_uncheck_8.put(term.getKey(), term.getValue());
					if(term_uncheck_8.size()>5000){
						// 写入txt文件
						fileUtil.writeMap2Txt(term_uncheck_8, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-8.txt", true);
						term_uncheck_5.clear();
						System.out.println("--term_uncheck_8 write--");
					}
					break;
				default:
					term_uncheck_0.put(term.getKey(), term.getValue());
					break;
				}
			} else {
				wait = true;
			}
		}
		System.out.println("count_term_check: " + count_term_check + "  , count_term_ok_no: " + count_term_ok_no + "  , count_term_child: " + count_term_child);
		// 写入txt文件
		fileUtil.writeMap2Txt(term_uncheck_2, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-2.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_3, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-3.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_4, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-4.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_5, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-5.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_6, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-6.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_7, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-7.txt", true);
		fileUtil.writeMap2Txt(term_uncheck_8, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-8.txt", true);
		fileUtil.writeTxt(term_uncheck_0, ConstantValue.DATA_ROOT_PATH+"term\\online\\wait_online_check-0.txt", false);
		System.out.println("--over--");
	}
	
	private Map<String, Integer> loadTerm(String termpath){
		int min = 4;
		Map<String, Integer> map = new HashMap<>();
		try {
			String encoding = "UTF-8";
			File file=new File(termpath);
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					String[] temp = lineTxt.split("\t");
					if(temp.length==2){
						Integer count = Integer.parseInt(temp[1]);
						if(count>=min)
							map.put(temp[0].trim(), count);
					}
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}	
	
	/**
	* @Title: countSegment
	* @Description: 将各领域的分词结果汇总
	*/
	@Test
	public void countSegment() throws IOException{
		
		//1、读取停用词性集合
		HashSet<String> stopNatures = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopnature.txt");
		//2、读取停用词集合
		HashSet<String> stopWords = fileUtil.readDicUTF8(ConstantValue.UTIL_PATH+"stopword.txt");
		
		// 实例化InputStreamReader
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		
		String[] dirs = {"answer", "train"};
		
		for (String dir : dirs) {
			// 获取各领域文件夹
			List<File> listDomains = fileUtil.getAllFileDirs(ConstantValue.SEGMENT_PATH+dir+"\\");
			// 首先统计各领域的分词结果
			for (File domain : listDomains) {
				List<File> listFiles = fileUtil.getAllFiles(domain.getPath());
				Map<String, Integer> map_domain_term = new HashMap<>();
				for (File file : listFiles) {
					read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
					bufferedReader = new BufferedReader(read);
					String lineTxt = null;
					while((lineTxt = bufferedReader.readLine()) != null){
						String[] words = lineTxt.split("\t");
						if(words.length==2){
							// 过来词长<2
							if(words[0].trim().length()<2){
								continue;
							}//删除非中文字符
							else if(!ChineseCharUtil.isChinese(words[0].trim())){
								continue;
							}//根据停用词性删除
							else if(stopNatures.contains(words[1].substring(0, 1).trim())){	//含有停用词性，继续下一个循环
								continue;
							}//根据停用词删除
							else if(stopWords.contains(words[0].trim())){	//含有停用词，继续下一个循环
								continue;
							} else {	// 添加到map集合
								Integer count = map_domain_term.get(words[0].trim());
								map_domain_term.put(words[0].trim(), count==null ? 1 : count+1);
							}
						}
					}
				}
				// 写入txt文件
				fileUtil.writeTxt(map_domain_term, ConstantValue.SEGMENT_PATH+dir+"\\"+domain.getName()+".txt", false);
			}
			// 然后将各领域的分词结果统计汇总
			List<File> listFiles = fileUtil.getAllFiles(ConstantValue.SEGMENT_PATH+dir+"\\");
			Map<String, Integer> map_term = new HashMap<>();
			for (File file : listFiles) {
				read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
				bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					String[] words = lineTxt.split("\t");
					if(words.length==2){
						String word = words[0].trim().replaceAll("_", "");
						int w_count = Integer.parseInt(words[1]);
						Integer count = map_term.get(word);
						map_term.put(word, count==null ? w_count : w_count+count);
					}
				}
			}
			// 写入txt文件
			fileUtil.writeTxt(map_term, ConstantValue.SEGMENT_PATH + dir + "_total_segment.txt", false);
		}
		System.out.println("--over--");
	}
	
	/**
	* @Title: countOnlineInfo
	* @Description: 统计术语在线比对后的术语
	*/
	@Test
	public void countOnlineInfo() throws IOException{
		String onlineInfoPath = ConstantValue.DATA_ROOT_PATH + "online\\agg_online\\";
		//读取指定文件夹下的所有文件
		List<File> listFiles = fileUtil.getAllFiles(onlineInfoPath);
		Map<String, String> map_term_ok = new HashMap<>();
		Map<String, String> map_term_no = new HashMap<>();
		Set<String> set_term_ok = new HashSet<>();
		Set<String> set_term_no = new HashSet<>();
		String encoding = "UTF-8";
		for (File file : listFiles) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				String[] temp = lineTxt.split("\t");
				if(temp.length==2){
					// 根据术语在线的统计信息判断指定术语
					if(parseOnlineInfo(temp[1])){
						set_term_ok.add(temp[0].trim());
						map_term_ok.put(temp[0].trim(), temp[1]);
					} else {
						set_term_no.add(temp[0].trim());
						map_term_no.put(temp[0].trim(), temp[1]);
					}
				}
			}
			read.close();
		}
		// 将当前结果写入指定文件
		String txt = "";
		for(Entry<String, String> term_ok : map_term_ok.entrySet()){
			txt += term_ok.getKey() + "\t" + term_ok.getValue() + "\n";
			if(txt.length()>10000){
				fileUtil.writeTxt(txt, ConstantValue.ONLINE_INFO_OK, true);
				txt = "";
			}
		}
		fileUtil.writeTxt(txt, ConstantValue.ONLINE_INFO_OK, true);
		txt = "";
		for(Entry<String, String> term_no : map_term_no.entrySet()){
			txt += term_no.getKey() + "\t" + term_no.getValue() + "\n";
			if(txt.length()>10000){
				fileUtil.writeTxt(txt, ConstantValue.ONLINE_INFO_NO, true);
				txt = "";
			}
		}
		fileUtil.writeTxt(txt, ConstantValue.ONLINE_INFO_NO, true);
		txt = "";
		for (String term : set_term_ok) {
			txt += term + "\n";
			if(txt.length()>10000){
				fileUtil.writeTxt(txt, ConstantValue.ONLINE_TERM_OK, true);
				txt = "";
			}
		}
		fileUtil.writeTxt(txt, ConstantValue.ONLINE_TERM_OK, true);
		txt = "";
		for (String term : set_term_no) {
			txt += term + "\n";
			if(txt.length()>10000){
				fileUtil.writeTxt(txt, ConstantValue.ONLINE_TERM_NO, true);
				txt = "";
			}
		}
		fileUtil.writeTxt(txt, ConstantValue.ONLINE_TERM_NO, true);
		System.out.println("--over--");
	}
	
	/**
	* @Title: parseOnlineInfo
	* @Description: 通过Json解析术语在线的返回信息
	* @param onlineInfo
	* @return boolean
	*/
	private boolean parseOnlineInfo(String onlineInfo){
		//将onlineInfo转换为Json，获取count
        JSONObject jsonResult = JSONObject.parseObject(onlineInfo);
        if(jsonResult!=null && jsonResult.getString("count")!=null && !jsonResult.getString("count").equals("0")){
        	return true;
        } else {
        	return false;
        }
	}
	
	// 统计在线术语比对结果，分别存储到指定文件夹
	public void mergeTermOk() throws IOException{
		String path = ConstantValue.DATA_ROOT_PATH + "online\\agg_online\\";
		List<File> listFiles = fileUtil.getAllFiles(path);
		Map<String, String> map_term_ok = new HashMap<>();
		Map<String, String> map_term_no = new HashMap<>();
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		for (File file : listFiles) {
			read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				String[] temp = lineTxt.split("\t");
				if(temp.length==2){
					// 根据术语在线的统计信息判断术语
					JSONObject jsonResult = JSONObject.parseObject(temp[1]);
					if(jsonResult!=null && jsonResult.getString("count")!=null && !jsonResult.getString("count").equals("0")){
						map_term_ok.put(temp[0], temp[1]);
						if(writeMap(map_term_ok, "term_online_ok.txt", false)){
							map_term_ok.clear();
						}
					} else {
						map_term_no.put(temp[0], temp[1]);
						if(writeMap(map_term_no, "term_online_no.txt", false)){
							map_term_no.clear();
						}
					}
				}
			}
			read.close();
		}
		writeMap(map_term_ok, "term_online_ok.txt", true);
		writeMap(map_term_no, "term_online_no.txt", true);
	}
	
	public boolean writeMap(Map<String, String> map_term, String txtName, boolean write) throws IOException{
		if(map_term.size()>5000 || write){
			String txt = "";
			String txt_online = "";
			for(Entry<String, String> term : map_term.entrySet()){
				txt_online += term.getKey() + "\t" + term.getValue() + "\n";
				txt += term.getKey() + "\n";
			}
			fileUtil.writeTxt(txt_online, ConstantValue.DATA_ROOT_PATH + "term\\" + txtName, true);
			fileUtil.writeTxt(txt, ConstantValue.DATA_ROOT_PATH + "term\\" + txtName.replaceAll("online_", ""), true);
			return true;
		} else {
			return false;
		}
	}

}