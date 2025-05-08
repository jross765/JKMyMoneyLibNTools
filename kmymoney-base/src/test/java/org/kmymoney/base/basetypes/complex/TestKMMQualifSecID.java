package org.kmymoney.base.basetypes.complex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kmymoney.base.basetypes.simple.KMMSecID;

import junit.framework.JUnit4TestAdapter;

public class TestKMMQualifSecID {
	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMMQualifSecID.class);
	}

	// -----------------------------------------------------------------

//  @Test
//  public void test01() throws Exception
//  {
//    try 
//    {
//	KMMSecID commCurr = new KMMSecID(KMMSecCurrID.Type.CURRENCY, "EUR");
//    }
//    catch ( Exception exc ) 
//    {
//	assertEquals(0, 0);
//    }
//  }

	@Test
	public void test02() throws Exception {
		KMMQualifSecID secID = new KMMQualifSecID("E000001");

		assertEquals(KMMQualifSecCurrID.Type.SECURITY, secID.getType());
		assertEquals(new KMMSecID("E000001"), secID.getSecID());
		assertEquals("E000001", secID.getCode());
		assertEquals("SECURITY:E000001", secID.toString());

		try {
			secID = new KMMQualifSecID("C000001"); // invalid string
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test03() throws Exception {
		KMMQualifSecID secID = KMMQualifSecID.parse("SECURITY:E000001");

		assertEquals(KMMQualifSecCurrID.Type.SECURITY, secID.getType());
		assertEquals(new KMMSecID("E000001"), secID.getSecID());
		assertEquals("E000001", secID.getCode());
		assertEquals("SECURITY:E000001", secID.toString());

		try {
			secID = new KMMQualifSecID("C000001"); // invalid string
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

}
