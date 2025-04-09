package org.kmymoney.base.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A technical entry ID for a tag
 * (primarily defined for type safety).
 */
public class KMMTagID extends KMMID {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMTagID.class);

    // P 000 001
    //   6   3
    private final static char PREFIX = 'G';

    // -----------------------------------------------------------------

    public KMMTagID() {
    	super();
    }

    public KMMTagID(String idStr) throws InvalidKMMIDException {
    	super(idStr);
    	set(idStr);
    }

    public KMMTagID(long counter) throws InvalidKMMIDException {
    	super(counter);
    	set(counter);
    }

    // -----------------------------------------------------------------

    public void set(long counter) throws InvalidKMMIDException {
    	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

    	if ( counter < 1 || 
    		 counter > Math.pow(10, coreLength) - 1 )
    		throw new InvalidKMMIDException("Cannot generate KMM tag ID from long " + counter + ": range error");

    	String fmtStr = "%0" + coreLength + "d";
    	String coreStr = String.format(fmtStr, counter);
    	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMIDException {
    	if ( kmmID.length() != STANDARD_LENGTH )
    		throw new InvalidKMMIDException("No valid KMM tag ID string: '" + kmmID + "': wrong string length");

    	if ( kmmID.charAt(0) != PREFIX )
    		throw new InvalidKMMIDException("No valid KMM tag ID string: '" + kmmID + "': wrong prefix");

    	for ( int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++ ) {
    		if ( ! Character.isDigit(kmmID.charAt(i)) ) {
    			logger.error("Char '" + kmmID.charAt(i) + "' is invalid in KMMTagID '" + kmmID + "'");
    			throw new InvalidKMMIDException("No valid KMM tag ID string: '" + kmmID + "': wrong character at pos " + i);
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
