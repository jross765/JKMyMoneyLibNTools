package org.kmymoney.write;

import org.kmymoney.read.KMyMoneyObject;

/**
 * Interface that all interfaces for writable gnucash-entities shall implement
 */
public interface KMyMoneyWritableObject {

    /**
     * @return the File we belong to.
     */
    KMyMoneyWritableFile getWritableGnucashFile();

    /**
     * @param name the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(String name, String value);
}
