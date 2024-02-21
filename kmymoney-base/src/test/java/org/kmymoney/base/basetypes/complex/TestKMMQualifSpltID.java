package org.kmymoney.base.basetypes.complex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;

public class TestKMMQualifSpltID {
	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMMQualifSpltID.class);
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
		KMMQualifSpltID spltID1 = new KMMQualifSpltID(new KMMTrxID("T000000000000000001"), new KMMSpltID("S0001"));

		assertEquals(new KMMTrxID("T000000000000000001"), spltID1.getTransactionID());
		assertEquals("T000000000000000001", spltID1.getTransactionID().toString());
		assertEquals(new KMMSpltID("S0001"), spltID1.getSplitID());
		assertEquals("S0001", spltID1.getSplitID().toString());
		assertEquals("T000000000000000001:S0001", spltID1.toString());

		KMMQualifSpltID spltID2 = new KMMQualifSpltID("T000000000000000001", "S0001");

		assertEquals(new KMMTrxID("T000000000000000001"), spltID2.getTransactionID());
		assertEquals("T000000000000000001", spltID2.getTransactionID().toString());
		assertEquals(new KMMSpltID("S0001"), spltID2.getSplitID());
		assertEquals("S0001", spltID2.getSplitID().toString());
		assertEquals("T000000000000000001:S0001", spltID2.toString());

		assertEquals(spltID1.toString(), spltID2.toString());
		assertEquals(spltID1, spltID2);

		try {
			KMMQualifSpltID spltID = new KMMQualifSpltID("C000001", "alexander"); // invalid strings
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

}
