package org.example.kmymoney.read;

import java.io.File;

import org.kmymoney.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.basetypes.simple.KMMSpltID;
import org.kmymoney.basetypes.simple.KMMTrxID;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

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
	KMMQualifSplitID qualifID = new KMMQualifSplitID(trxID, spltID);
	KMyMoneyTransactionSplit splt = kmmFile.getTransactionSplitByID(qualifID);

	// ------------------------

	try {
	    System.out.println("Qualif. ID:     " + splt.getQualifId());
	} catch (Exception exc) {
	    System.out.println("Qualif. ID:     " + "ERROR");
	}

	try {
	    System.out.println("toString:       " + splt.toString());
	} catch (Exception exc) {
	    System.out.println("toString:       " + "ERROR");
	}

	try {
	    System.out.println("Transaction ID: " + splt.getTransaction().getId());
	} catch (Exception exc) {
	    System.out.println("Transaction ID: " + "ERROR");
	}

	try {
	    System.out.println("Account ID:     " + splt.getAccountId());
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
