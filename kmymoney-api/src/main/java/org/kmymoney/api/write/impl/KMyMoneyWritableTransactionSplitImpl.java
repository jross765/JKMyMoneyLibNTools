package org.kmymoney.api.write.impl;

import java.text.ParseException;

import javax.naming.spi.ObjectFactory;

import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.IllegalTransactionSplitActionException;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;

/**
 * Transaction-Split that can be newly created or removed from it's transaction.
 */
public class KMyMoneyWritableTransactionSplitImpl extends KMyMoneyTransactionSplitImpl 
                                                 implements KMyMoneyWritableTransactionSplit 
{

	/**
	 * Our helper to implement the GnucashWritableObject-interface.
	 */
	private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(this);

	/**
	 * @see KMyMoneyWritableObject#setUserDefinedAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		helper.setUserDefinedAttribute(name, value);
	}

	/**
	 * @see KMyMoneyTransactionSplitImpl#getTransaction()
	 */
	@Override
	public KMyMoneyWritableTransaction getTransaction() {
		return (KMyMoneyWritableTransaction) super.getTransaction();
	}

	/**
	 * @param jwsdpPeer   the JWSDP-object we are facading.
	 * @param transaction the transaction we belong to
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableTransactionSplitImpl(final SPLIT jwsdpPeer,
			final KMyMoneyWritableTransaction transaction) {
		super(jwsdpPeer, transaction);
	}

	/**
	 * create a new split and and add it to the given transaction.
	 *
	 * @param transaction transaction the transaction we will belong to
	 * @param acct     the account we take money (or other things) from or give
	 *                    it to
	 */
	public KMyMoneyWritableTransactionSplitImpl(
			final KMyMoneyWritableTransactionImpl trx,
			final KMyMoneyAccount acct) {
		super(createTransactionSplit_int(trx.getWritingFile(), trx, acct, trx.getNewSplitID()));

		// this is a workaound.
		// if super does account.addSplit(this) it adds an instance on
		// GnucashTransactionSplitImpl that is "!=
		// (GnucashTransactionSplitWritingImpl)this";
		// thus we would get warnings about dublicate split-ids and can no longer
		// compare splits by instance.
		// if(account!=null)
		// ((GnucashAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getId()),
		// GnucashTransactionSplitWritingImpl.this);

		transaction.addSplit(this);
	}

	/**
	 * Creates a new Transaction and add's it to the given gnucash-file Don't modify
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
			throw new IllegalArgumentException("empty ID given");
		}

		// this is needed because transaction.addSplit() later
		// must have an already build List of splits.
		// if not it will create the list from the JAXB-Data
		// thus 2 instances of this GnucashTransactionSplitWritingImpl
		// will exist. One created in getSplits() from this JAXB-Data
		// the other is this object.
		trx.getSplits();

		ObjectFactory factory = file.getObjectFactory();

		SPLIT splt = file.createSplitType();
		
		splt.setId(newID.toString());
		splt.setAccount(acct.getID().toString());
		splt.setShares(new FixedPointNumber().toKMyMoneyString());
		splt.setValue(new FixedPointNumber().toKMyMoneyString());
		
		trx.addSplit(splt);
		
		return splt;
	}

	/**
	 * remove this split from it's transaction.
	 */
	public void remove() {
		getTransaction().remove(this);
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setAccount(KMyMoneyAccount)
	 */
	public void setAccountID(final String accountId) {
		setAccount(getTransaction().getKMyMoneyFile().getAccountByID(accountId));
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setAccount(KMyMoneyAccount)
	 */
	public void setAccount(final KMyMoneyAccount acct) {
		if ( acct == null ) {
			throw new NullPointerException("null account given");
		}
		String old = (getJwsdpPeer().getAccount() == null ? null : getJwsdpPeer().getAccount());
		jwsdpPeer.setAccount(acct.getID().toString());
		((KMyMoneyWritableFile) getWritableKMyMoneyFile()).setModified(true);

		if ( old == null || !old.equals(acct.getId()) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("accountID", old, acct.getId());
			}
		}

	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setShares(FixedPointNumber)
	 */
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
		String secCurrID = acct.getSecCurrID();
		if ( secCurrID == null ) {
			return false;
		}
		return (secCurrID.equals(trx.getCommodity()) && actCNS.equals(trx.getCurrencyNameSpace()));
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setShares(FixedPointNumber)
	 */
	public void setShares(final FixedPointNumber n) {
		if ( n == null ) {
			throw new NullPointerException("null quantity given");
		}

		String old = getJwsdpPeer().getShares();
		jwsdpPeer.setShares(n.toKMyMoneyString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		if ( isCurrencyMatching() ) {
			String oldvalue = getJwsdpPeer().getValue();
			getJwsdpPeer().setValue(n.toKMyMoneyString());
			if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
				if ( getPropertyChangeSupport() != null ) {
					getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(oldvalue), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(old), n);
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValue(FixedPointNumber)
	 */
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
	public void setValue(final FixedPointNumber n) {
		if ( n == null ) {
			throw new NullPointerException("null value given");
		}
		String old = getJwsdpPeer().getValue();
		jwsdpPeer.setValue(n.toKMyMoneyString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		if ( isCurrencyMatching() ) {
			String oldquantity = getJwsdpPeer().getShares();
			getJwsdpPeer().setShares(n.toKMyMoneyString());
			if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
				if ( getPropertyChangeSupport() != null ) {
					getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldquantity), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(old), n);
			}
		}
	}

	/**
	 * Set the description-text.
	 *
	 * @param desc the new description
	 */
	public void setDescription(final String desc) {
		if ( desc == null ) {
			throw new IllegalArgumentException(
					"null description given! Please use the empty string instead of null for an empty description");
		}

		String old = getJwsdpPeer().getMemo();
		jwsdpPeer.setMemo(desc);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if ( old == null || !old.equals(desc) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	/**
	 * Set the type of association this split has with an invoice's lot.
	 *
	 * @param act null, or one of the defined ACTION_xyz values
	 * @throws IllegalTransactionSplitActionException
	 */
	public void setSplitAction(final String act) throws IllegalTransactionSplitActionException {
//		if ( action != null &&
//             ! action.equals(ACTION_PAYMENT) &&
//             ! action.equals(ACTION_INVOICE) &&
//             ! action.equals(ACTION_BILL) && 
//             ! action.equals(ACTION_BUY) && 
//             ! action.equals(ACTION_SELL) ) {
//                throw new IllegalSplitActionException();
//		}

		String old = getJwsdpPeer().getAction();
		jwsdpPeer.setAction(act);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if ( old == null || !old.equals(act) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("splitAction", old, act);
			}
		}
	}

	// --------------------- support for propertyChangeListeners ---------------

	/**
	 * @see KMyMoneyWritableTransactionSplit#setSharesFormattedForHTML(java.lang.String)
	 */
	public void setSharesFormattedForHTML(final String n) {
		this.setShares(n);
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValueFormattedForHTML(java.lang.String)
	 */
	public void setValueFormattedForHTML(final String n) {
		this.setValue(n);
	}

	/**
	 * ${@inheritDoc}.
	 */
	public KMyMoneyWritableFile getWritableGnucashFile() {
		return (KMyMoneyWritableFile) getKMyMoneyFile();
	}
}
