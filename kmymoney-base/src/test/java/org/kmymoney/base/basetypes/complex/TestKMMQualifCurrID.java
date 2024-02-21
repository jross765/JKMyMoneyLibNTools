package org.kmymoney.base.basetypes.complex;

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
		KMMQualifCurrID secCurr = new KMMQualifCurrID(Currency.getInstance("EUR"));

		assertEquals(KMMQualifCurrID.Type.CURRENCY, secCurr.getType());
		assertEquals("EUR", secCurr.getCode());
		assertEquals("EUR", secCurr.getCurrency().getCurrencyCode());
		assertEquals("CURRENCY:EUR", secCurr.toString());

		// ---

		secCurr = new KMMQualifCurrID(Currency.getInstance("USD"));

		assertEquals(KMMQualifCurrID.Type.CURRENCY, secCurr.getType());
		assertEquals("USD", secCurr.getCode());
		assertEquals("USD", secCurr.getCurrency().getCurrencyCode());
		assertEquals("CURRENCY:USD", secCurr.toString());

		// ---

		try {
			secCurr = new KMMQualifCurrID(Currency.getInstance("XYZ")); // invalid code
			assertEquals(1, 0);
		} catch (Exception exc) {
			// correct behaviour: Throw exception
			assertEquals(0, 0);
		}
	}

	@Test
	public void test04_1() throws Exception {
		KMMQualifCurrID secCurrPrs = KMMQualifCurrID.parse("CURRENCY:EUR");
		KMMQualifCurrID secCurrRef = new KMMQualifCurrID(Currency.getInstance("EUR"));

		assertEquals(KMMQualifCurrID.Type.CURRENCY, secCurrPrs.getType());
		assertEquals("CURRENCY:EUR", secCurrPrs.toString());
		assertEquals(secCurrRef, secCurrPrs);

		// ---

		secCurrPrs = KMMQualifCurrID.parse("CURRENCY:USD");
		secCurrRef = new KMMQualifCurrID(Currency.getInstance("USD"));

		assertEquals(KMMQualifCurrID.Type.CURRENCY, secCurrPrs.getType());
		assertEquals("CURRENCY:USD", secCurrPrs.toString());
		assertEquals(secCurrRef, secCurrPrs);
	}

	@Test
	public void test04_2() throws Exception {
		try {
			KMMQualifCurrID secCurrPrs = KMMQualifCurrID.parse("EURONEXT:SAP");
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test04_3() throws Exception {
		try {
			KMMQualifCurrID secCurrPrs = KMMQualifCurrID.parse("FUXNSTUELL:BURP");
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}
}
