package org.kmymoney.api.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.generated.KEYVALUEPAIRS;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.generated.PRICES;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.read.impl.hlp.FileAccountManager;
import org.kmymoney.api.read.impl.hlp.FileCurrencyManager;
import org.kmymoney.api.read.impl.hlp.FilePayeeManager;
import org.kmymoney.api.read.impl.hlp.FilePriceManager;
import org.kmymoney.api.read.impl.hlp.FileSecurityManager;
import org.kmymoney.api.read.impl.hlp.FileTransactionManager;
import org.kmymoney.api.read.impl.hlp.NamespaceRemoverReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Implementation of KMyMoneyFile that can only
 * read but not modify KMyMoney-Files. <br/>
 * @see KMyMoneyFile
 */
public class KMyMoneyFileImpl implements KMyMoneyFile
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyFileImpl.class);

    // ---------------------------------------------------------------

    private File file;
    
    // ----------------------------

    private KMYMONEYFILE rootElement;
    private KMyMoneyObjectImpl myKMyMoneyObject;

    // ----------------------------

    private volatile ObjectFactory myJAXBFactory;
    private volatile JAXBContext myJAXBContext;

    // ----------------------------
    
    protected FileAccountManager     acctMgr = null;
    protected FileTransactionManager trxMgr  = null;
    protected FilePayeeManager       pyeMgr  = null;
    protected FileSecurityManager    secMgr  = null;
    protected FileCurrencyManager    currMgr = null;
    
    // ----------------------------

    private final ComplexPriceTable  currencyTable = new ComplexPriceTable();
    protected FilePriceManager       prcMgr        = null;

    // ---------------------------------------------------------------

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final File pFile) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	super();
	loadFile(pFile);
    }

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final InputStream is) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	super();
	loadInputStream(is);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
	return file;
    }

    /**
     * Internal method, just sets this.file .
     *
     * @param pFile the file loaded
     */
    protected void setFile(final File pFile) {
	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}
	file = pFile;
    }

    // ----------------------------

    /**
     * loads the file and calls setRootElement.
     *
     * @param pFile the file to read
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #setRootElement(KMYMONEYFILE)
     */
    protected void loadFile(final File pFile) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	long start = System.currentTimeMillis();

	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}

	if (!pFile.exists()) {
	    throw new IllegalArgumentException("Given file '" + pFile.getAbsolutePath() + "' does not exist!");
	}

	setFile(pFile);

	InputStream in = new FileInputStream(pFile);
	if ( pFile.getName().endsWith(".gz") ||
	     pFile.getName().endsWith(".kmy") ) {
	    in = new BufferedInputStream(in);
	    in = new GZIPInputStream(in);
	} else {
	    // determine if it's gzipped by the magic bytes
	    byte[] magic = new byte[2];
	    in.read(magic);
	    in.close();

	    in = new FileInputStream(pFile);
	    in = new BufferedInputStream(in);
	    if (magic[0] == 31 && magic[1] == -117) {
		in = new GZIPInputStream(in);
	    }
	}

	loadInputStream(in);

	long end = System.currentTimeMillis();
	LOGGER.info("loadFile: Took " + (end - start) + " ms (total) ");

    }

    protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	long start = System.currentTimeMillis();

	NamespaceRemoverReader reader = new NamespaceRemoverReader(new InputStreamReader(in, "utf-8"));
	try {

	    JAXBContext myContext = getJAXBContext();
	    if ( myContext == null ) {
		// ::TODO: make it fatal
		LOGGER.error("loadInputStream: JAXB context cannot be found/generated");
		throw new IOException("JAXB context cannot be found/generated");
	    }
	    Unmarshaller unmarshaller = myContext.createUnmarshaller();

	    KMYMONEYFILE obj = (KMYMONEYFILE) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
	    long start2 = System.currentTimeMillis();
	    setRootElement(obj);
	    long end = System.currentTimeMillis();
	    LOGGER.info("loadFileInputStream: Took " + 
	                (end - start) + " ms (total), " + 
		        (start2 - start) + " ms (jaxb-loading), " + 
	                (end - start2) + " ms (building facades)");

	} catch (JAXBException e) {
	    LOGGER.error("loadInputStream: " + e.getMessage(), e);
	    throw new IllegalStateException(e);
	} finally {
	    reader.close();
	}
    }

    // ---------------------------------------------------------------

    /**
     * @return Returns the currencyTable.
     * @link #currencyTable
     */
    public ComplexPriceTable getCurrencyTable() {
	return currencyTable;
    }

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "ISO4217" .
     *
     * @return the default-currency to use.
     */
    public String getDefaultCurrencyID() {
	KMYMONEYFILE root = getRootElement();
	if (root == null) {
	    return Const.DEFAULT_CURRENCY;
	}
	
	KEYVALUEPAIRS kvpList = root.getKEYVALUEPAIRS();
	if (kvpList == null) {
	    return Const.DEFAULT_CURRENCY;
	}
	
	for ( PAIR kvp : kvpList.getPAIR() ) {
	    if ( kvp.getKey().equals("kmm-baseCurrency") ) { // ::MAGIC
		return kvp.getValue();
	    }
	}
	
	// not found
	return Const.DEFAULT_CURRENCY;
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getAccountByID(java.lang.String)
     */
    @Override
    public KMyMoneyAccount getAccountByID(final KMMComplAcctID acctID) {
	return acctMgr.getAccountByID(acctID);
    }

    /**
     * @param acctID if null, gives all account that have no parent
     * @return the sorted collection of children of that account
     */
    @Override
    public Collection<KMyMoneyAccount> getAccountsByParentID(final KMMComplAcctID id) {
        return acctMgr.getAccountsByParentID(id);
    }

    @Override
    public Collection<KMyMoneyAccount> getAccountsByName(final String name) {
	return acctMgr.getAccountsByName(name);
    }
    
    /**
     * @see KMyMoneyFile#getAccountsByName(java.lang.String)
     */
    @Override
    public Collection<KMyMoneyAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
	return acctMgr.getAccountsByName(expr, qualif, relaxed);
    }

    @Override
    public KMyMoneyAccount getAccountByNameUniq(final String name, final boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByNameUniq(name, qualif);
    }
    
    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param nameRegEx the regular expression of the name to look for
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    @Override
    public KMyMoneyAccount getAccountByNameEx(final String nameRegEx) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByNameEx(nameRegEx);
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    @Override
    public KMyMoneyAccount getAccountByIDorName(final KMMComplAcctID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorName(id, name);
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    @Override
    public KMyMoneyAccount getAccountByIDorNameEx(final KMMComplAcctID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorNameEx(id, name);
    }

    @Override
    public Collection<KMyMoneyAccount> getAccountsByTypeAndName(KMyMoneyAccount.Type type, String expr, 
	                                                        boolean qualif, boolean relaxed) throws UnknownAccountTypeException {
	return acctMgr.getAccountsByTypeAndName(type, expr, qualif, relaxed);
    }

    /**
     * @return a read-only collection of all accounts
     */
    @Override
    public Collection<KMyMoneyAccount> getAccounts() {
        return acctMgr.getAccounts();
    }

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     */
    @Override
    public Collection<? extends KMyMoneyAccount> getParentlessAccounts() {
	return acctMgr.getParentlessAccounts();
    }

    @Override
    public Collection<KMMComplAcctID> getTopAccountIDs() {
	return acctMgr.getTopAccountIDs();
    }
	    
    @Override    
    public Collection<KMyMoneyAccount> getTopAccounts() {
	return acctMgr.getTopAccounts();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getTransactionByID(java.lang.String)
     */
    @Override
    public KMyMoneyTransaction getTransactionByID(final KMMTrxID trxID) {
	return trxMgr.getTransactionByID(trxID);
    }

    /**
     * @see KMyMoneyFile#getTransactions()
     */
    @Override
    public Collection<? extends KMyMoneyTransaction> getTransactions() {
	return trxMgr.getTransactions();
    }
    
    // ---------------------------------------------------------------
    
    /**
     * @see KMyMoneyFile#getTransactionByID(java.lang.String)
     */
    @Override
    public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSpltID spltID) {
	return trxMgr.getTransactionSplitByID(spltID);
    }

    @Override
    public Collection<KMyMoneyTransactionSplit> getTransactionSplits() {
	return trxMgr.getTransactionSplits();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneyPayee getPayeeByID(final KMMPyeID id) {
	return pyeMgr.getPayeeByID(id);
    }

    @Override
    public Collection<KMyMoneyPayee> getPayeesByName(String expr) {
	return pyeMgr.getPayeesByName(expr);
    }

    @Override
    public Collection<KMyMoneyPayee> getPayeesByName(String expr, boolean relaxed) {
	return pyeMgr.getPayeesByName(expr, relaxed);
    }

    @Override
    public KMyMoneyPayee getPayeesByNameUniq(String expr)
	    throws NoEntryFoundException, TooManyEntriesFoundException {
	return pyeMgr.getPayeesByNameUniq(expr);
    }

    @Override
    public Collection<KMyMoneyPayee> getPayees() {
	return pyeMgr.getPayees();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneySecurity getSecurityByID(final KMMSecID secID) {
	return secMgr.getSecurityByID(secID);
    }

    @Override
    public KMyMoneySecurity getSecurityByID(final String idStr) {
	return secMgr.getSecurityByID(idStr);
    }

    @Override
    public KMyMoneySecurity getSecurityByQualifID(final KMMQualifSecID secID) {
	return secMgr.getSecurityByQualifID(secID);
    }

    @Override
    public KMyMoneySecurity getSecurityByQualifID(final String qualifIDStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return secMgr.getSecurityByQualifID(qualifIDStr);
    }

    @Override
    public KMyMoneySecurity getSecurityBySymbol(final String symb) {
	return secMgr.getSecurityBySymbol(symb);
    }

    @Override
    public KMyMoneySecurity getSecurityByCode(final String code) {
	return secMgr.getSecurityByCode(code);
    }

    @Override
    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr) {
	return secMgr.getSecuritiesByName(expr);
    }

    @Override
    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr, final boolean relaxed) {
	return secMgr.getSecuritiesByName(expr, relaxed);
    }

    @Override
    public KMyMoneySecurity getSecurityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
	return secMgr.getSecurityByNameUniq(expr);
    }
    
    @Override
    public Collection<KMyMoneySecurity> getSecurities() {
	return secMgr.getSecurities();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneyCurrency getCurrencyByID(String currID) {
	return currMgr.getCurrencyByID(currID);
    }

    @Override
    public KMyMoneyCurrency getCurrencyByQualifID(KMMQualifCurrID currID) {
	return currMgr.getCurrencyByQualifID(currID);
    }

    @Override
    public Collection<KMyMoneyCurrency> getCurrencies() {
	return currMgr.getCurrencies();
    }

    // ---------------------------------------------------------------
    
    @Override
    public KMyMoneyPrice getPriceByID(KMMPriceID prcID) {
	return prcMgr.getPriceByID(prcID);
    }

    @Override
    public Collection<KMyMoneyPrice> getPrices() {
	return prcMgr.getPrices();
    }

    @Override
    public FixedPointNumber getLatestPrice(KMMQualifSecCurrID secCurrID)
	    throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return prcMgr.getLatestPrice(secCurrID);
    }

    // ---------------------------------------------------------------
    
    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    public KMYMONEYFILE getRootElement() {
	return rootElement;
    }

    /**
     * Set the new root-element and load all accounts, transactions,... from it.
     *
     * @param pRootElement the new root-element
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     */
    protected void setRootElement(final KMYMONEYFILE pRootElement) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if (pRootElement == null) {
	    throw new IllegalArgumentException("null not allowed for field this.rootElement");
	}
	rootElement = pRootElement;

	// fill prices
	prcMgr  = new FilePriceManager(this);

	loadPriceDatabase(pRootElement);

	// fill maps
	acctMgr = new FileAccountManager(this);
	
	trxMgr  = new FileTransactionManager(this);

	secMgr  = new FileSecurityManager(this);
	
	currMgr = new FileCurrencyManager(this);

	pyeMgr  = new FilePayeeManager(this);
    }

    // ---------------------------------------------------------------

    /**
     * @param pRootElement the root-element of the KMyMoney-file
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     */
    private void loadPriceDatabase(final KMYMONEYFILE pRootElement) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	boolean noPriceDB = true;
	
	PRICES priceDB = pRootElement.getPRICES();
	if ( priceDB.getPRICEPAIR().size() > 0 )
	    noPriceDB = false;

	loadPriceDatabaseCore(priceDB);

	if ( noPriceDB ) {
	    // no price DB in file
	    getCurrencyTable().clear();
	}
    }

    private void loadPriceDatabaseCore(PRICES priceDB) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
//  	getCurrencyTable().clear();
//  	getCurrencyTable().setConversionFactor(KMMSecCurrID.Type.CURRENCY, 
//  		                               getDefaultCurrencyID(), 
//  		                               new FixedPointNumber(1));

	String baseCurrency = getDefaultCurrencyID();
	
	for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
	    String fromSecCurr = pricePair.getFrom();
	    // String toCurr      = pricePair.getTo();
	    
	    // ::TODO: Try to implement Security type
	    KMMQualifSecCurrID.Type nameSpace = null;
	    if ( fromSecCurr.startsWith("E0") ) // ::MAGIC
		nameSpace = KMMQualifSecCurrID.Type.SECURITY;
	    else
		nameSpace = KMMQualifSecCurrID.Type.CURRENCY;

	    // Check if we already have a latest price for this security
	    // (= currency, fund, ...)
	    if ( getCurrencyTable().getConversionFactor(nameSpace, fromSecCurr) != null ) {
		continue;
	    }

	    if ( fromSecCurr.equals(baseCurrency) ) {
		LOGGER.warn("loadPriceDatabaseCore: Ignoring price-quote for " + baseCurrency 
		    + " because " + baseCurrency + " is our base-currency.");
		continue;
	    }

	    // get the latest price in the file and insert it into
	    // our currency table
	    FixedPointNumber factor = getLatestPrice(new KMMQualifSecCurrID(nameSpace, fromSecCurr));

	    if ( factor != null ) {
		getCurrencyTable().setConversionFactor(nameSpace, fromSecCurr, factor);
	    } else {
		LOGGER.warn("loadPriceDatabaseCore: The KMyMoney file defines a factor for a security '" 
			+ fromSecCurr + "' but has no security for it");
	    }
	} // for pricePair
    }

    // ---------------------------------------------------------------

    /**
     * @return the jaxb object-factory used to create new peer-objects to extend
     *         this
     */
    @SuppressWarnings("exports")
    public ObjectFactory getObjectFactory() {
	if (myJAXBFactory == null) {
	    myJAXBFactory = new ObjectFactory();
	}
	return myJAXBFactory;
    }

    /**
     * @return the JAXB-context
     */
    protected JAXBContext getJAXBContext() {
	if (myJAXBContext == null) {
	    try {
		myJAXBContext = JAXBContext.newInstance("org.kmymoney.api.generated", this.getClass().getClassLoader());
	    } catch (JAXBException e) {
		LOGGER.error("getJAXBContext: " + e.getMessage(), e);
	    }
	}
	return myJAXBContext;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return this;
    }
    
    // ---------------------------------------------------------------
    // Helpers for class FileStats_Cache
    
    @SuppressWarnings("exports")
    public FileAccountManager getAcctMgr() {
	return acctMgr;
    }
    
    @SuppressWarnings("exports")
    public FileTransactionManager getTrxMgr() {
	return trxMgr;
    }
    
    @SuppressWarnings("exports")
    public FilePayeeManager getPyeMgr() {
	return pyeMgr;
    }
    
    @SuppressWarnings("exports")
    public FileSecurityManager getSecMgr() {
	return secMgr;
    }
    
    @SuppressWarnings("exports")
    public FileCurrencyManager getCurrMgr() {
	return currMgr;
    }
    
    @SuppressWarnings("exports")
    public FilePriceManager getPrcMgr() {
	return prcMgr;
    }
    
    // ---------------------------------------------------------------
    
    public String toString() {
	String result = "KMyMoneyFileImpl: [\n";
	
	result += "  Stats (raw):\n"; 
	KMMFileStats stats;
	try {
	    stats = new KMMFileStats(this);

	    result += "    No. of accounts:           " + stats.getNofEntriesAccounts(KMMFileStats.Type.RAW) + "\n"; 
	    result += "    No. of transactions:       " + stats.getNofEntriesTransactions(KMMFileStats.Type.RAW) + "\n"; 
	    result += "    No. of transaction splits: " + stats.getNofEntriesTransactionSplits(KMMFileStats.Type.RAW) + "\n"; 
	    result += "    No. of payees:             " + stats.getNofEntriesPayees(KMMFileStats.Type.RAW) + "\n"; 
	    result += "    No. of securities:         " + stats.getNofEntriesSecurities(KMMFileStats.Type.RAW) + "\n"; 
	    result += "    No. of currencies:         " + stats.getNofEntriesCurrencies(KMMFileStats.Type.RAW) + "\n";
	    result += "    No. of price pairs:        " + stats.getNofEntriesPricePairs(KMMFileStats.Type.RAW) + "\n";
	    result += "    No. of prices:             " + stats.getNofEntriesPrices(KMMFileStats.Type.RAW) + "\n";
	} catch (Exception e) {
	    result += "ERROR\n"; 
	}
	
	result += "]";
	
	return result;
    }

}
