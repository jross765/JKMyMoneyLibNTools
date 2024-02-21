package org.kmymoney.base.basetypes.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestKMMSecID {

	private static KMMSecID kmmID = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMMSecID.class);
	}

	@Before
	public void initialize() throws Exception {
		kmmID = new KMMSecID();
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		kmmID.set(1);
		assertEquals("E000001", kmmID.get());

		kmmID.set(123);
		assertEquals("E000123", kmmID.get());
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

		kmmID.set(999999);
		assertEquals("E999999", kmmID.get());

		try {
			kmmID.set(1000000);
			assertEquals(2, 1);
		} catch (Exception InvalidKMMWPIDException) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}
	}

	@Test
	public void test03() throws Exception {
		kmmID.set("E000012");
		assertEquals("E000012", kmmID.get());

		kmmID.set("E999999");
		assertEquals("E999999", kmmID.get());

		try {
			kmmID.set("E00001"); // invalid string: too short
			assertEquals(1, 0);
		} catch (Exception exc) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}

		try {
			kmmID.set("E0000001"); // invalid string: too long
			assertEquals(1, 0);
		} catch (Exception exc) {
			// Muss Exception werfen, wenn er hier landet, ist es richtig
			assertEquals(1, 1);
		}
	}
}
