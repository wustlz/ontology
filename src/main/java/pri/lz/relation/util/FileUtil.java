package pri.lz.relation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileUtil {

	/**
	 * @Title: readTxt
	 * @Description: 全部读取当前文件
	 * @param @param
	 *            path
	 * @return String
	 * @throws IOException
	 */
	public List<String> readTxt(String path, String encoding) throws IOException {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				list.add(lineTxt);
			}
			read.close();
		}
		return list;
	}

	/**
	 * @Title: readTxt
	 * @Description: 以指定编码读取当前文件的全部内容,并按行返回
	 * @param file-File参数
	 * @return List<String>
	 * @throws IOException
	 */
	public List<String> readTxTLine(File file, String encoding) throws IOException {
		List<String> list = new ArrayList<>();
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				list.add(lineTxt.trim());
			}
			read.close();
		}
		return list;
	}

	/**
	 * @Title: readTxt
	 * @Description: 以指定编码读取当前文件的全部内容
	 * @param file-File参数
	 * @return String
	 * @throws IOException
	 */
	public String readTxt(File file, String encoding) throws IOException {
		String text = "";
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				text += lineTxt + "\n";
			}
			read.close();
		}
		return text;
	}

	/**
	 * @Title: readDic
	 * @Description: 根据文件路径分行以UTF-8编码读取内容并用hashset存储
	 * @param path
	 * @return HashSet<String>
	 */
	public HashSet<String> readDicUTF8(String path) {
		HashSet<String> words = new HashSet<>();
		try {
			String encoding = "UTF-8";
			File file = new File(path);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					words.add(lineTxt.trim());
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return words;
	}
	
	/**
	* @Title: readMap
	* @Description: 读取map集合
	* @return HashSet<String>
	*/
	public Map<String, String> readInfo(String path){
		Map<String, String> words = new HashMap<>();
		try {
			File file = new File(path);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] temps = lineTxt.split("\t");
					if(temps.length>0){
						words.put(temps[0].trim(),temps[1].trim());
					}
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return words;
	}
	
	/**
	* @Title: readMap
	* @Description: 读取map集合写入txt文件中的第1列值，存储到hashset中
	* @return HashSet<String>
	*/
	public HashSet<String> readMap(String path){
		HashSet<String> words = new HashSet<>();
		try {
			File file = new File(path);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] temps = lineTxt.split("\t");
					if(temps.length>0){
						words.add(temps[0].trim());
					}
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return words;
	}

	/**
	 * @Title: writeTxt
	 * @Description: 将指定内容写入TXT文件
	 * @param @param
	 *            txt
	 * @param @param
	 *            path
	 * @throws IOException
	 */
	public void writeTxt(String txt, String path, boolean append) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		} else if (!append) { // 清空文件内容
			FileWriter fw = new FileWriter(file);
			fw.write("");
			fw.close();
		}
		FileOutputStream out = new FileOutputStream(file, true);
		StringBuffer sb = new StringBuffer();
		sb.append(txt);
		out.write(sb.toString().getBytes("utf-8"));
		out.close();
	}

	/**
	 * @Title: getAllFiles
	 * @Description: 获取指定文件夹下的所有文件,不包括文件夹下的子文件夹
	 * @param dirPath-文件夹路径
	 * @return List<File>——返回文件集合
	 */
	public List<File> getAllFiles(String dirPath) {
		List<File> files = new ArrayList<>();
		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			File[] fileArr = dir.listFiles();
			for (int i = 0; i < fileArr.length; i++) {
				File f = fileArr[i];
				if (f.isFile()) {
					files.add(f);
				}
			}
		}
		return files;
	}
	
	/**
	* @Title: traverseFile
	* @Description: 递归遍历获取指定文件夹下的所有文件
	*/
	public List<File> traverseFile(String rootpath){
		File file = new File(rootpath);		//获取指定文件位置
		File[] files = file.listFiles();	// 该文件目录下文件全部放入数组
		List<File> listFiles = new ArrayList<>();
		
		if(files != null){
			for(int i=0; i<files.length; i++){
				if(files[i].isFile()){
					listFiles.add(files[i]);
				} else if(files[i].isDirectory()){
					listFiles.addAll(traverseFile(files[i].getAbsolutePath()));
				}
			}
		}
		return listFiles;
	}

	/**
	 * @Title: getAllFileDirs
	 * @Description: 获取指定文件夹下的所有子文件夹
	 * @param dirPath-文件夹路径
	 * @return List<File>——返回文件夹集合
	 */
	public List<File> getAllFileDirs(String dirPath) {
		List<File> files = new ArrayList<>();
		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			File[] fileArr = dir.listFiles();
			for (int i = 0; i < fileArr.length; i++) {
				File f = fileArr[i];
				if (f.isDirectory()) {
					files.add(f);
				}
			}
		}
		return files;
	}

	/**
	 * @Title: writeTxt
	 * @Description: 将Map<String, Integer>降序写入文件
	 * @param word_map
	 * @param path
	 * @param append
	 */
	public void writeTxt(Map<String, Integer> word_map, String path, boolean desc) {
		try {
			File file = new File(path);
			if(!desc && file.exists()){
				FileWriter fw = new FileWriter(file);
				fw.write("");
				fw.close();
			}
			
			MapUtil mapUtil = new MapUtil();
			Map<String, Integer> map = mapUtil.sortMapByValueDesc(word_map, true);
			String txt = "";
			for (Entry<String, Integer> term : map.entrySet()) {
				txt += term.getKey() + "\t" + term.getValue() + "\n";
				if (txt.length() > 10000) {
					writeTxt(txt, path, true);
					txt = "";
				}
			}
			writeTxt(txt, path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: writeTxt
	 * @Description: 将Map<String, String>写入文件
	 * @param word_map
	 * @param path
	 */
	public void writeMap2Txt(Map<String, Object> word_map, String path, boolean append) {
		String txt = "";
		try {
			File file = new File(path);
			if(!append && file.exists()){
				FileWriter fw = new FileWriter(file);
				fw.write("");
				fw.close();
			}
			
			for (Entry<String, Object> term : word_map.entrySet()) {
				txt += term.getKey() + "\t" + term.getValue() + "\n";
				if (txt.length() > 10000) {
					writeTxt(txt, path, true);
					txt = "";
				}
			}
			writeTxt(txt, path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* @Title: writeSet2Txt
	* @Description: 将hashset集合写入TXT文件
	*/
	public void writeSet2Txt(HashSet<String> set, String path, boolean append){
		String txt = "";
		try {
			File file = new File(path);
			if(!append && file.exists()){
				FileWriter fw = new FileWriter(file);
				fw.write("");
				fw.close();
			}
			
			for (String temp : set) {
				txt += temp + "\n";
				if (txt.length() > 10000) {
					writeTxt(txt, path, true);
					txt = "";
				}
			}
			writeTxt(txt, path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
