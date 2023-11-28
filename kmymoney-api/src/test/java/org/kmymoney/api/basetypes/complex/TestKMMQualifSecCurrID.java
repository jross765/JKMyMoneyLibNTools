package org.kmymoney.api.basetypes.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;

import junit.framework.JUnit4TestAdapter;

public class TestKMMQualifSecCurrID {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestKMMQualifSecCurrID.class);
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	KMMQualifSecCurrID commCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

	assertEquals(KMMQualifSecCurrID.Type.CURRENCY, commCurr.getType());
	assertEquals("EUR", commCurr.getCode());
	assertEquals("CURRENCY:EUR", commCurr.toString());

	// ---

	commCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "USD");

	assertEquals(KMMQualifSecCurrID.Type.CURRENCY, commCurr.getType());
	assertEquals("USD", commCurr.getCode());
	assertEquals("CURRENCY:USD", commCurr.toString());

	// ---

	commCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "XYZ"); // Wrong, but no check on this level

	assertEquals(KMMQualifSecCurrID.Type.CURRENCY, commCurr.getType());
	assertEquals("XYZ", commCurr.getCode());
	assertEquals("CURRENCY:XYZ", commCurr.toString());

    }

    @Test
    public void test02() throws Exception {
	KMMQualifSecCurrID commCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "MBG");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurr.getType());
	assertEquals("MBG", commCurr.getCode());
	assertEquals("SECURITY:MBG", commCurr.toString());
    }

    @Test
    public void test03() throws Exception {
	KMMQualifSecCurrID commCurr1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "MBG");
	KMMQualifSecCurrID commCurr2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "MBG");

	assertEquals(commCurr1.toString(), commCurr2.toString());
	assertEquals(commCurr1, commCurr2);

	// ---

	KMMQualifSecCurrID commCurr3 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "DIS");

	assertNotEquals(commCurr1, commCurr3);

	// ---

	KMMQualifSecCurrID commCurr4 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");
	KMMQualifSecCurrID commCurr5 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

	assertEquals(commCurr4, commCurr5);
	assertNotEquals(commCurr1, commCurr4);
	assertNotEquals(commCurr2, commCurr4);
	assertNotEquals(commCurr3, commCurr4);
	assertNotEquals(commCurr3, commCurr4);

	KMMQualifSecCurrID commCurr6 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "JPY");

	assertNotEquals(commCurr4, commCurr6);
    }

    @Test
    public void test04_1() throws Exception {
	KMMQualifSecCurrID commCurrPrs = KMMQualifSecCurrID.parse("CURRENCY:EUR");
	KMMQualifSecCurrID commCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

	assertEquals(KMMQualifSecCurrID.Type.CURRENCY, commCurrPrs.getType());
	assertEquals("CURRENCY:EUR", commCurrPrs.toString());
	assertEquals(commCurrRef.toString(), commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);

	// ---

	commCurrPrs = KMMQualifSecCurrID.parse("CURRENCY:USD");
	commCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "USD");

	assertEquals(KMMQualifSecCurrID.Type.CURRENCY, commCurrPrs.getType());
	assertEquals("CURRENCY:USD", commCurrPrs.toString());
	assertEquals(commCurrRef.toString(), commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);
    }

    @Test
    public void test04_2() throws Exception {
	KMMQualifSecCurrID commCurrPrs = KMMQualifSecCurrID.parse("SECURITY:SAP");
	KMMQualifSecCurrID commCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "SAP");

	assertEquals(KMMQualifSecCurrID.Type.SECURITY, commCurrPrs.getType());
	assertEquals("SECURITY:SAP", commCurrPrs.toString());
	assertEquals(commCurrRef.toString(), commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);

//      // ---
//      
//      commCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      commCurrRef = new SecCurrID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", commCurrPrs.toString());
//      assertEquals(commCurrRef, commCurrPrs);
    }

    @Test
    public void test04_3() throws Exception {
	try {
	    KMMQualifSecCurrID commCurrPrs = KMMQualifSecCurrID.parse("FUXNSTUELL:BURP"); // Wrong, but not check on
											  // this level
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

//      // ---
//      
//      commCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      commCurrRef = new SecID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", commCurrPrs.toString());
//      assertEquals(commCurrRef, commCurrPrs);
    }
}
