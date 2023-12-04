package org.kmymoney.api.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.generated.KEYVALUEPAIRS;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.generated.PRICES;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.aux.KMMPrice;
import org.kmymoney.api.read.aux.KMMPricePair;
import org.kmymoney.api.read.impl.aux.KMMPriceImpl;
import org.kmymoney.api.read.impl.aux.KMMPricePairImpl;
import org.kmymoney.api.read.impl.hlp.FileAccountManager;
import org.kmymoney.api.read.impl.hlp.FileCurrencyManager;
import org.kmymoney.api.read.impl.hlp.FilePayeeManager;
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
public class KMyMoneyFileImpl implements KMyMoneyFile,
                                         KMyMoneyFileStats 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyFileImpl.class);

    /**
     * my CurrencyTable.
     */
    private final ComplexPriceTable currencyTable = new ComplexPriceTable();

    private static final String PADDING_TEMPLATE = "000000";

    // ---------------------------------------------------------------

    private File file;
    
    // ----------------------------

    private KMYMONEYFILE rootElement;
    private KMyMoneyObjectImpl myKMyMoneyObject;

    // ----------------------------

    /**
     * @see #getObjectFactory()
     */
    private volatile ObjectFactory myJAXBFactory;

    // ----------------------------
    
    protected FileAccountManager     acctMgr = null;
    protected FileTransactionManager trxMgr  = null;
    protected FilePayeeManager       pyeMgr  = null;
    protected FileSecurityManager    secMgr  = null;
    protected FileCurrencyManager    currMgr = null;

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
     * @see KMyMoneyFile#getAccountById(java.lang.String)
     */
    @Override
    public KMyMoneyAccount getAccountById(final KMMComplAcctID acctID) {
	return acctMgr.getAccountById(acctID);
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
     * @see #getAccountById(String)
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
     * @see #getAccountById(String)
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
     * @see #getAccountById(String)
     * @see #getAccountByName(String)
     */
    @Override
    public KMyMoneyAccount getAccountByIDorNameEx(final KMMComplAcctID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorNameEx(id, name);
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
    public Collection<? extends KMyMoneyAccount> getRootAccounts() {
	return acctMgr.getRootAccounts();
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public KMMPrice getPriceById(String id) {
        if (priceById == null) {
            getPrices();
        }
        
        return priceById.get(id);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<KMMPrice> getPrices() {
        if (priceById == null) {
            priceById = new HashMap<KMMPriceID, KMMPrice>();

            PRICES priceDB = getRootElement().getPRICES();
            List<PRICEPAIR> prices = priceDB.getPRICEPAIR();
            for ( PRICEPAIR jwsdpPricePair  : prices ) {
        	String fromCurr = jwsdpPricePair.getFrom();
        	String toCurr = jwsdpPricePair.getTo();
        	KMMPricePair pricePair = new KMMPricePairImpl(jwsdpPricePair, this);
        	for ( PRICE jwsdpPrice : jwsdpPricePair.getPRICE() ) {
        	    XMLGregorianCalendar cal = jwsdpPrice.getDate();
        	    LocalDate date = LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
        	    String dateStr = date.toString();
        	    KMMPriceID priceID = new KMMPriceID(fromCurr, toCurr, dateStr);
        	    KMMPriceImpl price = new KMMPriceImpl(pricePair, jwsdpPrice, this);
        	    priceById.put(priceID, price);
        	}
            }
        } 

        return priceById.values();
    }

    public FixedPointNumber getLatestPrice(final String secCurrIDStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( secCurrIDStr.startsWith("E0") ) { // ::MAGIC
	    return getLatestPrice(new KMMQualifSecID(secCurrIDStr));
	} else {
	    return getLatestPrice(new KMMQualifCurrID(secCurrIDStr));
	}
    }

    /**
     * {@inheritDoc}
     */
    public FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return getLatestPrice(secCurrID, 0);
    }

    protected Map<KMMPriceID, KMMPrice> priceById = null;

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
		LOGGER.warn("Ignoring price-quote for " + baseCurrency 
		    + " because " + baseCurrency + " is our base-currency.");
		continue;
	    }

	    // get the latest price in the file and insert it into
	    // our currency table
	    FixedPointNumber factor = getLatestPrice(new KMMQualifSecCurrID(nameSpace, fromSecCurr));

	    if ( factor != null ) {
		getCurrencyTable().setConversionFactor(nameSpace, fromSecCurr, factor);
	    } else {
		LOGGER.warn("The KMyMoney file defines a factor for a security '" 
			+ fromSecCurr + "' but has no security for it");
	    }
	} // for pricePair
    }

    /**
     * @see {@link #getLatestPrice(String, String)}
     */
    protected static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @param depth       used for recursion. Allways call with '0' for aborting
     *                    recursive quotes (quotes to other then the base- currency)
     *                    we abort if the depth reached 6.
     * @return the latest price-quote in the kmymoney-file in the default-currency
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see {@link KMyMoneyFile#getLatestPrice(String, String)}
     * @see #getDefaultCurrencyID()
     */
    private FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID, final int depth) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if (secCurrID == null) {
	    throw new IllegalArgumentException("null parameter 'secCurrID' given");
	}
	// System.err.println("depth: " + depth);

	LocalDate latestDate = null;
	FixedPointNumber latestQuote = null;
	FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
	final int maxRecursionDepth = 5; // ::MAGIC

	PRICES priceDB = getRootElement().getPRICES();
	for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
	    String fromSecCurr = pricePair.getFrom();
	    String toCurr      = pricePair.getTo();
	    // System.err.println("pricepair: " + fromCurr + " -> " + toCurr);
		
	    if ( fromSecCurr == null ) {
		LOGGER.warn("KMyMoney-file contains price-quotes without from-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    if ( toCurr == null ) {
		LOGGER.warn("KMyMoney file contains price-quotes without to-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    for ( PRICE prc : pricePair.getPRICE() ) {
		    if (prc == null) {
			LOGGER.warn("KMyMoney file contains null price-quotes - there may be a problem with JWSDP");
			continue;
		    }
		    
		try {
		    if (prc.getDate() == null) {
			LOGGER.warn("KMyMoney file contains price-quotes without date: '"
				+ pricePair.toString() + "'");
			continue;
		    }
		    
		    if (prc.getPrice() == null) {
			LOGGER.warn("KMyMoney file contains price-quotes without price value: '"
				+ pricePair.toString() + "'");
			continue;
		    }

		    if ( ! fromSecCurr.equals(secCurrID.getCode()) ) {
			continue;
		    }

		    // BEGIN core
		    if ( toCurr.startsWith("E0") ) {
		      // is security
			if ( depth > maxRecursionDepth ) {
			    LOGGER.warn("Ignoring price-quote that is not in an ISO4217-currency"
				    + " but in '" + toCurr + "'");
			    continue;
			}
			factor = getLatestPrice(new KMMQualifSecID(toCurr), depth + 1);
		    } else {
		      // is currency
			if ( ! toCurr.equals(getDefaultCurrencyID()) ) {
			    if ( depth > maxRecursionDepth ) {
				LOGGER.warn("Ignoring price-quote that is not in " + getDefaultCurrencyID()
					+ " but in '" + toCurr + "'");
				continue;
			    }
			    factor = getLatestPrice(new KMMQualifCurrID(toCurr), depth + 1);
			}
		    }
		    // END core

		    XMLGregorianCalendar dateCal = prc.getDate();
		    LocalDate date = LocalDate.of(dateCal.getYear(), dateCal.getMonth(), dateCal.getDay());

		    if (latestDate == null || latestDate.isBefore(date)) {
			latestDate = date;
			latestQuote = new FixedPointNumber(prc.getPrice());
			LOGGER.debug("getLatestPrice(pSecCurrId='" + secCurrID.toString()
				+ "') converted " + latestQuote + " <= " + prc.getPrice());
		    }

		} catch (NumberFormatException e) {
		    LOGGER.error("[NumberFormatException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (NullPointerException e) {
		    LOGGER.error("[NullPointerException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (ArithmeticException e) {
		    LOGGER.error("[ArithmeticException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		}
	    } // for price
	} // for pricepair

	LOGGER.debug(getClass().getName() + ".getLatestPrice(pSecCurrId='"
		+ secCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

	if (latestQuote == null) {
	    return null;
	}

	if (factor == null) {
	    factor = new FixedPointNumber(1);
	}

	return factor.multiply(latestQuote);
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
     * @see #getJAXBContext()
     */
    private volatile JAXBContext myJAXBContext;

    /**
     * @return the JAXB-context
     */
    protected JAXBContext getJAXBContext() {
	if (myJAXBContext == null) {
	    try {
		myJAXBContext = JAXBContext.newInstance("org.kmymoney.api.generated", this.getClass().getClassLoader());
	    } catch (JAXBException e) {
		LOGGER.error(e.getMessage(), e);
	    }
	}
	return myJAXBContext;
    }

    /**
     * @return the number of transactions
     */
    protected BigInteger getTransactionCount() {
	return getRootElement().getTRANSACTIONS().getCount();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneyCurrency getCurrencyById(String currID) {
	return currMgr.getCurrencyById(currID);
    }

    @Override
    public KMyMoneyCurrency getCurrencyByQualifId(KMMQualifCurrID currID) {
	return currMgr.getCurrencyByQualifId(currID);
    }

    @Override
    public Collection<KMyMoneyCurrency> getCurrencies() {
	return currMgr.getCurrencies();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneyPayee getPayeeById(final KMMPyeID id) {
	return pyeMgr.getPayeeById(id);
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
    public KMyMoneySecurity getSecurityById(final KMMSecID secID) {
	return secMgr.getSecurityById(secID);
    }

    @Override
    public KMyMoneySecurity getSecurityById(final String idStr) {
	return secMgr.getSecurityById(idStr);
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

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    @Override
    public KMyMoneyTransaction getTransactionById(final KMMTrxID trxID) {
	return trxMgr.getTransactionById(trxID);
    }

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    @Override
    public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSplitID spltID) {
	return trxMgr.getTransactionSplitByID(spltID);
    }

    /**
     * @see KMyMoneyFile#getTransactions()
     */
    @Override
    public Collection<? extends KMyMoneyTransaction> getTransactions() {
	return trxMgr.getTransactions();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return this;
    }

    // ---------------------------------------------------------------
    // Statistics (for test purposes)

    @Override
    public int getNofEntriesAccountMap() {
	return acctMgr.getNofEntriesAccountMap();
    }

    @Override
    public int getNofEntriesTransactionMap() {
	return trxMgr.getNofEntriesTransactionMap();
    }

    @Override
    public int getNofEntriesTransactionSplitMap() {
	return trxMgr.getNofEntriesTransactionSplitMap();
    }

    @Override
    public int getNofEntriesPayeeMap() {
	return pyeMgr.getNofEntriesPayeeMap();
    }

    @Override
    public int getNofEntriesSecurityMap() {
	return secMgr.getNofEntriesSecurityMap();
    }

}
