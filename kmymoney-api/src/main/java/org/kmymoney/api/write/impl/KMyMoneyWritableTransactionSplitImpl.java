package org.kmymoney.api.write.impl;

import java.text.ParseException;

import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.read.IllegalTransactionSplitActionException;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction-Split that can be newly created or removed from it's transaction.
 */
public class KMyMoneyWritableTransactionSplitImpl extends KMyMoneyTransactionSplitImpl 
                                                  implements KMyMoneyWritableTransactionSplit 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableTransactionImpl.class);

    // ---------------------------------------------------------------

	/**
	 * Our helper to implement the KMyMoneyWritableObject-interface.
	 */
	private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

	// ---------------------------------------------------------------
	
	/**
	 * @param jwsdpPeer   the JWSDP-object we are facading.
	 * @param trx the transaction we belong to
     * @param addSpltToAcct 
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableTransactionSplitImpl(
			final SPLIT jwsdpPeer,
			final KMyMoneyWritableTransaction trx, 
    		final boolean addSpltToAcct) {
		super(jwsdpPeer, trx, 
			  addSpltToAcct);
	}

    /**
	 * create a new split and and add it to the given transaction.
	 * @param trx transaction the transaction we will belong to
	 * @param acct     the account we take money (or other things) from or give
	 *                    it to
	 */
	public KMyMoneyWritableTransactionSplitImpl(
			final KMyMoneyWritableTransactionImpl trx,
			final KMyMoneyAccount acct) {
		super(createTransactionSplit_int(trx.getWritableFile(), 
				                         trx, acct, 
				                         trx.getNewSplitID()),
		      trx,
		      true);

		// this is a workaound.
		// if super does account.addSplit(this) it adds an instance on
		// KMyMoneyTransactionSplitImpl that is "!=
		// (KMyMoneyTransactionSplitWritingImpl)this";
		// thus we would get warnings about dublicate split-ids and can no longer
		// compare splits by instance.
		// if(account!=null)
		// ((KMyMoneyAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getId()),
		// KMyMoneyTransactionSplitWritingImpl.this);

		trx.addSplit(this);
	}

    public KMyMoneyWritableTransactionSplitImpl(final KMyMoneyTransactionSplitImpl splt) throws IllegalArgumentException {
	super(splt.getJwsdpPeer(), splt.getTransaction(), 
		  true);
    }

	// ---------------------------------------------------------------
	
	/**
	 * Creates a new Transaction and add's it to the given KMyMoney file Don't modify
	 * the ID of the new transaction!
	 */
	protected static SPLIT createTransactionSplit_int(
			final KMyMoneyWritableFileImpl file, 
			final KMyMoneyWritableTransactionImpl trx, 
			final KMyMoneyAccount acct, 
			final KMMSpltID newID) {

		if ( trx == null ) {
			throw new IllegalArgumentException("null transaction given");
		}

		if ( acct == null ) {
			throw new IllegalArgumentException("null account given");
		}

		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}

		// This is needed because transaction.addSplit() later
		// must have an already built list of splits.
		// Otherwise, it will create the list from the JAXB-Data
		// thus 2 instances of this KMyMoneyWritableTransactionSplitImpl
		// will exist. One created in getSplits() from this JAXB-Data
		// the other is this object.
		trx.getSplits();

		// ObjectFactory fact = file.getObjectFactory();

		SPLIT jwsdpSplt = file.createSplitType();
		
		jwsdpSplt.setId(newID.toString());
		jwsdpSplt.setAccount(acct.getID().toString());
		jwsdpSplt.setShares(new FixedPointNumber().toKMyMoneyString());
		jwsdpSplt.setValue(new FixedPointNumber().toKMyMoneyString());
		
		// NO, not here but in the calling method:
		// trx.addSplit(new KMyMoneyWritableTransactionSplitImpl(jwsdpSplt, trx.getKMyMoneyFile(), trx));
		// No:
		// trx.getJwsdpPeer().getSPLITS().getSPLIT().add(jwsdpSplt);
		file.setModified(true);
    
        LOGGER.debug("createTransactionSplit_int: Created new transaction split (core): " + jwsdpSplt.getId());
		
        return jwsdpSplt;
	}

	// ---------------------------------------------------------------
	
	/**
	 * @see KMyMoneyTransactionSplitImpl#getTransaction()
	 */
	@Override
	public KMyMoneyWritableTransaction getTransaction() {
		return (KMyMoneyWritableTransaction) super.getTransaction();
	}

	/**
	 * remove this split from it's transaction.
	 */
	@Override
	public void remove() {
		getTransaction().remove(this);
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setAccount(KMyMoneyAccount)
	 */
	@Override
	public void setAccountID(final KMMComplAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}
		
		String old = (getJwsdpPeer().getAccount() == null ? null : getJwsdpPeer().getAccount());
		jwsdpPeer.setAccount(acctID.toString());
		((KMyMoneyWritableFile) getWritableKMyMoneyFile()).setModified(true);

		if ( old == null || 
			 ! old.equals(acctID) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("accountID", old, acctID.toString());
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setAccount(KMyMoneyAccount)
	 */
	@Override
	public void setAccount(final KMyMoneyAccount acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("null account given");
		}
		
		setAccountID(acct.getID());
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setShares(FixedPointNumber)
	 */
	@Override
	public void setShares(final String n) {
		try {
			this.setShares(new FixedPointNumber(n.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		} catch (NumberFormatException e) {
			try {
				Number parsed = this.getSharesCurrencyFormat().parse(n);
				this.setShares(new FixedPointNumber(parsed.toString()));
			} catch (NumberFormatException e1) {
				throw e;
			} catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @return true if the currency of transaction and account match
	 */
	private boolean isCurrencyMatching() {
		KMyMoneyAccount acct = getAccount();
		if ( acct == null ) {
			return false;
		}
		KMyMoneyWritableTransaction trx = getTransaction();
		if ( trx == null ) {
			return false;
		}
		KMMQualifSecCurrID secCurrID = acct.getQualifSecCurrID();
		if ( secCurrID == null ) {
			return false;
		}
		return secCurrID.toString().equals(trx.getQualifSecCurrID().toString());
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setShares(FixedPointNumber)
	 */
	@Override
	public void setShares(final FixedPointNumber n) {
		if ( n == null ) {
			throw new IllegalArgumentException("null quantity given");
		}

		String old = getJwsdpPeer().getShares();
		jwsdpPeer.setShares(n.toKMyMoneyString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		if ( isCurrencyMatching() ) {
			String oldvalue = getJwsdpPeer().getValue();
			getJwsdpPeer().setValue(n.toKMyMoneyString());
			if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
				if ( helper.getPropertyChangeSupport() != null ) {
					helper.getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(oldvalue), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("shares", new FixedPointNumber(old), n);
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	@Override
	public void setValue(final String n) {
		try {
			this.setValue(new FixedPointNumber(n.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		} catch (NumberFormatException e) {
			try {
				Number parsed = this.getValueCurrencyFormat().parse(n);
				this.setValue(new FixedPointNumber(parsed.toString()));
			} catch (NumberFormatException e1) {
				throw e;
			} catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	@Override
	public void setValue(final FixedPointNumber n) {
		if ( n == null ) {
			throw new IllegalArgumentException("null value given");
		}
		
		String old = getJwsdpPeer().getValue();
		jwsdpPeer.setValue(n.toKMyMoneyString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		
		if ( isCurrencyMatching() ) {
			String oldquantity = getJwsdpPeer().getShares();
			getJwsdpPeer().setShares(n.toKMyMoneyString());
			if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
				if ( helper.getPropertyChangeSupport() != null ) {
					helper.getPropertyChangeSupport().firePropertyChange("shares", new FixedPointNumber(oldquantity), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(old), n);
			}
		}
	}

	@Override
	public void setPrice(final FixedPointNumber prc) {
		if ( prc == null ) {
			throw new IllegalArgumentException("null price given");
		}
		
		String old = getJwsdpPeer().getPrice();
		jwsdpPeer.setPrice(prc.toKMyMoneyString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		
		if ( old == null || !old.equals(prc.toKMyMoneyString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("price", new FixedPointNumber(old), prc);
			}
		}
	}

	@Override
	public void setPayeeID(KMMPyeID pyeID) {
		if ( pyeID == null ) {
			throw new IllegalArgumentException("null payee ID given");
		}
		
		String old = (getJwsdpPeer().getPayee() == null ? null : getJwsdpPeer().getPayee());
		jwsdpPeer.setPayee(pyeID.toString());
		((KMyMoneyWritableFile) getWritableKMyMoneyFile()).setModified(true);

		if ( old == null || 
			 ! old.equals(pyeID) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("payeeID", old, pyeID.toString());
			}
		}
	}

	@Override
	public void setPayee(KMyMoneyPayee pye) {
		if ( pye == null ) {
			throw new IllegalArgumentException("null payee given");
		}
		
		setPayeeID(pye.getID());
	}

	/**
	 * Set the description-text.
	 *
	 * @param desc the new description
	 */
	@Override
	public void setDescription(final String desc) {
		if ( desc == null ) {
			throw new IllegalArgumentException("null description given! Please use the empty string instead of null for an empty description");
		}

		String old = getJwsdpPeer().getMemo();
		jwsdpPeer.setMemo(desc);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if ( old == null || !old.equals(desc) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	@Override
	public void setAction(final Action act) throws IllegalTransactionSplitActionException {
		setActionStr(act.getCode());
	}
	
	/**
	 * Set the type of association this split has with an invoice's lot.
	 *
	 * @param actStr null, or one of the defined ACTION_xyz values
	 * @throws IllegalTransactionSplitActionException
	 */
	public void setActionStr(final String actStr) throws IllegalTransactionSplitActionException {
		if ( actStr == null ) {
			throw new IllegalArgumentException("null action given");
		}

		if ( actStr.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty action given");
		}

		String old = getJwsdpPeer().getAction();
		jwsdpPeer.setAction(actStr);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if ( old == null || !old.equals(actStr) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("splitAction", old, actStr);
			}
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyWritableTransactionSplit#setSharesFormattedForHTML(java.lang.String)
	 */
	@Override
	public void setSharesFormattedForHTML(final String n) {
		this.setShares(n);
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValueFormattedForHTML(java.lang.String)
	 */
	@Override
	public void setValueFormattedForHTML(final String n) {
		this.setValue(n);
	}

	// ---------------------------------------------------------------

	/**
	 * The KMyMoney file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	@Override
	public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}

	/**
	 * The KMyMoney file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	@Override
	public KMyMoneyWritableFileImpl getKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}

    // ---------------------------------------------------------------

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyWritableTransactionSplitImpl [");

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

	buffer.append(", payee-id=");
	buffer.append(getPayeeID());

	buffer.append(", memo='");
	buffer.append(getMemo() + "'");

	// usually not set:
	// buffer.append(" transaction-description: '");
	// buffer.append(getTransaction().getMemo() + "'");

	buffer.append(", value=");
	buffer.append(getValue());

	buffer.append(", shares=");
	buffer.append(getShares());

	buffer.append(", price=");
	buffer.append(getPrice());

	buffer.append("]");
	return buffer.toString();
    }

}
