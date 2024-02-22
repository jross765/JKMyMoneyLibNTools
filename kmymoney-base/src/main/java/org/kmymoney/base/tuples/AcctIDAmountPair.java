package org.kmymoney.base.tuples;

import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.numbers.FixedPointNumber;

public record AcctIDAmountPair(KMMAcctID accountID, FixedPointNumber amount) {
	
	private final static double UNSET_VALUE = -999999;
	
	// ---------------------------------------------------------------
	
	public boolean isNotNull() {
		if ( accountID == null)
			return false;
		
		if ( amount == null)
			return false;
		
		return true;
	}

	public boolean isSet() {
		return accountID.isSet() && ( amount.doubleValue() != UNSET_VALUE );
	}

}
