package org.kmymoney.api.basetypes.simple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite();

	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMAcctID.suite());
	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMInstID.suite());
	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMPyeID.suite());
	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMSecID.suite());
	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMSpltID.suite());
	suite.addTest(org.kmymoney.api.basetypes.simple.TestKMMTrxID.suite());

	return suite;
    }
}
