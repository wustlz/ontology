package pri.lz.relation.util;

import java.io.File;

/**
* @ClassName: TermUtil
* @Description: 有关术语和特征词典构建的工具类
* @author 廖劲为
* @date 2017年8月14日 上午8:54:20
* 
*/
public class TermUtil {
	
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
