package org.kmymoney.base.basetypes.complex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

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
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			acctID = new KMMComplAcctID("B000004"); // invalid string
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		KMMComplAcctID acctID1 = new KMMComplAcctID("AStd::Asset");
		KMMComplAcctID acctID2 = KMMComplAcctID.get(KMMComplAcctID.Top.ASSET);

		assertEquals(KMMComplAcctID.Type.SPECIAL, acctID1.getType());
		assertEquals("AStd::Asset", acctID1.getSpecID());
		assertEquals("AStd::Asset", acctID2.getSpecID());
		assertEquals(acctID1.getSpecID(), acctID2.getSpecID());

		try {
			assertEquals("123", acctID1.getStdID()); // invalid call
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			acctID1 = new KMMComplAcctID("AStd::Anlagevermoegen"); // invalid string
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

}
