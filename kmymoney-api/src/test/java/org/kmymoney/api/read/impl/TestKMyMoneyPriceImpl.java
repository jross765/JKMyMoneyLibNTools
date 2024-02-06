package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneySecurity;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyPriceImpl {
	public static final KMMPriceID PRC_1_ID = new KMMPriceID("E000001", "EUR", "2023-11-03"); // SAP/EUR
	public static final KMMPriceID PRC_2_ID = new KMMPriceID("E000002", "EUR", "2023-11-01"); // MBG/EUR
	public static final KMMPriceID PRC_3_ID = new KMMPriceID("USD", "EUR", "2023-12-04");

	// -----------------------------------------------------------------

	private KMyMoneyFile kmmFile = null;
	private KMyMoneyPrice prc = null;

	KMMQualifSecCurrID secID1 = null;
	KMMQualifSecCurrID secID2 = null;

	KMMQualifCurrID currID1 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyPriceImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		InputStream kmmFileStream = null;
		try {
			kmmFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmFile = new KMyMoneyFileImpl(kmmFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney file");
			exc.printStackTrace();
		}

		// ---

		secID1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
		secID2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000002");

		currID1 = new KMMQualifCurrID("USD");
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		Collection<KMyMoneyPrice> priceList = kmmFile.getPrices();

		assertEquals(5, priceList.size());

		// ::TODO: Sort array for predictability
//      Object[] priceArr = priceList.toArray();
//      
//      assertEquals(PRICE_1_ID, ((KMMPrice) priceArr[0]).getID());
//      assertEquals(PRICE_2_ID, ((KMMPrice) priceArr[1]).getID());
//      assertEquals(PRICE_3_ID, ((KMMPrice) priceArr[2]).getID());
	}

	@Test
	public void test01_1() throws Exception {
		prc = kmmFile.getPriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID());
		assertEquals(secID1.toString(), prc.getFromSecCurrQualifID().toString());
		assertEquals(secID1.toString(), prc.getFromSecurityQualifID().toString());
		assertEquals(secID1.getCode().toString(), prc.getFromSecurityQualifID().getSecID().toString());
		assertNotEquals(secID1.getCode(), prc.getFromSecurityQualifID().getSecID()); // sic
		assertNotEquals(secID1, prc.getFromSecurityQualifID()); // sic
		assertEquals("SAP AG", prc.getFromSecurity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals("Transaction", prc.getSource());
		assertEquals(LocalDate.of(2023, 11, 3), prc.getDate());
		assertEquals(120.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			KMMQualifCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prc.getFromCurrencyCode(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			KMyMoneyCurrency dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_2() throws Exception {
		prc = kmmFile.getPriceByID(PRC_2_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_2_ID, prc.getID());
		assertEquals(secID2.toString(), prc.getFromSecCurrQualifID().toString());
		assertEquals(secID2.toString(), prc.getFromSecurityQualifID().toString());
		assertEquals(secID2.getCode().toString(), prc.getFromSecurityQualifID().getSecID().toString());
		assertNotEquals(secID2.getCode(), prc.getFromSecurityQualifID().getSecID()); // sic
		assertNotEquals(secID2, prc.getFromSecurityQualifID()); // sic
		assertEquals("Mercedes-Benz Group AG", prc.getFromSecurity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals("User", prc.getSource());
		assertEquals(LocalDate.of(2023, 11, 1), prc.getDate());
		assertEquals(116.5, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			KMMQualifCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prc.getFromCurrencyCode(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			KMyMoneyCurrency dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_3() throws Exception {
		prc = kmmFile.getPriceByID(PRC_3_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_3_ID, prc.getID());
		assertEquals(currID1.toString(), prc.getFromSecCurrQualifID().toString());
		assertEquals(currID1.toString(), prc.getFromCurrencyQualifID().toString());
		assertEquals("USD", prc.getFromCurrencyCode());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals("User", prc.getSource());
		assertEquals(LocalDate.of(2023, 12, 4), prc.getDate());
		assertEquals(0.92, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			KMMQualifSecID dummy = prc.getFromSecurityQualifID(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			KMyMoneySecurity dummy = prc.getFromSecurity(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

}
