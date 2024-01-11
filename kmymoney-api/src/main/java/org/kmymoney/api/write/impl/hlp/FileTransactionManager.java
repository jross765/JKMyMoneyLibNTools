package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.gnucash.api.read.impl.hlp.FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	public FileTransactionManager(KMyMoneyWritableFileImpl kmmFile) {
		super(kmmFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
		KMyMoneyWritableTransactionImpl trx = new KMyMoneyWritableTransactionImpl(jwsdpTrx, kmmFile);
		LOGGER.debug("Generated new writable transaction: " + trx.getID());
		return trx;
	}

	// ::TODO
//    @Override
//    protected KMyMoneyTransactionSplitImpl createTransactionSplit(
//	    final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
//	    final KMyMoneyTransaction trx,
//	    final boolean addSpltToAcct,
//	    final boolean addSpltToInvc)  throws IllegalArgumentException {
//	KMyMoneyWritableTransactionSplitImpl splt = new KMyMoneyWritableTransactionSplitImpl(jwsdpTrxSplt, trx, 
//                								           addSpltToAcct, addSpltToInvc);
//	LOGGER.debug("Generated new writable transaction split: " + splt.getID());
//	return splt;
//    }

}
