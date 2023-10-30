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
import org.kmymoney.generated.ACCOUNT;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyTransactionSplit;
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

    /**
     * the JWSDP-object we are facading.
     */
    private ACCOUNT jwsdpPeer;

    // ---------------------------------------------------------------

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
	// ::TODO
	// file = gncfile;
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

    /**
     * @see KMyMoneyAccount#getType()
     */
    public BigInteger getType() {
	return jwsdpPeer.getType();
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

	KMyMoneyTransactionSplit old = getTransactionSplitByID(split.getId());
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

}
