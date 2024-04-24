package org.kmymoney.api.read;

import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.hlp.HasUserDefinedAttributes;
import org.kmymoney.base.basetypes.simple.KMMInstID;

public interface KMyMoneyInstitution extends HasUserDefinedAttributes 
{

    /**
     * @return
     */
    KMMInstID getID();

    // ------------------------------------------------------------

    /**
     * @return
     */
    String getName();
    
    /**
     * It's called the "rounding number" on the GUI
     * 
     * @return
     */
    String getSortCode(); 

    KMMAddress getAddress();
    
    // ------------------------------------------------------------
    
    String getBIC();
    
    String getURL();
    
}
