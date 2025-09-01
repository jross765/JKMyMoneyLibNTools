package org.kmymoney.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.simple.KMMPyeID;

import junit.framework.JUnit4TestAdapter;

public class TestKMAddressImpl {
	public static final KMMPyeID PYE_3_ID = new KMMPyeID("P000005"); // Schnorzelmoeller

	// -----------------------------------------------------------------

	private KMyMoneyFile kmmFile = null;
	private KMyMoneyPayee pye = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMAddressImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		InputStream kmmFileStream = null;
		try {
			kmmFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmFile = new KMyMoneyFileImpl(kmmFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_3() throws Exception {
		pye = kmmFile.getPayeeByID(PYE_3_ID);
		assertNotEquals(null, pye);

		KMMAddress addr = pye.getAddress();
		assertNotEquals(null, addr);

		assertEquals("Krailbacher Gasse 123 a\n" + 
		             "Postfach ABC\n" + 
				     "Kennwort Kasperlpost", addr.getStreet());
		assertEquals("Wien", addr.getCity());
		assertEquals("Österreich", addr.getCounty());
		assertEquals("1136", addr.getPostCode());
		assertEquals("Österreich", addr.getState());
		assertEquals("1136", addr.getZip());
		assertEquals("1136", addr.getZipCode());
		assertEquals("+43 - 12 - 277278279", addr.getTelephone());
	}
}
