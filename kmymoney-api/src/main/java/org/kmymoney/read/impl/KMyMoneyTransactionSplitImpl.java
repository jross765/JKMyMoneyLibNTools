package org.kmymoney.read.impl;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMComplAcctID;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.generated.SPLIT;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.UnknownSplitActionException;
import org.kmymoney.read.UnknownSplitStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashTransactionSplit that uses JWSDSP.
 */
public class KMyMoneyTransactionSplitImpl implements KMyMoneyTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionSplitImpl.class);

    // ::MAGIC
    private static final int ACTION_UNKNOWN           = -1;
    private static final int ACTION_CHECK             = 0;
    private static final int ACTION_DEPOSIT           = 1;
    private static final int ACTION_TRANSFER          = 2;
    private static final int ACTION_WITHDRAWAL        = 3;
    private static final int ACTION_ATM               = 4;
    private static final int ACTION_AMORTIZATION      = 5;
    private static final int ACTION_INTEREST          = 6;
    private static final int ACTION_BUY_SHARES        = 7;
    private static final int ACTION_DIVIDEND          = 8;
    private static final int ACTION_REINVEST_DIVIDEND = 9;
    private static final int ACTION_YIELD             = 10;
    private static final int ACTION_ADD_SHARES        = 11;
    private static final int ACTION_SPLIT_SHARES      = 12;
    private static final int ACTION_INTEREST_INCOME   = 13;
    
    // ::MAGIC
    public static final int STATE_UNKNOWN        = -1;
    public static final int STATE_NOT_RECONCILED = 0;
    public static final int STATE_CLEARED        = 1;
    public static final int STATE_RECONCILED     = 2;
    public static final int STATE_FROZEN         = 3;
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private SPLIT jwsdpPeer;

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
	    final SPLIT peer, 
	    final KMyMoneyTransaction trx) {
	jwsdpPeer = peer;
	myTransaction = trx;

	// ::CHECK
	// ::TODO
	KMyMoneyAccount acct = getAccount();
	if (acct == null) {
	    System.err.println("No such Account id='" + getAccountId() + "' for Transactions-Split with id '" + getId()
		    + "' description '" + getMemo() + "' in transaction with id '" + getTransaction().getId()
		    + "' description '" + getTransaction().getMemo() + "'");
	} else {
	    acct.addTransactionSplit(this);
	}

    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransactionSplit#getId()
     */
    @Override
    public String getId() {
	return jwsdpPeer.getId();
    }

    @Override
    public KMMQualifSplitID getQualifId() {
	return new KMMQualifSplitID(getTransactionId(), getId());
    }

    public Action getAction() throws UnknownSplitActionException {
	
	String actionStr = getJwsdpPeer().getAction();
	int actionVal = Integer.parseInt(actionStr);
	
	if ( actionVal == ACTION_UNKNOWN )
	    return Action.UNKNOWN;
	else if ( actionVal == ACTION_CHECK )
	    return Action.CHECK;
	else if ( actionVal == ACTION_DEPOSIT )
	    return Action.DEPOSIT;
	else if ( actionVal == ACTION_TRANSFER )
	    return Action.TRANSFER;
	else if ( actionVal == ACTION_WITHDRAWAL )
	    return Action.WITHDRAWAL;
	else if ( actionVal == ACTION_ATM )
	    return Action.ATM;
	else if ( actionVal == ACTION_AMORTIZATION )
	    return Action.AMORTIZATION;
	else if ( actionVal == ACTION_INTEREST )
	    return Action.INTEREST;
	else if ( actionVal == ACTION_BUY_SHARES )
	    return Action.BUY_SHARES;
	else if ( actionVal == ACTION_DIVIDEND )
	    return Action.DIVIDEND;
	else if ( actionVal == ACTION_REINVEST_DIVIDEND )
	    return Action.REINVEST_DIVIDEND;
	else if ( actionVal == ACTION_YIELD )
	    return Action.YIELD;
	else if ( actionVal == ACTION_ADD_SHARES )
	    return Action.ADD_SHARES;
	else if ( actionVal == ACTION_SPLIT_SHARES )
	    return Action.SPLIT_SHARES;
	else if ( actionVal == ACTION_INTEREST_INCOME )
	    return Action.INTEREST_INCOME;
	else
	    throw new UnknownSplitActionException();
    }

    public State getState() throws UnknownSplitStateException {
	
	// ::TODO
	// int stateVal = jwsdpPeer.getState();
	int stateVal = STATE_UNKNOWN;
	
	if ( stateVal == STATE_UNKNOWN )
	    return State.UNKNOWN;
	else if ( stateVal == STATE_NOT_RECONCILED )
	    return State.NOT_RECONCILED;
	else if ( stateVal == STATE_CLEARED )
	    return State.CLEARED;
	else if ( stateVal == STATE_RECONCILED )
	    return State.RECONCILED;
	else if ( stateVal == STATE_FROZEN )
	    return State.FROZEN;
	else
	    throw new UnknownSplitStateException();
    }

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public SPLIT getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @param newPeer the JWSDP-object we are facading.
     */
    protected void setJwsdpPeer(final SPLIT newPeer) {
	if (newPeer == null) {
	    throw new IllegalArgumentException("null not allowed for field this.jwsdpPeer");
	}

	jwsdpPeer = newPeer;
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountId()
     */
    public KMMComplAcctID getAccountId() {
	String id = jwsdpPeer.getAccount();
	assert id != null;
	return new KMMComplAcctID(id);
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccount()
     */
    public KMyMoneyAccount getAccount() {
	return myTransaction.getKMyMoneyFile().getAccountById(getAccountId());
    }

    @Override
    public String getTransactionId() {
	return myTransaction.getId();
    }

    /**
     * @see KMyMoneyTransactionSplit#getTransaction()
     */
    @Override
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
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     */
    protected NumberFormat getSharesCurrencyFormat() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

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
    public String getValueFormatted(final Locale lcl) {

	NumberFormat cf = NumberFormat.getInstance(lcl);
	if (getTransaction().getCommodity().equals("XYZ")) { // ::TODO: is currency, not security 
	    cf.setCurrency(Currency.getInstance(getTransaction().getCommodity()));
	} else {
	    cf = NumberFormat.getNumberInstance(lcl);
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
    public String getValueFormattedForHTML(final Locale lcl) {
	return getValueFormatted(lcl).replaceFirst("€", "&euro;");
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountBalance()
     */
    public FixedPointNumber getAccountBalance() {
	return getAccount().getBalance(this);
    }

    /**
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     * @see KMyMoneyTransactionSplit#getAccountBalanceFormatted()
     */
    public String getAccountBalanceFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return ((KMyMoneyAccountImpl) getAccount()).getCurrencyFormat().format(getAccountBalance());
    }

    /**
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     * @see KMyMoneyTransactionSplit#getAccountBalanceFormatted(java.util.Locale)
     */
    public String getAccountBalanceFormatted(final Locale lcl) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getAccount().getBalanceFormatted(lcl);
    }

    /**
     * @see KMyMoneyTransactionSplit#getShares()
     */
    public FixedPointNumber getShares() {
	return new FixedPointNumber(jwsdpPeer.getShares());
    }

    /**
     * The value is in the currency of the account!
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     */
    public String getSharesFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getSharesCurrencyFormat().format(getShares());
    }

    /**
     * The value is in the currency of the account!
     *
     * @param lcl the locale to format to
     * @return the formatted number
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     */
    public String getSharesFormatted(final Locale lcl) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	NumberFormat nf = NumberFormat.getCurrencyInstance(lcl);
	if ( getAccount().getSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
	    nf.setCurrency(new KMMQualifCurrID(getAccount().getSecCurrID()).getCurrency());
	    return nf.format(getShares());
	}
	else {
	    return nf.format(getShares()) + " " + getAccount().getSecCurrID().toString(); 
	}
    }

    /**
     * The value is in the currency of the account!
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     */
    public String getSharesFormattedForHTML() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getSharesFormatted().replaceFirst("€", "&euro;");
    }

    /**
     * The value is in the currency of the account!
     * @throws InvalidQualifSecCurrIDException 
     * @throws InvalidQualifSecCurrTypeException 
     */
    public String getSharesFormattedForHTML(final Locale lcl) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getSharesFormatted(lcl).replaceFirst("€", "&euro;");
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
    
    // ---------------------------------------------------------------

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

	    if ( ! otherSplt.getQualifId().equals(getQualifId()) ) {
		return otherSplt.getId().compareTo(getId());
	    }

	    if (otherSplt != this) {
		System.err.println("Duplicate transaction-split-id!! " + otherSplt.getQualifId() + "["
			+ otherSplt.getClass().getName() + "] and " + getQualifId() + "[" + getClass().getName() + "]\n"
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

    // ---------------------------------------------------------------

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[KMyMoneyTransactionSplitImpl:");

	buffer.append(" qualif-id: ");
	buffer.append(getQualifId());

//	buffer.append(" transaction-id: ");
//	buffer.append(getTransaction().getId());
//
	buffer.append(" Action: ");
	try {
	    buffer.append(getAction());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(" account-id: ");
	buffer.append(getAccountId());

	buffer.append(" account: ");
	try {
	    KMyMoneyAccount account = getAccount();
	    buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(" memo: '");
	buffer.append(getMemo() + "'");

	// usually not set:
	// buffer.append(" transaction-description: '");
	// buffer.append(getTransaction().getMemo() + "'");

	buffer.append(" value: ");
	buffer.append(getValue());

	buffer.append(" shares: ");
	buffer.append(getShares());

	buffer.append("]");
	return buffer.toString();
    }

}
