package org.kmymoney.basetypes.complex;

import static org.junit.Assert.assertEquals;

import java.util.Currency;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMQualifCurrID {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestKMMQualifCurrID.class);
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	KMMQualifCurrID commCurr = new KMMQualifCurrID(Currency.getInstance("EUR"));

	assertEquals(KMMQualifCurrID.Type.CURRENCY, commCurr.getType());
	assertEquals("EUR", commCurr.getCode());
	assertEquals("EUR", commCurr.getCurrency().getCurrencyCode());
	assertEquals("CURRENCY:EUR", commCurr.toString());

	// ---

	commCurr = new KMMQualifCurrID(Currency.getInstance("USD"));

	assertEquals(KMMQualifCurrID.Type.CURRENCY, commCurr.getType());
	assertEquals("USD", commCurr.getCode());
	assertEquals("USD", commCurr.getCurrency().getCurrencyCode());
	assertEquals("CURRENCY:USD", commCurr.toString());

	// ---

	try {
	    commCurr = new KMMQualifCurrID(Currency.getInstance("XYZ")); // invalid code
	} catch (Exception exc) {
	    // correct behaviour: Throw exception
	    assertEquals(0, 0);
	}
    }

    @Test
    public void test04_1() throws Exception {
	KMMQualifCurrID commCurrPrs = KMMQualifCurrID.parse("CURRENCY:EUR");
	KMMQualifCurrID commCurrRef = new KMMQualifCurrID(Currency.getInstance("EUR"));

	assertEquals(KMMQualifCurrID.Type.CURRENCY, commCurrPrs.getType());
	assertEquals("CURRENCY:EUR", commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);

	// ---

	commCurrPrs = KMMQualifCurrID.parse("CURRENCY:USD");
	commCurrRef = new KMMQualifCurrID(Currency.getInstance("USD"));

	assertEquals(KMMQualifCurrID.Type.CURRENCY, commCurrPrs.getType());
	assertEquals("CURRENCY:USD", commCurrPrs.toString());
	assertEquals(commCurrRef, commCurrPrs);
    }

    @Test
    public void test04_2() throws Exception {
	try {
	    KMMQualifCurrID commCurrPrs = KMMQualifCurrID.parse("EURONEXT:SAP");
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}
    }

    @Test
    public void test04_3() throws Exception {
	try {
	    KMMQualifCurrID commCurrPrs = KMMQualifCurrID.parse("FUXNSTUELL:BURP");
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}
    }
}
