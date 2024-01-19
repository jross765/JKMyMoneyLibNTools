package org.kmymoney.api.read.hlp;

import org.kmymoney.api.read.KMyMoneyFile;

/**
 * Interface all gnucash-entities implement.
 */
public interface KMyMoneyObject {

    /**
     * @return the file we belong to.
     */
    KMyMoneyFile getKMyMoneyFile();

}
