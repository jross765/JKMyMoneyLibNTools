package org.kmymoney.read;

import java.math.BigInteger;

import org.kmymoney.basetypes.complex.KMMComplAcctID;
import org.kmymoney.basetypes.simple.KMMPyeID;
import org.kmymoney.read.aux.KMMAddress;

public interface KMyMoneyPayee {
    
    KMMPyeID getId();

    String getName();

    public KMMComplAcctID getDefaultAccountId();
    
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
