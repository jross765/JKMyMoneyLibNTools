package org.kmymoney.api.write.impl.hlp;

import java.util.List;

import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasWritableUserDefinedAttributesImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HasWritableUserDefinedAttributesImpl.class);

	// ---------------------------------------------------------------

	public static void setUserDefinedAttributeCore(final List<PAIR> kvpList,
			                                       final KMyMoneyWritableFile kmmFile,
			                                       final String name, final String value) {
		for ( PAIR kvp : kvpList ) {
			if ( kvp.getKey().equals(name) ) {
				LOGGER.debug("setUserDefinedAttributeCore: (name=" + name + ", value=" + value
						+ ") - overwriting existing key-value-pair");

				kvp.setValue(value);
				kmmFile.setModified(true);
				return;
			}
		}

		kmmFile.setModified(true);
	}

}
