package org.kmymoney.api.currency;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.kmymoney.api.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSecurityQuoteTable implements SimplePriceTable,
                                                 Serializable 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSecurityQuoteTable.class);

    private static final long serialVersionUID = -1023793498877236292L;

    // -----------------------------------------------------------

    /**
     * maps a currency-name in capital letters(e.g. "GBP") to a factor
     * {@link FixedPointNumber} that is to be multiplied with an amount of that
     * currency to get the value in the base-currency.
     *
     * @see {@link #getConversionFactor(String)}
     */
    private Map<String, FixedPointNumber> mSecID2Factor = null;

    // -----------------------------------------------------------

    public SimpleSecurityQuoteTable() {
	mSecID2Factor = new Hashtable<String, FixedPointNumber>();
    }

    // -----------------------------------------------------------

    /**
     * @param secID a currency-name in capital letters(e.g. "GBP")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    @Override
    public FixedPointNumber getConversionFactor(final String secID) {
	return mSecID2Factor.get(secID);
    }

    /**
     * @param secID a currency-name in capital letters(e.g. "GBP")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    @Override
    public void setConversionFactor(final String secID, final FixedPointNumber factor) {
	mSecID2Factor.put(secID, factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param secID the currency to convert to
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertFromBaseCurrency(FixedPointNumber value, final String secID) {
        FixedPointNumber factor = getConversionFactor(secID);
        if (factor == null) {
            return false;
        }
        value.divideBy(factor);
        return true;
    }

    /**
     * @param value           the value to convert
     * @param secID it's currency
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertToBaseCurrency(FixedPointNumber value, final String secID) {
	FixedPointNumber factor = getConversionFactor(secID);
	if (factor == null) {
	    return false;
	}
	value.multiply(factor);
	return true;
    }

    // ---------------------------------------------------------------

    /**
     * @return all currency-names
     */
    @Override
    public Collection<String> getCurrencies() {
	return mSecID2Factor.keySet();
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
        mSecID2Factor.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "[SimpleSecurityQuoteTable:\n";
	
	result += "No. of entries: " + mSecID2Factor.size() + "\n";
	
	result += "Entries:\n";
	for ( String secID : mSecID2Factor.keySet() ) {
	    // result += " - " + secID + "\n";
	    result += " - " + secID + ";" + mSecID2Factor.get(secID) + "\n";
	}
	
	result += "]";
	
	return result;
    }

}
