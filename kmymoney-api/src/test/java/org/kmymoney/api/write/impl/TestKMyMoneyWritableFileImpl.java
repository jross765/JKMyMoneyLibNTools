package org.kmymoney.api.write.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyWritableFileImpl {
    private KMyMoneyWritableFileImpl kmmInFile = null;
    private KMyMoneyFileImpl kmmOutFile = null;

    private KMMFileStats kmmInFileStats = null;

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
		return new JUnit4TestAdapter(TestKMyMoneyWritableFileImpl.class);
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

		kmmInFileStats = new KMMFileStats(kmmInFile);
	}

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestKMyMoneyFile.test01/02
    //
    // Check whether the KMyMoneyWritableFile objects returned by
    // KMyMoneyWritableFileImpl.getWritableFileByID() are actually
    // complete (as complete as returned be KMyMoneyFileImpl.getFileByID().

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	assertEquals(ConstTest.Stats.NOF_ACCT, kmmInFileStats.getNofEntriesAccounts(KMMFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_ACCT, kmmInFileStats.getNofEntriesAccounts(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_ACCT, kmmInFileStats.getNofEntriesAccounts(KMMFileStats.Type.CACHE));
    }

    @Test
    public void test02() throws Exception {
	assertEquals(ConstTest.Stats.NOF_TRX, kmmInFileStats.getNofEntriesTransactions(KMMFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_TRX, kmmInFileStats.getNofEntriesTransactions(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_TRX, kmmInFileStats.getNofEntriesTransactions(KMMFileStats.Type.CACHE));
    }

    @Test
    public void test03() throws Exception {
	assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		kmmInFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.RAW));
	// This one is an exception:
	// assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
	// kmmInFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		kmmInFileStats.getNofEntriesTransactionSplits(KMMFileStats.Type.CACHE));
    }

    // ------------------------------
    @Test
    public void test04() throws Exception {
	assertEquals(ConstTest.Stats.NOF_PYE, kmmInFileStats.getNofEntriesPayees(KMMFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_PYE, kmmInFileStats.getNofEntriesPayees(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_PYE, kmmInFileStats.getNofEntriesPayees(KMMFileStats.Type.CACHE));
    }

    // ------------------------------

    @Test
    public void test05() throws Exception {
	// CAUTION: This one is an exception:
	// There is one additional security object on the "raw" level:
	// the "template".
	assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));
    }

    @Test
    public void test06() throws Exception {
	assertEquals(ConstTest.Stats.NOF_PRCPR, kmmInFileStats.getNofEntriesPricePairs(KMMFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_PRCPR, kmmInFileStats.getNofEntriesPricePairs(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_PRCPR, kmmInFileStats.getNofEntriesPricePairs(KMMFileStats.Type.CACHE));
    }

    @Test
    public void test07() throws Exception {
	assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.RAW));
	// n/a:
	// assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPricePairs(KMMFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_PRC, kmmInFileStats.getNofEntriesPrices(KMMFileStats.Type.CACHE));
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the KMyMoneyWritableFile objects returned by
    // can actually be modified -- both in memory and persisted in file.

    // ::TODO

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ::TODO

}
