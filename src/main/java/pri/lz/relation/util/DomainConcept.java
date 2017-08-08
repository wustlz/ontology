package pri.lz.relation.util;

import java.util.HashMap;
import java.util.Map;

public class DomainConcept {
//	public Map<String, String> DOMAINONLINE = new HashMap<>();
	public Map<String, String> domain_ke = new HashMap<>();
	
	public Map<String, String> getDomain_ke() {
		domain_ke.put("C3-Art", "C3-Art");
		domain_ke.put("C4-Literature", "C4-Literature");
		domain_ke.put("C5-Education", "C5-Education");
		domain_ke.put("C6-Philosophy", "C6-Philosophy");
		domain_ke.put("C7-History", "C7-History");
		domain_ke.put("C11-Space", "C11-Space");
		domain_ke.put("C15-Energy", "C15-Energy");
		domain_ke.put("C16-Electronics", "C16-Electronics");
		domain_ke.put("C17-Communication", "C17-Communication");
//		domain_ke.put("C19-Computer", "计算机科学技术,信息科学技术");
		domain_ke.put("C19-Computer", "计算机科学技术");
		domain_ke.put("C23-Mine", "C23-Mine");
		domain_ke.put("C29-Transport", "C29-Transport");
		domain_ke.put("C31-Enviornment", "C31-Enviornment");
		domain_ke.put("C32-Agriculture", "C32-Agriculture");
		domain_ke.put("C34-Economy", "C34-Economy");
		domain_ke.put("C35-Law", "C35-Law");
		domain_ke.put("C36-Medical", "C36-Medical");
		domain_ke.put("C37-Military", "C37-Military");
		domain_ke.put("C38-Politics", "C38-Politics");
		domain_ke.put("C39-Sports", "C39-Sports");
		return domain_ke;
	}

	public void setDomain_ke(Map<String, String> domain_ke) {
		this.domain_ke = domain_ke;
	}
	
}