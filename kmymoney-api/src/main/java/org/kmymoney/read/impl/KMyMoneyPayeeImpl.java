package org.kmymoney.read.impl;

import org.kmymoney.generated.PAYEE;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyPayee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyPayeeImpl implements KMyMoneyPayee {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTransactionImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private PAYEE jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final KMyMoneyFile file;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneyPayeeImpl(
	    final PAYEE peer, 
	    final KMyMoneyFile gncFile) {

	jwsdpPeer = peer;
	file = gncFile;

    }

    // ---------------------------------------------------------------

    @Override
    public String getName() {
	return jwsdpPeer.getName();
    }

}
