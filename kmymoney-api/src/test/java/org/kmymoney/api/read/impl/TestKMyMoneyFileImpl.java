package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.read.KMyMoneyFile;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyFileImpl {
    private KMyMoneyFile kmmFile = null;

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
	// System.err.println("GnuCash test file resource: '" + kmmFileURL + "'");
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
	    System.err.println("Cannot parse GnuCash file");
	    exc.printStackTrace();
	}
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	assertEquals(67, ((KMyMoneyFileStats) kmmFile).getNofEntriesAccountMap());
    }

    @Test
    public void test02() throws Exception {
	assertEquals(5, ((KMyMoneyFileStats) kmmFile).getNofEntriesTransactionMap());
    }

    @Test
    public void test03() throws Exception {
	assertEquals(12, ((KMyMoneyFileStats) kmmFile).getNofEntriesTransactionSplitMap());
    }

    @Test
    public void test04() throws Exception {
	assertEquals(5, ((KMyMoneyFileStats) kmmFile).getNofEntriesPayeeMap());
    }

    @Test
    public void test05() throws Exception {
	assertEquals(2, ((KMyMoneyFileStats) kmmFile).getNofEntriesSecurityMap());
    }

    @Test
    public void test06() throws Exception {
	assertEquals(2, ((KMyMoneyFileStats) kmmFile).getNofEntriesCurrencyMap());
    }

    @Test
    public void test07() throws Exception {
	assertEquals(5, ((KMyMoneyFileStats) kmmFile).getNofEntriesPriceMap());
    }

}
