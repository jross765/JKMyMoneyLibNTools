package org.kmymoney.api.read.impl.hlp;

import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper-Class used to implement functions all gnucash-objects support.
 */
public class KMyMoneyObjectImpl implements KMyMoneyObject {

	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyObjectImpl.class);

	// ---------------------------------------------------------------

	protected final KMyMoneyFile myFile;

	// -----------------------------------------------------------------

	public KMyMoneyObjectImpl(final KMyMoneyFile myFile) {
		super();

		this.myFile = myFile;
	}

	// ---------------------------------------------------------------

	@Override
	public KMyMoneyFile getKMyMoneyFile() {
		return myFile;
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		return "KMyMoneyObjectImpl@" + hashCode();
	}

}
