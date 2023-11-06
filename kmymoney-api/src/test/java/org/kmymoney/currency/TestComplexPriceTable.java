package org.kmymoney.currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.ConstTest;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

import junit.framework.JUnit4TestAdapter;

public class TestComplexPriceTable
{
  private KMyMoneyFile      kmmFile = null;
  private ComplexPriceTable complPriceTab = null;

  // -----------------------------------------------------------------
    
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestComplexPriceTable.class);  
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
    complPriceTab = kmmFile.getCurrencyTable();
    assertNotEquals(null, complPriceTab);
    
    assertEquals(2, complPriceTab.getNameSpaces().size());
    
    Object[] nameSpaceArr = complPriceTab.getNameSpaces().toArray();
    assertEquals(KMMSecCurrID.Type.CURRENCY, (KMMSecCurrID.Type) nameSpaceArr[0]);
    assertEquals(KMMSecCurrID.Type.SECURITY, (KMMSecCurrID.Type) nameSpaceArr[1]);
  }

  @Test
  public void test02() throws Exception
  {
    complPriceTab = kmmFile.getCurrencyTable();
    assertNotEquals(null, complPriceTab);
    
    assertEquals(120.0, 
	         complPriceTab.getConversionFactor(KMMSecCurrID.Type.SECURITY, "E000001").doubleValue(), 
	         ConstTest.DIFF_TOLERANCE);
    assertEquals(115.0, 
	         complPriceTab.getConversionFactor(KMMSecCurrID.Type.SECURITY, "E000002").doubleValue(), 
	         ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void test03_1() throws Exception
  {
    complPriceTab = kmmFile.getCurrencyTable();
    assertNotEquals(null, complPriceTab);
    
    FixedPointNumber val = new FixedPointNumber("101.0");
    assertEquals(true, complPriceTab.convertToBaseCurrency(val, new KMMSecID("E000001")));
    assertEquals(12120.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    val = new FixedPointNumber("101.0");
    assertEquals(true, complPriceTab.convertToBaseCurrency(val, new KMMSecID("E000002")));
    assertEquals(11615.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void test03_2() throws Exception
  {
    complPriceTab = kmmFile.getCurrencyTable();
    assertNotEquals(null, complPriceTab);
    
    FixedPointNumber val = new FixedPointNumber("12120.0");
    assertEquals(true, complPriceTab.convertFromBaseCurrency(val, new KMMSecID("E000001")));
    assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

    val = new FixedPointNumber("11615.0");
    assertEquals(true, complPriceTab.convertFromBaseCurrency(val, new KMMSecID("E000002")));
    assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
  }
}
