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

    private static final String PREFIX_SECURITY = "E0";  // ::MAGIC

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
    
    // ---------------------------------------------------------------

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
    @Override
    public Collection<KMyMoneyAccount> getChildren() {
    	return getKMyMoneyFile().getAccountsByParentID(getID());
    }

    @Override
    public Collection<KMyMoneyAccount> getChildrenRecursive() {
    	return getChildrenRecursiveCore(getChildren());
    }

    private static Collection<KMyMoneyAccount> getChildrenRecursiveCore(Collection<KMyMoneyAccount> accts) {
    	Collection<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();
    	
    	for ( KMyMoneyAccount acct : accts ) {
    		result.add(acct);
    		for ( KMyMoneyAccount childAcct : getChildrenRecursiveCore(acct.getChildren()) ) {
    			result.add(childAcct);
    		}
    	}
    	
    	return result;
    }

    // ---------------------------------------------------------------

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


    @Override
    public Type getType() throws UnknownAccountTypeException {
    	try {
    	    Type result = Type.valueOff( getTypeBigInt().intValue() );
    	    return result;
    	} catch ( Exception exc ) {
    	    throw new UnknownAccountTypeException();
    	}
    }

    @Override
    public BigInteger getTypeBigInt() {
    	return jwsdpPeer.getType();
    }

    @Override
	public KMMQualifSecCurrID getQualifSecCurrID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	
	KMMQualifSecCurrID result = null;
	
	if ( jwsdpPeer.getCurrency().startsWith(PREFIX_SECURITY) ) {
	    result = new KMMQualifSecID(jwsdpPeer.getCurrency());
	} else {
	    result = new KMMQualifCurrID(jwsdpPeer.getCurrency());
	}
	
	return result;
	}

	/**
     * @see KMyMoneyAccount#getTransactionSplits()
     */
    @Override
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
    public void addTransactionSplit(final KMyMoneyTransactionSplit splt) {
	KMyMoneyTransactionSplit old = getTransactionSplitByID(splt.getQualifID());
	if ( old != null ) {
	    // There already is a split with that ID
	    if ( ! old.equals(splt) ) {
			System.err.println("addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " + 
					splt.getQualifID() + "[" + splt.getClass().getName() + "] and " + 
					old.getQualifID() + "[" + old.getClass().getName() + "]\n" + 
					"new=" + splt.toString() + "\n" + 
					"old=" + old.toString());
			LOGGER.error("addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " + 
					splt.getQualifID() + "[" + splt.getClass().getName() + "] and " + 
					old.getQualifID() + "[" + old.getClass().getName() + "]\n" + 
					"new=" + splt.toString() + "\n" + 
					"old=" + old.toString());
			IllegalStateException exc = new IllegalStateException("DEBUG");
			exc.printStackTrace();
			replaceTransactionSplit(old, splt);
	    }
	} else {
	    // There is no split with that ID yet
	    mySplits.add(splt);
	    mySplitsNeedSorting = true;
	}
    }

    /**
     * For internal use only.
     *
     * @param splt
     */
    private void replaceTransactionSplit(final KMyMoneyTransactionSplit splt,
	    final KMyMoneyTransactionSplit impl) {
    	if ( ! mySplits.remove(splt) ) {
    		throw new IllegalArgumentException("old object not found!");
    	}

    	mySplits.add(impl);
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
	    buffer.append(getQualifSecCurrID() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR");
	}
	
	buffer.append("]");
	
	return buffer.toString();
    }
    
}
