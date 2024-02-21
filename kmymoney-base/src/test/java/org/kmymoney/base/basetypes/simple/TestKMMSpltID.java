package org.kmymoney.base.basetypes.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMSpltID {

	private static KMMSpltID kmmID = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMMSpltID.class);
	}

	@Before
	public void initialize() throws Exception {
		kmmID = new KMMSpltID();
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		kmmID.set(1);
		assertEquals("S0001", kmmID.get());

		kmmID.set(123);
		assertEquals("S0123", kmmID.get());
	}

	@Test
	public void test02() throws Exception {
		try {
			kmmID.set(-12);
			assertEquals(2, 1);
		} catch (Exception InvalidKMMWPIDException) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}

		kmmID.set(9999);
		assertEquals("S9999", kmmID.get());

		try {
			kmmID.set(10000);
			assertEquals(2, 1);
		} catch (Exception InvalidKMMWPIDException) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}
	}

	@Test
	public void test03() throws Exception {
		kmmID.set("S0012");
		assertEquals("S0012", kmmID.get());

		kmmID.set("S9999");
		assertEquals("S9999", kmmID.get());

		try {
			kmmID.set("S001"); // invalid string: too short
			assertEquals(1, 0);
		} catch (Exception exc) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}

		try {
			kmmID.set("S00001"); // invalid string: too long
			assertEquals(1, 0);
		} catch (Exception exc) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}
	}
}
