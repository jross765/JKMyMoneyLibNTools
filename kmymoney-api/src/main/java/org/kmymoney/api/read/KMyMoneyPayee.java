package org.kmymoney.api.read;

import java.math.BigInteger;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.read.aux.KMMAddress;

public interface KMyMoneyPayee {
    
    KMMPyeID getID();

    String getName();

    public KMMComplAcctID getDefaultAccountID();
    
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
