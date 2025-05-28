package org.kmymoney.api.write;

import org.kmymoney.api.read.KMyMoneyTag;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;

/**
 * Tag that can be modified.
 * 
 * @see KMyMoneyTag
 */
public interface KMyMoneyWritableTag extends KMyMoneyTag,
                                             KMyMoneyWritableObject
{

    void remove();
   
	// ---------------------------------------------------------------

    void setName(String name);

    void setNotes(String nts);
    
    void setColor(String clr);
    
	// ---------------------------------------------------------------
    
    void setClosed(boolean val);

}
