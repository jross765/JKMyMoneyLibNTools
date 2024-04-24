package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.INSTITUTION;
import org.kmymoney.api.read.impl.KMyMoneyInstitutionImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableInstitutionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInstitutionManager extends org.kmymoney.api.read.impl.hlp.FileInstitutionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInstitutionManager.class);

	// ---------------------------------------------------------------

	public FileInstitutionManager(KMyMoneyWritableFileImpl kmmFile) {
		super(kmmFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneyInstitutionImpl createInstitution(final INSTITUTION jwsdpInst) {
		KMyMoneyWritableInstitutionImpl inst = new KMyMoneyWritableInstitutionImpl(jwsdpInst, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createInstitution: Generated new writable institution: " + inst.getID());
		return inst;
	}

}
