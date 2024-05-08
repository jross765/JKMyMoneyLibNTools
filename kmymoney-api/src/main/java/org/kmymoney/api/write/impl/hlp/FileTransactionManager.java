package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionSplitImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.kmymoney.api.read.impl.hlp.FileTransactionManager {

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
		LOGGER.debug("createTransaction: Generated new writable transaction: " + trx.getID());
		return trx;
	}

    @Override
	protected KMyMoneyTransactionSplitImpl createTransactionSplit(
			final SPLIT jwsdpTrxSplt,
			final KMyMoneyTransaction trx, // actually, should be KMyMoney*Writable*Transaction, 
			                               // but then the compiler is not happy...
			final boolean addSpltToAcct) {
    	if ( ! ( trx instanceof KMyMoneyWritableTransaction ) ) {
    		throw new IllegalArgumentException("transaction must be a writable one");
    	}
    	
    	KMyMoneyWritableTransactionSplitImpl splt = new KMyMoneyWritableTransactionSplitImpl(jwsdpTrxSplt, 
    																						 (KMyMoneyWritableTransaction) trx, 
                								           									 addSpltToAcct);
    	LOGGER.debug("createTransactionSplit: Generated new writable transaction split: " + splt.getID());
    	return splt;
    }

}
