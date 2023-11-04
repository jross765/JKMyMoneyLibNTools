package org.kmymoney.basetypes;

import java.util.Objects;

/**
 * KMyMoney's Split "IDs" are in fact no IDs, because they are only unique within
 * the scope of their respective transaction.
 * 
 * In order to get the ID "real" (like globally unique), we have to pair it
 * with its transaction ID.
 */
public class KMMSplitID {
    
    private String trxID;
    private String spltID;
    
    // ---------------------------------------------------------------

    public KMMSplitID(String trxID, String spltID) {
	setTransactionID(trxID);
	setSplitID(spltID);
    }
    
    // ---------------------------------------------------------------

    public String getTransactionID() {
        return trxID;
    }

    public void setTransactionID(String trxID) {
        this.trxID = trxID;
    }

    public String getSplitID() {
        return spltID;
    }

    public void setSplitID(String spltID) {
        this.spltID = spltID;
    }

    // ---------------------------------------------------------------
    
    public void set(String trxID, String spltID) {
	setTransactionID(trxID);
	setSplitID(spltID);
    }

    public void set(KMMSplitID spltID) {
	setTransactionID(spltID.getTransactionID());
	setSplitID(spltID.getSplitID());
    }

    // ---------------------------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(spltID, trxID);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof KMMSplitID)) {
	    return false;
	}
	KMMSplitID other = (KMMSplitID) obj;
	return Objects.equals(spltID, other.spltID) && Objects.equals(trxID, other.trxID);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	return "KMMSplitID [trxID=" + trxID + ", spltID=" + spltID + "]";
    }

}
