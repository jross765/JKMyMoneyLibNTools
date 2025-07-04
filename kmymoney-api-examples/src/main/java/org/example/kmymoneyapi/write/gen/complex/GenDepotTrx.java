package org.example.kmymoneyapi.write.gen.complex;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.apiext.secacct.SecuritiesAccountTransactionManager;
import org.kmymoney.apiext.secacct.SecuritiesAccountTransactionManager.Type;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.tuples.AcctIDAmountPair;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenDepotTrx {
	// CAUTION: The following account IDs are all of type
	// KMMAcctID. Why not KMMComplAcctID? Yes, that would work
	// as well, but we never book to the special top-level
	// accounts. Thus, this is a precautionary measure.

	// BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.xml";
    private static String kmmOutFileName = "example_out.xml";

	private static SecuritiesAccountTransactionManager.Type type = Type.DIVIDEND;

	private static KMMAcctID stockAcctID  = new KMMAcctID( "A000063" );
	private static KMMAcctID incomeAcctID = new KMMAcctID( "A000070" ); // only for dividend, not for buy/sell
	private static List<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>(); // only for dividend, not for buy/sell
	private static KMMAcctID offsetAcctID = new KMMAcctID( "A000004" );
	
	private static FixedPointNumber nofStocks      = new FixedPointNumber(15); // only for buy/sell, not for dividend
	private static FixedPointNumber stockPrc       = new FixedPointNumber("23080/100"); // only for buy/sell, not for dividend
	private static FixedPointNumber divDistrGross  = new FixedPointNumber("11223/100"); // only for dividend, not for buy/sell

	private static LocalDate datPst = LocalDate.of(2024, 3, 1);
	private static String descr = "Dividend payment";
	// END Example data

    // -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GenDepotTrx tool = new GenDepotTrx();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

		KMyMoneyAccount stockAcct = kmmFile.getAccountByID(stockAcctID);
		if ( stockAcct == null )
			System.err.println("Error: Cannot get account with ID '" + stockAcctID + "'");

		KMyMoneyAccount incomeAcct = null;
		if ( incomeAcctID != null ) {
			incomeAcct = kmmFile.getAccountByID(incomeAcctID);
			if ( incomeAcct == null )
				System.err.println("Error: Cannot get account with ID '" + incomeAcctID + "'");
		}

		for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
			KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
			if ( expensesAcct == null )
				System.err.println("Error: Cannot get account with ID '" + elt.accountID() + "'");
		}

		KMyMoneyAccount offsetAcct = kmmFile.getAccountByID(offsetAcctID);
		if ( offsetAcct == null )
			System.err.println("Error: Cannot get account with ID '" + offsetAcctID + "'");

		System.err.println("Account 1 name (stock):      '" + stockAcct.getQualifiedName() + "'");
		if ( incomeAcctID != null )
			System.err.println("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");

		int counter = 1;
		for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
			KMyMoneyAccount expensesAcct = kmmFile.getAccountByID(elt.accountID());
			System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
			counter++;
		}

		System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");

		// ---

		KMyMoneyWritableTransaction trx = null;
		initExpAccts();
		if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK ) {
			trx = SecuritiesAccountTransactionManager
					.genBuyStockTrx(kmmFile, 
									stockAcctID, expensesAcctAmtList, offsetAcctID,
									nofStocks, stockPrc, 
									datPst, descr);
		} else if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND ) {
			trx = SecuritiesAccountTransactionManager
					.genDividDistribTrx(kmmFile, 
									stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
									KMyMoneyTransactionSplit.Action.DIVIDEND, divDistrGross, datPst, 
									descr);
		} else if ( type == SecuritiesAccountTransactionManager.Type.DISTRIBUTION ) {
			trx = SecuritiesAccountTransactionManager
					.genDividDistribTrx(kmmFile, 
									stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
									KMyMoneyTransactionSplit.Action.YIELD, divDistrGross, datPst, // This specific split-action does not really make any difference in KMyMoney --
									descr);                                                       // it will essentially be ignored
		}

		// ---

		System.out.println("Transaction to write: " + trx.toString());
		kmmFile.writeFile(new File(kmmOutFileName));

		System.out.println("OK");
	}
	
	// Example for a dividend payment in Germany (domestic share).
	// If we had a foreign share (e.g. US), we would have to add a 
	// third entry to the list: "Auslaend. Quellensteuer" (that 
	// account is not in the test file yet).
	private void initExpAccts() {
		KMMAcctID expAcct1 = new KMMAcctID( "A000067" ); // Kapitalertragsteuer
		FixedPointNumber amt1 = divDistrGross.copy().multiply(new FixedPointNumber("25/100"));
		AcctIDAmountPair acctAmtPr1 = new AcctIDAmountPair(expAcct1, amt1);
		expensesAcctAmtList.add(acctAmtPr1);
		
		KMMAcctID expAcct2 = new KMMAcctID( "A000027" ); // Soli
		FixedPointNumber amt2 = amt1.copy().multiply(new FixedPointNumber("55/100"));
		AcctIDAmountPair acctAmtPr2 = new AcctIDAmountPair(expAcct2, amt2);
		expensesAcctAmtList.add(acctAmtPr2);
	}
}
