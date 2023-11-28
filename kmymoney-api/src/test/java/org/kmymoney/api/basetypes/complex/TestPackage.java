package org.kmymoney.api.basetypes.complex;

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

	suite.addTest(org.kmymoney.api.basetypes.complex.TestKMMQualifSecCurrID.suite());
	suite.addTest(org.kmymoney.api.basetypes.complex.TestKMMQualifCurrID.suite());
	suite.addTest(org.kmymoney.api.basetypes.complex.TestKMMQualifSecID.suite());
	suite.addTest(org.kmymoney.api.basetypes.complex.TestKMMQualifSpltID.suite());

	return suite;
    }
}
