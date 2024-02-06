package org.kmymoney.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyPayeeImpl {
	public static final KMMPyeID PYE_1_ID = new KMMPyeID("P000002"); // Gehalt
	public static final KMMPyeID PYE_2_ID = new KMMPyeID("P000003"); // Geldautomat
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
		return new JUnit4TestAdapter(TestKMyMoneyPayeeImpl.class);
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
	public void test01_1() throws Exception {
		pye = kmmFile.getPayeeByID(PYE_1_ID);

		assertEquals(PYE_1_ID, pye.getID());
		assertEquals("Gehalt", pye.getName());
	}

	@Test
	public void test01_2() throws Exception {
		pye = kmmFile.getPayeeByID(PYE_2_ID);

		assertEquals(PYE_2_ID, pye.getID());
		assertEquals("Geldautomat", pye.getName());
	}

	@Test
	public void test01_3() throws Exception {
		pye = kmmFile.getPayeeByID(PYE_3_ID);

		assertEquals(PYE_3_ID, pye.getID());
		assertEquals("Fürchtegott Schnorzelmöller", pye.getName());
		assertEquals(null, pye.getDefaultAccountID());
		assertEquals("fuerchtegott.schnorzelmoeller@prater.at", pye.getEmail());
		assertEquals("", pye.getReference()); // sic, not null
		assertEquals("Pezi-Bär von der Urania kennt ihn gut", pye.getNotes());

		KMMAddress addr = pye.getAddress();
		assertEquals("Krailbacher Gasse 123 a\n" + "Postfach ABC\n" + "Kennwort Kasperlpost", addr.getStreet());
		assertEquals("Wien", addr.getCity());
		assertEquals(null, addr.getCounty());
		assertEquals("1136", addr.getPostCode());
		assertEquals("Österreich", addr.getState());
		assertEquals(null, addr.getZip());
		assertEquals(null, addr.getZipCode());
		assertEquals("+43 - 12 - 277278279", addr.getTelephone());
	}
}
