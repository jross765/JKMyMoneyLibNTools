package org.kmymoney.base.basetypes.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

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
		KMMQualifSecCurrID secCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

		assertEquals(KMMQualifSecCurrID.Type.CURRENCY, secCurr.getType());
		assertEquals("EUR", secCurr.getCode());
		assertEquals("CURRENCY:EUR", secCurr.toString());

		// ---

		secCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "USD");

		assertEquals(KMMQualifSecCurrID.Type.CURRENCY, secCurr.getType());
		assertEquals("USD", secCurr.getCode());
		assertEquals("CURRENCY:USD", secCurr.toString());

		// ---

		secCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "XYZ"); // Wrong, but no check on this level

		assertEquals(KMMQualifSecCurrID.Type.CURRENCY, secCurr.getType());
		assertEquals("XYZ", secCurr.getCode());
		assertEquals("CURRENCY:XYZ", secCurr.toString());

	}

	@Test
	public void test02() throws Exception {
		KMMQualifSecCurrID secCurr = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");

		assertEquals(KMMQualifSecCurrID.Type.SECURITY, secCurr.getType());
		assertEquals("E000001", secCurr.getCode());
		assertEquals("SECURITY:E000001", secCurr.toString());
	}

	@Test
	public void test03() throws Exception {
		KMMQualifSecCurrID secCurr1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
		KMMQualifSecCurrID secCurr2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");

		assertEquals(secCurr1.toString(), secCurr2.toString());
		assertEquals(secCurr1, secCurr2);

		// ---

		KMMQualifSecCurrID secCurr3 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000002");

		assertNotEquals(secCurr1, secCurr3);

		// ---

		KMMQualifSecCurrID secCurr4 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");
		KMMQualifSecCurrID secCurr5 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

		assertEquals(secCurr4, secCurr5);
		assertNotEquals(secCurr1, secCurr4);
		assertNotEquals(secCurr2, secCurr4);
		assertNotEquals(secCurr3, secCurr4);
		assertNotEquals(secCurr3, secCurr4);

		KMMQualifSecCurrID secCurr6 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "JPY");

		assertNotEquals(secCurr4, secCurr6);
	}

	@Test
	public void test04_1() throws Exception {
		KMMQualifSecCurrID secCurrPrs = KMMQualifSecCurrID.parse("CURRENCY:EUR");
		KMMQualifSecCurrID secCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");

		assertEquals(KMMQualifSecCurrID.Type.CURRENCY, secCurrPrs.getType());
		assertEquals("CURRENCY:EUR", secCurrPrs.toString());
		assertEquals(secCurrRef.toString(), secCurrPrs.toString());
		assertEquals(secCurrRef, secCurrPrs);

		// ---

		secCurrPrs = KMMQualifSecCurrID.parse("CURRENCY:USD");
		secCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "USD");

		assertEquals(KMMQualifSecCurrID.Type.CURRENCY, secCurrPrs.getType());
		assertEquals("CURRENCY:USD", secCurrPrs.toString());
		assertEquals(secCurrRef.toString(), secCurrPrs.toString());
		assertEquals(secCurrRef, secCurrPrs);
	}

	@Test
	public void test04_2() throws Exception {
		KMMQualifSecCurrID secCurrPrs = KMMQualifSecCurrID.parse("SECURITY:E000003");
		KMMQualifSecCurrID secCurrRef = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000003");

		assertEquals(KMMQualifSecCurrID.Type.SECURITY, secCurrPrs.getType());
		assertEquals("SECURITY:E000003", secCurrPrs.toString());
		assertEquals(secCurrRef.toString(), secCurrPrs.toString());
		assertEquals(secCurrRef, secCurrPrs);

//      // ---
//      
//      secCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      secCurrRef = new SecCurrID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", secCurrPrs.toString());
//      assertEquals(secCurrRef, secCurrPrs);
	}

	@Test
	public void test04_3() throws Exception {
		try {
			KMMQualifSecCurrID secCurrPrs = KMMQualifSecCurrID.parse("FUXNSTUELL:BURP"); // Wrong, but not check on
			// this level
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

//      // ---
//      
//      secCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      secCurrRef = new SecID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", secCurrPrs.toString());
//      assertEquals(secCurrRef, secCurrPrs);
	}

	@Test
	public void test05() throws Exception {
		KMMQualifSecCurrID secCurr1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.CURRENCY, "EUR");
		KMMQualifSecCurrID secCurr2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
		KMMQualifSecCurrID curr1    = new KMMQualifCurrID("EUR");
		KMMQualifSecCurrID sec2     = new KMMQualifSecID("E000001");

		assertNotEquals(secCurr1, curr1); // sic
		assertEquals(secCurr1.toString(), curr1.toString());
		assertNotEquals(secCurr1.toStringLong(), curr1.toString()); // sic
		
		assertNotEquals(secCurr2, sec2); // sic
		assertEquals(secCurr2.toString(), sec2.toString());
		assertNotEquals(secCurr2.toStringLong(), sec2.toString()); // sic
	}

}
