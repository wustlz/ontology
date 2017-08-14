package pri.lz.relation.util;

import org.junit.Test;

public class TermUtilTest {

	TermUtil termUtil = new TermUtil();

	@Test
	public void testCountDoc() {
		System.out.println(termUtil.countDoc(ConstantValue.PREDEAL_PATH));
	}
	
	@Test
	public void testMath(){
		System.out.println((double)3/5);
	}

}
