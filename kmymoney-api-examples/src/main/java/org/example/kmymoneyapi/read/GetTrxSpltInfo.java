package org.example.kmymoneyapi.read;

import java.io.File;

import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

public class GetTrxSpltInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = "example_in.xml";
    private static KMMTrxID  trxID    = new KMMTrxID("xyz");
    private static KMMSpltID spltID   = new KMMSpltID("S0001"); 
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetTrxSpltInfo tool = new GetTrxSpltInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

	// You normally would get the transaction-split-ID by first choosing
	// a specific transaction (cf. GetTrxInfo), getting its list of splits
	// and then choosing from them.
	KMMQualifSpltID qualifID = new KMMQualifSpltID(trxID, spltID);
	KMyMoneyTransactionSplit splt = kmmFile.getTransactionSplitByID(qualifID);

	// ------------------------

	try {
	    System.out.println("Qualif. ID:     " + splt.getQualifID());
	} catch (Exception exc) {
	    System.out.println("Qualif. ID:     " + "ERROR");
	}

	try {
	    System.out.println("toString:       " + splt.toString());
	} catch (Exception exc) {
	    System.out.println("toString:       " + "ERROR");
	}

	try {
	    System.out.println("Transaction ID: " + splt.getTransaction().getID());
	} catch (Exception exc) {
	    System.out.println("Transaction ID: " + "ERROR");
	}

	try {
	    System.out.println("Account ID:     " + splt.getAccountID());
	} catch (Exception exc) {
	    System.out.println("Account ID:     " + "ERROR");
	}

	try {
	    System.out.println("Action:         " + splt.getAction());
	} catch (Exception exc) {
	    System.out.println("Action:         " + "ERROR");
	}

	try {
	    System.out.println("Value:          " + splt.getValueFormatted());
	} catch (Exception exc) {
	    System.out.println("Value:          " + "ERROR");
	}

	try {
	    System.out.println("Quantity:       " + splt.getSharesFormatted());
	} catch (Exception exc) {
	    System.out.println("Quantity:       " + "ERROR");
	}

	try {
	    System.out.println("Description:    '" + splt.getMemo() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:    " + "ERROR");
	}
    }
}
