package org.kmymoney.api.read.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.kmymoney.api.read.impl.hlp.SimpleAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of KMyMoneyAccount that used a
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
    protected ACCOUNT jwsdpPeer;

    // ---------------------------------------------------------------

    // protected KMyMoneyObjectImpl helper;

    /**
     * The splits of this transaction. May not be fully initialized during loading
     * of the gnucash-file.
     *
     * @see #mySplitsNeedSorting
     */
    private final List<KMyMoneyTransactionSplit> mySplits = new ArrayList<KMyMoneyTransactionSplit>();

    /**
     * If {@link #mySplits} needs to be sorted because it was modified. Sorting is
     * done in a lazy way.
     */
    private boolean mySplitsNeedSorting = false;
    
    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param kmmFile the file to register under
     */
    @SuppressWarnings("exports")
    public KMyMoneyAccountImpl(final ACCOUNT peer, final KMyMoneyFile kmmFile) {
	super(kmmFile);

	jwsdpPeer = peer;
	
	// helper = new KMyMoneyObjectImpl(kmmFile);
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyAccount#getID()
     */
    public KMMComplAcctID getID() {
	// CAUTION: In the KMyMoney file, the prefix for the special top-level accounts
	// is always "AStd::" (two colons).
	// However, the method jwsdpPeer.getId() under certain circumstances returns this 
	// special ID with "__" (two underscores). (I cannot explain why at the moment; 
	// it actually should not happen.) In these cases, we have to replace the 
	// double-underscore by double-colon
	if ( jwsdpPeer.getId().startsWith(KMMComplAcctID.SPEC_PREFIX.replace("::", "__")))
	    return new KMMComplAcctID(jwsdpPeer.getId().replace("__", "::"));
	else
	    return new KMMComplAcctID(jwsdpPeer.getId());
    }

    /**
     * @see KMyMoneyAccount#getParentAccountID()
     */
    public KMMComplAcctID getParentAccountID() {
	try {
	    // Cf. getID()
	    if ( jwsdpPeer.getParentaccount().startsWith(KMMComplAcctID.SPEC_PREFIX.replace("::", "__")))
		return new KMMComplAcctID(jwsdpPeer.getParentaccount().replace("__", "::"));
	    else
		return new KMMComplAcctID(jwsdpPeer.getParentaccount());
	} catch ( Exception exc ) {
	    return null;
	}
    }

    /**
     * @see KMyMoneyAccount#getChildren()
     */
    public Collection<KMyMoneyAccount> getChildren() {
	return getKMyMoneyFile().getAccountsByParentID(getID());
    }

    /**
     * @see KMyMoneyAccount#getName()
     */
    public String getName() {
	return jwsdpPeer.getName();
    }

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

	KMMQualifSpltID kmmSpltID = new KMMQualifSpltID(split.getTransaction().getID(), split.getID());
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
    public KMMQualifSecCurrID getSecCurrID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

	KMMQualifSecCurrID result = null;

	if ( jwsdpPeer.getCurrency().startsWith("E0") ) { // ::MAGIC
	    result = new KMMQualifSecID(jwsdpPeer.getCurrency());
	} else {
	    result = new KMMQualifCurrID(jwsdpPeer.getCurrency());
	}

	return result;
    }
    
    // ---------------------------------------------------------------

	/**
	 * @param name the name of the user-defined attribute
	 * @return the value or null if not set
	 */
	public String getUserDefinedAttribute(final String name) {
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(kvpList, name);
	}

    /**
     * @return all keys that can be used with
     *         ${@link #getUserDefinedAttribute(String)}}.
     */
	public Collection<String> getUserDefinedAttributeKeys() {
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(kvpList);
	}

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyAccountImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", type=");
	try {
	    buffer.append(getType());
	} catch (UnknownAccountTypeException e) {
	    buffer.append("ERROR");
	}
	
	buffer.append(", qualif-name='");
	buffer.append(getQualifiedName() + "'");
	
	buffer.append(", security/currency='");
	try {
	    buffer.append(getSecCurrID() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR");
	}
	
	buffer.append("]");
	
	return buffer.toString();
    }
    
}
