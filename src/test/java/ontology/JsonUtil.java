package ontology;


import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	@Test
	public void testJson(){
		String text = "{\"xks\":\"自然辩证法，化学工程\",\"tw\":\"實驗數據\",\"count\":3,"
				+ "\"en\":\"experimental data，data experimental\",\"cn\":\"实验数据\",\"k\":\"cn\"}";
		JSONObject jsonobjec = JSON.parseObject(text);
		System.out.println(jsonobjec.get("xks"));
		String[] checks = jsonobjec.get("xks").toString().split("，");
		for (String string : checks) {
			System.out.println(string);
		}
		
	}
	
}