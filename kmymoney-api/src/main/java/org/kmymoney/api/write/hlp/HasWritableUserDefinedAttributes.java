package org.kmymoney.api.write.hlp;

import org.kmymoney.api.read.hlp.HasUserDefinedAttributes;

public interface HasWritableUserDefinedAttributes extends HasUserDefinedAttributes {

    void setUserDefinedAttribute(String name, String value);
    
}
