package org.kmymoney.basetypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMSecID
{
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMMSecID.class);  
  }
  
  // -----------------------------------------------------------------

//  @Test
//  public void test01() throws Exception
//  {
//    try 
//    {
//	KMMSecID commCurr = new KMMSecID(KMMSecCurrID.Type.CURRENCY, "EUR");
//    }
//    catch ( Exception exc ) 
//    {
//	assertEquals(0, 0);
//    }
//  }

  @Test
  public void test02() throws Exception
  {
    KMMSecID commCurr = new KMMSecID("MBG");
    
    assertEquals(KMMSecCurrID.Type.SECURITY, commCurr.getType());
    assertEquals("MBG", commCurr.getCode());
    assertEquals("SECURITY:MBG", commCurr.toString());

    commCurr = new KMMSecID("DE0007100000");
    
    assertEquals(KMMSecCurrID.Type.SECURITY, commCurr.getType());
    assertEquals("DE0007100000", commCurr.getCode());
    assertEquals("SECURITY:DE0007100000", commCurr.toString());
  }

  @Test
  public void test03() throws Exception
  {
    KMMSecID commCurr1  = new KMMSecID("MBG");
    KMMSecID commCurr2 = new KMMSecID("MBG");
  
    assertEquals(commCurr1.toString(), commCurr2.toString());
    assertEquals(commCurr1, commCurr2);
      
    // ---

    KMMSecID commCurr3 = new KMMSecID("DIS");
    
    assertNotEquals(commCurr1, commCurr3);
    
    // ---

    KMMCurrID commCurr4 = new KMMCurrID("EUR");
  
    assertNotEquals(commCurr1, commCurr4);
    assertNotEquals(commCurr2, commCurr4);
    assertNotEquals(commCurr3, commCurr4);
    assertNotEquals(commCurr3, commCurr4);
    
    KMMCurrID commCurr6 = new KMMCurrID("JPY");
    
    assertNotEquals(commCurr4, commCurr6);
  }
  
  @Test
  public void test04_1() throws Exception
  {
      try 
      {
	  KMMSecID commCurrPrs = KMMSecID.parse("CURRENCY:EUR");
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      // ---
      
      try 
      {
	  KMMSecID commCurrPrs = KMMSecID.parse("CURRENCY:USD");
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
  }

  @Test
  public void test04_2() throws Exception
  {
      KMMSecID commCurrPrs = KMMSecID.parse("SECURITY:SAP");
      KMMSecID commCurrRef = new KMMSecID("SAP");
      
      assertEquals(KMMSecCurrID.Type.SECURITY, commCurrPrs.getType());
      assertEquals("SECURITY:SAP", commCurrPrs.toString());
      assertEquals(commCurrRef.toString(), commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);

      // ---
      
      commCurrPrs = KMMSecID.parse("SECURITY:DE0007164600");
      commCurrRef = new KMMSecID("DE0007164600");
      
      assertEquals(KMMSecCurrID.Type.SECURITY, commCurrPrs.getType());
      assertEquals("SECURITY:DE0007164600", commCurrPrs.toString());
      assertEquals(commCurrRef.toString(), commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);
  }

  @Test
  public void test04_3() throws Exception
  {
      try
      {
	  KMMSecID commCurrPrs = KMMSecID.parse("CURRENCY:EUR"); // Wrong
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
      
      // ---

      try
      {
	  KMMSecID commCurrPrs = KMMSecID.parse("FUXNSTUELL:BURP"); // Wrong, but not check on this level
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
  }
}
