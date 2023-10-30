package org.kmymoney.currency;

import java.util.Collection;

import org.kmymoney.numbers.FixedPointNumber;

public interface SimplePriceTable {

    public FixedPointNumber getConversionFactor(final String code);

    public void setConversionFactor(final String code, final FixedPointNumber factor);

    // ---------------------------------------------------------------

    public boolean convertFromBaseCurrency(final FixedPointNumber value, final String code);

    public boolean convertToBaseCurrency(final FixedPointNumber value, final String code);

    // ---------------------------------------------------------------

    public Collection<String> getCurrencies();

    public void clear();

}
