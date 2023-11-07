package org.kmymoney.read;

import java.math.BigInteger;

import org.kmymoney.read.aux.KMMAddress;

public interface KMyMoneyPayee {
    
    String getId();

    String getName();

    public String getDefaultAccountId();
    
    public KMMAddress getAddress();

    public String getEmail();
    
    public String getReference();
    
    public String getNotes();
    
    // ---------------------------------------------------------------

    public BigInteger getMatchingEnabled();

    public String getMatchKey();

    public BigInteger getUsingMatchKey();

    public BigInteger getMatchIgnoreCase();

}
