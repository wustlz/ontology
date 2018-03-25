package pri.lz.relation.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
* @ClassName: TermUtil
* @Description: 有关术语和特征词典构建的工具类
* @author 廖劲为
* @date 2017年8月14日 上午8:54:20
* 
*/
public class TermUtil {
	
	/**
	* @Title: getDomains
	* @Description: 需要合并answer和train的内容，因此先指定领域
	*/
	public List<String> getDomains(){
		List<String> domains = new ArrayList<>();
		domains.add("C3-Art");
		domains.add("C4-Literature");
		domains.add("C5-Education");
		domains.add("C6-Philosophy");
		domains.add("C7-History");
		domains.add("C11-Space");
		domains.add("C15-Energy");
		domains.add("C16-Electronics");
		domains.add("C17-Communication");
		domains.add("C19-Computer");
		domains.add("C23-Mine");
		domains.add("C29-Transport");
		domains.add("C31-Enviornment");
		domains.add("C32-Agriculture");
		domains.add("C34-Economy");
		domains.add("C35-Law");
		domains.add("C36-Medical");
		domains.add("C37-Military");
		domains.add("C38-Politics");
		domains.add("C39-Sports");
		
		return domains;
	}
	
	/**
	* @Title: countDoc
	* @Description: 统计指定路径下的文件总数
	*/
	public int countDoc(String path){
		File file = new File(path);
		File[] files = file.listFiles();	// 该文件目录下文件全部放入数组
		int count = 0;
		
		if(files != null){
			for(int i=0; i<files.length; i++){
				if(files[i].isFile()){
					count++;
				} else if(files[i].isDirectory()){
					count += countDoc(files[i].getAbsolutePath());	//递归读取文件夹
				}
			}
		}
		
		return count;
	}
	
	
}
