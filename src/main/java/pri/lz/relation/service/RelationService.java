package pri.lz.relation.service;

import java.io.IOException;

/**
* @ClassName: RelationService
* @Description: 概念对关系判断
* @author 廖劲为
* @date 2017年3月16日 上午11:20:58
* 
*/
public interface RelationService {

	/**
	* @Title: featureVector
	* @Description: 计算领域概念特征向量
	* @param domainName-领域名称
	* @param typeName-类型，train，answer
	* @param conceptPath-待计算特征向量的概念文档路径
	* @return void
	*/
	public void featureVector(String domainName, String typeName, String conceptPath);
	
	/**
	* @Title: computeRelated
	* @Description: 计算概念之间的相关性
	* @param concept1
	* @param concept2
	* @param domainName
	* @return double-概念相似度
	*/
	public double computeRelated(String concept1, String concept2, String domainName);

	/**
	* @Title: countIndex
	* @Description: 统计当前领域概念包括所有特征向量不为0的特征词索引
	* @param domainName-领域名称
	* @param typeName-类型，train，answer
	*/
	public void countIndexMatrix(String typeName, String domainName) throws IOException;
	
	/**
	* @Title: trainByBP
	* @Description: 利用BP训练当前领域的概念关系分类器
	* @param domainName-领域名称
	*/
	public void trainByBP(String domainName) throws IOException;
}