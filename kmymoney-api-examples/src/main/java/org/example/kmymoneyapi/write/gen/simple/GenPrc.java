package org.example.kmymoneyapi.write.gen.simple;

import java.io.File;
import java.time.LocalDate;

import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenPrc {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.xml";
    private static String kmmOutFileName = "example_out.xml";

    private static KMMQualifSecID       fromSecCurr1ID = new KMMQualifSecID("E000001");
    private static KMMQualifCurrID      toCurr1ID      = new KMMQualifCurrID("EUR");
    private static LocalDate            date1          = LocalDate.of(2024, 1, 15);
    private static FixedPointNumber     value1         = new FixedPointNumber("123/1");
    
    private static KMMQualifCurrID      fromSecCurr2ID = new KMMQualifCurrID("BRL");
    private static KMMQualifCurrID      toCurr2ID      = toCurr1ID;
    private static LocalDate            date2          = LocalDate.of(2024, 2, 15);
    private static FixedPointNumber     value2         = new FixedPointNumber("540/100");
    
    private static KMyMoneyPrice.Source source        = KMyMoneyPrice.Source.USER;
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

	System.out.println("---------------------");
	System.out.println("Generate price no. 1:");
	System.out.println("---------------------");
	genPrc1(kmmFile);
	
	System.out.println("");
	System.out.println("---------------------");
	System.out.println("Generate price no. 2:");
	System.out.println("---------------------");
	genPrc2(kmmFile);
	
	System.out.println("");
	System.out.println("---------------------------");
	System.out.println("Write file:");
	System.out.println("---------------------------");
	kmmFile.writeFile(new File(kmmOutFileName));
	
	System.out.println("OK");
    }

    private void genPrc1(KMyMoneyWritableFileImpl kmmFile) {
	KMMPricePairID prcPrID = new KMMPricePairID(fromSecCurr1ID, toCurr1ID);
	KMyMoneyWritablePricePair prcPr = kmmFile.getWritablePricePairByID(prcPrID);
	if (prcPr == null) {
	    System.err.println("Price pair '" + prcPrID + "' does not exist in KMyMoney file yet.");
	    System.err.println("Will generate it.");
	    prcPr = kmmFile.createWritablePricePair(fromSecCurr1ID, toCurr1ID);
	} else {
	    System.err.println("Price pair '" + prcPrID + "' already exists in KMyMoney file.");
	    System.err.println("Will take that one.");
	}

	KMyMoneyWritablePrice prc = kmmFile.createWritablePrice((KMyMoneyPricePairImpl) prcPr, date1);
	prc.setValue(value1);
	prc.setSource(source);

	System.out.println("Price to write: " + prc.toString());
    }

    private void genPrc2(KMyMoneyWritableFileImpl kmmFile) {
	KMMPricePairID prcPrID = new KMMPricePairID(fromSecCurr2ID, toCurr2ID);
	KMyMoneyWritablePricePair prcPr = kmmFile.getWritablePricePairByID(prcPrID);
	if (prcPr == null) {
	    System.err.println("Price pair '" + prcPrID + "' does not exist in KMyMoney file yet.");
	    System.err.println("Will generate it.");
	    prcPr = kmmFile.createWritablePricePair(fromSecCurr2ID, toCurr2ID);
	} else {
	    System.err.println("Price pair '" + prcPrID + "' already exists in KMyMoney file.");
	    System.err.println("Will take that one.");
	}

	KMyMoneyWritablePrice prc = kmmFile.createWritablePrice((KMyMoneyPricePairImpl) prcPr, date2);
	prc.setValue(value2);
	prc.setSource(source);

	System.out.println("Price to write: " + prc.toString());
    }

}
