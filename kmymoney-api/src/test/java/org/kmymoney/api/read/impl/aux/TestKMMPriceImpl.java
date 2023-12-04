package org.kmymoney.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.aux.KMMPrice;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;

import junit.framework.JUnit4TestAdapter;

public class TestKMMPriceImpl
{
  private static final KMMPriceID PRICE_1_ID = new KMMPriceID("E000001", "EUR", "2023-11-03"); // SAP/EUR
  private static final KMMPriceID PRICE_2_ID = new KMMPriceID("E000002", "EUR", "2023-11-01"); // MBG/EUR

  // -----------------------------------------------------------------
  
  private KMyMoneyFile  kmmFile = null;
  private KMMPrice    prc = null;
  
  KMMQualifSecCurrID secID1 = null;
  KMMQualifSecCurrID secID2 = null;
  
  Currency currID1   = null;
  
  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMMPriceImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL kmmFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
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
      System.err.println("Cannot parse KMyMoney file");
      exc.printStackTrace();
    }
    
    // ---
    
    secID1 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000001");
    secID2 = new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, "E000002");
    
    currID1   = Currency.getInstance("USD");
  }

  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {
      Collection<KMMPrice> priceList = kmmFile.getPrices();
      
      assertEquals(4, priceList.size());

      // ::TODO: Sort array for predictability
//      Object[] priceArr = priceList.toArray();
//      
//      assertEquals(PRICE_1_ID, ((KMMPrice) priceArr[0]).getId());
//      assertEquals(PRICE_2_ID, ((KMMPrice) priceArr[1]).getId());
//      assertEquals(PRICE_3_ID, ((KMMPrice) priceArr[2]).getId());
  }

  @Test
  public void test02_1() throws Exception
  {
      prc = kmmFile.getPriceById(PRICE_1_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRICE_1_ID, prc.getId());
      assertEquals(secID1.toString(), prc.getFromSecCurrQualifId().toString());
      assertEquals(secID1.toString(), prc.getFromSecurityQualifId().toString());
      assertEquals(secID1.getCode().toString(), prc.getFromSecurityQualifId().getSecID().toString());
      assertNotEquals(secID1.getCode(), prc.getFromSecurityQualifId().getSecID()); // sic
      assertNotEquals(secID1, prc.getFromSecurityQualifId()); // sic
      assertEquals("SAP AG", prc.getFromSecurity().getName());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifId().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals("Transaction", prc.getSource());
      assertEquals(LocalDate.of(2023, 11, 3), prc.getDate());
      assertEquals(120.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      try
      {
	  KMMQualifCurrID dummy = prc.getFromCurrencyQualifId(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      try
      {
	  String dummy = prc.getFromCurrencyCode(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      try
      {
	  KMyMoneyCurrency dummy = prc.getFromCurrency(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
  }

  @Test
  public void test02_2() throws Exception
  {
      prc = kmmFile.getPriceById(PRICE_2_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRICE_2_ID, prc.getId());
      assertEquals(secID2.toString(), prc.getFromSecCurrQualifId().toString());
      assertEquals(secID2.toString(), prc.getFromSecurityQualifId().toString());
      assertEquals(secID2.getCode().toString(), prc.getFromSecurityQualifId().getSecID().toString());
      assertNotEquals(secID2.getCode(), prc.getFromSecurityQualifId().getSecID()); // sic
      assertNotEquals(secID2, prc.getFromSecurityQualifId()); // sic
      assertEquals("Mercedes-Benz Group AG", prc.getFromSecurity().getName());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifId().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals("User", prc.getSource());
      assertEquals(LocalDate.of(2023, 11, 1), prc.getDate());
      assertEquals(116.5, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
      try
      {
	  KMMQualifCurrID dummy = prc.getFromCurrencyQualifId(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      try
      {
	  String dummy = prc.getFromCurrencyCode(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      try
      {
	  KMyMoneyCurrency dummy = prc.getFromCurrency(); // illegal call in this context
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
  }

}
