package org.kmymoney.read.impl;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.basetypes.KMMSplitID;
import org.kmymoney.generated.ACCOUNT;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.UnknownAccountTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashAccount that used a
 * jwsdp-generated backend.
 */
public class KMyMoneyAccountImpl extends SimpleAccount 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyAccountImpl.class);

    // ---------------------------------------------------------------

    // ::MAGIC
    private static final int TYPE_CHECKING            = 1;
    private static final int TYPE_SAVINGS             = 2;
    private static final int TYPE_CASH                = 3;
    private static final int TYPE_CREDIT_CARD         = 4;
    private static final int TYPE_LOAN                = 5;
    private static final int TYPE_CERTIFICATE_DEPOSIT = 6;
    private static final int TYPE_INVESTMENT          = 7;
    private static final int TYPE_MONEY_MARKET        = 8;
    private static final int TYPE_ASSET               = 9;
    private static final int TYPE_LIABILITY           = 10;
    private static final int TYPE_CURRENCY            = 11;
    private static final int TYPE_INCOME              = 12;
    private static final int TYPE_EXPENSE             = 13;
    private static final int TYPE_ASSET_LOAN          = 14;
    private static final int TYPE_STOCK               = 15;
    private static final int TYPE_EQUITY              = 16;
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private ACCOUNT jwsdpPeer;

    // ---------------------------------------------------------------

    // protected KMyMoneyObjectImpl helper;

    /**
     * The splits of this transaction. May not be fully initialized during loading
     * of the gnucash-file.
     *
     * @see #mySplitsNeedSorting
     */
    private final List<KMyMoneyTransactionSplit> mySplits = new LinkedList<KMyMoneyTransactionSplit>();

    /**
     * If {@link #mySplits} needs to be sorted because it was modified. Sorting is
     * done in a lazy way.
     */
    private boolean mySplitsNeedSorting = false;
    
    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public KMyMoneyAccountImpl(final ACCOUNT peer, final KMyMoneyFile gncFile) {
	super(gncFile);

	jwsdpPeer = peer;
	
	// helper = new KMyMoneyObjectImpl(gncFile);
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyAccount#getId()
     */
    public String getId() {
	return jwsdpPeer.getId();
    }

    /**
     * @see KMyMoneyAccount#getParentAccountId()
     */
    public String getParentAccountId() {
	return jwsdpPeer.getParentaccount();
    }

    /**
     * @see KMyMoneyAccount#getChildren()
     */
    public Collection<KMyMoneyAccount> getChildren() {
	return getKMyMoneyFile().getAccountsByParentID(getId());
    }

    /**
     * @see KMyMoneyAccount#getName()
     */
    public String getName() {
	return jwsdpPeer.getName();
    }

    /**
     * @see KMyMoneyAccount#getDescription()
     */
    public String getMemo() {
	return jwsdpPeer.getDescription();
    }

    public String getNumber() {
	return jwsdpPeer.getNumber();
    }

    public Type getType() throws UnknownAccountTypeException {
	
	BigInteger typeVal = jwsdpPeer.getType();
	
	if ( typeVal.intValue() == TYPE_CHECKING )
	    return Type.CHECKING;
	else if ( typeVal.intValue() == TYPE_SAVINGS )
	    return Type.SAVINGS;
	else if ( typeVal.intValue() == TYPE_CASH )
	    return Type.CASH;
	else if ( typeVal.intValue() == TYPE_CREDIT_CARD )
	    return Type.CREDIT_CARD;
	else if ( typeVal.intValue() == TYPE_LOAN )
	    return Type.LOAN;
	else if ( typeVal.intValue() == TYPE_CERTIFICATE_DEPOSIT )
	    return Type.CERTIFICATE_DEPOSIT;
	else if ( typeVal.intValue() == TYPE_INVESTMENT )
	    return Type.INVESTMENT;
	else if ( typeVal.intValue() == TYPE_MONEY_MARKET )
	    return Type.MONEY_MARKET;
	else if ( typeVal.intValue() == TYPE_ASSET )
	    return Type.ASSET;
	else if ( typeVal.intValue() == TYPE_LIABILITY )
	    return Type.LIABILITY;
	else if ( typeVal.intValue() == TYPE_CURRENCY )
	    return Type.CURRENCY;
	else if ( typeVal.intValue() == TYPE_INCOME )
	    return Type.INCOME;
	else if ( typeVal.intValue() == TYPE_EXPENSE )
	    return Type.EXPENSE;
	else if ( typeVal.intValue() == TYPE_ASSET_LOAN )
	    return Type.ASSET_LOAN;
	else if ( typeVal.intValue() == TYPE_STOCK )
	    return Type.STOCK;
	else if ( typeVal.intValue() == TYPE_EQUITY )
	    return Type.EQUITY;
	else
	    throw new UnknownAccountTypeException();
    }

    /**
     * @see KMyMoneyAccount#getTransactionSplits()
     */
    public List<KMyMoneyTransactionSplit> getTransactionSplits() {

	if (mySplitsNeedSorting) {
	    Collections.sort(mySplits);
	    mySplitsNeedSorting = false;
	}

	return mySplits;
    }

    /**
     * @see KMyMoneyAccount#addTransactionSplit(KMyMoneyTransactionSplit)
     */
    public void addTransactionSplit(final KMyMoneyTransactionSplit split) {

	KMMSplitID kmmSpltID = new KMMSplitID(split.getTransaction().getId(), split.getId());
	KMyMoneyTransactionSplit old = getTransactionSplitByID(kmmSpltID);
	if (old != null) {
	    if (old != split) {
		IllegalStateException ex = new IllegalStateException("DEBUG");
		ex.printStackTrace();
		replaceTransactionSplit(old, split);
	    }
	} else {
	    mySplits.add(split);
	    mySplitsNeedSorting = true;
	}
    }

    /**
     * For internal use only.
     *
     * @param transactionSplitByID -
     * @param impl                 -
     */
    private void replaceTransactionSplit(final KMyMoneyTransactionSplit transactionSplitByID,
	    final KMyMoneyTransactionSplit impl) {
	if (!mySplits.remove(transactionSplitByID)) {
	    throw new IllegalArgumentException("old object not found!");
	}

	mySplits.add(impl);
    }

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public ACCOUNT getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    protected void setJwsdpPeer(final ACCOUNT newPeer) {
	if (newPeer == null) {
	    throw new IllegalArgumentException("null not allowed for field this.jwsdpPeer");
	}

	jwsdpPeer = newPeer;
    }

    @Override
    public KMMSecCurrID getSecCurrID() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {

	KMMSecCurrID result = null;

	if ( jwsdpPeer.getCurrency().startsWith("E0") ) { // ::MAGIC
	    result = new KMMSecID(jwsdpPeer.getCurrency());
	} else {
	    result = new KMMCurrID(jwsdpPeer.getCurrency());
	}

	return result;
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[KMyMoneyAccountImpl:");
	
	buffer.append(" id: ");
	buffer.append(getId());
	
	buffer.append(" type: ");
	try {
	    buffer.append(getType());
	} catch (UnknownAccountTypeException e) {
	    buffer.append("ERROR");
	}
	
	buffer.append(" qualif-name: '");
	buffer.append(getQualifiedName() + "'");
	
	buffer.append(" security/currency: '");
	try {
	    buffer.append(getSecCurrID() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR");
	}
	
	buffer.append("]");
	
	return buffer.toString();
    }
    
}
