package org.kmymoney.basetypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMSecCurrID
{
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMMSecCurrID.class);  
  }
  
  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {
    KMMSecCurrID commCurr = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "EUR");
    
    assertEquals(KMMSecCurrID.Type.CURRENCY, commCurr.getType());
    assertEquals("EUR", commCurr.getCode());
    assertEquals("CURRENCY:EUR", commCurr.toString());
    
    // ---
    
    commCurr = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "USD");
    
    assertEquals(KMMSecCurrID.Type.CURRENCY, commCurr.getType());
    assertEquals("USD", commCurr.getCode());
    assertEquals("CURRENCY:USD", commCurr.toString());

    // ---
    
    commCurr = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "XYZ"); // Wrong, but no check on this level
    
    assertEquals(KMMSecCurrID.Type.CURRENCY, commCurr.getType());
    assertEquals("XYZ", commCurr.getCode());
    assertEquals("CURRENCY:XYZ", commCurr.toString());

  }

  @Test
  public void test02() throws Exception
  {
    KMMSecCurrID commCurr = new KMMSecCurrID(KMMSecCurrID.Type.SECURITY, "MBG");
    
    assertEquals(KMMSecCurrID.Type.SECURITY, commCurr.getType());
    assertEquals("MBG", commCurr.getCode());
    assertEquals("SECURITY:MBG", commCurr.toString());
  }

  @Test
  public void test03() throws Exception
  {
    KMMSecCurrID commCurr1  = new KMMSecCurrID(KMMSecCurrID.Type.SECURITY, "MBG");
    KMMSecCurrID commCurr2 = new KMMSecCurrID(KMMSecCurrID.Type.SECURITY, "MBG");
  
    assertEquals(commCurr1.toString(), commCurr2.toString());
    assertEquals(commCurr1, commCurr2);
      
    // ---

    KMMSecCurrID commCurr3 = new KMMSecCurrID(KMMSecCurrID.Type.SECURITY, "DIS");
    
    assertNotEquals(commCurr1, commCurr3);
    
    // ---

    KMMSecCurrID commCurr4 = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "EUR");
    KMMSecCurrID commCurr5 = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "EUR");
  
    assertEquals(commCurr4, commCurr5);
    assertNotEquals(commCurr1, commCurr4);
    assertNotEquals(commCurr2, commCurr4);
    assertNotEquals(commCurr3, commCurr4);
    assertNotEquals(commCurr3, commCurr4);
    
    KMMSecCurrID commCurr6 = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "JPY");
    
    assertNotEquals(commCurr4, commCurr6);
  }
  
  @Test
  public void test04_1() throws Exception
  {
      KMMSecCurrID commCurrPrs = KMMSecCurrID.parse("CURRENCY:EUR");
      KMMSecCurrID commCurrRef = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "EUR");
      
      assertEquals(KMMSecCurrID.Type.CURRENCY, commCurrPrs.getType());
      assertEquals("CURRENCY:EUR", commCurrPrs.toString());
      assertEquals(commCurrRef.toString(), commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);

      // ---
      
      commCurrPrs = KMMSecCurrID.parse("CURRENCY:USD");
      commCurrRef = new KMMSecCurrID(KMMSecCurrID.Type.CURRENCY, "USD");
      
      assertEquals(KMMSecCurrID.Type.CURRENCY, commCurrPrs.getType());
      assertEquals("CURRENCY:USD", commCurrPrs.toString());
      assertEquals(commCurrRef.toString(), commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);
  }

  @Test
  public void test04_2() throws Exception
  {
      KMMSecCurrID commCurrPrs = KMMSecCurrID.parse("SECURITY:SAP");
      KMMSecCurrID commCurrRef = new KMMSecCurrID(KMMSecCurrID.Type.SECURITY, "SAP");
      
      assertEquals(KMMSecCurrID.Type.SECURITY, commCurrPrs.getType());
      assertEquals("SECURITY:SAP", commCurrPrs.toString());
      assertEquals(commCurrRef.toString(), commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);

//      // ---
//      
//      commCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      commCurrRef = new SecCurrID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", commCurrPrs.toString());
//      assertEquals(commCurrRef, commCurrPrs);
  }

  @Test
  public void test04_3() throws Exception
  {
      try
      {
	  KMMSecCurrID commCurrPrs = KMMSecCurrID.parse("FUXNSTUELL:BURP"); // Wrong, but not check on this level
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }

//      // ---
//      
//      commCurrPrs = SecCurrID.parse("CURRENCY:USD");
//      commCurrRef = new SecID(Currency.getInstance("USD"));
//      
//      assertEquals("CURRENCY:USD", commCurrPrs.toString());
//      assertEquals(commCurrRef, commCurrPrs);
  }
}
