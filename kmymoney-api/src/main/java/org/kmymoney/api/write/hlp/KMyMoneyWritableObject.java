package org.kmymoney.api.write.hlp;

import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.api.write.KMyMoneyWritableFile;

/**
 * Interface that all interfaces for writable gnucash-entities shall implement
 */
public interface KMyMoneyWritableObject {

	/**
	 * @return the File we belong to.
	 */
	KMyMoneyWritableFile getWritableGnucashFile();

	/**
	 * @param name  the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
	 */
	void setUserDefinedAttribute(String name, String value);
}
