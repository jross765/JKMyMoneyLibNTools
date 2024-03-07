package org.kmymoney.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.ConstTest;
import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.basetypes.complex.KMMPriceID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPrice.Source;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.read.impl.TestKMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.TestKMyMoneyPricePairImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.read.impl.hlp.FileStats;
import org.kmymoney.api.write.KMyMoneyWritablePrice;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyWritablePriceImpl {
	private static final KMMPriceID PRC_1_ID = TestKMyMoneyPriceImpl.PRC_1_ID;
	private static final KMMPriceID PRC_2_ID = TestKMyMoneyPriceImpl.PRC_2_ID;
	private static final KMMPriceID PRC_3_ID = TestKMyMoneyPriceImpl.PRC_3_ID;
	private static final KMMPriceID PRC_4_ID = TestKMyMoneyPriceImpl.PRC_4_ID;
	private static final KMMPriceID PRC_5_ID = TestKMyMoneyPriceImpl.PRC_5_ID;

	private static final KMMPricePairID PRCPR_1_ID = TestKMyMoneyPricePairImpl.PRCPR_1_ID;
	private static final KMMPricePairID PRCPR_2_ID = TestKMyMoneyPricePairImpl.PRCPR_2_ID;
	private static final KMMPricePairID PRCPR_3_ID = TestKMyMoneyPricePairImpl.PRCPR_3_ID;

	// -----------------------------------------------------------------

    private KMyMoneyWritableFileImpl kmmInFile = null;
    private KMyMoneyFileImpl kmmOutFile = null;

    private KMMFileStats kmmInFileStats = null;
    private KMMFileStats kmmOutFileStats = null;

	KMMQualifSecCurrID secID1 = null;
	KMMQualifSecCurrID secID2 = null;

	KMMQualifCurrID currID1 = null;

	private KMMPriceID newID = null;

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
		return new JUnit4TestAdapter(TestKMyMoneyWritablePriceImpl.class);
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
		
		newID = new KMMPriceID("EUR", "EUR", "1970-01-01"); // dummy
	}

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestKMyMoneyPriceImpl.test01_xyz
    //
    // Check whether the KMyMoneyWritablePrice objects returned by
    // KMyMoneyWritableFileImpl.getWritablePriceByID() are actually
    // complete (as complete as returned be KMyMoneyFileImpl.getPriceByID().

	@Test
	public void test01() throws Exception {
		Collection<KMyMoneyWritablePrice> prcColl = kmmInFile.getWritablePrices();
		List<KMyMoneyWritablePrice> prcList = new ArrayList<KMyMoneyWritablePrice>(prcColl);
		prcList.sort(Comparator.naturalOrder());

		assertEquals(5, prcList.size());
		assertEquals(PRC_1_ID, prcList.get(0).getID());
		assertEquals(PRC_2_ID, prcList.get(1).getID());
		assertEquals(PRC_3_ID, prcList.get(2).getID());
		assertEquals(PRC_4_ID, prcList.get(3).getID());
		assertEquals(PRC_5_ID, prcList.get(4).getID());
	}

	@Test
	public void test01_1() throws Exception {
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(PRC_1_ID);
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
		assertEquals(Source.TRANSACTION, prc.getSource()); // unchanged
		assertEquals("Transaction", ((KMyMoneyWritablePriceImpl) prc).getSourceStr()); // unchanged
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
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(PRC_4_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_4_ID, prc.getID());
		assertEquals(secID2.toString(), prc.getFromSecCurrQualifID().toString());
		assertEquals(secID2.toString(), prc.getFromSecurityQualifID().toString());
		assertEquals(secID2.getCode().toString(), prc.getFromSecurityQualifID().getSecID().toString());
		assertNotEquals(secID2.getCode(), prc.getFromSecurityQualifID().getSecID()); // sic
		assertNotEquals(secID2, prc.getFromSecurityQualifID()); // sic
		assertEquals("Mercedes-Benz Group AG", prc.getFromSecurity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(Source.USER, prc.getSource());
		assertEquals("User", ((KMyMoneyWritablePriceImpl) prc).getSourceStr());
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
		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(PRC_5_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_5_ID, prc.getID());
		assertEquals(currID1.toString(), prc.getFromSecCurrQualifID().toString());
		assertEquals(currID1.toString(), prc.getFromCurrencyQualifID().toString());
		assertEquals("USD", prc.getFromCurrencyCode());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(Source.USER, prc.getSource());
		assertEquals("User", ((KMyMoneyWritablePriceImpl) prc).getSourceStr());
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

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the KMyMoneyWritablePrice objects returned by
    // can actually be modified -- both in memory and persisted in file.
	
	@Test
	public void test02_1() throws Exception {
		kmmInFileStats = new KMMFileStats(kmmInFile);

		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,         kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		KMyMoneyWritablePrice prc = kmmInFile.getWritablePriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID());

		// ----------------------------
		// Modify the object

		// CAUTION: No, not date, because that would change the "ID".
		// Cf. TestKMyMoneyWritablePricePair.
		// prc.setDate(LocalDate.of(1977, 7, 12));
		prc.setValue(new FixedPointNumber(123.71));
		prc.setSource(Source.USER);

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(prc);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
                          // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	@Test
	public void test02_2() throws Exception {
		// ::TODO
	}

	private void test02_1_check_memory(KMyMoneyWritablePrice prc) throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,         kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		assertEquals(PRC_1_ID, prc.getID()); // unchanged
		assertEquals(secID1.toString(), prc.getFromSecCurrQualifID().toString()); // unchanged
		assertEquals(secID1.toString(), prc.getFromSecurityQualifID().toString()); // unchanged
		assertEquals(secID1.getCode().toString(), prc.getFromSecurityQualifID().getSecID().toString()); // unchanged
		assertNotEquals(secID1.getCode(), prc.getFromSecurityQualifID().getSecID()); // unchanged
		assertNotEquals(secID1, prc.getFromSecurityQualifID()); // unchanged
		assertEquals("SAP AG", prc.getFromSecurity().getName()); // unchanged
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString()); // unchanged
		assertEquals("EUR", prc.getToCurrencyCode()); // unchanged
		assertEquals("User", ((KMyMoneyWritablePriceImpl) prc).getSourceStr()); // unchanged
		assertEquals(Source.USER, prc.getSource()); // unchanged
		assertEquals(LocalDate.of(2023, 11, 3), prc.getDate()); // unchanged
		assertEquals(123.71, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,         kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		KMyMoneyPrice prc = kmmOutFile.getPriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID());
		assertEquals(secID1.toString(), prc.getFromSecCurrQualifID().toString()); // unchanged
		assertEquals(secID1.toString(), prc.getFromSecurityQualifID().toString()); // unchanged
		assertEquals(secID1.getCode().toString(), prc.getFromSecurityQualifID().getSecID().toString()); // unchanged
		assertNotEquals(secID1.getCode(), prc.getFromSecurityQualifID().getSecID()); // unchanged
		assertNotEquals(secID1, prc.getFromSecurityQualifID()); // unchanged
		assertEquals("SAP AG", prc.getFromSecurity().getName()); // unchanged
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString()); // unchanged
		assertEquals("EUR", prc.getToCurrencyCode()); // unchanged
		assertEquals("User", ((KMyMoneyPriceImpl) prc).getSourceStr()); // changed
		assertEquals(Source.USER, prc.getSource()); // changed
		assertEquals(LocalDate.of(2023, 11, 3), prc.getDate()); // unchanged
		assertEquals(123.71, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

    @Test
	public void test03_1_1() throws Exception {
		kmmInFileStats = new KMMFileStats(kmmInFile);

		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,         kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		KMyMoneyPricePair prcPr = kmmInFile.getPricePairByID(PRCPR_1_ID);
		KMyMoneyWritablePrice prc = kmmInFile.createWritablePrice((KMyMoneyPricePairImpl) prcPr, LocalDate.of(1910, 5, 1));
		prc.setValue(new FixedPointNumber(345.21));
		prc.setSource(Source.USER);

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_1_check_memory(prc);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
						  // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test03_1_1_check_persisted(outFile);
	}

	private void test03_1_1_check_memory(KMyMoneyWritablePrice prc) throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC + 1, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,             kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		newID.set( prc.getID() );
		assertEquals(PRCPR_1_ID.getFromSecCurr().getCode(), newID.getFromSecCurr());
		assertEquals(PRCPR_1_ID.getToCurr().getCode(), newID.getToCurr());
		assertEquals("1910-05-01", newID.getDateStr());
		assertEquals(LocalDate.of(1910, 5, 1), newID.getDate());
		
		assertEquals(PRCPR_1_ID.getFromSecCurr(), prc.getFromSecCurrQualifID());
		assertEquals(PRCPR_1_ID.getToCurr(), prc.getToCurrencyQualifID());
		assertEquals("1910-05-01T00:00:00.000+01:00", prc.getDateStr()); // sic
		assertEquals(LocalDate.of(1910, 5, 1), prc.getDate());
		assertEquals(345.21, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("User", ((KMyMoneyWritablePriceImpl) prc).getSourceStr());
		assertEquals(Source.USER, prc.getSource());
	}

	private void test03_1_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		assertEquals(ConstTest.Stats.NOF_PRC + 1, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		assertEquals(FileStats.ERROR,             kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));

		KMyMoneyPrice prc = kmmOutFile.getPriceByID(newID);
		assertNotEquals(null, prc);

		assertEquals(newID, prc.getID());
		assertEquals(PRCPR_1_ID.getFromSecCurr().getCode(), newID.getFromSecCurr());
		assertEquals(PRCPR_1_ID.getToCurr().getCode(), newID.getToCurr());
		assertEquals("1910-05-01", newID.getDateStr());
		assertEquals(LocalDate.of(1910, 5, 1), newID.getDate());
		
		assertEquals(PRCPR_1_ID.getFromSecCurr(), prc.getFromSecCurrQualifID());
		assertEquals(PRCPR_1_ID.getToCurr(), prc.getToCurrencyQualifID());
		assertEquals("1910-05-01+01:00", prc.getDateStr()); // sic
		assertEquals(LocalDate.of(1910, 5, 1), prc.getDate());
		assertEquals(345.21, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("User", ((KMyMoneyPriceImpl) prc).getSourceStr());
		assertEquals(Source.USER, prc.getSource());
	}

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

	// ::TODO

}
