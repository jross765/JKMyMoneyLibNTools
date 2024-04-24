package org.kmymoney.api.write.hlp;

import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.write.aux.KMMWritableAddress;

public interface HasWritableAddress {

    KMMWritableAddress getWritableAddress();
    
    KMMWritableAddress createWritableAddress();
    
	void removeAddress(KMMWritableAddress impl);

    void setAddress(KMMAddress adr);
    
}
