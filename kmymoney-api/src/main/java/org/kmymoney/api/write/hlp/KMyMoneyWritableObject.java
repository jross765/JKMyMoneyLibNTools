package org.kmymoney.api.write.hlp;

import org.kmymoney.api.write.KMyMoneyWritableFile;

/**
 * Interface that all interfaces for writable gnucash-entities shall implement
 */
public interface KMyMoneyWritableObject {

	/**
	 * @return the File we belong to.
	 */
	KMyMoneyWritableFile getWritableKMyMoneyFile();

}
