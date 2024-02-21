package org.kmymoney.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.api.Const;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.SplitNotFoundException;
import org.kmymoney.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.TRANSACTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of KMyMoneyTransaction that uses JWSDP.
 */
public class KMyMoneyTransactionImpl implements KMyMoneyTransaction 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionImpl.class);

    protected static final DateTimeFormatter DATE_POSTED_FORMAT  = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_ENTERED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected TRANSACTION jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final KMyMoneyFile kmmFile;

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransaction#getDatePosted()
     */
    protected LocalDate postDate;

    /**
     * @see KMyMoneyTransaction#getEntryDate()
     */
    protected LocalDate entryDate;

    /**
     * The Currency-Format to use if no locale is given.
     */
    protected NumberFormat currencyFormat;

    // ---------------------------------------------------------------

    /**
     * Create a new Transaction, facading a JWSDP-transaction.
     *
     * @param peer    the JWSDP-object we are facading.
     * @param kmmFile the file to register under
     */
    @SuppressWarnings("exports")
    public KMyMoneyTransactionImpl(
	    final TRANSACTION peer, 
	    final KMyMoneyFile kmmFile) {

	this.jwsdpPeer = peer;
	this.kmmFile = kmmFile;

    }

    // Copy-constructor
    public KMyMoneyTransactionImpl(final KMyMoneyTransaction trx) {
	jwsdpPeer = trx.getJwsdpPeer();
	kmmFile = trx.getKMyMoneyFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransaction#isBalanced()
     */
    public boolean isBalanced() {

	return getBalance().equals(new FixedPointNumber());

    }

    /**
     * @see KMyMoneyAccount#getQualifSecCurrID()
     */
    public KMMQualifSecCurrID getQualifSecCurrID() {
    	if ( jwsdpPeer.getCommodity() == null )
    		return null;
    	
    	KMMQualifSecCurrID result = null;
    	if ( jwsdpPeer.getCommodity().startsWith(KMMQualifSecCurrID.PREFIX_SECURITY) ) {
    		// is security
    		result = new KMMQualifSecID(jwsdpPeer.getCommodity());
    	} else {
    		// is currency
    		result = new KMMQualifCurrID(jwsdpPeer.getCommodity());
    	}
    	
    	return result;
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @return the balance of the sum of all splits
     * @see KMyMoneyTransaction#getBalance()
     */
    public FixedPointNumber getBalance() {

	FixedPointNumber fp = new FixedPointNumber();

	for (KMyMoneyTransactionSplit split : getSplits()) {
	    fp.add(split.getValue());
	}

	return fp;
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see KMyMoneyTransaction#getBalanceFormatted()
     */
    public String getBalanceFormatted() {
    	return getCurrencyFormat().format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see KMyMoneyTransaction#getBalanceFormatted(java.util.Locale)
     */
    public String getBalanceFormatted(final Locale loc) {

	NumberFormat cf = NumberFormat.getInstance(loc);
	if ( getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
	    cf.setCurrency(Currency.getInstance(getQualifSecCurrID().getCode()));
	} else {
	    cf.setCurrency(null);
	}

	return cf.format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @throws NumberFormatException if the input is not valid
     * @see KMyMoneyTransaction#getNegatedBalance()
     */
    public FixedPointNumber getNegatedBalance() throws NumberFormatException {
	return getBalance().multiply(new FixedPointNumber("-100/100"));
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see KMyMoneyTransaction#getNegatedBalanceFormatted()
     */
    public String getNegatedBalanceFormatted() throws NumberFormatException {
	return getCurrencyFormat().format(getNegatedBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @see KMyMoneyTransaction#getNegatedBalanceFormatted(java.util.Locale)
     */
    public String getNegatedBalanceFormatted(final Locale loc) throws NumberFormatException {
	NumberFormat nf = NumberFormat.getInstance(loc);
	if ( getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
	    nf.setCurrency(Currency.getInstance(getQualifSecCurrID().getCode()));
	} else {
	    nf.setCurrency(null);
	}

	return nf.format(getNegatedBalance());
    }

    /**
     * @see KMyMoneyTransaction#getID()
     */
    public KMMTrxID getID() {
	return new KMMTrxID(jwsdpPeer.getId());
    }

    /**
     * @see KMyMoneyTransaction#getMemo()
     */
    public String getMemo() {
	return jwsdpPeer.getMemo();
    }

    // ----------------------------

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public TRANSACTION getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @see KMyMoneyTransaction#getKMyMoneyFile()
     */
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return kmmFile;
    }

    // ----------------------------

    /**
     * @see #getSplits()
     */
    protected List<KMyMoneyTransactionSplit> mySplits = null;

    /**
     * @param impl the split to add to mySplits
     */
    protected void addSplit(final KMyMoneyTransactionSplitImpl impl) {
	if (!jwsdpPeer.getSPLITS().getSPLIT().contains(impl.getJwsdpPeer())) {
	    jwsdpPeer.getSPLITS().getSPLIT().add(impl.getJwsdpPeer());
	}

	Collection<KMyMoneyTransactionSplit> splits = getSplits();
	if (!splits.contains(impl)) {
	    splits.add(impl);
	}

    }

    /**
     * @see KMyMoneyTransaction#getSplitsCount()
     */
    public int getSplitsCount() {
	return getSplits().size();
    }

    /**
     * @see KMyMoneyTransaction#getSplitByID(java.lang.String)
     */
    public KMyMoneyTransactionSplit getSplitByID(final String id) {
	for (KMyMoneyTransactionSplit split : getSplits()) {
	    if (split.getID().equals(id)) {
		return split;
	    }

	}
	return null;
    }

    /**
     * @throws SplitNotFoundException 
     * @see KMyMoneyTransaction#getFirstSplit()
     */
    public KMyMoneyTransactionSplit getFirstSplit() throws SplitNotFoundException {
	if ( getSplits().size() == 0 )
	    throw new SplitNotFoundException();
	
	Iterator<KMyMoneyTransactionSplit> iter = getSplits().iterator();
	return iter.next();
    }

    /**
     * @throws SplitNotFoundException 
     * @see KMyMoneyTransaction#getSecondSplit()
     */
    public KMyMoneyTransactionSplit getSecondSplit() throws SplitNotFoundException {
	if ( getSplits().size() <= 1 )
	    throw new SplitNotFoundException();
	
	Iterator<KMyMoneyTransactionSplit> iter = getSplits().iterator();
	iter.next();
	return iter.next();
    }

    /**
     * @see KMyMoneyTransaction#getSplits()
     */
    public List<KMyMoneyTransactionSplit> getSplits() {
    	return getSplits(false);
    }

    public List<KMyMoneyTransactionSplit> getSplits(final boolean addToAcct) {
	if (mySplits == null) {
	    initSplits(addToAcct);
	}
	return mySplits;
    }

    private void initSplits(final boolean addToAcct) {
	    List<SPLIT> jwsdpSplits = jwsdpPeer.getSPLITS().getSPLIT();

	    mySplits = new ArrayList<KMyMoneyTransactionSplit>();
	    for (SPLIT element : jwsdpSplits) {
		mySplits.add(createSplit(element,
								 addToAcct));
	    }
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     */
    protected KMyMoneyTransactionSplitImpl createSplit(
    		final SPLIT jwsdpSplt, 
    		final boolean addToAcct) {
	return new KMyMoneyTransactionSplitImpl(jwsdpSplt, this, 
											addToAcct);
    }

    /**
     * @see KMyMoneyTransaction#getEntryDate()
     */
    public LocalDate getEntryDate() {
	if (entryDate == null) {
	    String dateStr = jwsdpPeer.getEntrydate();
	    entryDate = LocalDate.parse(dateStr);
	}

	return entryDate;
    }

    /**
     * The Currency-Format to use if no locale is given.
     *
     * @return default currency-format with the transaction's currency set
     */
    protected NumberFormat getCurrencyFormat() {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	    if ( getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) { 
	    	currencyFormat.setCurrency(Currency.getInstance(getQualifSecCurrID().getCode()));
	    } else {
			currencyFormat = NumberFormat.getInstance();
	    }

	}
	return currencyFormat;
    }

    /**
     * @see KMyMoneyTransaction#getDatePostedFormatted()
     */
    public String getDatePostedFormatted() {
	return DateFormat.getDateInstance().format(getDatePosted());
    }

    /**
     * @see KMyMoneyTransaction#getDatePosted()
     */
    public LocalDate getDatePosted() {
	if (postDate == null) {
	    XMLGregorianCalendar cal = jwsdpPeer.getPostdate();
	    postDate = LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
	}

	return postDate;
    }

    // ---------------------------------------------------------------

	/**
	 * @param name the name of the user-defined attribute
	 * @return the value or null if not set
	 */
	public String getUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(kvpList, name);
	}

    /**
     * @return all keys that can be used with
     *         ${@link #getUserDefinedAttribute(String)}}.
     */
	public Collection<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(kvpList);
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyTransactionImpl [");

	buffer.append("id=");
	buffer.append(getID());

	// ::TODO: That only works in simple cases --
	// need a more generic approach
	buffer.append(", amount=");
	try {
	    buffer.append(getFirstSplit().getValueFormatted());
	} catch (SplitNotFoundException e) {
	    buffer.append("ERROR");
	}

	buffer.append(", description='");
	buffer.append(getMemo() + "'");

	buffer.append(", #splits=");
	buffer.append(getSplitsCount());

	buffer.append(", post-date=");
	try {
	    buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDatePosted().toString());
	}

	buffer.append(", entry-date=");
	try {
	    buffer.append(getEntryDate().format(DATE_ENTERED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getEntryDate().toString());
	}

	buffer.append("]");

	return buffer.toString();
    }

    /**
     * sorts primarily on the date the transaction happened and secondarily on the
     * date it was entered.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final KMyMoneyTransaction otherTrx) {
	try {
	    int compare = otherTrx.getDatePosted().compareTo(getDatePosted());
	    if (compare != 0) {
		return compare;
	    }

	    return otherTrx.getEntryDate().compareTo(getEntryDate());
	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

}
