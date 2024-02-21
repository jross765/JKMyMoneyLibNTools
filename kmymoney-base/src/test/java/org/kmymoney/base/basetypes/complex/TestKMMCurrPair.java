package org.kmymoney.base.basetypes.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMCurrPair {
	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMMCurrPair.class);
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		KMMQualifSecCurrID secCurr1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "USD");
		KMMQualifCurrID    currID2  = new KMMQualifCurrID("EUR");
		KMMPricePairID currPr = new KMMPricePairID(secCurr1, currID2);
		assertEquals(secCurr1, currPr.getFromSecCurr());
		assertEquals(currID2, currPr.getToCurr());
		assertEquals("USD", currPr.getFromSecCurr().getCode());
		assertEquals("EUR", currPr.getToCurr().getCode());
		
		// ---
		
		secCurr1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
		currPr = new KMMPricePairID(secCurr1, currID2);
		assertEquals(secCurr1, currPr.getFromSecCurr());
		assertEquals(currID2, currPr.getToCurr());
		assertEquals("E000001", currPr.getFromSecCurr().getCode());
		assertEquals("EUR", currPr.getToCurr().getCode());
	}

	@Test
	public void test02() throws Exception {
		KMMQualifSecID  secID1   = new KMMQualifSecID("E000001");
		KMMQualifCurrID secCurr2 = new KMMQualifCurrID("EUR");
		
		KMMPricePairID currPr = new KMMPricePairID(secID1, secCurr2);
		assertNotEquals(secID1, currPr.getFromSecCurr()); // sic!
		assertEquals(secID1.toString(), currPr.getFromSecCurr().toString());
		assertEquals(secCurr2, currPr.getToCurr());
		assertEquals("E000001", currPr.getFromSecCurr().getCode());
		assertEquals("EUR", currPr.getToCurr().getCode());
	}

	@Test
	public void test03() throws Exception {
		KMMQualifCurrID currID1 = new KMMQualifCurrID("SGD");
		KMMQualifCurrID currID2 = new KMMQualifCurrID("EUR");
		
		KMMPricePairID currPr = new KMMPricePairID(currID1, currID2);
		assertNotEquals(currID1, currPr.getFromSecCurr()); // sic!
		assertEquals(currID1.toString(), currPr.getFromSecCurr().toString());
		assertEquals(currID2, currPr.getToCurr());
		assertEquals("SGD", currPr.getFromSecCurr().getCode());
		assertEquals("EUR", currPr.getToCurr().getCode());
	}

}
