package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableSecurityImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSecurityManager extends org.kmymoney.api.read.impl.hlp.FileSecurityManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileSecurityManager.class);

	// ---------------------------------------------------------------

	public FileSecurityManager(KMyMoneyWritableFileImpl kmmFile) {
		super(kmmFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneySecurityImpl createSecurity(final SECURITY jwsdpCmdty) {
		KMyMoneyWritableSecurityImpl sec = new KMyMoneyWritableSecurityImpl(jwsdpCmdty, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("Generated new writable security: " + sec.getQualifID());
		return sec;
	}

}
