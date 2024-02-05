package org.kmymoney.api.read.impl;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.MappingException;
import org.kmymoney.api.read.UnknownSplitStateException;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of KMyMoneyTransactionSplit that uses JWSDSP.
 */
public class KMyMoneyTransactionSplitImpl extends KMyMoneyObjectImpl
                                          implements KMyMoneyTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionSplitImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected SPLIT jwsdpPeer;
    
    /**
     * the transaction this split belongs to.
     */
    private final KMyMoneyTransaction myTransaction;

    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param kmmFile 
     * @param trx  the transaction this split belongs to
     */
    @SuppressWarnings("exports")
    public KMyMoneyTransactionSplitImpl(
	    final SPLIT peer,
	    final KMyMoneyTransaction trx,
	    final boolean addSpltToAcct) {
    super(trx.getKMyMoneyFile());
    	
	this.jwsdpPeer = peer;
	this.myTransaction = trx;

	if ( addSpltToAcct ) {
		KMyMoneyAccount acct = getAccount();
		if (acct == null) {
		    LOGGER.error("No such Account id='" + getAccountID() + "' for Transactions-Split with id '" + getQualifID()
			    + "' description '" + getMemo() + "' in transaction with id '" + getTransaction().getID()
			    + "' description '" + getTransaction().getMemo() + "'");
		} else {
		    acct.addTransactionSplit(this);
		}
	}
    }

    // ---------------------------------------------------------------

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

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyTransactionSplit#getID()
     */
    @Override
    public KMMSpltID getID() {
	return new KMMSpltID(jwsdpPeer.getId());
    }

    @Override
    public KMMQualifSpltID getQualifID() {
	return new KMMQualifSpltID(getTransactionID(), getID());
    }

    @Override
    public Action getAction() {
    	try {
    		return Action.valueOff( getActionStr() );
    	} catch (Exception e) {
    		throw new MappingException("Could not map string '" + getActionStr() + "' to Action enum");
    	}
    }

    /**
     * @see KMyMoneyTransactionSplit#getAction()
     */
    public String getActionStr() {
    	if (getJwsdpPeer().getAction() == null) {
    		return "";
    	}

    	return getJwsdpPeer().getAction();
    }

    public State getState() throws UnknownSplitStateException {
	BigInteger reconFlag = getJwsdpPeer().getReconcileflag();
	return State.valueOff(reconFlag.intValue());
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccountID()
     */
    public KMMComplAcctID getAccountID() {
	String acctID = jwsdpPeer.getAccount();
	assert acctID != null;
	return new KMMComplAcctID(acctID);
    }

    /**
     * @see KMyMoneyTransactionSplit#getAccount()
     */
    public KMyMoneyAccount getAccount() {
    	return myTransaction.getKMyMoneyFile().getAccountByID(getAccountID());
    }

    @Override
    public KMMTrxID getTransactionID() {
    	return myTransaction.getID();
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
	if (getTransaction().getSecurity().equals("XYZ")) { // ::TODO: is currency, not security 
	    cf.setCurrency(Currency.getInstance(getTransaction().getSecurity()));
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

    public String getMemo() {
	if (jwsdpPeer.getMemo() == null) {
	    return "";
	}
	return jwsdpPeer.getMemo();
    }
    
    // ---------------------------------------------------------------

    public int compareTo(final KMyMoneyTransactionSplit otherSplt) {
	try {
	    KMyMoneyTransaction otherTrans = otherSplt.getTransaction();
	    int c = otherTrans.compareTo(getTransaction());
	    if (c != 0) {
		return c;
	    }

	    if ( ! otherSplt.getQualifID().equals(getQualifID()) ) {
		return otherSplt.getID().compareTo(getID());
	    }

	    if (otherSplt != this) {
		System.err.println("Duplicate transaction-split-id!! " + otherSplt.getQualifID() + "["
			+ otherSplt.getClass().getName() + "] and " + getQualifID() + "[" + getClass().getName() + "]\n"
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
	buffer.append("KMyMoneyTransactionSplitImpl [");

	buffer.append("qualif-id=");
	buffer.append(getQualifID());

	// Part of qualif-id:
	// buffer.append(" transaction-id=");
	// buffer.append(getTransaction().getID());

	buffer.append(", action=");
	try {
	    buffer.append(getAction());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(", state=");
	try {
	    buffer.append(getState());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(", account-id=");
	buffer.append(getAccountID());

//	buffer.append(", account=");
//	try {
//	    KMyMoneyAccount account = getAccount();
//	    buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");
//	} catch (Exception e) {
//	    buffer.append("ERROR");
//	}

	buffer.append(", memo='");
	buffer.append(getMemo() + "'");

	// usually not set:
	// buffer.append(" transaction-description: '");
	// buffer.append(getTransaction().getMemo() + "'");

	buffer.append(", value=");
	buffer.append(getValue());

	buffer.append(", shares=");
	buffer.append(getShares());

	buffer.append("]");
	return buffer.toString();
    }

}
