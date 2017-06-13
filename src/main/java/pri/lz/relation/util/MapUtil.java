package pri.lz.relation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapUtil {
	
	/**
	* @Title: sortMapByKey
	* @Description: 按键对map排序
	* @param @param oriMap
	* @param @return
	* @return Map<String,String>
	* @throws
	*/ 
	public Map<String, String> sortMapByKey(Map<String, String> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String key1, String key2) {
				int intKey1 = 0, intKey2 = 0;
				try {
					intKey1 = getInt(key1);
					intKey2 = getInt(key2);
				} catch (Exception e) {
					intKey1 = 0; 
					intKey2 = 0;
				}
				return intKey1 - intKey2;
			}});
		sortedMap.putAll(oriMap);
		return sortedMap;
	}
	
	private int getInt(String str) {
		int i = 0;
		try {
			Pattern p = Pattern.compile("^\\d+");
			Matcher m = p.matcher(str);
			if (m.find()) {
				i = Integer.valueOf(m.group());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	/**
	* @Title: sortMapByValue
	* @Description: 按值对map排序
	* @param @param oriMap
	* @param @return
	* @return Map<String,String>
	* @throws
	*/ 
	public Map<String, String> sortMapByValue(Map<String, String> oriMap) {
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, String>>() {
						public int compare(Entry<String, String> entry1,
								Entry<String, String> entry2) {
							int value1 = 0, value2 = 0;
							try {
								value1 = getInt(entry1.getValue());
								value2 = getInt(entry2.getValue());
							} catch (NumberFormatException e) {
								value1 = 0;
								value2 = 0;
							}
							return value2 - value1;
						}
					});
			Iterator<Map.Entry<String, String>> iter = entryList.iterator();
			Map.Entry<String, String> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
	
	/**
	 * @Title: sortMapByValue
	 * @Description: 按值对map排序
	 * @param @param oriMap
	 * @param @return
	 * @return Map<String,Integer>
	 * @throws
	 */ 
	public Map<String, Integer> sortMapByValueDesc(Map<String, Integer> oriMap, boolean desc) {
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> entry1,
						Entry<String, Integer> entry2) {
					int value1 = 0, value2 = 0;
					try {
						value1 = entry1.getValue();
						value2 = entry2.getValue();
					} catch (NumberFormatException e) {
						value1 = 0;
						value2 = 0;
					}
					if(desc){
						return value2 - value1;
					} else {
						return value1 - value2;
					}
				}
			});
			Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
			Map.Entry<String, Integer> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
	
	/**
	 * @Title: sortMapByValue
	 * @Description: 按值对map排序
	 * @param @param oriMap
	 * @param @return
	 * @return Map<String,Integer>
	 * @throws
	 */ 
	public Map<String, Double> sortMapDoubleByValueDesc(Map<String, Double> oriMap, boolean desc) {
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Double>>() {
				public int compare(Entry<String, Double> entry1,
						Entry<String, Double> entry2) {
					double value1 = 0.0, value2 = 0.0;
					try {
						value1 = entry1.getValue();
						value2 = entry2.getValue();
					} catch (NumberFormatException e) {
						value1 = 0;
						value2 = 0;
					}
					int descFlag = 1;
					int ascFlag = -1;
					if(value2-value1<0.0){
						descFlag = -1;
						ascFlag = 1;
					}
					if(desc){
						return descFlag;
					} else {
						return ascFlag;
					}
				}
			});
			Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
			Map.Entry<String, Double> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
}
