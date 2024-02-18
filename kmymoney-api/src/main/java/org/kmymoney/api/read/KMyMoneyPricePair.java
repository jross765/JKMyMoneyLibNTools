package org.kmymoney.api.read;

import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMPricePairID;
import org.kmymoney.api.read.hlp.KMyMoneyPricePairCore;

/**
 * In KMyMoney, a price pair is a data structure which holds all the 
 * prices with <strong>one</strong> specific pair of from-currency/security
 * and to-currency, such as e.g.:
 * <ul>
 *   <li>USD to EUR (currency quotes)</li>   
 *   <li>Security MBG.DE (Mercedes-Benz Group AG) to EUR (share prices)</li>
 * </ul>
 * So, e.g., <strong>all</strong> USD/EUR quotes are held in the USD-EUR price pair,
 * <strong>all</strong> MBG.DE/EUR quotes are held in the MBG.DE-EUR price pair,
 * etc.
 */
public interface KMyMoneyPricePair extends KMyMoneyPricePairCore {

    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMMPricePairID getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    /**
     * @return
     */
    Collection<KMyMoneyPrice> getPrices();
	
}
