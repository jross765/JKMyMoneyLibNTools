package org.kmymoney.api.write.impl.hlp;

import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritablePriceImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritablePricePairImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
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
	protected KMyMoneyPricePairImpl createPricePair(final PRICEPAIR jwsdpPrcPr) {
		KMyMoneyWritablePricePairImpl prcPr = new KMyMoneyWritablePricePairImpl(jwsdpPrcPr,
				  (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createPricePair: Generated new writable price pair: " + prcPr.getID());
		return prcPr;
	}

	// ----------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected KMyMoneyPriceImpl createPrice(final KMyMoneyPricePair prcPr, final PRICE jwsdpPrc) {
		KMyMoneyWritablePriceImpl prc = new KMyMoneyWritablePriceImpl(
				new KMyMoneyWritablePricePairImpl((KMyMoneyPricePairImpl) prcPr), 
				jwsdpPrc, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createPrice: Generated new writable price: " + prc.getID());
		return prc;
	}

	protected KMyMoneyPriceImpl createPrice(final KMyMoneyWritablePricePair prcPr, final PRICE jwsdpPrc) {
		KMyMoneyWritablePriceImpl prc = new KMyMoneyWritablePriceImpl(
				prcPr, 
				jwsdpPrc, (KMyMoneyWritableFileImpl) kmmFile);
		LOGGER.debug("createPrice: Generated new writable price: " + prc.getID());
		return prc;
	}

}
