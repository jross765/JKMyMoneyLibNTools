package org.kmymoney.read.impl;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.kmymoney.Const;
import org.kmymoney.currency.CurrencyNameSpace;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashTransactionSplit that uses JWSDSP.
 */
public class KMyMoneyTransactionSplitImpl implements KMyMoneyTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionSplitImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    private KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT jwsdpPeer;

    /**
     * the transaction this split belongs to.
     */
    private final KMyMoneyTransaction myTransaction;

    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param trx  the transaction this split belongs to
     * @see #jwsdpPeer
     * @see #myTransaction
     */
    @SuppressWarnings("exports")
    public KMyMoneyTransactionSplitImpl(
	    final KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT peer, 
	    final KMyMoneyTransaction trx) {
	jwsdpPeer = peer;
	myTransaction = trx;

	KMyMoneyAccount acct = getAccount();
	if (acct == null) {
	    System.err.println("No such Account id='" + getAccountID() + "' for Transactions-Split with id '" + getId()
		    + "' description '" + getMemo() + "' in transaction with id '" + getTransaction().getId()
		    + "' description '" + getTransaction().getMemo() + "'");
	} else {
	    acct.addTransactionSplit(this);
	}

    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransactionSplit#getAction()
     */
    public String getAction() {
	if (getJwsdpPeer().getAction() == null) {
	    return "";
	}

	return getJwsdpPeer().getAction();
    }

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @param newPeer the JWSDP-object we are facading.
     */
    protected void setJwsdpPeer(final KMYMONEYFILE.TRANSACTIONS.TRANSACTION.SPLITS.SPLIT newPeer) {
	if (newPeer == null) {
	    throw new IllegalArgumentException("null not allowed for field this.jwsdpPeer");
	}

	jwsdpPeer = newPeer;
    }

    /**
     * @see KMyMoneyTransactionSplit#getId()
     */
    public String getId() {
	return jwsdpPeer.getId();
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountID()
     */
    public String getAccountID() {
	String id = jwsdpPeer.getAccount();
	assert id != null;
	return id;
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccount()
     */
    public KMyMoneyAccount getAccount() {
	return myTransaction.getKMyMoneyFile().getAccountByID(getAccountID());
    }

    /**
     * @see KMyMoneyTransactionSplit#getTransaction()
     */
    public KMyMoneyTransaction getTransaction() {
	return myTransaction;
    }

    /**
     * @see KMyMoneyTransactionSplit#getValue()
     */
    public FixedPointNumber getValue() {
	return new FixedPointNumber(jwsdpPeer.getValue());
    }

    /**
     * @return The currencyFormat for the quantity to use when no locale is given.
     */
    protected NumberFormat getSharesCurrencyFormat() {

	return ((KMyMoneyAccountImpl) getAccount()).getCurrencyFormat();
    }

    /**
     * @return the currency-format of the transaction
     */
    public NumberFormat getValueCurrencyFormat() {

	return ((KMyMoneyTransactionImpl) getTransaction()).getCurrencyFormat();
    }

    /**
     * @see KMyMoneyTransactionSplit#getValueFormatted()
     */
    public String getValueFormatted() {
	return getValueCurrencyFormat().format(getValue());
    }

    /**
     * @see KMyMoneyTransactionSplit#getValueFormatted(java.util.Locale)
     */
    public String getValueFormatted(final Locale locale) {

	NumberFormat cf = NumberFormat.getInstance(locale);
	if (getTransaction().getCurrencyNameSpace().equals(CurrencyNameSpace.NAMESPACE_CURRENCY)) {
	    cf.setCurrency(Currency.getInstance(getTransaction().getCurrencyID()));
	} else {
	    cf = NumberFormat.getNumberInstance(locale);
	}

	return cf.format(getValue());
    }

    /**
     * @see KMyMoneyTransactionSplit#getValueFormattedForHTML()
     */
    public String getValueFormattedForHTML() {
	return getValueFormatted().replaceFirst("€", "&euro;");
    }

    /**
     * @see KMyMoneyTransactionSplit#getValueFormattedForHTML(java.util.Locale)
     */
    public String getValueFormattedForHTML(final Locale locale) {
	return getValueFormatted(locale).replaceFirst("€", "&euro;");
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountBalance()
     */
    public FixedPointNumber getAccountBalance() {
	return getAccount().getBalance(this);
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountBalanceFormatted()
     */
    public String getAccountBalanceFormatted() {
	return ((KMyMoneyAccountImpl) getAccount()).getCurrencyFormat().format(getAccountBalance());
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountBalanceFormatted(java.util.Locale)
     */
    public String getAccountBalanceFormatted(final Locale locale) {
	return getAccount().getBalanceFormatted(locale);
    }

    /**
     * @see KMyMoneyTransactionSplit#getShares()
     */
    public FixedPointNumber getShares() {
	return new FixedPointNumber(jwsdpPeer.getShares());
    }

    /**
     * The value is in the currency of the account!
     */
    public String getSharesFormatted() {
	return getSharesCurrencyFormat().format(getShares());
    }

    /**
     * The value is in the currency of the account!
     *
     * @param locale the locale to format to
     * @return the formatted number
     */
    public String getSharesFormatted(final Locale locale) {
	if (getTransaction().getCurrencyNameSpace().equals(CurrencyNameSpace.NAMESPACE_CURRENCY)) {
	    return NumberFormat.getNumberInstance(locale).format(getShares());
	}

	NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
	nf.setCurrency(Currency.getInstance(getAccount().getCurrency()));
	return nf.format(getShares());
    }

    /**
     * The value is in the currency of the account!
     */
    public String getSharesFormattedForHTML() {
	return getSharesFormatted().replaceFirst("€", "&euro;");
    }

    /**
     * The value is in the currency of the account!
     */
    public String getSharesFormattedForHTML(final Locale locale) {
	return getSharesFormatted(locale).replaceFirst("€", "&euro;");
    }

    /**
     * {@inheritDoc}
     *
     * @see KMyMoneyTransactionSplit#getDescription()
     */
    public String getMemo() {
	if (jwsdpPeer.getMemo() == null) {
	    return "";
	}
	return jwsdpPeer.getMemo();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[KMyMoneyTransactionSplitImpl:");

	buffer.append(" id: ");
	buffer.append(getId());

	buffer.append(" Action: '");
	buffer.append(getAction() + "'");

	buffer.append(" transaction-id: ");
	buffer.append(getTransaction().getId());

	buffer.append(" accountID: ");
	buffer.append(getAccountID());

	buffer.append(" account: ");
	KMyMoneyAccount account = getAccount();
	buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");

	buffer.append(" memo: '");
	buffer.append(getMemo() + "'");

	buffer.append(" transaction-description: '");
	buffer.append(getTransaction().getMemo() + "'");

	buffer.append(" value: ");
	buffer.append(getValue());

	buffer.append(" quantity: ");
	buffer.append(getShares());

	buffer.append("]");
	return buffer.toString();
    }

    /**
     * @see java.lang.Comparable#compareTo(KMyMoneyTransactionSplit)
     */
    public int compareTo(final KMyMoneyTransactionSplit otherSplt) {
	try {
	    KMyMoneyTransaction otherTrans = otherSplt.getTransaction();
	    int c = otherTrans.compareTo(getTransaction());
	    if (c != 0) {
		return c;
	    }

	    c = otherSplt.getId().compareTo(getId());
	    if (c != 0) {
		return c;
	    }

	    if (otherSplt != this) {
		System.err.println("Duplicate transaction-split-id!! " + otherSplt.getId() + "["
			+ otherSplt.getClass().getName() + "] and " + getId() + "[" + getClass().getName() + "]\n"
			+ "split0=" + otherSplt.toString() + "\n" + "split1=" + toString() + "\n");
		IllegalStateException x = new IllegalStateException("DEBUG");
		x.printStackTrace();

	    }

	    return 0;

	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

}
