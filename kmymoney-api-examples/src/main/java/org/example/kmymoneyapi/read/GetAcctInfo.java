package org.example.kmymoneyapi.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;

public class GetAcctInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName    = "example_in.xml";
    private static Helper.Mode mode      = Helper.Mode.ID;
    private static KMMComplAcctID acctID = new KMMComplAcctID("xyz");
    private static String acctName       = "abc";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetAcctInfo tool = new GetAcctInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

	KMyMoneyAccount acct = null;
	if (mode == Helper.Mode.ID) {
	    acct = kmmFile.getAccountByID(acctID);
	    if (acct == null) {
		System.err.println("Found no account with that name");
		throw new NoEntryFoundException();
	    }
	} else if (mode == Helper.Mode.NAME) {
	    Collection<KMyMoneyAccount> acctList = null;
	    acctList = kmmFile.getAccountsByName(acctName, true, true);
	    if (acctList.size() == 0) {
		System.err.println("Found no account with that name.");
		throw new NoEntryFoundException();
	    } else if (acctList.size() > 1) {
		System.err.println("Found several accounts with that name.");
		System.err.println("Taking first one.");
	    }
	    acct = acctList.iterator().next(); // first element
	}
	
	// ------------------------

	printAcctInfo(acct, 0);
    }

    private void printAcctInfo(KMyMoneyAccount acct, int depth) {
	System.out.println("Depth:           " + depth);

	try {
	    System.out.println("ID:              " + acct.getID());
	} catch (Exception exc) {
	    System.out.println("ID:              " + "ERROR");
	}

	try {
	    System.out.println("toString:        " + acct.toString());
	} catch (Exception exc) {
	    System.out.println("toString:        " + "ERROR");
	}

	try {
	    System.out.println("Type:            " + acct.getType());
	} catch (Exception exc) {
	    System.out.println("Type:            " + "ERROR");
	}

	try {
	    System.out.println("Name:            '" + acct.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:            " + "ERROR");
	}

	try {
	    System.out.println("Qualified name:  '" + acct.getQualifiedName() + "'");
	} catch (Exception exc) {
	    System.out.println("Qualified name:  " + "ERROR");
	}

	try {
	    System.out.println("Memo:            '" + acct.getMemo() + "'");
	} catch (Exception exc) {
	    System.out.println("Memo:            " + "ERROR");
	}

	try {
	    System.out.println("Cmdty/Curr:      '" + acct.getQualifSecCurrID() + "'");
	} catch (Exception exc) {
	    System.out.println("Cmdty/Curr:      " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Balance:         " + acct.getBalanceFormatted());
	} catch (Exception exc) {
	    System.out.println("Balance:         " + "ERROR");
	}

	try {
	    System.out.println("Balance recurs.: " + acct.getBalanceRecursiveFormatted());
	} catch (Exception exc) {
	    System.out.println("Balance recurs.: " + "ERROR");
	}

	// ---

	showParents(acct, depth);
	showChildren(acct, depth);
	showTransactions(acct);
    }

    // -----------------------------------------------------------------

    private void showParents(KMyMoneyAccount acct, int depth) {
	// ::MAGIC
	if (depth <= 0 && !acct.getID().equals("AStd::Asset") && !acct.getID().equals("AStd::Liability")
		&& !acct.getID().equals("AStd::Expense") && !acct.getID().equals("AStd::Income")
		&& !acct.getID().equals("AStd::Equity")) {
	    System.out.println("");
	    System.out.println(">>> BEGIN Parent Account");
	    printAcctInfo(acct.getParentAccount(), depth - 1);
	    System.out.println("<<< END Parent Account");
	}
    }

    private void showChildren(KMyMoneyAccount acct, int depth) {
	System.out.println("");
	System.out.println("Children:");

	if (depth >= 0) {
	    System.out.println(">>> BEGIN Child Account");
	    for (KMyMoneyAccount childAcct : acct.getChildren()) {
		printAcctInfo(childAcct, depth + 1);
	    }
	    System.out.println("<<< END Child Account");
	}
    }

    private void showTransactions(KMyMoneyAccount acct) {
	System.out.println("");
	System.out.println("Transactions:");

	for (KMyMoneyTransaction trx : acct.getTransactions()) {
	    System.out.println(" - " + trx.toString());
	}
    }
}
