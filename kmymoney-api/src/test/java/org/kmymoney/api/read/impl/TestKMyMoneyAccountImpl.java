package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;

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
  
  private static final String ACCT_1_ID = "A000004"; // Asset::Girokonto
  private static final String ACCT_2_ID = "A000062"; // Asset::Finanzanlagen::Depot RaiBa
  private static final String ACCT_3_ID = "5008258df86243ee86d37dee64327c27"; // Root Account::Fremdkapital
  private static final String ACCT_4_ID = "68a4c19f9a8c48909fc69d0dc18c37a6"; // Root Account::Fremdkapital::Lieferanten::Lieferfanto
  private static final String ACCT_5_ID = "7e223ee2260d4ba28e8e9e19ce291f43"; // Root Account::Aktiva::Forderungen::Unfug_Quatsch
  private static final String ACCT_6_ID = "ebc834e7f20e4be38f445d655142d6b1"; // Root Account::Anfangsbestand
  private static final String ACCT_7_ID = "d49554f33a0340bdb6611a1ab5575998"; // Root Account::Aktiva::Depots::Depot RaiBa::DE0007100000 Mercedes-Benz

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
    acct = kmmFile.getAccountById(new KMMComplAcctID(ACCT_1_ID));
    
    assertEquals(ACCT_1_ID, acct.getId().toString());
    assertEquals(KMyMoneyAccount.Type.CHECKING, acct.getType());
    assertEquals("Giro RaiBa", acct.getName());
    assertEquals("Asset::Barvermögen::Giro RaiBa", acct.getQualifiedName());
    assertEquals("Girokonto 1", acct.getMemo());
    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
         
    assertEquals("A000002", acct.getParentAccountId().toString());
    
    assertEquals(7374.50, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(7374.50, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(5, acct.getTransactions().size());
    assertEquals("T000000000000000001", acct.getTransactions().get(0).getId().toString());
    assertEquals("T000000000000000002", acct.getTransactions().get(1).getId().toString());
  }

  @Test
  public void test01_2() throws Exception
  {
    acct = kmmFile.getAccountById(new KMMComplAcctID(ACCT_2_ID));
    
    assertEquals(ACCT_2_ID, acct.getId().toString());
    assertEquals(KMyMoneyAccount.Type.INVESTMENT, acct.getType());
    assertEquals("Depot RaiBa", acct.getName());
    assertEquals("Asset::Finanzanlagen::Depot RaiBa", acct.getQualifiedName());
    assertEquals("Aktiendepot 1", acct.getMemo());
    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
    
    assertEquals("A000061", acct.getParentAccountId().toString());

    // ::TODO
    assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(3773.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    // ::TODO
    assertEquals(0, acct.getTransactions().size());
//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getId());
//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getId());
  }

//  @Test
//  public void test01_3() throws Exception
//  {
//    acct = kmmFile.getAccountByID(ACCT_3_ID);
//    
//    assertEquals(ACCT_3_ID, acct.getId());
//    assertEquals(KMyMoneyAccount.Type.LIABILITY, acct.getType());
//    assertEquals("Fremdkapital", acct.getName());
//    assertEquals("Root Account::Fremdkapital", acct.getQualifiedName());
//    assertEquals("alle Verbindlichkeiten", acct.getMemo());
//    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
//    
//    assertEquals("14305dc80e034834b3f531696d81b493", acct.getParentAccountId());
//
//    assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    // ::CHECK: Should'nt the value in the following assert be positive
//    // (that's how it is displayed in GnuCacsh, after all, at least with
//    // standard settings).
//    assertEquals(-289.92, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
//
//    assertEquals(0, acct.getTransactions().size());
//  }

//  @Test
//  public void test01_4() throws Exception
//  {
//    acct = kmmFile.getAccountByID(ACCT_4_ID);
//    
//    assertEquals(ACCT_4_ID, acct.getId());
//    assertEquals(KMyMoneyAccount.Type.PAYABLE, acct.getType());
//    assertEquals("Lieferfanto", acct.getName());
//    assertEquals("Root Account::Fremdkapital::Lieferanten::Lieferfanto", acct.getQualifiedName());
//    assertEquals(null, acct.getMemo());
//    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
//    
//    assertEquals("a6d76c8d72764905adecd78d955d25c0", acct.getParentAccountId());
//
//    // ::TODO
//    assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
//
//    // ::TODO
//    assertEquals(2, acct.getTransactions().size());
//    assertEquals("aa64d862bb5e4d749eb41f198b28d73d", acct.getTransactions().get(0).getId());
//    assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(1).getId());
//  }
//  
//  @Test
//  public void test01_5() throws Exception
//  {
//    acct = kmmFile.getAccountByID(ACCT_5_ID);
//    
//    assertEquals(ACCT_5_ID, acct.getId());
//    assertEquals(KMyMoneyAccount.Type.RECEIVABLE, acct.getType());
//    assertEquals("Unfug_Quatsch", acct.getName());
//    assertEquals("Root Account::Aktiva::Forderungen::Unfug_Quatsch", acct.getQualifiedName());
//    assertEquals(null, acct.getMemo());
//    assertEquals("CURRENCY:EUR", acct.getSecCurrID().toString());
//    
//    assertEquals("74401ce4880c4f4487c4301027a71bde", acct.getParentAccountId());
//
//    assertEquals(709.95, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(709.95, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
//
//    assertEquals(4, acct.getTransactions().size());
//    assertEquals("c97032ba41684b2bb5d1391c9d7547e9", acct.getTransactions().get(0).getId());
//    assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getId());
//    assertEquals("9e066e5f3081485ab08539e41bf85495", acct.getTransactions().get(2).getId());
//    assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(3).getId());
//  }
//
//  @Test
//  public void test01_6() throws Exception
//  {
//    acct = kmmFile.getAccountByID(ACCT_6_ID);
//    
//    assertEquals(ACCT_6_ID, acct.getId());
//    assertEquals(KMyMoneyAccount.Type.EQUITY, acct.getType());
//    assertEquals("Anfangsbestand", acct.getName());
//    assertEquals("Root Account::Anfangsbestand", acct.getQualifiedName());
//    assertEquals("Anfangsbestand", acct.getDescription());
//    assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());
//    
//    assertEquals("14305dc80e034834b3f531696d81b493", acct.getParentAccountId());
//
//    assertEquals(-4128.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(-4128.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
//
//    assertEquals(2, acct.getTransactions().size());
//    assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(0).getId());
//    assertEquals("4307689faade47d8aab4db87c8ce3aaf", acct.getTransactions().get(1).getId());
//  }
//
//  @Test
//  public void test01_7() throws Exception
//  {
//    acct = kmmFile.getAccountByID(ACCT_7_ID);
//    
//    assertEquals(ACCT_7_ID, acct.getId());
//    assertEquals(KMyMoneyAccount.Type.STOCK, acct.getType());
//    assertEquals("DE0007100000 Mercedes-Benz", acct.getName());
//    assertEquals("Root Account::Aktiva::Depots::Depot RaiBa::DE0007100000 Mercedes-Benz", acct.getQualifiedName());
//    assertEquals("Mercedes-Benz Group AG", acct.getMemo());
//    assertEquals("EURONEXT:MBG", acct.getSecCurrID().toString());
//    
//    assertEquals(ACCT_2_ID, acct.getParentAccountId());
//
//    assertEquals(100.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(100.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
//
//    assertEquals(1, acct.getTransactions().size());
//    assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(0).getId());
//  }
}
