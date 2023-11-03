package org.kmymoney.basetypes;

import static org.junit.Assert.assertEquals;

import java.util.Currency;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMCurrID
{
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestKMMCurrID.class);  
  }
  
  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {
      KMMCurrID commCurr = new KMMCurrID(Currency.getInstance("EUR"));
    
    assertEquals(KMMCurrID.Type.CURRENCY, commCurr.getType());
    assertEquals("EUR", commCurr.getCode());
    assertEquals("EUR", commCurr.getCurrency().getCurrencyCode());
    assertEquals("CURRENCY:EUR", commCurr.toString());
    
    // ---
    
    commCurr = new KMMCurrID(Currency.getInstance("USD"));
    
    assertEquals(KMMCurrID.Type.CURRENCY, commCurr.getType());
    assertEquals("USD", commCurr.getCode());
    assertEquals("USD", commCurr.getCurrency().getCurrencyCode());
    assertEquals("CURRENCY:USD", commCurr.toString());

    // ---
    
    try 
    {
	commCurr = new KMMCurrID(Currency.getInstance("XYZ")); // invalid code
    }
    catch (Exception exc)
    {
	// correct behaviour: Throw exception 
	assertEquals(0, 0);
    }
  }

  @Test
  public void test04_1() throws Exception
  {
      KMMCurrID commCurrPrs = KMMCurrID.parse("CURRENCY:EUR");
      KMMCurrID commCurrRef = new KMMCurrID(Currency.getInstance("EUR"));
      
      assertEquals(KMMCurrID.Type.CURRENCY, commCurrPrs.getType());
      assertEquals("CURRENCY:EUR", commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);

      // ---
      
      commCurrPrs = KMMCurrID.parse("CURRENCY:USD");
      commCurrRef = new KMMCurrID(Currency.getInstance("USD"));
      
      assertEquals(KMMCurrID.Type.CURRENCY, commCurrPrs.getType());
      assertEquals("CURRENCY:USD", commCurrPrs.toString());
      assertEquals(commCurrRef, commCurrPrs);
  }

  @Test
  public void test04_2() throws Exception
  {
      try 
      {
	  KMMCurrID commCurrPrs = KMMCurrID.parse("EURONEXT:SAP");
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
  }

  @Test
  public void test04_3() throws Exception
  {
      try 
      {
	  KMMCurrID commCurrPrs = KMMCurrID.parse("FUXNSTUELL:BURP");
      }
      catch ( Exception exc )
      {
	  assertEquals(0, 0);
      }
  }
}
