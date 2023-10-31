package org.kmymoney.read.impl.aux;

import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrPair;
import org.kmymoney.generated.PRICE;
import org.kmymoney.generated.PRICEPAIR;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.aux.KMMPricePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMPricePairImpl implements KMMPricePair {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMMPricePairImpl.class);

    // -----------------------------------------------------------

    /**
     * The JWSDP-object we are wrapping.
     */
    private final PRICEPAIR jwsdpPeer;

    private final KMyMoneyFile file;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public KMMPricePairImpl(final PRICEPAIR newPeer, final KMyMoneyFile file) {
	super();
		
	this.jwsdpPeer = newPeer;
	this.file      = file;
    }

    // -----------------------------------------------------------
    
    @Override
    public KMMCurrPair getId() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	return new KMMCurrPair(jwsdpPeer.getFrom(), jwsdpPeer.getTo());
    }

    // -----------------------------------------------------------
    
    @Override
    public Collection<KMMPrice> getPrices() {
	Collection<KMMPrice> result = new ArrayList<KMMPrice>();
	
	for ( PRICE prc : jwsdpPeer.getPRICE() ) {
	    KMMPrice newPrc = new KMMPriceImpl(prc, file);
	    result.add(newPrc);
	}
	
	try {
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + getId());
	} catch (Exception e) {
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + "ERROR");
	}
	
	return result;
    }

}
