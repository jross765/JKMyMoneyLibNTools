package org.kmymoney.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.ConstTest;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.TestKMyMoneySecurityImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyWritableSecurityImpl {
	private static final String SEC_1_ID     = TestKMyMoneySecurityImpl.SEC_1_ID;
	private static final String SEC_1_ISIN   = TestKMyMoneySecurityImpl.SEC_1_ISIN;
	private static final String SEC_1_TICKER = TestKMyMoneySecurityImpl.SEC_1_TICKER;

	private static final String SEC_2_ID     = TestKMyMoneySecurityImpl.SEC_2_ID;
	private static final String SEC_2_ISIN   = TestKMyMoneySecurityImpl.SEC_1_ISIN;
	private static final String SEC_2_TICKER = TestKMyMoneySecurityImpl.SEC_1_TICKER;

    // ---------------------------------------------------------------

    private KMyMoneyWritableFileImpl kmmInFile = null;
    private KMyMoneyFileImpl kmmOutFile = null;

    private KMMFileStats kmmInFileStats = null;
    private KMMFileStats kmmOutFileStats = null;

    private KMMSecID newID = new KMMSecID();

    private KMMQualifSecID secID1 = null;
    private KMMQualifSecID secID2 = null;
//    private KMMQualifSecID secCurrID3 = null;

    // https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
    @SuppressWarnings("exports")
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    // -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyWritableSecurityImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		InputStream kmmInFileStream = null;
		try {
			kmmInFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmInFile = new KMyMoneyWritableFileImpl(kmmInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney in-file");
			exc.printStackTrace();
		}

		// ---

		secID1 = new KMMQualifSecID(SEC_1_ID);
		secID2 = new KMMQualifSecID(SEC_2_ID);
	}

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestKMyMoneySecurityImpl.test01_1/01_4
    //
    // Check whether the KMyMoneyWritableSecurity objects returned by
    // KMyMoneyWritableFileImpl.getWritableSecurityByID() are actually
    // complete (as complete as returned be KMyMoneyFileImpl.getSecurityByID().

	@Test
	public void test01_1() throws Exception {
		KMyMoneyWritableSecurity sec = kmmInFile.getWritableSecurityByQualifID(secID1);
		assertNotEquals(null, sec);

		assertEquals(secID1.toString(), sec.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, sec.getID());
		// ::TODO: Convert to SecurityID_Exchange, then it should be equal
//    assertEquals(secCurrID1, sec.getQualifID()); // not trivial!
		// ::TODO
		// assertEquals(SEC_1_ISIN, sec.getSymbol());
		assertEquals(SEC_1_TICKER, sec.getSymbol());
		assertEquals("Mercedes-Benz Group AG", sec.getName());
	}

	@Test
	public void test01_2() throws Exception {
		Collection<KMyMoneyWritableSecurity> secList = kmmInFile.getWritableSecuritiesByName("mercedes");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());

		assertEquals(secID1.toString(), ((KMyMoneySecurity) secList.toArray()[0]).getQualifID().toString());
		assertEquals(secID1, ((KMyMoneySecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to SecurityID_Exchange, then it should be equal
//    assertEquals(secCurrID1, 
//	        ((KMyMoneySecurity) secList.toArray()[0]).getQualifID()); // not trivial!
		// ::TODO
		// assertEquals(SEC_1_ISIN, ((KMyMoneySecurity)
		// secList.toArray()[0]).getSymbol());
		assertEquals(SEC_1_TICKER, ((KMyMoneySecurity) secList.toArray()[0]).getSymbol());
		assertEquals("Mercedes-Benz Group AG", ((KMyMoneySecurity) secList.toArray()[0]).getName());

		secList = kmmInFile.getWritableSecuritiesByName("BENZ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		assertEquals(secID1, ((KMyMoneySecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to SecurityID_Exchange, then it should be equal
//    assertEquals(secCurrID1, 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifID());

		secList = kmmInFile.getWritableSecuritiesByName(" MeRceDeS-bEnZ  ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		assertEquals(secID1.toString(), ((KMyMoneySecurity) secList.toArray()[0]).getQualifID().toString());
		assertEquals(secID1, ((KMyMoneySecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to SecurityID_Exchange, then it should be equal
//    assertEquals(secCurrID1, 
//	         ((KMyMoneySecurity) secList.toArray()[0]).getQualifID()); // not trivial!
	}

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the KMyMoneyWritableSecurity objects returned by
    // can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		kmmInFileStats = new KMMFileStats(kmmInFile);

		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		KMyMoneyWritableSecurity sec = kmmInFile.getWritableSecurityByQualifID(secID1);
		assertNotEquals(null, sec);

		assertEquals(secID1, sec.getQualifID());
		assertEquals(SEC_1_ID, sec.getQualifID().getCode());

		// ----------------------------
		// Modify the object

		sec.setType(KMMSecCurr.Type.MUTUAL_FUND);
		sec.setName("Benzedes Merc");
		sec.setCode("BNZMRC");
		sec.setSymbol("DE00071BNZ00");
		sec.setPP(BigInteger.valueOf(3));
		sec.setSAF(BigInteger.valueOf(1000));
		sec.setTradingCurrency(new KMMQualifCurrID("CZK"));
		sec.setTradingMarket("Kleinkleckersdorf a. d. Lahn");
		// ::TODO
		// sec.setRoundingMethod(KMMSecCurr.RoundingMethod.CEIL);

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(sec);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		                  // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	@Test
	public void test02_2() throws Exception {
		// ::TODO
	}

	private void test02_1_check_memory(KMyMoneyWritableSecurity sec) throws Exception {
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		assertEquals(secID1, sec.getQualifID()); // unchanged
		assertEquals(SEC_1_ID, sec.getQualifID().getCode()); // unchanged
		assertEquals(KMMSecCurr.Type.MUTUAL_FUND, sec.getType()); // changed
		assertEquals("Benzedes Merc", sec.getName()); // changed
		assertEquals("BNZMRC", sec.getCode()); // changed
		assertEquals("DE00071BNZ00", sec.getSymbol()); // changed
		assertEquals(3, sec.getPP().intValue()); // changed
		assertEquals(1000, sec.getSAF().intValue()); // changed
		assertEquals("CZK", sec.getTradingCurrency().getCode()); // changed
		assertEquals("Kleinkleckersdorf a. d. Lahn", sec.getTradingMarket()); // changed
		// ::TODO
		// assertEquals(KMMSecCurr.RoundingMethod.CEIL, sec.getRoundingMethod()); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		KMyMoneySecurity sec = kmmOutFile.getSecurityByID(SEC_1_ID);
		assertNotEquals(null, sec);

		assertEquals(secID1, sec.getQualifID()); // unchanged
		assertEquals(SEC_1_ID, sec.getQualifID().getCode()); // unchanged
		assertEquals(KMMSecCurr.Type.MUTUAL_FUND, sec.getType()); // changed
		assertEquals("Benzedes Merc", sec.getName()); // changed
		assertEquals("BNZMRC", sec.getCode()); // changed
		assertEquals("DE00071BNZ00", sec.getSymbol()); // changed
		assertEquals(3, sec.getPP().intValue()); // changed
		assertEquals(1000, sec.getSAF().intValue()); // changed
		assertEquals("CZK", sec.getTradingCurrency().getCode()); // changed
		assertEquals("Kleinkleckersdorf a. d. Lahn", sec.getTradingMarket()); // changed
		// ::TODO
		// assertEquals(KMMSecCurr.RoundingMethod.CEIL, sec.getRoundingMethod()); // changed
	}

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

	@Test
	public void test03_1_1() throws Exception {
		kmmInFileStats = new KMMFileStats(kmmInFile);

		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW)); // sic + 1
																												// for
																												// template
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER)); // sic,
																													// NOT
																													// +
																													// 1
																													// yet
		assertEquals(ConstTest.Stats.NOF_SEC, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		KMyMoneyWritableSecurity sec = kmmInFile.createWritableSecurity();
		newID.set(sec.getID());
		sec.setName("Best Corp Ever");

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_1_check_memory(sec);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableSecurityImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		                  // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test03_1_1_check_persisted(outFile);
	}

	private void test03_1_1_check_memory(KMyMoneyWritableSecurity sec) throws Exception {
		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW)); // sic
																													// +
																													// 1
																													// for
																													// template
		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER)); // sic,
																														// NOT
																														// +
																														// 1
																														// yet
		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		assertEquals(newID.toString(), sec.getID().toString());
		assertEquals("Best Corp Ever", sec.getName());
	}

	private void test03_1_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.RAW)); // sic
																													// +
																													// 1
																													// for
																													// template
		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.COUNTER)); // dto.
		assertEquals(ConstTest.Stats.NOF_SEC + 1, kmmInFileStats.getNofEntriesSecurities(KMMFileStats.Type.CACHE));

		KMyMoneySecurity sec = kmmOutFile.getSecurityByID(newID);
		assertNotEquals(null, sec);

		assertEquals(newID.toString(), sec.getID().toString());
		assertEquals("Best Corp Ever", sec.getName());
	}

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

	@Test
	public void test03_2_1() throws Exception {
		KMyMoneyWritableSecurity sec = kmmInFile.createWritableSecurity();
		sec.setName("Scam and Screw Corp.");
		sec.setSymbol("SCAM");

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableSecurityImpl.test01_1: '" + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		                  // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test03_2_1_check(outFile);
	}

    // -----------------------------------------------------------------

//  @Test
//  public void test03_2_2() throws Exception
//  {
//      assertNotEquals(null, outFileGlob);
//      assertEquals(true, outFileGlob.exists());
//
//      // Check if generated document is valid
//      // ::TODO: in fact, not even the input document is.
//      // Build document
//      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//      DocumentBuilder builder = factory.newDocumentBuilder(); 
//      Document document = builder.parse(outFileGlob);
//      System.err.println("xxxx XML parsed");
//
//      // https://howtodoinjava.com/java/xml/read-xml-dom-parser-example/
//      Schema schema = null;
//      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
//      SchemaFactory factory1 = SchemaFactory.newInstance(language);
//      schema = factory1.newSchema(outFileGlob);
//
//      Validator validator = schema.newValidator();
//      DOMResult validResult = null; 
//      validator.validate(new DOMSource(document), validResult);
//      System.out.println("yyy: " + validResult);
//      // assertEquals(validResult);
//  }

	private void test03_2_1_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("SECURITY");
		assertEquals(ConstTest.Stats.NOF_SEC + 1, nList.getLength());

		// Last (new) node
		Node lastNode = nList.item(nList.getLength() - 1);
		assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);
		Element elt = (Element) lastNode;
		assertEquals("Scam and Screw Corp.", elt.getAttribute("name"));
		assertEquals("SCAM", elt.getAttribute("symbol"));
	}

//    // -----------------------------------------------------------------

	@Test
	public void test03_2_2() throws Exception {
		KMyMoneyWritableSecurity sec1 = kmmInFile.createWritableSecurity();
		sec1.setName("Scam and Screw Corp.");
		sec1.setSymbol("US0123456789");

		KMyMoneyWritableSecurity sec2 = kmmInFile.createWritableSecurity();
		sec2.setName("Chocolaterie de la Grande Place");
		sec2.setSymbol("BE0123456789");

		KMyMoneyWritableSecurity sec3 = kmmInFile.createWritableSecurity();
		sec3.setName("Ils sont fous ces dingos!");
		sec3.setSymbol("FR0123456789");

		KMyMoneyWritableSecurity sec4 = kmmInFile.createWritableSecurity();
		sec4.setName("Ye Ole National British Trade Company Ltd.");
		sec4.setSymbol("GB10000A2222"); // sic, has to be set redundantly

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableSecurityImpl.test02_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		                  // and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test03_2_2_check(outFile);
	}

	private void test03_2_2_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("SECURITY");
		assertEquals(ConstTest.Stats.NOF_SEC + 4, nList.getLength());

		// Last three nodes (the new ones)
		Node node = nList.item(nList.getLength() - 4);
		assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
		Element elt = (Element) node;
		assertEquals("Scam and Screw Corp.", elt.getAttribute("name"));
		assertEquals("US0123456789", elt.getAttribute("symbol"));

		node = nList.item(nList.getLength() - 3);
		assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
		elt = (Element) node;
		assertEquals("Chocolaterie de la Grande Place", elt.getAttribute("name"));
		assertEquals("BE0123456789", elt.getAttribute("symbol"));

		node = nList.item(nList.getLength() - 2);
		assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
		elt = (Element) node;
		assertEquals("Ils sont fous ces dingos!", elt.getAttribute("name"));
		assertEquals("FR0123456789", elt.getAttribute("symbol"));

		node = nList.item(nList.getLength() - 1);
		assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
		elt = (Element) node;
		assertEquals("Ye Ole National British Trade Company Ltd.", elt.getAttribute("name"));
		assertEquals("GB10000A2222", elt.getAttribute("symbol"));
	}

}
