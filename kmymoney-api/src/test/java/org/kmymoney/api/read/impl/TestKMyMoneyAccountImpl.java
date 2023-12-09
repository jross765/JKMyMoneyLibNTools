package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyAccountImpl
{
  private KMyMoneyFile    kmmFile = null;
  private KMyMoneyAccount acct = null;
  
  private static final KMMComplAcctID ACCT_1_ID = new KMMComplAcctID("A000004"); // Asset::Girokonto
  private static final KMMComplAcctID ACCT_2_ID = new KMMComplAcctID("A000062"); // Asset::Finanzanlagen::Depot RaiBa
  private static final KMMComplAcctID ACCT_3_ID = new KMMComplAcctID("A000049"); // Root Account::Fremdkapital
//  private static final KMMComplAcctID ACCT_4_ID = new KMMComplAcctID("xyz"); // Root Account::Fremdkapital::Lieferanten::Lieferfanto
//  private static final KMMComplAcctID ACCT_5_ID = new KMMComplAcctID("xyz"); // Root Account::Aktiva::Forderungen::Unfug_Quatsch
//  private static final KMMComplAcctID ACCT_6_ID = new KMMComplAcctID("xyz"); // Root Account::Anfangsbestand
//  private static final KMMComplAcctID ACCT_7_ID = new KMMComplAcctID("xyz"); // Root Account::Aktiva::Depots::Depot RaiBa::DE0007100000 Mercedes-Benz

  // Top-level accounts
  private static final KMMComplAcctID ACCT_10_ID = KMMComplAcctID.get(KMMComplAcctID.Top.ASSET);
  private static final KMMComplAcctID ACCT_11_ID = KMMComplAcctID.get(KMMComplAcctID.Top.LIABILITY);
  private static final KMMComplAcctID ACCT_12_ID = KMMComplAcctID.get(KMMComplAcctID.Top.INCOME);
  private static final KMMComplAcctID ACCT_13_ID = KMMComplAcctID.get(KMMComplAcctID.Top.EXPENSE);
  private static final KMMComplAcctID ACCT_14_ID = KMMComplAcctID.get(KMMComplAcctID.Top.EQUITY);

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMyMoneyAccountImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL kmmFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("GnuCash test file resource: '" + kmmFileURL + "'");
    InputStream kmmFileStream = null;
    try 
    {
      kmmFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME);
    } 
    catch ( Exception exc ) 
    {
      System.err.println("Cannot generate input stream from resource");
      return;
    }
    
    try
    {
      kmmFile = new KMyMoneyFileImpl(kmmFileStream);
    }
    catch ( Exception exc )
    {
      System.err.println("Cannot parse GnuCash file");
      exc.printStackTrace();
    }
  }

  // -----------------------------------------------------------------

  @Test
  public void test01_1() throws Exception
  {
    acct = kmmFile.getAccountByID(ACCT_1_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_1_ID, acct.getID());
    assertEquals(KMyMoneyAccount.Type.CHECKING, acct.getType());
    assertEquals("Giro RaiBa", acct.getName());
    assertEquals("Asset::Barvermögen::Giro RaiBa", acct.getQualifiedName());
    assertEquals("Girokonto 1", acct.getMemo());
    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
         
    assertEquals("A000002", acct.getParentAccountID().toString());
    
    assertEquals(11674.50, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(11674.50, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(17, acct.getTransactions().size());
    assertEquals("T000000000000000001", acct.getTransactions().get(0).getID().toString());
    assertEquals("T000000000000000002", acct.getTransactions().get(1).getID().toString());
  }

  @Test
  public void test01_2() throws Exception
  {
    acct = kmmFile.getAccountByID(ACCT_2_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_2_ID, acct.getID());
    assertEquals(KMyMoneyAccount.Type.INVESTMENT, acct.getType());
    assertEquals("Depot RaiBa", acct.getName());
    assertEquals("Asset::Finanzanlagen::Depot RaiBa", acct.getQualifiedName());
    assertEquals("Aktiendepot 1", acct.getMemo());
    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
    
    assertEquals("A000061", acct.getParentAccountID().toString());

    // ::TODO
    assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(3773.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    // ::TODO
    assertEquals(0, acct.getTransactions().size());
//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID());
//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getID());
  }

  @Test
  public void test01_3() throws Exception
  {
    acct = kmmFile.getAccountByID(ACCT_3_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_3_ID, acct.getID());
    assertEquals(KMyMoneyAccount.Type.INCOME, acct.getType());
    assertEquals("Gehalt", acct.getName());
    assertEquals("Income::Gehalt", acct.getQualifiedName());
    assertEquals("", acct.getMemo());
    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
    
    assertEquals(ACCT_12_ID, acct.getParentAccountID());

    // ::CHECK: Really negative?
    assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(-6500.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    assertEquals(0, acct.getTransactions().size());
  }

  // -----------------------------------------------------------------
  // Top-level accounts

  @Test
  public void test01_10() throws Exception {
      acct = kmmFile.getAccountByID(ACCT_10_ID);
      assertNotEquals(null, acct);

      assertEquals(ACCT_10_ID, acct.getID());
      assertEquals(KMyMoneyAccount.Type.ASSET, acct.getType());
      assertEquals("Asset", acct.getName());
      assertEquals("Asset", acct.getQualifiedName());
      assertEquals("", acct.getMemo());
      assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

      assertEquals(null, acct.getParentAccountID());

      assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      assertEquals(15597.50, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

      assertEquals(0, acct.getTransactions().size());
  }

  @Test
  public void test01_11() throws Exception {
      acct = kmmFile.getAccountByID(ACCT_11_ID);
      assertNotEquals(null, acct);

      assertEquals(ACCT_11_ID, acct.getID());
      assertEquals(KMyMoneyAccount.Type.LIABILITY, acct.getType());
      assertEquals("Liability", acct.getName());
      assertEquals("Liability", acct.getQualifiedName());
      assertEquals("", acct.getMemo());
      assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

      assertEquals(null, acct.getParentAccountID());

      assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

      assertEquals(0, acct.getTransactions().size());
  }

  @Test
  public void test01_12() throws Exception {
      acct = kmmFile.getAccountByID(ACCT_12_ID);
      assertNotEquals(null, acct);

      assertEquals(ACCT_12_ID, acct.getID());
      assertEquals(KMyMoneyAccount.Type.INCOME, acct.getType());
      assertEquals("Income", acct.getName());
      assertEquals("Income", acct.getQualifiedName());
      assertEquals("", acct.getMemo());
      assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

      assertEquals(null, acct.getParentAccountID());

      // ::CHECK: Really negative?
      assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      assertEquals(-16500.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

      assertEquals(0, acct.getTransactions().size());
  }

  @Test
  public void test01_13() throws Exception {
      acct = kmmFile.getAccountByID(ACCT_13_ID);
      assertNotEquals(null, acct);

      assertEquals(ACCT_13_ID, acct.getID());
      assertEquals(KMyMoneyAccount.Type.EXPENSE, acct.getType());
      assertEquals("Expense", acct.getName());
      assertEquals("Expense", acct.getQualifiedName());
      assertEquals("", acct.getMemo());
      assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

      assertEquals(null, acct.getParentAccountID());

      assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      assertEquals(920.5, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

      assertEquals(0, acct.getTransactions().size());
  }

  @Test
  public void test01_14() throws Exception {
      acct = kmmFile.getAccountByID(ACCT_14_ID);
      assertNotEquals(null, acct);

      assertEquals(ACCT_14_ID, acct.getID());
      assertEquals("AStd::Equity", acct.getID().toString());
      assertEquals("Equity", acct.getName());
      assertEquals("Equity", acct.getQualifiedName());
      assertEquals("", acct.getMemo());
      assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());

      assertEquals(null, acct.getParentAccountID());

      assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

      assertEquals(0, acct.getTransactions().size());
  }
  
}
