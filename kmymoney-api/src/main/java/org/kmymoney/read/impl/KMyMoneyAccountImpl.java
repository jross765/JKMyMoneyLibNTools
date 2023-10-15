package org.kmymoney.read.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kmymoney.currency.CurrencyNameSpace;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyObject;
import org.kmymoney.read.KMyMoneyTransactionSplit;

/**
 * Implementation of GnucashAccount that used a
 * jwsdp-generated backend.
 */
public class KMyMoneyAccountImpl extends SimpleAccount 
                                implements KMyMoneyAccount 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyAccountImpl.class);

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public KMyMoneyAccountImpl(final KMYMONEYFILE.ACCOUNTS.ACCOUNT peer, final KMyMoneyFile gncFile) {
	super(gncFile);

	jwsdpPeer = peer;
	// ::TODO
	// file = gncfile;
    }

    /**
     * the JWSDP-object we are facading.
     */
    private KMYMONEYFILE.ACCOUNTS.ACCOUNT jwsdpPeer;

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
     * @return "ISO4217" for a currency "FUND" or a fond,...
     * @see KMyMoneyAccount#getCurrencyNameSpace()
     */
    public String getCurrency() {
	if (jwsdpPeer.getCurrency() == null) {
	    return CurrencyNameSpace.NAMESPACE_CURRENCY; // default-currency because gnucash 2.2 has no currency on the root-account
	}
	return jwsdpPeer.getCurrency();
    }

    /**
     * @see KMyMoneyAccount#getDescription()
     */
    public String getDescription() {
	return jwsdpPeer.getDescription();
    }

    /**
     * @see KMyMoneyAccount#getType()
     */
    public Byte getType() {
	return jwsdpPeer.getType();
    }

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
    public KMYMONEYFILE.ACCOUNTS.ACCOUNT getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    protected void setJwsdpPeer(final KMYMONEYFILE.ACCOUNTS.ACCOUNT newPeer) {
	if (newPeer == null) {
	    throw new IllegalArgumentException("null not allowed for field this.jwsdpPeer");
	}

	jwsdpPeer = newPeer;
    }

}
