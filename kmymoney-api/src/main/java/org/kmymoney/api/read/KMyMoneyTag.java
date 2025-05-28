package org.kmymoney.api.read;

import org.kmymoney.api.read.hlp.HasTransactions;
import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.base.basetypes.simple.KMMTagID;

public interface KMyMoneyTag extends KMyMoneyObject, 
									 HasTransactions
{
    
    /**
     * @return
     */
    KMMTagID getID();

	// ---------------------------------------------------------------

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getColor();

    /**
     * @return
     */
    String getNotes();
    
	// ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isClosed();

}
