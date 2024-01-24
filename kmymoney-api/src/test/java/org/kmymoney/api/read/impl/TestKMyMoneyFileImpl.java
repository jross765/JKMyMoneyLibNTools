package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.read.impl.aux.KMMFileStats;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyFileImpl {
	private KMyMoneyFileImpl kmmFile = null;
	private KMMFileStats kmmFileStats = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyFileImpl.class);
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

		kmmFileStats = new KMMFileStats(kmmFile);
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT, kmmFileStats.getNofEntriesAccounts(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, kmmFileStats.getNofEntriesAccounts(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, kmmFileStats.getNofEntriesAccounts(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test02() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX, kmmFileStats.getNofEntriesTransactions(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TRX, kmmFileStats.getNofEntriesTransactions(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX, kmmFileStats.getNofEntriesTransactions(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test03() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, kmmFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.RAW));
		// This one is an exception:
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		// kmmFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
				kmmFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test04() throws Exception {
		assertEquals(ConstTest.Stats.NOF_PYE, kmmFileStats.getNofEntriesPayees(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PYE, kmmFileStats.getNofEntriesPayees(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PYE, kmmFileStats.getNofEntriesPayees(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test05() throws Exception {
		assertEquals(ConstTest.Stats.NOF_SEC, kmmFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test06() throws Exception {
		assertEquals(ConstTest.Stats.NOF_CURR, kmmFileStats.getNofEntriesCurrencies(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_CURR, kmmFileStats.getNofEntriesCurrencies(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_CURR, kmmFileStats.getNofEntriesCurrencies(KMMFileStats.Type.CACHE));
	}

	@Test
	public void test07() throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, kmmFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
		// This one is an exception:
		// assertEquals(ConstTest.Stats.NOF_PRC,
		// kmmFileStats.getNofEntriesPrices(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, kmmFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));
	}

}
