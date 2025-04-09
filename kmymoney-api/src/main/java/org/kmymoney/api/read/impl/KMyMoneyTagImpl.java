package org.kmymoney.api.read.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.api.generated.TAG;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTag;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMTagID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyTagImpl extends KMyMoneyObjectImpl
                             implements KMyMoneyTag
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyTagImpl.class);

	// ---------------------------------------------------------------

	/**
	 * the JWSDP-object we are facading.
	 */
	protected final TAG jwsdpPeer;

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public KMyMoneyTagImpl(final TAG peer, final KMyMoneyFile kmmFile) {
		super(kmmFile);
		
		jwsdpPeer = peer;
	}

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public TAG getJwsdpPeer() {
	return jwsdpPeer;
    }

	// ---------------------------------------------------------------

//    protected void setAddress(final KMMAddressImpl addr) {
//		jwsdpPeer.setADDRESS(addr.getJwsdpPeer());
//    }
    
	// ---------------------------------------------------------------

	@Override
	public KMMTagID getID() {
		return new KMMTagID(jwsdpPeer.getId());
	}

	@Override
	public String getName() {
		return jwsdpPeer.getName();
	}

	@Override
	public String getColor() {
		return jwsdpPeer.getTagcolor();
	}

	@Override
	public String getNotes() {
		return jwsdpPeer.getNotes();
	}

	@Override
	public boolean isClosed() {
		if ( jwsdpPeer.getClosed() == BigInteger.ONE )
			return true;
		else
			return false;
	}

	// ---------------------------------------------------------------

	// ::TODO: Very inefficient code.
	// On the other hand: How could you possibly make it (much) more efficient 
	// (without dramatically changing larger parts of this lib)?
	// Cannot think of a better way at the moment...
	@Override
	public Collection<KMMQualifSpltID> getTransactionSplitIDs() {
//		Collection<KMyMoneyTransactionSplit> spltList = getKMyMoneyFile().getTransactionSplits();
//		
//		if ( spltList == null )
//			return null;
//		
//		ArrayList<KMMQualifSpltID> result = new ArrayList<KMMQualifSpltID>();
//		
//		for ( KMyMoneyTransactionSplit splt : spltList ) {
//			if ( splt.getTagIDs() != null ) {
//				// if ( splt.getTagIDs().contains(getID()) ) { // no, this wont's do because of the specific tag ID needed
//				for ( KMMTagID tagID : splt.getTagIDs() ) {
//					KMyMoneyTag tag = getKMyMoneyFile().getTagByID(tagID);
//					if ( tag.getID().equals(getID()) ) {
//						KMMQualifSpltID newID = new KMMQualifSpltID(splt.getTransaction().getID(), splt.getID());
//						result.add(newID);
//						continue;
//					}
//				}
//			}
//		}
//		
//		return result;
		// ::TODO
		return null;
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		return "KMyMoneyTagImpl [id=" + getID() + 
	                        ", name='" + getName() + "'" +
                           ", color='" + getColor() + "']";
	}

}
