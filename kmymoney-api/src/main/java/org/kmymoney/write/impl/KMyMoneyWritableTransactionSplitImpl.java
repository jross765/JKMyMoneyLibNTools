package org.kmymoney.write.impl;

import java.text.ParseException;

import org.gnucash.generated.GncTransaction;
import org.gnucash.generated.ObjectFactory;
import org.gnucash.generated.Slot;
import org.gnucash.generated.SlotValue;
import org.gnucash.generated.SlotsType;
import org.kmymoney.Const;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.IllegalTransactionSplitActionException;
import org.kmymoney.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.write.KMyMoneyWritableFile;
import org.kmymoney.write.KMyMoneyWritableObject;
import org.kmymoney.write.KMyMoneyWritableTransaction;
import org.kmymoney.write.KMyMoneyWritableTransactionSplit;

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
	 * @see KMyMoneyWritableObject#setUserDefinedAttribute(java.lang.String, java.lang.String)
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
	public KMyMoneyWritableTransactionSplitImpl(
		final GncTransaction.TrnSplits.TrnSplit jwsdpPeer, 
		final KMyMoneyWritableTransaction transaction) {
		super(jwsdpPeer, transaction);
	}

	/**
	 * create a new split and and add it to the given transaction.
	 *
	 * @param transaction transaction the transaction we will belong to
	 * @param account     the account we take money (or other things) from or give it to
	 */
	public KMyMoneyWritableTransactionSplitImpl(
		final KMyMoneyWritableTransactionImpl transaction, 
		final KMyMoneyAccount account) {
		super(createTransactionSplit(transaction, account, 
				(transaction.getWritingFile()).createGUID()), 
				transaction);

		// this is a workaound.
		// if super does account.addSplit(this) it adds an instance on GnucashTransactionSplitImpl that is "!=
		// (GnucashTransactionSplitWritingImpl)this";
		// thus we would get warnings about dublicate split-ids and can no longer compare splits by instance.
		//        if(account!=null)
		//            ((GnucashAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getId()),
		// GnucashTransactionSplitWritingImpl.this);

		transaction.addSplit(this);
	}

	/**
	 * Creates a new Transaction and add's it to the given gnucash-file
	 * Don't modify the ID of the new transaction!
	 */
	protected static GncTransaction.TrnSplits.TrnSplit createTransactionSplit(
		final KMyMoneyWritableTransactionImpl transaction,
		final KMyMoneyAccount account,
		final String pSplitID) {

		if (transaction == null) {
			throw new IllegalArgumentException("null transaction given");
		}

		if (account == null) {
			throw new IllegalArgumentException("null account given");
		}

		if (pSplitID == null || pSplitID.trim().length() == 0) {
			throw new IllegalArgumentException("null or empty pSplitID given");
		}

		// this is needed because transaction.addSplit() later
		// must have an already build List of splits.
		// if not it will create the list from the JAXB-Data
		// thus 2 instances of this GnucashTransactionSplitWritingImpl
		// will exist. One created in getSplits() from this JAXB-Data
		// the other is this object.
		transaction.getSplits();

		KMyMoneyWritableFileImpl gnucashFileImpl = transaction.getWritingFile();
		ObjectFactory factory = gnucashFileImpl.getObjectFactory();

		GncTransaction.TrnSplits.TrnSplit split = gnucashFileImpl.createGncTransactionTypeTrnSplitsTypeTrnSplitType();
		{
			GncTransaction.TrnSplits.TrnSplit.SplitId id = factory.createGncTransactionTrnSplitsTrnSplitSplitId();
			id.setType(Const.XML_DATA_TYPE_GUID);
			id.setValue(pSplitID);
			split.setSplitId(id);
		}

		split.setSplitReconciledState(KMyMoneyTransactionSplit.NREC);

		split.setSplitQuantity("0/100");
		split.setSplitValue("0/100");
		{
			GncTransaction.TrnSplits.TrnSplit.SplitAccount splitaccount = factory.createGncTransactionTrnSplitsTrnSplitSplitAccount();
			splitaccount.setType(Const.XML_DATA_TYPE_GUID);
			splitaccount.setValue(account.getId());
			split.setSplitAccount(splitaccount);
		}
		return split;
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
	public void setAccount(final KMyMoneyAccount account) {
		if (account == null) {
			throw new NullPointerException("null account given");
		}
		String old = (getJwsdpPeer().getSplitAccount() == null ? null
				:
						getJwsdpPeer().getSplitAccount().getValue());
		getJwsdpPeer().getSplitAccount().setType(Const.XML_DATA_TYPE_GUID);
		getJwsdpPeer().getSplitAccount().setValue(account.getId());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if (old == null || !old.equals(account.getId())) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("accountID", old, account.getId());
			}
		}

	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
	public void setQuantity(final String n) {
		try {
			this.setQuantity(new FixedPointNumber(n.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		}
		catch (NumberFormatException e) {
			try {
				Number parsed = this.getQuantityCurrencyFormat().parse(n);
				this.setQuantity(new FixedPointNumber(parsed.toString()));
			}
			catch (NumberFormatException e1) {
				throw e;
			}
			catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @return true if the currency of transaction and account match
	 */
	private boolean isCurrencyMatching() {
		KMyMoneyAccount account = getAccount();
		if (account == null) {
			return false;
		}
		KMyMoneyWritableTransaction transaction = getTransaction();
		if (transaction == null) {
			return false;
		}
		String actCID = account.getCurrencyID();
		if (actCID == null) {
			return false;
		}
		String actCNS = account.getCurrencyNameSpace();
		if (actCNS == null) {
			return false;
		}
		return (actCID.equals(transaction.getCurrencyID())
				&&
				actCNS.equals(transaction.getCurrencyNameSpace())
		);
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
	public void setQuantity(final FixedPointNumber n) {
		if (n == null) {
			throw new NullPointerException("null quantity given");
		}

		String old = getJwsdpPeer().getSplitQuantity();
		getJwsdpPeer().setSplitQuantity(n.toGnucashString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		if (isCurrencyMatching()) {
			String oldvalue = getJwsdpPeer().getSplitValue();
			getJwsdpPeer().setSplitValue(n.toGnucashString());
			if (old == null || !old.equals(n.toGnucashString())) {
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(oldvalue), n);
				}
			}
		}

		if (old == null || !old.equals(n.toGnucashString())) {
			if (getPropertyChangeSupport() != null) {
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
		}
		catch (NumberFormatException e) {
			try {
				Number parsed = this.getValueCurrencyFormat().parse(n);
				this.setValue(new FixedPointNumber(parsed.toString()));
			}
			catch (NumberFormatException e1) {
				throw e;
			}
			catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	public void setValue(final FixedPointNumber n) {
		if (n == null) {
			throw new NullPointerException("null value given");
		}
		String old = getJwsdpPeer().getSplitValue();
		getJwsdpPeer().setSplitValue(n.toGnucashString());
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);
		if (isCurrencyMatching()) {
			String oldquantity = getJwsdpPeer().getSplitQuantity();
			getJwsdpPeer().setSplitQuantity(n.toGnucashString());
			if (old == null || !old.equals(n.toGnucashString())) {
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldquantity), n);
				}
			}
		}

		if (old == null || !old.equals(n.toGnucashString())) {
			if (getPropertyChangeSupport() != null) {
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
		if (desc == null) {
			throw new IllegalArgumentException("null description given! Please use the empty string instead of null for an empty description");
		}

		String old = getJwsdpPeer().getSplitMemo();
		getJwsdpPeer().setSplitMemo(desc);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if (old == null || !old.equals(desc)) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	/**
	 * Set the type of association this split has with
	 * an invoice's lot.
	 *
	 * @param action null, or one of the defined ACTION_xyz values
	 * @throws IllegalTransactionSplitActionException 
	 */
	public void setSplitAction(final String action) throws IllegalTransactionSplitActionException {
//		if ( action != null &&
//             ! action.equals(ACTION_PAYMENT) &&
//             ! action.equals(ACTION_INVOICE) &&
//             ! action.equals(ACTION_BILL) && 
//             ! action.equals(ACTION_BUY) && 
//             ! action.equals(ACTION_SELL) ) {
//                throw new IllegalSplitActionException();
//		}

		String old = getJwsdpPeer().getSplitAction();
		getJwsdpPeer().setSplitAction(action);
		((KMyMoneyWritableFile) getKMyMoneyFile()).setModified(true);

		if (old == null || !old.equals(action)) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("splitAction", old, action);
			}
		}
	}

	public void setLotID(final String lotID) {

		KMyMoneyWritableTransactionImpl transaction = (KMyMoneyWritableTransactionImpl) getTransaction();
		KMyMoneyWritableFileImpl writingFile = transaction.getWritingFile();
		ObjectFactory factory = writingFile.getObjectFactory();

		if (getJwsdpPeer().getSplitLot() == null) {
			GncTransaction.TrnSplits.TrnSplit.SplitLot lot = factory.createGncTransactionTrnSplitsTrnSplitSplitLot();
			getJwsdpPeer().setSplitLot(lot);
		}
		getJwsdpPeer().getSplitLot().setValue(lotID);
		getJwsdpPeer().getSplitLot().setType(Const.XML_DATA_TYPE_GUID);

		// if we have a lot, and if we are a paying transaction, then check the slots
		SlotsType slots = getJwsdpPeer().getSplitSlots();
		if (slots == null) {
			slots = factory.createSlotsType();
			getJwsdpPeer().setSplitSlots(slots);
		}
		if (slots.getSlot() == null) {
			Slot slot = factory.createSlot();
			slot.setSlotKey("trans-txn-type");
			SlotValue value = factory.createSlotValue();
			value.setType("string");
			value.getContent().add(KMyMoneyTransaction.TYPE_PAYMENT);
			slot.setSlotValue(value);
			slots.getSlot().add(slot);
		}

	}

	// --------------------- support for propertyChangeListeners ---------------

	/**
	 * @see KMyMoneyWritableTransactionSplit#setQuantityFormattedForHTML(java.lang.String)
	 */
	public void setQuantityFormattedForHTML(final String n) {
		this.setQuantity(n);
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
