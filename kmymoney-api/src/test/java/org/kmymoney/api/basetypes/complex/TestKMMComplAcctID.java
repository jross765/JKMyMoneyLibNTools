package org.kmymoney.api.basetypes.complex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMAcctID;

import junit.framework.JUnit4TestAdapter;

public class TestKMMComplAcctID {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestKMMComplAcctID.class);
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	KMMComplAcctID acctID = new KMMComplAcctID("A000004");

	assertEquals(KMMComplAcctID.Type.STANDARD, acctID.getType());
	assertEquals(new KMMAcctID("A000004"), acctID.getStdID());
	assertEquals("A000004", acctID.getStdID().toString());
	
	try {
	    assertEquals("123", acctID.getSpecID()); // invalid call
	} catch ( Exception exc ) {
	    assertEquals(0, 0);
	}
	
	try {
	    acctID = new KMMComplAcctID("B000004"); // invalid string
	} catch ( Exception exc ) {
	    assertEquals(0, 0);
	}
    }

    @Test
    public void test02() throws Exception {
	KMMComplAcctID acctID = new KMMComplAcctID("AStd::Asset");

	assertEquals(KMMComplAcctID.Type.SPECIAL, acctID.getType());
	assertEquals("AStd::Asset", acctID.getSpecID());
	
	try {
	    assertEquals("123", acctID.getStdID()); // invalid call
	} catch ( Exception exc ) {
	    assertEquals(0, 0);
	}

	try {
	    acctID = new KMMComplAcctID("AStd::Anlagevermoegen"); // invalid string
	} catch ( Exception exc ) {
	    assertEquals(0, 0);
	}
    }

}
