package org.kmymoney;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite();

	suite.addTest(org.kmymoney.basetypes.TestPackage.suite());
	suite.addTest(org.kmymoney.currency.TestPackage.suite());
	suite.addTest(org.kmymoney.read.TestPackage.suite());
	// ::TODO
//    suite.addTest(org.kmymoney.write.TestPackage.suite());

	return suite;
    }
}
