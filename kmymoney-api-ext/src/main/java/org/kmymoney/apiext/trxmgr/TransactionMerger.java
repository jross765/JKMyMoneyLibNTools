package org.kmymoney.apiext.trxmgr;

import java.util.ArrayList;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.apiext.Const;
import org.kmymoney.base.basetypes.simple.KMMTrxID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TransactionMerger {
	
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionMerger.class);
    
    // ---------------------------------------------------------------
    
	private KMyMoneyWritableFile gcshFile = null;
	
    // ---------------------------------------------------------------
	
	public TransactionMerger(KMyMoneyWritableFile gcshFile) {
		this.gcshFile = gcshFile;
	}
    
    // ---------------------------------------------------------------
    
	public void merge(KMMTrxID survivorID, KMMTrxID dierID) throws MergePlausiCheckException {
		KMyMoneyTransaction survivor = gcshFile.getTransactionByID(survivorID);
		KMyMoneyWritableTransaction dier = gcshFile.getWritableTransactionByID(dierID);
		merge(survivor, dier);
	}

	public void merge(KMyMoneyTransaction survivor, KMyMoneyWritableTransaction dier) throws MergePlausiCheckException {
		if ( ! plausiCheck(survivor, dier) ) {
			LOGGER.error("merge: survivor-dier-pair did not pass plausi check: " + survivor.getID() + "/" + dier.getID());
			throw new MergePlausiCheckException();
		}
		
		KMMTrxID dierID = dier.getID();
		gcshFile.removeTransaction(dier);
		LOGGER.info("merge: Transaction " + dierID + " (dier) removed");
	}

    // ---------------------------------------------------------------
	
	private boolean plausiCheck(KMyMoneyTransaction survivor, KMyMoneyTransaction dier) {
		// Level 1:
		// ::TODO: Tolerance
		if ( ! survivor.getDatePosted().equals(dier.getDatePosted()) ) {
			LOGGER.warn("plausiCheck: Survivor- and dier-transaction do not have the same post-date");
			LOGGER.debug("plausiCheck: Survivor-date: " + survivor.getDatePosted());
			LOGGER.debug("plausiCheck: Dier-date: " + dier.getDatePosted());
			return false;
		}

		TransactionManager trxMgr = new TransactionManager(gcshFile);
		if ( ! trxMgr.isSane(survivor) ) {
			LOGGER.warn("plausiCheck: Survivor-transaction is not sane");
			return false;
		}
		
		if ( ! trxMgr.isSane(dier) ) {
			LOGGER.warn("plausiCheck: Dier-transaction is not sane");
			return false;
		}
		
		if ( ! ( trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.CHECKING) &&
			     trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING) 
			     ||
			     trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.CASH) &&
			     trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH)
			     ||
			     trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.STOCK) &&
			     trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK) 
			   ) ) {
			LOGGER.warn("plausiCheck: One or both transactions has/have no split belonging to bank/cash/stock account");
			return false;
		}
		
		// Level 2:
		// Splits belong to the same accounts -- per account type
		ArrayList<KMyMoneyTransactionSplit> spltListSurvBank = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING);
		ArrayList<KMyMoneyTransactionSplit> spltListDierBank = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING);
		if ( trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.CHECKING) &&
			 trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING) ) {
			spltListSurvBank = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING);
			spltListDierBank = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CHECKING);
			
			for ( KMyMoneyTransactionSplit spltSurv : spltListSurvBank ) {
				boolean accountInBothLists = false;
				
				for ( KMyMoneyTransactionSplit spltDier : spltListDierBank ) {
					if ( spltSurv.getAccount().getID().equals(spltDier.getAccount().getID() ) ) {
						accountInBothLists = true;
					}
				}
				
				if ( ! accountInBothLists ) {
					LOGGER.warn("plausiCheck: Survivor-split " + spltSurv.getID() + " has no according dier-split sibling (bank accounts)");
					return false;
				}
			}
		} 

		// sic, no else-if!
		ArrayList<KMyMoneyTransactionSplit> spltListSurvCash = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH);
		ArrayList<KMyMoneyTransactionSplit> spltListDierCash = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH);
		if ( trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.CASH) &&
			 trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH) ) {
			spltListSurvCash = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH);
			spltListDierCash = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.CASH);
			
			for ( KMyMoneyTransactionSplit spltSurv : spltListSurvCash ) {
				boolean accountInBothLists = false;
				
				for ( KMyMoneyTransactionSplit spltDier : spltListDierCash ) {
					if ( spltSurv.getAccount().getID().equals(spltDier.getAccount().getID() ) ) {
						accountInBothLists = true;
					}
				}
				
				if ( ! accountInBothLists ) {
					LOGGER.warn("plausiCheck: Survivor-split " + spltSurv.getID() + " has no according dier-split sibling (cash accounts)");
					return false;
				}
			}
		}
		
		// sic, no else-if!
		ArrayList<KMyMoneyTransactionSplit> spltListSurvStock = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK);
		ArrayList<KMyMoneyTransactionSplit> spltListDierStock = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK);
		if ( trxMgr.hasSplitBoundToAccounttType(survivor, KMyMoneyAccount.Type.STOCK) &&
			 trxMgr.hasSplitBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK) ) {
			spltListSurvStock = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK);
			spltListDierStock = trxMgr.getSplitsBoundToAccounttType(dier, KMyMoneyAccount.Type.STOCK);
			
			for ( KMyMoneyTransactionSplit spltSurv : spltListSurvStock ) {
				boolean accountInBothLists = false;
				
				for ( KMyMoneyTransactionSplit spltDier : spltListDierStock ) {
					if ( spltSurv.getAccount().getID().equals(spltDier.getAccount().getID() ) ) {
						accountInBothLists = true;
					}
				}
				
				if ( ! accountInBothLists ) {
					LOGGER.warn("plausiCheck: Survivor-split " + spltSurv.getID() + " has no according dier-split sibling (stock accounts)");
					return false;
				}
			}
		}
		
		// Level 3:
		// Split values are identical
		FixedPointNumber sumSurv = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListSurvBank ) {
			sumSurv = sumSurv.add(elt.getValue());
		}
		
		FixedPointNumber sumDier = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListDierBank ) {
			sumDier = sumDier.add(elt.getValue());
		}
		
		if ( Math.abs( sumSurv.getBigDecimal().doubleValue() - 
				       sumDier.getBigDecimal().doubleValue() ) > Const.DIFF_TOLERANCE_VALUE ) {
			LOGGER.warn("plausiCheck: Split-sums over survivor- and dier-splits are unequal (bank accounts)");
			LOGGER.debug("plausiCheck: sumSurv: " + sumSurv.getBigDecimal());
			LOGGER.debug("plausiCheck: sumDier: " + sumDier.getBigDecimal());
			return false;
		}
		
		// ---
		
		sumSurv = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListSurvCash ) {
			sumSurv = sumSurv.add(elt.getValue());
		}
		
		sumDier = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListDierCash ) {
			sumDier = sumDier.add(elt.getValue());
		}
		
		if ( Math.abs( sumSurv.getBigDecimal().doubleValue() - 
				       sumDier.getBigDecimal().doubleValue() ) > Const.DIFF_TOLERANCE_VALUE ) {
			LOGGER.warn("plausiCheck: Split-sums over survivor- and dier-splits are unequal (cash accounts)");
			LOGGER.debug("plausiCheck: sumSurv: " + sumSurv.getBigDecimal());
			LOGGER.debug("plausiCheck: sumDier: " + sumDier.getBigDecimal());
			return false;
		}
		
		// ---
		
		sumSurv = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListSurvStock ) {
			sumSurv = sumSurv.add(elt.getValue());
		}
		
		sumDier = new FixedPointNumber();
		for ( KMyMoneyTransactionSplit elt : spltListDierStock ) {
			sumDier = sumDier.add(elt.getValue());
		}
		
		if ( Math.abs( sumSurv.getBigDecimal().doubleValue() - 
				       sumDier.getBigDecimal().doubleValue() ) > Const.DIFF_TOLERANCE_VALUE ) {
			LOGGER.warn("plausiCheck: Split-sums over survivor- and dier-splits are unequal (stock accounts)");
			LOGGER.debug("plausiCheck: sumSurv: " + sumSurv.getBigDecimal());
			LOGGER.debug("plausiCheck: sumDier: " + sumDier.getBigDecimal());
			return false;
		}
		
		return true;
	}
    
}
