package org.kmymoney.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.TestKMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.TestKMyMoneyPricePairImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyWritablePricePairImpl {
    public static final KMMCurrPair PRCPR_1_ID = TestKMyMoneyPricePairImpl.PRCPR_1_ID;
    public static final KMMCurrPair PRCPR_2_ID = TestKMyMoneyPricePairImpl.PRCPR_2_ID;
    public static final KMMCurrPair PRCPR_3_ID = TestKMyMoneyPricePairImpl.PRCPR_3_ID;

    // -----------------------------------------------------------------

    private KMyMoneyWritableFileImpl kmmInFile = null;
    private KMyMoneyFileImpl kmmOutFile = null;

    private KMMFileStats kmmInFileStats = null;
    private KMMFileStats kmmOutFileStats = null;

	KMMQualifSecCurrID secID1 = null;
	KMMQualifSecCurrID secID2 = null;

	KMMQualifCurrID currID1 = null;

	private KMMCurrPair newID = null;

    // https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
    @SuppressWarnings("exports")
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    // -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyWritablePricePairImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		InputStream kmmInFileStream = null;
		try {
			kmmInFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmInFile = new KMyMoneyWritableFileImpl(kmmInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney in-file");
			exc.printStackTrace();
		}

		// ---

		secID1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
		secID2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000002");

		currID1 = new KMMQualifCurrID("USD");
	}

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestKMyMoneyPricePairImpl.test01_xyz
    //
    // Check whether the KMyMoneyWritablePricePair objects returned by
    // KMyMoneyWritableFileImpl.getWritablePricePairByID() are actually
    // complete (as complete as returned be KMyMoneyFileImpl.getPricePairByID().

	@Test
	public void test01_1() throws Exception {
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(TestKMyMoneyPriceImpl.PRC_1_ID);
		assertNotEquals(null, prc);
		KMyMoneyWritablePricePair prcPr = prc.getWritableParentPricePair();
		assertNotEquals(null, prcPr);

		assertEquals(PRCPR_1_ID, prcPr.getID());
		assertEquals(secID1.toString(), prcPr.getFromSecCurrQualifID().toString());
		assertEquals(secID1.toString(), prcPr.getFromSecurityQualifID().toString());
		assertEquals(secID1.getCode().toString(), prcPr.getFromSecurityQualifID().getSecID().toString());
		assertNotEquals(secID1.getCode(), prcPr.getFromSecurityQualifID().getSecID()); // sic
		assertNotEquals(secID1, prcPr.getFromSecurityQualifID()); // sic
		assertEquals("SAP AG", prcPr.getFromSecurity().getName());
		assertEquals("CURRENCY:EUR", prcPr.getToCurrencyQualifID().toString());
		assertEquals("EUR", prcPr.getToCurrencyCode());

		try {
			KMMQualifCurrID dummy = prcPr.getFromCurrencyQualifID(); // illegal call in this context
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
			KMyMoneyCurrency dummy = prcPr.getFromCurrency(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_2() throws Exception {
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(TestKMyMoneyPriceImpl.PRC_2_ID);
		assertNotEquals(null, prc);
		KMyMoneyWritablePricePair prcPr = prc.getWritableParentPricePair();
		assertNotEquals(null, prcPr);

		assertEquals(PRCPR_2_ID, prcPr.getID());
		assertEquals(secID2.toString(), prcPr.getFromSecCurrQualifID().toString());
		assertEquals(secID2.toString(), prcPr.getFromSecurityQualifID().toString());
		assertEquals(secID2.getCode().toString(), prcPr.getFromSecurityQualifID().getSecID().toString());
		assertNotEquals(secID2.getCode(), prcPr.getFromSecurityQualifID().getSecID()); // sic
		assertNotEquals(secID2, prcPr.getFromSecurityQualifID()); // sic
		assertEquals("Mercedes-Benz Group AG", prcPr.getFromSecurity().getName());
		assertEquals("CURRENCY:EUR", prcPr.getToCurrencyQualifID().toString());
		assertEquals("EUR", prcPr.getToCurrencyCode());

		try {
			KMMQualifCurrID dummy = prcPr.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prcPr.getFromCurrencyCode(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			KMyMoneyCurrency dummy = prcPr.getFromCurrency(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_3() throws Exception {
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(TestKMyMoneyPriceImpl.PRC_3_ID);
		assertNotEquals(null, prc);
		KMyMoneyWritablePricePair prcPr = prc.getWritableParentPricePair();
		assertNotEquals(null, prcPr);

		assertEquals(PRCPR_3_ID, prcPr.getID());
		assertEquals(currID1.toString(), prcPr.getFromSecCurrQualifID().toString());
		assertEquals(currID1.toString(), prcPr.getFromCurrencyQualifID().toString());
		assertEquals("USD", prcPr.getFromCurrencyCode());
		assertEquals("CURRENCY:EUR", prcPr.getToCurrencyQualifID().toString());
		assertEquals("EUR", prcPr.getToCurrencyCode());

		try {
			KMMQualifSecID dummy = prcPr.getFromSecurityQualifID(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			KMyMoneySecurity dummy = prcPr.getFromSecurity(); // illegal call in this context
			assertEquals(0, 1);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the KMyMoneyWritablePricePair objects returned by
    // can actually be modified -- both in memory and persisted in file.
	
	// ::TODO

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

	// ::TODO

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

	// ::TODO

}
