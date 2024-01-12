package org.kmymoney.api.write.hlp;

import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.basetypes.simple.KMMInstID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;

public interface IDManager {
	
	KMMInstID getNewInstitutionID();

	KMMAcctID getNewAccountID();

	KMMTrxID getNewTransactionID();

	KMMSpltID getNewSplitID();

	KMMPyeID getNewPayeeID();

	KMMSecID getNewSecurityID();

}
