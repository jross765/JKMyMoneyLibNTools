package org.kmymoney.currency;

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

	// ::TODO
	// suite.addTest(org.kmymoney.currency.TestSimpleCurrencyExchRateTable.suite());
	suite.addTest(org.kmymoney.currency.TestSimpleSecurityQuoteTable.suite());
	suite.addTest(org.kmymoney.currency.TestComplexPriceTable.suite());

	return suite;
    }
}
