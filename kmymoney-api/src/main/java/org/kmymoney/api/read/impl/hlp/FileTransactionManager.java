package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	protected KMyMoneyFileImpl kmmFile;

	private Map<KMMTrxID, KMyMoneyTransaction>             trxMap;
	private Map<KMMQualifSpltID, KMyMoneyTransactionSplit> trxSpltMap;

	// ---------------------------------------------------------------

	public FileTransactionManager(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
		init(kmmFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final KMYMONEYFILE pRootElement) {
		init1(pRootElement);
		init2(pRootElement);
	}

	private void init1(final KMYMONEYFILE pRootElement) {
		trxMap = new HashMap<KMMTrxID, KMyMoneyTransaction>();

		for ( KMyMoneyTransactionImpl trx : getTransactions_readAfresh() ) {
			trxMap.put(trx.getID(), trx);
		}

		LOGGER.debug("init1: No. of entries in transaction map: " + trxMap.size());
	}

	private void init2(final KMYMONEYFILE pRootElement) {
		trxSpltMap = new HashMap<KMMQualifSpltID, KMyMoneyTransactionSplit>();

		for ( KMyMoneyTransaction trx : trxMap.values() ) {
			try {
				List<KMyMoneyTransactionSplit> spltList = null;
				if ( kmmFile instanceof KMyMoneyWritableFileImpl ) {
					// CAUTION: As opposed to the code in the sister project,
					// the second arg here has to be set to "true", else the
					// whole shebang will not work.
					// Cannot explain this...
					spltList = ((KMyMoneyTransactionImpl) trx).getSplits(true);
				} else {
					spltList = ((KMyMoneyTransactionImpl) trx).getSplits(true);
				}
				for ( KMyMoneyTransactionSplit splt : spltList ) {
					trxSpltMap.put(splt.getQualifID(), splt);
				}
			} catch (RuntimeException e) {
				LOGGER.error("init2: [RuntimeException] Problem in " + getClass().getName() + ".init2: "
						+ "ignoring illegal Transaction entry with id=" + trx.getID(), e);
//		System.err.println("init2: ignoring illegal Transaction entry with id: " + trx.getID());
//		System.err.println("  " + e.getMessage());
			}
		} // for trx

		LOGGER.debug("init2: No. of entries in transaction split map: " + trxSpltMap.size());
	}

	// ----------------------------

	protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
		KMyMoneyTransactionImpl trx = new KMyMoneyTransactionImpl(jwsdpTrx, kmmFile);
		LOGGER.debug("createTransaction: Generated new transaction: " + trx.getID());
		return trx;
	}

	protected KMyMoneyTransactionSplitImpl createTransactionSplit(
			final SPLIT jwsdpTrxSplt,
			final KMyMoneyTransaction trx, 
			final boolean addSpltToAcct) {
		KMyMoneyTransactionSplitImpl splt = new KMyMoneyTransactionSplitImpl(jwsdpTrxSplt, trx, 
																			 addSpltToAcct);
		LOGGER.debug("createTransactionSplit: Generated new transaction split: " + splt.getQualifID());
		return splt;
	}

	// ---------------------------------------------------------------

	public void addTransaction(KMyMoneyTransaction trx) {
		addTransaction(trx, true);
	}

	public void addTransaction(KMyMoneyTransaction trx, boolean withSplt) {
		if ( trx == null ) {
			throw new IllegalStateException("null transaction given");
		}

		trxMap.put(trx.getID(), trx);

		if ( withSplt ) {
			for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
				addTransactionSplit(splt, false);
			}
		}

		LOGGER.debug("addTransaction: Added transaction to cache: " + trx.getID());
	}

	public void removeTransaction(KMyMoneyTransaction trx) {
		removeTransaction(trx, true);
	}

	public void removeTransaction(KMyMoneyTransaction trx, boolean withSplt) {
		if ( trx == null ) {
			throw new IllegalStateException("null transaction given");
		}

		if ( withSplt ) {
			for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
				removeTransactionSplit(splt, false);
			}
		}

		trxMap.remove(trx.getID());

		LOGGER.debug("removeTransaction: Removed transaction from cache: " + trx.getID());
	}

	// ---------------------------------------------------------------

	public void addTransactionSplit(KMyMoneyTransactionSplit splt) {
		addTransactionSplit(splt, true);
	}

	public void addTransactionSplit(KMyMoneyTransactionSplit splt, boolean withTrx) {
		if ( splt == null ) {
			throw new IllegalStateException("null split given");
		}

		trxSpltMap.put(splt.getQualifID(), splt);

		if ( withTrx ) {
			addTransaction(splt.getTransaction(), false);
		}
	}

	public void removeTransactionSplit(KMyMoneyTransactionSplit splt) {
		removeTransactionSplit(splt, false);
	}

	public void removeTransactionSplit(KMyMoneyTransactionSplit splt, boolean withTrx) {
		if ( splt == null ) {
			throw new IllegalStateException("null split given");
		}

		if ( withTrx ) {
			removeTransaction(splt.getTransaction(), false);
		}

		trxSpltMap.remove(splt.getQualifID());
	}

	// ---------------------------------------------------------------

	public KMyMoneyTransaction getTransactionByID(final KMMTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalStateException("null transaction ID given");
		}

		if ( ! trxID.isSet() ) {
			throw new IllegalStateException("unset transaction ID given");
		}

		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyTransaction retval = trxMap.get(trxID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionByID: No Transaction with ID '" + trxID + "'. We know " + trxMap.size() + " transactions.");
		}

		return retval;
	}

	public Collection<? extends KMyMoneyTransaction> getTransactions() {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		return Collections.unmodifiableCollection(trxMap.values());
	}

	// ---------------------------------------------------------------

	public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSpltID spltID) {
		if ( spltID == null ) {
			throw new IllegalStateException("null split ID given");
		}

		if ( ! spltID.isSet() ) {
			throw new IllegalStateException("unset split ID given");
		}

		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyTransactionSplit retval = trxSpltMap.get(spltID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionSplitByID: No Transaction-Split with ID '" + spltID + "'. We know " + trxSpltMap.size() + " transaction splits.");
		}

		return retval;
	}

	public List<KMyMoneyTransactionImpl> getTransactions_readAfresh() {
		List<KMyMoneyTransactionImpl> result = new ArrayList<KMyMoneyTransactionImpl>();

		for ( TRANSACTION jwsdpTrx : getTransactions_raw() ) {
			try {
				KMyMoneyTransactionImpl trx = createTransaction(jwsdpTrx);
				result.add(trx);
			} catch (RuntimeException e) {
				LOGGER.error("getTransactions_readAfresh: [RuntimeException] Problem in " + getClass().getName()
						+ ".getTransactions_readAfresh: " + "ignoring illegal Transaction entry with id="
						+ jwsdpTrx.getId(), e);
//		System.err.println("getTransactions_readAfresh: ignoring illegal Transaction entry with id: " + jwsdpTrx.getTrnID().getValue());
//		System.err.println("  " + e.getMessage());
			}
		}

		return result;
	}

	private List<TRANSACTION> getTransactions_raw() {
		List<TRANSACTION> result = new ArrayList<TRANSACTION>();

		for ( TRANSACTION jwsdpTrx : kmmFile.getRootElement().getTRANSACTIONS().getTRANSACTION() ) {
			result.add(jwsdpTrx);
		}

		return result;
	}

	// ----------------------------

	public List<KMyMoneyTransactionSplit> getTransactionSplits() {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<KMyMoneyTransactionSplit> result = new ArrayList<KMyMoneyTransactionSplit>();
		for ( KMyMoneyTransactionSplit elt : trxSpltMap.values() ) {
			result.add(elt);
		}
		
		return Collections.unmodifiableList(result);
	}

	public List<KMyMoneyTransactionSplitImpl> getTransactionSplits_readAfresh() {
		List<KMyMoneyTransactionSplitImpl> result = new ArrayList<KMyMoneyTransactionSplitImpl>();

		for ( KMyMoneyTransaction trx : getTransactions_readAfresh() ) {
			for ( SPLIT jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
				try {
					KMyMoneyTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx,
																			   false);
					result.add(splt);
				} catch (RuntimeException e) {
					LOGGER.error("getTransactionSplits_readAfresh(1): [RuntimeException] Problem in "
							+ "ignoring illegal Transaction Split entry with id="
							+ trx.getID() + ":" + jwsdpTrxSplt.getId(), e);
//			System.err.println("getTransactionSplits_readAfresh(1): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
				}
			} // for jwsdpTrxSplt
		} // for trx

		return result;
	}

	public List<KMyMoneyTransactionSplitImpl> getTransactionSplits_readAfresh(final KMMTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalStateException("null transaction ID given");
		}

		if ( ! trxID.isSet() ) {
			throw new IllegalStateException("unset transaction ID given");
		}

		List<KMyMoneyTransactionSplitImpl> result = new ArrayList<KMyMoneyTransactionSplitImpl>();

		for ( KMyMoneyTransaction trx : getTransactions_readAfresh() ) {
			if ( trx.getID().equals(trxID) ) {
				for ( SPLIT jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
					try {
						KMyMoneyTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx, 
																				   true);
						result.add(splt);
					} catch (RuntimeException e) {
						LOGGER.error("getTransactionSplits_readAfresh(2): [RuntimeException] Problem in "
								+ "ignoring illegal Transaction Split entry with id="
								+ trx.getID() + ":" + jwsdpTrxSplt.getId(), e);
//			System.err.println("getTransactionSplits_readAfresh(2): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
					}
				} // for jwsdpTrxSplt
			} // if
		} // for trx

		return result;
	}

	private List<SPLIT> getTransactionSplits_raw(final TRANSACTION jwsdpTrx) {
		List<SPLIT> result = new ArrayList<SPLIT>();

		for ( SPLIT jwsdpTrxSplt : jwsdpTrx.getSPLITS().getSPLIT() ) {
			result.add(jwsdpTrxSplt);
		}

		return result;
	}

	private List<SPLIT> getTransactionSplits_raw(final KMMTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalStateException("null transaction ID given");
		}

		if ( ! trxID.isSet() ) {
			throw new IllegalStateException("unset transaction ID given");
		}

		List<SPLIT> result = new ArrayList<SPLIT>();

		for ( TRANSACTION jwsdpTrx : getTransactions_raw() ) {
			if ( jwsdpTrx.getId().equals(trxID.toString()) ) {
				for ( SPLIT jwsdpTrxSplt : jwsdpTrx.getSPLITS().getSPLIT() ) {
					result.add(jwsdpTrxSplt);
				}
			}
		}

		return result;
	}

	// ---------------------------------------------------------------
	
	protected TRANSACTION getTransaction_raw(final KMMTrxID trxID) {
		for ( TRANSACTION jwsdpTrx : getTransactions_raw() ) {
			if ( jwsdpTrx.getId().equals(trxID.toString())) {
				return jwsdpTrx;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTransactionMap() {
		return trxMap.size();
	}

	public int getNofEntriesTransactionSplitMap() {
		return trxSpltMap.size();
	}

}
