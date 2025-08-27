package org.example.kmymoneyapi.read;

import java.io.File;

import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

public class GetTrxInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = "example_in.xml";
    private static KMMTrxID trxID     = new KMMTrxID("xyz");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetTrxInfo tool = new GetTrxInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

	// You normally would get the transaction-ID by first choosing 
	// a specific account (cf. GetAcctInfo), getting its list of 
	// transactions and then choosing from them.
	KMyMoneyTransaction trx = kmmFile.getTransactionByID(trxID);

	// ------------------------

	try {
	    System.out.println("ID:              " + trx.getID());
	} catch (Exception exc) {
	    System.out.println("ID:              " + "ERROR");
	}

	try {
	    System.out.println("toString:        " + trx.toString());
	} catch (Exception exc) {
	    System.out.println("toString:        " + "ERROR");
	}

	try {
	    System.out.println("Balance:         " + trx.getBalanceFormatted());
	} catch (Exception exc) {
	    System.out.println("Balance:         " + "ERROR");
	}

//    try
//    {
//      System.out.println("Cmdty/Curr:      '" + trx.getCmdtyCurrID() + "'");
//    }
//    catch ( Exception exc )
//    {
//      System.out.println("Cmdty/Curr:      " + "ERROR");
//    }

	try {
	    System.out.println("Description:     '" + trx.getMemo() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:     " + "ERROR");
	}

	// ---

	showSplits(trx);
    }

    // -----------------------------------------------------------------

    private void showSplits(KMyMoneyTransaction trx) {
	System.out.println("");
	System.out.println("Splits:");

	for (KMyMoneyTransactionSplit splt : trx.getSplits()) {
	    System.out.println(" - " + splt.toString());
	}
    }
}
