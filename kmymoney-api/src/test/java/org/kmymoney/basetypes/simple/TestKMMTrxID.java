package org.kmymoney.basetypes.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMTrxID {

    private static KMMTrxID kmmID = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestKMMTrxID.class);
    }

    @Before
    public void initialize() throws Exception {
	kmmID = new KMMTrxID();
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	kmmID.set(1);
	assertEquals("T000000000000000001", kmmID.get());

	kmmID.set(123);
	assertEquals("T000000000000000123", kmmID.get());
    }

//  @Test
//  public void test02() throws Exception
//  {
//    try
//    {
//      kmmID.set(-12);
//      assertEquals(2, 1);
//    }
//    catch ( Exception InvalidKMMTrxIDException )
//    {
//      // Muss Exception werfen, wenn er hier landet, ist es richtig
//      assertEquals(1, 1);
//    }
//
//    kmmID.set(999999999999999999);
//    assertEquals("T999999999999999999", kmmID.get());
//
//    try
//    {
//      kmmID.set(1000000);
//      assertEquals(2, 1);
//    }
//    catch ( Exception InvalidKMMIDException )
//    {
//      // Muss Exception werfen, wenn er hier landet, ist es richtig
//      assertEquals(1, 1);
//    }
//  }
}
