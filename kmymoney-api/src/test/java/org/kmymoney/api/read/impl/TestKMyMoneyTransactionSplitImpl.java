package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyTransactionSplitImpl {
	public static final KMMComplAcctID ACCT_1_ID = TestKMyMoneyAccountImpl.ACCT_1_ID;
	public static final KMMComplAcctID ACCT_8_ID = TestKMyMoneyAccountImpl.ACCT_8_ID;

	public static final KMMTrxID TRX_1_ID = new KMMTrxID("T000000000000000001");
	public static final KMMTrxID TRX_2_ID = new KMMTrxID("T000000000000000018");

	public static final KMMQualifSpltID TRXSPLT_1_ID = new KMMQualifSpltID("T000000000000000001", "S0001");
	public static final KMMQualifSpltID TRXSPLT_2_ID = new KMMQualifSpltID("T000000000000000018", "S0003");

	// -----------------------------------------------------------------

	private KMyMoneyFile kmmFile = null;
	private KMyMoneyTransactionSplit splt = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyTransactionSplitImpl.class);
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
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		splt = kmmFile.getTransactionSplitByID(TRXSPLT_1_ID);

		assertEquals(TRXSPLT_1_ID, splt.getQualifID());
		assertEquals(TRX_1_ID, splt.getTransactionID());
		assertEquals(ACCT_1_ID, splt.getAccountID());
		assertEquals(null, splt.getAction());
		assertEquals(10000.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("10.000,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("10.000,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(10000.00, splt.getShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		// ::TODO: The following two do in fact not work:
		// they should display the Euro-sign
		// Cf. TestKMyMoneyWritableTransactionSplitImpl
		assertEquals("10.000", splt.getSharesFormatted());
		assertEquals("10.000", splt.getSharesFormattedForHTML());
		assertEquals("", splt.getMemo());
//		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	@Test
	public void test02() throws Exception {
		splt = kmmFile.getTransactionSplitByID(TRXSPLT_2_ID);

		assertEquals(TRXSPLT_2_ID, splt.getQualifID());
		assertEquals(TRX_2_ID, splt.getTransactionID());
		assertEquals(ACCT_8_ID, splt.getAccountID());
		assertEquals(KMyMoneyTransactionSplit.Action.BUY_SHARES, splt.getAction());
		assertEquals(1800.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.800,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("1.800,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(15.00, splt.getShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("15", splt.getSharesFormatted());
		assertEquals("15", splt.getSharesFormattedForHTML());
		assertEquals("", splt.getMemo());
//		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

}
