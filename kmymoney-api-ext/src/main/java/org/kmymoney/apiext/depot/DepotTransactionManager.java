package org.kmymoney.apiext.depot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.kmymoney.base.tuples.AcctIDAmountPair;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepotTransactionManager {
    
    public enum Type {
	BUY_STOCK,
	DIVIDEND
    }
    
    // ---------------------------------------------------------------
    
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DepotTransactionManager.class);
    
    // ---------------------------------------------------------------
    
    @SuppressWarnings("exports")
    public static KMyMoneyWritableTransaction genBuyStockTrx(
	    final KMyMoneyWritableFileImpl kmmFile,
	    final KMMAcctID stockAcctID,
	    final KMMAcctID taxFeeAcctID,
	    final KMMAcctID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final FixedPointNumber taxesFees,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
	
	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
	expensesAcctAmtList.add(newPair);
	
	return genBuyStockTrx(kmmFile, 
			      stockAcctID, expensesAcctAmtList, offsetAcctID, 
			      nofStocks, stockPrc, 
			      postDate, descr);	
    }
    
    @SuppressWarnings("exports")
    public static KMyMoneyWritableTransaction genBuyStockTrx(
	    final KMyMoneyWritableFileImpl kmmFile,
	    final KMMAcctID stockAcctID,
	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
	    final KMMAcctID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
	
	if ( kmmFile == null ) {
	    throw new IllegalArgumentException("null KMyMoney file given");
	}
		
	if ( stockAcctID == null ||
	     offsetAcctID == null ) {
	    throw new IllegalArgumentException("null account ID given");
	}
	
	if ( ! ( stockAcctID.isSet()  ) ||
	     ! ( offsetAcctID.isSet() ) ) {
	    throw new IllegalArgumentException("unset account ID given");
	}
		
	if ( expensesAcctAmtList == null ) {
	    throw new IllegalArgumentException("null expenses account list given");
	}
			
	if ( expensesAcctAmtList.isEmpty() ) {
	    throw new IllegalArgumentException("empty expenses account list given");
	}
			
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    if ( ! elt.isNotNull() ) {
		throw new IllegalArgumentException("null expenses account list element given");
	    }
	    if ( ! elt.isSet() ) {
		throw new IllegalArgumentException("unset expenses account list element given");
	    }
	}

	if ( nofStocks == null  ||
	     stockPrc == null ) {
	    throw new IllegalArgumentException("null amount given");
	}
		
	if ( nofStocks.doubleValue() <= 0.0 ) {
	    throw new IllegalArgumentException("number of stocks <= 0.0 given");
	}
			
	if ( stockPrc.doubleValue() <= 0.0 ) {
	    throw new IllegalArgumentException("stock price <= 0.0 given");
	}
	
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    if ( elt.amount().doubleValue() <= 0.0 ) {
		throw new IllegalArgumentException("expense <= 0.0 given");
	    }
	}

	LOGGER.debug("genBuyStockTrx: Account 1 name (stock):      '" + kmmFile.getAccountByID(stockAcctID).getQualifiedName() + "'");
	int counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    LOGGER.debug("genBuyStockTrx: Account 2." + counter + " name (expenses): '" + kmmFile.getAccountByID(elt.accountID()).getQualifiedName() + "'");
	    counter++;
	}
	LOGGER.debug("genBuyStockTrx: Account 3 name (offsetting): '" + kmmFile.getAccountByID(offsetAcctID).getQualifiedName() + "'");

	// ---
	// Check account types
	KMyMoneyAccount stockAcct  = kmmFile.getAccountByID(stockAcctID);
	if ( stockAcct.getType() != KMyMoneyAccount.Type.STOCK ) {
	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + KMyMoneyAccount.Type.STOCK);
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != KMyMoneyAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
	    }
	}
	
	KMyMoneyAccount offsetAcct = kmmFile.getAccountByID(offsetAcctID);
	if ( offsetAcct.getType() != KMyMoneyAccount.Type.CHECKING ) {
	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + KMyMoneyAccount.Type.CHECKING);
	}

	// ---

	FixedPointNumber amtNet   = nofStocks.copy().multiply(stockPrc);
	LOGGER.debug("genBuyStockTrx: Net amount: " + amtNet);

	FixedPointNumber amtGross = amtNet.copy();
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    amtGross.add(elt.amount());
	}
	LOGGER.debug("genBuyStockTrx: Gross amount: " + amtGross);

	// ---

	KMyMoneyWritableTransaction trx = kmmFile.createWritableTransaction();
	// Does not work like that: The description/memo on transaction
	// level is purely internal:
	// trx.setDescription(description);
	trx.setDescription("Generated by DepotTransactionManager, " + LocalDateTime.now());

	// ---

	KMyMoneyWritableTransactionSplit splt1 = trx.createWritableSplit(offsetAcct);
	splt1.setValue(new FixedPointNumber(amtGross.negate()));
	splt1.setShares(new FixedPointNumber(amtGross.negate()));
	// splt3.setPrice("1/1"); // completely optional
	// This is what we actually want (cf. above):
	splt1.setDescription(descr); // sic, only here
	LOGGER.debug("genBuyStockTrx: Split 1 to write: " + splt1.toString());

	// ---
	
	KMyMoneyWritableTransactionSplit splt2 = trx.createWritableSplit(stockAcct);
	splt2.setValue(new FixedPointNumber(amtNet));
	splt2.setShares(new FixedPointNumber(nofStocks));
	splt2.setPrice(stockPrc); // optional (sic), but advisable
	splt2.setAction(KMyMoneyTransactionSplit.Action.BUY_SHARES);
	LOGGER.debug("genBuyStockTrx: Split 2 to write: " + splt2.toString());

	// ---

	counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
	    KMyMoneyWritableTransactionSplit splt3 = trx.createWritableSplit(expensesAcct);
	    splt3.setValue(new FixedPointNumber(elt.amount()));
	    splt3.setShares(new FixedPointNumber(elt.amount()));
	    // splt3.setPrice("1/1"); // completely optional
	    LOGGER.debug("genBuyStockTrx: Split 3." + counter + " to write: " + splt3.toString());
	    counter++;
	}

	// ---

	trx.setDatePosted(postDate);
	trx.setDateEntered(LocalDate.now());

	// ---

	LOGGER.info("genBuyStockTrx: Generated new Transaction: " + trx.getID());
	return trx;
    }
    
    // ---------------------------------------------------------------
    
    @SuppressWarnings("exports")
    public static KMyMoneyWritableTransaction genDivivendTrx(
	    final KMyMoneyWritableFileImpl kmmFile,
	    final KMMAcctID stockAcctID,
	    final KMMAcctID incomeAcctID,
	    final KMMAcctID taxFeeAcctID,
	    final KMMAcctID offsetAcctID,
	    final FixedPointNumber divGross,
	    final FixedPointNumber taxesFees,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
	
	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
	expensesAcctAmtList.add(newPair);
	
	return genDivivendTrx(kmmFile, 
			      stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
			      divGross, 
			      postDate, descr);
    }
    
    @SuppressWarnings("exports")
    public static KMyMoneyWritableTransaction genDivivendTrx(
	    final KMyMoneyWritableFileImpl kmmFile,
	    final KMMAcctID stockAcctID,
	    final KMMAcctID incomeAcctID,
	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
	    final KMMAcctID offsetAcctID,
	    final FixedPointNumber divGross,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
	
	if ( kmmFile == null ) {
	    throw new IllegalArgumentException("null KMyMoney file given");
	}
		
	if ( stockAcctID == null  ||
	     incomeAcctID == null ||
	     offsetAcctID == null ) {
	    throw new IllegalArgumentException("null account ID given");
	}
	
	if ( ! ( stockAcctID.isSet() ) ||
	     ! ( incomeAcctID.isSet() ) ||
	     ! ( offsetAcctID.isSet() ) ) {
	    throw new IllegalArgumentException("unset account ID given");
	}
		
	if ( expensesAcctAmtList == null ) {
	    throw new IllegalArgumentException("null expenses account list given");
	}
			
	if ( expensesAcctAmtList.isEmpty() ) {
	    throw new IllegalArgumentException("empty expenses account list given");
	}
			
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    if ( ! elt.isNotNull() ) {
		throw new IllegalArgumentException("null expenses account list element given");
	    }
	    if ( ! elt.isSet() ) {
		throw new IllegalArgumentException("unset expenses account list element given");
	    }
	}

	if ( divGross == null ) {
	    throw new IllegalArgumentException("null gross dividend given");
	}

	// CAUTION: The following two: In fact, this can happen
	// (negative booking after cancellation / Stornobuchung)
//	if ( divGross.doubleValue() <= 0.0 ) {
//	    throw new IllegalArgumentException("gross dividend <= 0.0 given");
//	}
//				
//	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
//	    if ( elt.amount().doubleValue() <= 0.0 ) {
//		throw new IllegalArgumentException("expense <= 0.0 given");
//	    }
//	}

	LOGGER.debug("genDivivendTrx: Account 1 name (stock):      '" + kmmFile.getAccountByID(stockAcctID).getQualifiedName() + "'");
	LOGGER.debug("genDivivendTrx: Account 2 name (income):     '" + kmmFile.getAccountByID(incomeAcctID).getQualifiedName() + "'");
	int counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    LOGGER.debug("genDivivendTrx: Account 3." + counter + " name (expenses): '" + kmmFile.getAccountByID(elt.accountID()).getQualifiedName() + "'");
	    counter++;
	}
	LOGGER.debug("genDivivendTrx: Account 4 name (offsetting): '" + kmmFile.getAccountByID(offsetAcctID).getQualifiedName() + "'");

	// ---
	// Check account types
	KMyMoneyAccount stockAcct  = kmmFile.getAccountByID(stockAcctID);
	if ( stockAcct.getType() != KMyMoneyAccount.Type.STOCK ) {
	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + KMyMoneyAccount.Type.STOCK);
	}

	KMyMoneyAccount incomeAcct = kmmFile.getAccountByID(incomeAcctID);
	if ( incomeAcct.getType() != KMyMoneyAccount.Type.INCOME ) {
	    throw new IllegalArgumentException("Account with ID " + incomeAcct + " is not of type " + KMyMoneyAccount.Type.INCOME);
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != KMyMoneyAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
	    }
	}
	
	KMyMoneyAccount offsetAcct = kmmFile.getAccountByID(offsetAcctID);
	if ( offsetAcct.getType() != KMyMoneyAccount.Type.CHECKING ) {
	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + KMyMoneyAccount.Type.CHECKING);
	}

	// ---

	FixedPointNumber expensesSum = new FixedPointNumber();
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    expensesSum.add(elt.amount());
	}
	LOGGER.debug("genDivivendTrx: Sum of all expenses: " + expensesSum);

	FixedPointNumber divNet = divGross.copy().subtract(expensesSum);
	LOGGER.debug("genDivivendTrx: Net dividend: " + divNet);

	// ---

	KMyMoneyWritableTransaction trx = kmmFile.createWritableTransaction();
	// Does not work like that: The description/memo on transaction
	// level is purely internal:
	// trx.setDescription(description);
	trx.setDescription("Generated by DepotTransactionManager, " + LocalDateTime.now());

	// ---
	
	KMyMoneyWritableTransactionSplit splt1 = trx.createWritableSplit(stockAcct);
	splt1.setValue(new FixedPointNumber());
	splt1.setShares(new FixedPointNumber());
	splt1.setAction(KMyMoneyTransactionSplit.Action.DIVIDEND);
	// splt1.setPrice("1/1"); // completely optional
	LOGGER.debug("genDivivendTrx: Split 1 to write: " + splt1.toString());

	// ---

	KMyMoneyWritableTransactionSplit splt2 = trx.createWritableSplit(offsetAcct);
	splt2.setValue(new FixedPointNumber(divNet));
	splt2.setShares(new FixedPointNumber(divNet));
	// splt2.setPrice("1/1"); // completely optional
	// This is what we actually want (cf. above):
	splt2.setDescription(descr); // sic, only here
	LOGGER.debug("genDivivendTrx: Split 2 to write: " + splt2.toString());

	// ---

	KMyMoneyWritableTransactionSplit splt3 = trx.createWritableSplit(incomeAcct);
	splt3.setValue(new FixedPointNumber(divGross.negate()));
	splt3.setShares(new FixedPointNumber(divGross.negate()));
	// splt3.setPrice("1/1"); // completely optional
	LOGGER.debug("genDivivendTrx: Split 3 to write: " + splt3.toString());

	// ---

	counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
	    KMyMoneyWritableTransactionSplit splt4 = trx.createWritableSplit(expensesAcct);
	    splt4.setValue(new FixedPointNumber(elt.amount()));
	    splt4.setShares(new FixedPointNumber(elt.amount()));
	    // splt4.setPrice("1/1"); // completely optional
	    LOGGER.debug("genDivivendTrx: Split 4." + counter + " to write: " + splt4.toString());
	    counter++;
	}

	// ---

	trx.setDatePosted(postDate);
	trx.setDateEntered(LocalDate.now());

	// ---

	LOGGER.info("genDivivendTrx: Generated new Transaction: " + trx.getID());
	return trx;
    }
    
}
