// $Header: svn://gila_svn_priv/finanzen/KMyMoneyToolsXML/trunk/KMyMoneyToolsXML/src/main/java/de/riegelmuenchen/kmymoney/tools/xml/gen/GenPrc.java 8578 2024-02-15 13:51:56Z thilo $

package org.example.kmymoneyapi.write;

import java.io.File;
import java.time.LocalDate;

import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class GenPrc {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName = null;
    private static String kmmOutFileName = null;

    private static KMMQualifSecCurrID fromSecCurrID = null;
    private static KMMQualifCurrID toCurrID = null;
    private static LocalDate date = null;
    private static FixedPointNumber value = null;
    private static KMyMoneyPrice.Source source = null;
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenPrc tool = new GenPrc();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

	KMMCurrPair prcPrID = new KMMCurrPair(fromSecCurrID, toCurrID);
	KMyMoneyWritablePricePair prcPr = kmmFile.getWritablePricePairByID(prcPrID);
	if (prcPr == null) {
	    System.err.println("Price pair '" + prcPrID + "' does not exist in KMyMoney file yet.");
	    System.err.println("Will generate it.");
	    prcPr = kmmFile.createWritablePricePair(fromSecCurrID, toCurrID);
	    prcPr.setFromSecCurrQualifID(fromSecCurrID);
	    prcPr.setToCurrencyQualifID(toCurrID);
	} else {
	    System.err.println("Price pair '" + prcPrID + "' already exists in KMyMoney file.");
	    System.err.println("Will take that one.");
	}

	KMyMoneyWritablePrice prc = kmmFile.createWritablePrice((KMyMoneyPricePairImpl) prcPr);
	// prc.setFromSecCurrQualifID(fromSecCurrID);
	// prc.setToCurrencyQualifID(toCurrID);
	prc.setDate(date);
	prc.setValue(value);
	prc.setSource(source);

	System.out.println("Price to write: " + prc.toString());
	kmmFile.writeFile(new File(kmmOutFileName));
	System.out.println("OK");
    }

}
