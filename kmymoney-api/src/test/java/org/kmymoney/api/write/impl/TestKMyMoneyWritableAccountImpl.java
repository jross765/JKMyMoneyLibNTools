package org.kmymoney.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.TestKMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.write.KMyMoneyWritableAccount;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyWritableAccountImpl {
    public static final KMMComplAcctID ACCT_1_ID = TestKMyMoneyAccountImpl.ACCT_1_ID;
    public static final KMMComplAcctID ACCT_2_ID = TestKMyMoneyAccountImpl.ACCT_2_ID;
    public static final KMMComplAcctID ACCT_3_ID = TestKMyMoneyAccountImpl.ACCT_3_ID;
    public static final KMMComplAcctID ACCT_4_ID = TestKMyMoneyAccountImpl.ACCT_4_ID;
    
	// Top-level accounts
    public static final KMMComplAcctID ACCT_10_ID = TestKMyMoneyAccountImpl.ACCT_10_ID;
    public static final KMMComplAcctID ACCT_11_ID = TestKMyMoneyAccountImpl.ACCT_11_ID;
    public static final KMMComplAcctID ACCT_12_ID = TestKMyMoneyAccountImpl.ACCT_12_ID;
    public static final KMMComplAcctID ACCT_13_ID = TestKMyMoneyAccountImpl.ACCT_13_ID;
    public static final KMMComplAcctID ACCT_14_ID = TestKMyMoneyAccountImpl.ACCT_14_ID;

    // -----------------------------------------------------------------

    private KMyMoneyWritableFileImpl kmmInFile = null;
    private KMyMoneyFileImpl kmmOutFile = null;

    private KMMFileStats kmmInFileStats = null;
    private KMMFileStats kmmOutFileStats = null;

    private KMMAcctID newID = null;

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
	return new JUnit4TestAdapter(TestKMyMoneyWritableAccountImpl.class);
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
    }

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestKMyMoneyAccountImpl.test01_xyz
    //
    // Check whether the KMyMoneyWritableAccount objects returned by
    // KMyMoneyWritableFileImpl.getWritableAccountByID() are actually
    // complete (as complete as returned be KMyMoneyFileImpl.getAccountByID().

	@Test
	public void test01_1() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.CHECKING, acct.getType());
		assertEquals("Giro RaiBa", acct.getName());
		assertEquals("Asset:Barvermögen:Giro RaiBa", acct.getQualifiedName());
		assertEquals("Girokonto 1", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals("A000002", acct.getParentAccountID().toString());
		assertEquals(0, acct.getChildren().size());

		assertEquals(11674.50, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(11674.50, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(17, acct.getTransactions().size());
		assertEquals("T000000000000000001", acct.getTransactions().get(0).getID().toString());
		assertEquals("T000000000000000002", acct.getTransactions().get(1).getID().toString());
	}

	@Test
	public void test01_2() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_2_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.INVESTMENT, acct.getType());
		assertEquals("Depot RaiBa", acct.getName());
		assertEquals("Asset:Finanzanlagen:Depot RaiBa", acct.getQualifiedName());
		assertEquals("Aktiendepot 1", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals("A000061", acct.getParentAccountID().toString());
		assertEquals(2, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000064", ((KMyMoneyAccount) acctArr[0]).getID().toString());
		assertEquals("A000063", ((KMyMoneyAccount) acctArr[1]).getID().toString());

		// ::TODO
		assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3773.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		// ::TODO
		assertEquals(0, acct.getTransactions().size());
//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID());
//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getID());
	}

	@Test
	public void test01_3() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_3_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_3_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.INCOME, acct.getType());
		assertEquals("Gehalt", acct.getName());
		assertEquals("Income:Gehalt", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(ACCT_12_ID, acct.getParentAccountID());
		assertEquals(2, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000050", ((KMyMoneyAccount) acctArr[0]).getID().toString());
		assertEquals("A000051", ((KMyMoneyAccount) acctArr[1]).getID().toString());

		// ::CHECK: Really negative?
		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(-6500.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	@Test
	public void test01_4() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_4_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_4_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.STOCK, acct.getType());
		assertEquals("DE0007100000 Mercedes-Benz Group AG", acct.getName());
		assertEquals("Asset:Finanzanlagen:Depot RaiBa:DE0007100000 Mercedes-Benz Group AG", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("SECURITY:E000002", acct.getSecCurrID().toString());

		assertEquals(ACCT_2_ID, acct.getParentAccountID());
		assertEquals(0, acct.getChildren().size());

		assertEquals(17, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(17, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1980.50, acct.getBalance(LocalDate.now(), Currency.getInstance("EUR")).doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1980.50, acct.getBalanceRecursive(LocalDate.now(), Currency.getInstance("EUR")).doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1, acct.getTransactions().size());
	}

	// -----------------------------------------------------------------
	// Top-level accounts
	
	@Test
	public void test01_10() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_10_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_10_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.ASSET, acct.getType());
		assertEquals("Asset", acct.getName());
		assertEquals("Asset", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(null, acct.getParentAccountID());
		assertEquals(2, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000002", ((KMyMoneyAccount) acctArr[0]).getID().toString());
		assertEquals("A000061", ((KMyMoneyAccount) acctArr[1]).getID().toString());
		
		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(15597.50, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	@Test
	public void test01_11() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_11_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_11_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.LIABILITY, acct.getType());
		assertEquals("Liability", acct.getName());
		assertEquals("Liability", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(null, acct.getParentAccountID());
		assertEquals(1, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000058", ((KMyMoneyAccount) acctArr[0]).getID().toString());

		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	@Test
	public void test01_12() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_12_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_12_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.INCOME, acct.getType());
		assertEquals("Income", acct.getName());
		assertEquals("Income", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(null, acct.getParentAccountID());
		assertEquals(4, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000049", ((KMyMoneyAccount) acctArr[0]).getID().toString());
		assertEquals("A000052", ((KMyMoneyAccount) acctArr[1]).getID().toString());
		assertEquals("A000053", ((KMyMoneyAccount) acctArr[2]).getID().toString());
		assertEquals("A000054", ((KMyMoneyAccount) acctArr[3]).getID().toString());

		// ::CHECK: Really negative?
		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(-16500.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	@Test
	public void test01_13() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_13_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_13_ID, acct.getID());
		assertEquals(KMyMoneyAccount.Type.EXPENSE, acct.getType());
		assertEquals("Expense", acct.getName());
		assertEquals("Expense", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(null, acct.getParentAccountID());
		assertEquals(15, acct.getChildren().size());
		Object[] acctArr = acct.getChildren().toArray();
		assertEquals("A000006", ((KMyMoneyAccount) acctArr[0]).getID().toString());
		assertEquals("A000011", ((KMyMoneyAccount) acctArr[1]).getID().toString());
		assertEquals("A000012", ((KMyMoneyAccount) acctArr[2]).getID().toString());
		// etc.
		// assertEquals("A000xyz", ((KMyMoneyAccount) acctArr[3]).getID().toString());

		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(920.5, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	@Test
	public void test01_14() throws Exception {
		KMyMoneyWritableAccount acct = kmmInFile.getWritableAccountByID(ACCT_14_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_14_ID, acct.getID());
		assertEquals("AStd::Equity", acct.getID().toString());
		assertEquals("Equity", acct.getName());
		assertEquals("Equity", acct.getQualifiedName());
		assertEquals("", acct.getMemo());
		assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

		assertEquals(null, acct.getParentAccountID());
		assertEquals(0, acct.getChildren().size());

		assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the KMyMoneyWritableAccount objects returned by
    // can actually be modified -- both in memory and persisted in file.
	
	// ::TODO

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

	// ::TODO

}
