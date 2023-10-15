package org.kmymoney.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.kmymoney.Const;
import org.kmymoney.currency.CurrencyNameSpace;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.SplitNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashTransaction that uses JWSDP.
 */
public class KMyMoneyTransactionImpl implements KMyMoneyTransaction 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    private KMYMONEYFILE.TRANSACTIONS.TRANSACTION jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final KMyMoneyFile file;

    // ---------------------------------------------------------------

    /**
     * Create a new Transaction, facading a JWSDP-transaction.
     *
     * @param peer    the JWSDP-object we are facading.
     * @param gncFile the file to register under
     * @see #jwsdpPeer
     */
    @SuppressWarnings("exports")
    public KMyMoneyTransactionImpl(
	    final KMYMONEYFILE.TRANSACTIONS.TRANSACTION peer, 
	    final KMyMoneyFile gncFile) {

	jwsdpPeer = peer;
	file = gncFile;

    }

    // Copy-constructor
    public KMyMoneyTransactionImpl(final KMyMoneyTransaction trx) {
	jwsdpPeer = trx.getJwsdpPeer();
	file = trx.getKMyMoneyFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransaction#isBalanced()
     */
    public boolean isBalanced() {

	return getBalance().equals(new FixedPointNumber());

    }

    /**
     * @return "ISO4217" for a currency "FUND" or a fond,...
     * @see KMyMoneyAccount#getCurrencyNameSpace()
     */
    public String getCurrencyNameSpace() {
	return jwsdpPeer.getCurrency().getCmdtySpace();
    }

    /**
     * @see KMyMoneyAccount#getCurrencyID()
     */
    public String getCurrencyID() {
	return jwsdpPeer.getCurrency().getCmdtyId();
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
	if (getCurrencyNameSpace().equals(CurrencyNameSpace.NAMESPACE_CURRENCY)) {
	    cf.setCurrency(Currency.getInstance(getCurrencyID()));
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
	NumberFormat cf = NumberFormat.getInstance(loc);
	if (getCurrencyNameSpace().equals(CurrencyNameSpace.NAMESPACE_CURRENCY)) {
	    cf.setCurrency(Currency.getInstance(getCurrencyID()));
	} else {
	    cf.setCurrency(null);
	}

	return cf.format(getNegatedBalance());
    }

    /**
     * @see KMyMoneyTransaction#getId()
     */
    public String getId() {
	return jwsdpPeer.getId();
    }

    /**
     * @see KMyMoneyTransaction#getDescription()
     */
    public String getDescription() {
	return jwsdpPeer.getDescription();
    }

    // ----------------------------

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GncTransaction getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @see KMyMoneyTransaction#getKMyMoneyFile()
     */
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return file;
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
	    if (split.getId().equals(id)) {
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
	if (mySplits == null) {
	    List<KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT> jwsdpSplits = jwsdpPeer.getSPLITS().getSPLIT();

	    mySplits = new ArrayList<KMyMoneyTransactionSplit>(jwsdpSplits.size());
	    for (KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT element : jwsdpSplits) {

		mySplits.add(createSplit(element));
	    }
	}
	return mySplits;
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param element the jaxb-data
     * @return the new split-instance
     */
    protected KMyMoneyTransactionSplitImpl createSplit(final KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT element) {
	return new KMyMoneyTransactionSplitImpl(element, this);
    }

    /**
     * @see KMyMoneyTransaction#getDateEntered()
     */
    protected static final DateTimeFormatter DATE_ENTERED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);

    /**
     * @see KMyMoneyTransaction#getDateEntered()
     */
    protected ZonedDateTime dateEntered;

    /**
     * @see KMyMoneyTransaction#getDateEntered()
     */
    public ZonedDateTime getDateEntered() {
	if (dateEntered == null) {
	    String s = jwsdpPeer.getDateEntered().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		dateEntered = ZonedDateTime.parse(s, DATE_ENTERED_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException("unparsable date '" + s + "' in transaction!");
		ex.initCause(e);
		throw ex;
	    }
	}

	return dateEntered;
    }

    /**
     * format of the dataPosted-field in the xml(jwsdp)-file.
     */
    private static final DateTimeFormatter DATE_POSTED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);

    /**
     * @see KMyMoneyTransaction#getDatePosted()
     */
    protected ZonedDateTime datePosted;

    /**
     * The Currency-Format to use if no locale is given.
     */
    protected NumberFormat currencyFormat;

    /**
     * The Currency-Format to use if no locale is given.
     *
     * @return default currency-format with the transaction's currency set
     */
    protected NumberFormat getCurrencyFormat() {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	    if (getCurrencyNameSpace().equals(CurrencyNameSpace.NAMESPACE_CURRENCY)) {
		currencyFormat.setCurrency(Currency.getInstance(getCurrencyID()));
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
    public ZonedDateTime getDatePosted() {
	if (datePosted == null) {
	    String s = jwsdpPeer.getDatePosted().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		datePosted = ZonedDateTime.parse(s, DATE_POSTED_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException(
			"unparsable date '" + s + "' in transaction with id='" + getId() + "'!");
		ex.initCause(e);
		throw ex;
	    }
	}

	return datePosted;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[GnucashTransactionImpl:");

	buffer.append(" id: ");
	buffer.append(getId());

	// ::TODO: That only works in simple cases --
	// need a more generic approach
	buffer.append(" amount: ");
	try {
	    buffer.append(getFirstSplit().getValueFormatted());
	} catch (SplitNotFoundException e) {
	    buffer.append("ERROR");
	}

	buffer.append(" description: '");
	buffer.append(getDescription() + "'");

	buffer.append(" #splits: ");
	buffer.append(getSplitsCount());

	buffer.append(" date-posted: ");
	try {
	    buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDatePosted().toString());
	}

	buffer.append(" date-entered: ");
	try {
	    buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDateEntered().toString());
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

	    return otherTrx.getDateEntered().compareTo(getDateEntered());
	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

}
