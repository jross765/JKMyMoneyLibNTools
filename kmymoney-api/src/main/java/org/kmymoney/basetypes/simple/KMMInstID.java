package org.kmymoney.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMInstID extends KMMID {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMInstID.class);

    // A 000 001
    //   6   3
    private final static char PREFIX = 'I';

    // -----------------------------------------------------------------

    public KMMInstID() {
	super();
    }

    public KMMInstID(String idStr) throws InvalidKMMIDException {
	super(idStr);
	set(idStr);
    }

    public KMMInstID(long counter) throws InvalidKMMIDException {
	super(counter);
	set(counter);
    }

    // -----------------------------------------------------------------

    public void set(long counter) throws InvalidKMMIDException {
	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

	if ( counter < 1 || 
	     counter > Math.pow(10, coreLength) - 1 )
	    throw new InvalidKMMIDException();

	String fmtStr = "%0" + coreLength + "d";
	String coreStr = String.format(fmtStr, counter);
	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMIDException {
	if (kmmID.length() != STANDARD_LENGTH)
	    throw new InvalidKMMIDException();

	if (kmmID.charAt(0) != PREFIX)
	    throw new InvalidKMMIDException();

	for (int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++) {
	    if (!Character.isDigit(kmmID.charAt(i))) {
		logger.warn("Char '" + kmmID.charAt(i) + "' is invalid in KMMInstID '" + kmmID + "'");
		throw new InvalidKMMIDException();
	    }
	}
    }

    // -----------------------------------------------------------------

    public String getPrefix() throws KMMIDNotSetException {
	if (!isSet)
	    throw new KMMIDNotSetException();

	return kmmID.substring(0, PREFIX_LENGTH);
    }

}
