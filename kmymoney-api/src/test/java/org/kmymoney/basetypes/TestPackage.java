package org.kmymoney.basetypes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase
{
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static Test suite() throws Exception
  {
    TestSuite suite = new TestSuite();
    
    suite.addTest(org.kmymoney.basetypes.TestKMMSecCurrID.suite());
    suite.addTest(org.kmymoney.basetypes.TestKMMCurrID.suite());
    suite.addTest(org.kmymoney.basetypes.TestKMMSecID.suite());

    return suite;
  }
}
