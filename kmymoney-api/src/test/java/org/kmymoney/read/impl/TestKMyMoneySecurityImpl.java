package org.kmymoney.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.ConstTest;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneySecurity;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneySecurityImpl
{
  // Mercedes-Benz Group AG
  public static final String SEC_1_ID    = "MBG";   
  public static final String SEC_1_ISIN  = "DE0007100000";
  
  // SAP SE
  public static final String SEC_2_ID    = "SAP";   
  public static final String SEC_2_ISIN  = "DE0007164600";
    
  // AstraZeneca Plc
  // Note that in the SecIDType variants, the ISIN/CUSIP/SEDOL/WKN/whatever
  // is stored twice in the object, redundantly
  public static final String SEC_3_ID    = "GB0009895292";   
  public static final String SEC_3_ISIN  = SEC_3_ID;
    
  // -----------------------------------------------------------------
    
  private KMyMoneyFile      kmmFile = null;
  private KMyMoneySecurity sec = null;
  
  private KMMSecCurrID secCurrID1 = null;
  private KMMSecCurrID secCurrID2 = null;
  private KMMSecCurrID secCurrID3 = null;
  
  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMyMoneySecurityImpl.class);  
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
    
    // ---
    
    secCurrID1 = new KMMSecID(SEC_1_ID);
    secCurrID2 = new KMMSecID(SEC_2_ID);
    secCurrID3 = new KMMSecID(SEC_3_ID);
  }

  // -----------------------------------------------------------------
  
  @Test 
  public void test00() throws Exception
  {
      // Cf. TestCmdtyCurrID -- let's just double-check 
      assertEquals(KMMSecCurrID.Type.SECURITY.toString() + KMMSecCurrID.SEPARATOR + SEC_1_ID, secCurrID1.toString());
      assertEquals(KMMSecCurrID.Type.SECURITY.toString() + KMMSecCurrID.SEPARATOR + SEC_2_ID, secCurrID2.toString());
      assertEquals(KMMSecCurrID.Type.SECURITY.toString() + KMMSecCurrID.SEPARATOR + SEC_3_ID, secCurrID3.toString());
  }
  
  // ------------------------------

//  @Test
//  public void test01_1() throws Exception
//  {
//    sec = kmmFile.getSecurityByQualifID(SEC_1_EXCH, SEC_1_ID);
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID1.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_1_ISIN, sec.getXCode());
//    assertEquals("Mercedes-Benz Group AG", sec.getName());
//  }
//
//  @Test
//  public void test01_2() throws Exception
//  {
//    sec = kmmFile.getSecurityByQualifID(secCurrID1.toString());
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID1.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_1_ISIN, sec.getXCode());
//    assertEquals("Mercedes-Benz Group AG", sec.getName());
//  }
//
//  @Test
//  public void test01_3() throws Exception
//  {
//    sec = kmmFile.getSecurityByXCode(SEC_1_ISIN);
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID1.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_1_ISIN, sec.getXCode());
//    assertEquals("Mercedes-Benz Group AG", sec.getName());
//  }
//
//  @Test
//  public void test01_4() throws Exception
//  {
//    Collection<KMyMoneySecurity> secList = kmmFile.getSecuritiesByName("mercedes");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    
//    assertEquals(secCurrID1.toString(), 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	        ((KMyMoneySecurity) secList.toArray()[0]).getQualifId()); // not trivial!
//    assertEquals(SEC_1_ISIN, 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getXCode());
//    assertEquals("Mercedes-Benz Group AG", 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getName());
//
//    secList = kmmFile.getSecuritiesByName("BENZ");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    
//    secList = kmmFile.getSecuritiesByName(" MeRceDeS-bEnZ  ");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    assertEquals(secCurrID1.toString(), 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID1, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId()); // not trivial!
//  }
//
//  // ------------------------------
//
//  @Test
//  public void test02_1() throws Exception
//  {
//    sec = kmmFile.getSecurityByQualifID(SEC_3_SECIDTYPE.toString(), SEC_3_ID);
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID3.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_3_ISIN, sec.getXCode());
//    assertEquals("AstraZeneca Plc", sec.getName());
//  }
//
//  @Test
//  public void test02_2() throws Exception
//  {
//    sec = kmmFile.getSecurityByQualifID(secCurrID3.toString());
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID3.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_3_ISIN, sec.getXCode());
//    assertEquals("AstraZeneca Plc", sec.getName());
//  }
//
//  @Test
//  public void test02_3() throws Exception
//  {
//    sec = kmmFile.getSecurityBySymbol(SEC_3_ISIN);
//    assertNotEquals(null, sec);
//    
//    assertEquals(secCurrID3.toString(), sec.getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, sec.getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, sec.getQualifId()); // not trivial!
//    assertEquals(SEC_3_ISIN, sec.getSymbol());
//    assertEquals("AstraZeneca Plc", sec.getName());
//  }
//
//  @Test
//  public void test02_4() throws Exception
//  {
//    Collection<KMyMoneySecurity> secList = kmmFile.getSecuritiesByName("astra");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    
//    assertEquals(secCurrID3.toString(), 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	        ((KMyMoneySecurity) secList.toArray()[0]).getQualifId()); // not trivial!
//    assertEquals(SEC_3_ISIN, 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getXCode());
//    assertEquals("AstraZeneca Plc", 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getName());
//
//    secList = kmmFile.getSecuritiesByName("BENZ");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    
//    secList = kmmFile.getSecuritiesByName(" aStrAzENeCA  ");
//    assertNotEquals(null, secList);
//    assertEquals(1, secList.size());
//    assertEquals(secCurrID3.toString(), 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId().toString());
//    // *Not* equal because of class
//    assertNotEquals(secCurrID3, 
//	            ((KMyMoneySecurity) secList.toArray()[0]).getQualifId());
//    // ::TODO: Convert to CommodityID_Exchange, then it should be equal
////    assertEquals(secCurrID1, 
////	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifId()); // not trivial!
//  }
}
