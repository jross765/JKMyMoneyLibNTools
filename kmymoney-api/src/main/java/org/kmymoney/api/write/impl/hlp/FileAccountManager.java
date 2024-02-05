package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableAccountImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccountManager extends org.kmymoney.api.read.impl.hlp.FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	public FileAccountManager(KMyMoneyWritableFileImpl kmmFile) {
		super(kmmFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneyAccountImpl createAccount(final ACCOUNT jwsdpAcct) {
		KMyMoneyWritableAccountImpl acct = new KMyMoneyWritableAccountImpl(jwsdpAcct, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createAccount: Generated new writable account: " + acct.getID());
		return acct;
	}

}
