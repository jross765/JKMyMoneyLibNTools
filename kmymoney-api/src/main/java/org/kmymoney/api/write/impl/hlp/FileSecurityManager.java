package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.KMyMoneySecurity;
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
	protected KMyMoneySecurityImpl createSecurity(final SECURITY jwsdpSec) {
		KMyMoneyWritableSecurityImpl sec = new KMyMoneyWritableSecurityImpl(jwsdpSec, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createSecurity: Generated new writable security: " + sec.getQualifID());
		return sec;
	}

	// ---------------------------------------------------------------

	public void addSecurity(KMyMoneySecurity sec) {
		if ( sec == null ) {
			throw new IllegalStateException("null security given");
		}

		secMap.put(sec.getID(), sec);

		if ( sec.getSymbol() != null )
			symbMap.put(sec.getSymbol(), sec.getQualifID().getSecID());

		if ( sec.getCode() != null )
			codeMap.put(sec.getCode(), sec.getQualifID().getSecID());

		LOGGER.debug("addSecurity: Added security to cache: " + sec.getID());
	}

	public void removeSecurity(KMyMoneySecurity sec) {
		if ( sec == null ) {
			throw new IllegalStateException("null security given");
		}

		secMap.remove(sec.getID());

		for ( String symb : symbMap.keySet() ) {
			if ( symbMap.get(symb).equals(sec.getQualifID().getSecID()) )
				symbMap.remove(symb);
		}

		for ( String code : codeMap.keySet() ) {
			if ( codeMap.get(code).equals(sec.getQualifID().getSecID()) )
				codeMap.remove(code);
		}

		LOGGER.debug("removeSecurity: Removed security from cache: " + sec.getID());
	}

}
