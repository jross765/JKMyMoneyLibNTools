package org.kmymoney.basetypes.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

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
	KMMQualifSecID commCurr = new KMMQualifSecID("MBG");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurr.getType());
	assertEquals("MBG", commCurr.getCode());
	assertEquals("SECURITY:MBG", commCurr.toString());

	commCurr = new KMMQualifSecID("DE0007100000");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurr.getType());
	assertEquals("DE0007100000", commCurr.getCode());
	assertEquals("SECURITY:DE0007100000", commCurr.toString());
    }

    @Test
    public void test03() throws Exception {
	KMMQualifSecID commCurr1 = new KMMQualifSecID("MBG");
	KMMQualifSecID commCurr2 = new KMMQualifSecID("MBG");

	assertEquals(commCurr1.toString(), commCurr2.toString());
	assertEquals(commCurr1, commCurr2);

	// ---

	KMMQualifSecID commCurr3 = new KMMQualifSecID("DIS");

	assertNotEquals(commCurr1, commCurr3);

	// ---

	KMMQualifCurrID commCurr4 = new KMMQualifCurrID("EUR");

	assertNotEquals(commCurr1, commCurr4);
	assertNotEquals(commCurr2, commCurr4);
	assertNotEquals(commCurr3, commCurr4);
	assertNotEquals(commCurr3, commCurr4);

	KMMQualifCurrID commCurr6 = new KMMQualifCurrID("JPY");

	assertNotEquals(commCurr4, commCurr6);
    }

    @Test
    public void test04_1() throws Exception {
	try {
	    KMMQualifSecID commCurrPrs = KMMQualifSecID.parse("CURRENCY:EUR");
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

	// ---

	try {
	    KMMQualifSecID commCurrPrs = KMMQualifSecID.parse("CURRENCY:USD");
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

    }

    @Test
    public void test04_2() throws Exception {
	KMMQualifSecID commCurrPrs = KMMQualifSecID.parse("SECURITY:SAP");
	KMMQualifSecID commCurrRef = new KMMQualifSecID("SAP");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurrPrs.getType());
	assertEquals("SECURITY:SAP", commCurrPrs.toString());
	assertEquals(commCurrRef.toString(), commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);

	// ---

	commCurrPrs = KMMQualifSecID.parse("SECURITY:DE0007164600");
	commCurrRef = new KMMQualifSecID("DE0007164600");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurrPrs.getType());
	assertEquals("SECURITY:DE0007164600", commCurrPrs.toString());
	assertEquals(commCurrRef.toString(), commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);
    }

    @Test
    public void test04_3() throws Exception {
	try {
	    KMMQualifSecID commCurrPrs = KMMQualifSecID.parse("CURRENCY:EUR"); // Wrong
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

	// ---

	try {
	    KMMQualifSecID commCurrPrs = KMMQualifSecID.parse("FUXNSTUELL:BURP"); // Wrong, but not check on this level
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}
    }
}
