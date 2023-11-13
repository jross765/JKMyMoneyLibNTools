package org.kmymoney.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.ConstTest;
import org.kmymoney.basetypes.simple.KMMTrxID;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyTransaction;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyTransactionImpl
{
  private KMyMoneyFile        kmmFile = null;
  private KMyMoneyTransaction trx = null;
  
  public static final String TRX_1_ID = "T000000000000000001";
  public static final String TRX_2_ID = "T000000000000000002";

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMyMoneyTransactionImpl.class);  
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
  public void test01() throws Exception
  {
    trx = kmmFile.getTransactionById(new KMMTrxID(TRX_1_ID));
    assertNotEquals(null, trx);
    
    assertEquals(TRX_1_ID, trx.getId().toString());
    assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals("", trx.getMemo());
    assertEquals("2023-01-01", trx.getDatePosted().toString());
    assertEquals("2023-11-03", trx.getEntryDate().toString());
        
    assertEquals(2, trx.getSplitsCount());
    assertEquals("S0001", trx.getSplits().get(0).getId());
    assertEquals("S0002", trx.getSplits().get(1).getId());
  }
  
  @Test
  public void test02() throws Exception
  {
    trx = kmmFile.getTransactionById(new KMMTrxID(TRX_2_ID));
    assertNotEquals(null, trx);
    
    assertEquals(TRX_2_ID, trx.getId().toString());
    assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals("", trx.getMemo());
    assertEquals("2023-01-03", trx.getDatePosted().toString());
    assertEquals("2023-10-14", trx.getEntryDate().toString());
        
    assertEquals(2, trx.getSplitsCount());
    assertEquals("S0001", trx.getSplits().get(0).getId());
    assertEquals("S0002", trx.getSplits().get(1).getId());
  }
}
