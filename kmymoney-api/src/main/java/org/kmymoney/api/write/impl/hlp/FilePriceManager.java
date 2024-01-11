package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritablePriceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePriceManager extends org.kmymoney.api.read.impl.hlp.FilePriceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);

	// ---------------------------------------------------------------

	public FilePriceManager(KMyMoneyWritableFileImpl kmmFile) {
		super(kmmFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneyPriceImpl createPrice(final PRICE jwsdpPrc) {
		KMyMoneyWritablePriceImpl prc = new KMyMoneyWritablePriceImpl(jwsdpPrc, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("Generated new writable price: " + prc.getID());
		return prc;
	}

}
