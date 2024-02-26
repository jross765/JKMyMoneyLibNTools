package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.kmymoney.api.generated.PAIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasUserDefinedAttributesImpl // implements HasUserDefinedAttributes
{

	private static final Logger LOGGER = LoggerFactory.getLogger(HasUserDefinedAttributesImpl.class);

	// ---------------------------------------------------------------

    public static String getUserDefinedAttributeCore(final List<PAIR> kvpList, 
    		                                         final String name) {
		if ( ! getUserDefinedAttributeKeysCore(kvpList).contains(name) ) {
			throw new KVPListDoesNotContainKeyException();
		}
		
		for ( PAIR kvp : kvpList ) {
			if ( kvp.getKey().equals(name) ) {
				return kvp.getValue();
			}
		}
		
		return null;
    }

    public static List<String> getUserDefinedAttributeKeysCore(final List<PAIR> kvpList) {
		List<String> retval = new ArrayList<String>();

		for ( PAIR kvp : kvpList ) {
			retval.add(kvp.getKey());
		}

		return retval;
	}

}
