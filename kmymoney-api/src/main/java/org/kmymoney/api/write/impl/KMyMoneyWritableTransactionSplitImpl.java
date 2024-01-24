package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeListener;
import java.text.ParseException;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.IllegalTransactionSplitActionException;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction-Split that can be newly created or removed from it's transaction.
 */
public class KMyMoneyWritableTransactionSplitImpl extends KMyMoneyTransactionSplitImpl 
                                                  implements KMyMoneyWritableTransactionSplit 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableTransactionImpl.class);

	/**
	 * Our helper to implement the KMyMoneyWritableObject-interface.
	 */
	private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

	/**
	 * @see KMyMoneyTransactionSplitImpl#getTransaction()
	 */
	@Override
	public KMyMoneyWritableTransaction getTransaction() {
		return (KMyMoneyWritableTransaction) super.getTransaction();
	}

	// ---------------------------------------------------------------
	
	/**
	 * @param jwsdpPeer   the JWSDP-object we are facading.
	 * @param kmmFile 
	 * @param trx the transaction we belong to
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableTransactionSplitImpl(
			final SPLIT jwsdpPeer,
			final KMyMoneyWritableFile kmmFile,
			final KMyMoneyWritableTransaction trx) {
		super(jwsdpPeer, kmmFile, trx);
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
			  trx.getWritableKMyMoneyFile(),
		      trx);

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
	super(splt.getJwsdpPeer(), splt.getKMyMoneyFile(), splt.getTransaction());
    }

	// ---------------------------------------------------------------
	
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
		// thus 2 instances of this KMyMoneyTransactionSplitWritingImpl
		// will exist. One created in getSplits() from this JAXB-Data
		// the other is this object.
		trx.getSplits();

		// ObjectFactory factory = file.getObjectFactory();

		SPLIT jwsdpSplt = file.createSplitType();
		
		jwsdpSplt.setId(newID.toString());
		jwsdpSplt.setAccount(acct.getID().toString());
		jwsdpSplt.setShares(new FixedPointNumber().toKMyMoneyString());
		jwsdpSplt.setValue(new FixedPointNumber().toKMyMoneyString());
		
		trx.addSplit(new KMyMoneyWritableTransactionSplitImpl(jwsdpSplt, trx.getKMyMoneyFile(), trx));
		// No:
		// trx.getJwsdpPeer().getSPLITS().getSPLIT().add(jwsdpSplt);
		file.setModified(true);
    
        LOGGER.debug("createTransactionSplit_int: Created new transaction split (core): " + jwsdpSplt.getId());
		
        return jwsdpSplt;
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
	public void setAccountID(final KMMComplAcctID acctID) {
		setAccount(getTransaction().getKMyMoneyFile().getAccountByID(acctID));
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

		if ( old == null || !old.equals(acct.getID()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("accountID", old, acct.getID().toString());
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
		KMMQualifSecCurrID secCurrID = acct.getSecCurrID();
		if ( secCurrID == null ) {
			return false;
		}
		return secCurrID.equals(trx.getSecurity());
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
				if ( helper.getPropertyChangeSupport() != null ) {
					helper.getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(oldvalue), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(old), n);
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
				if ( helper.getPropertyChangeSupport() != null ) {
					helper.getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldquantity), n);
				}
			}
		}

		if ( old == null || !old.equals(n.toKMyMoneyString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(old), n);
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
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("description", old, desc);
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
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("splitAction", old, act);
			}
		}
	}

	// ---------------------------------------------------------------

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

	// ---------------------------------------------------------------

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	@Override
	public KMyMoneyWritableFileImpl getKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("exports")
	@Override
	public void addPropertyChangeListener(String ptyName, PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("exports")
	@Override
	public void removePropertyChangeListener(String ptyName, PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("exports")
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

}
