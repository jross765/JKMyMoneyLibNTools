package org.kmymoney.tools.xml.gen.simple;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.builder();
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.dateutils.DateHelpers;
import xyz.schnorxoborx.base.dateutils.LocalDateHelpers;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenTrx extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenTrx.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String           kmmInFileName = null;
  private static String           kmmOutFileName = null;
  private static KMMAcctID        fromAcctID = null; // sic, not KMMComplAcctID
  private static KMMAcctID        toAcctID = null;   // dto.
  private static KMMPyeID         pyeID = null;
  private static FixedPointNumber amount = null;
  private static FixedPointNumber quantity = null;
  private static LocalDate        datePosted = null;
  private static String           description = null;

  public static void main( String[] args )
  {
    try
    {
      GenTrx tool = new GenTrx ();
      tool.execute(args);
    }
    catch (CouldNotExecuteException exc) 
    {
      System.err.println("Execution exception. Aborting.");
      exc.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  protected void init() throws Exception
  {
    datePosted = LocalDateHelpers.parseLocalDate(DateHelpers.DATE_UNSET);

    // cfg = new PropertiesConfiguration(System.getProperty("config"));
    // getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFileIn = Option.builder()
      .required()
      .hasArg()
      .argName("file")
      .withDescription("KMyMoney file (in)")
      .longOpt("kmymoney-in-file")
      .create("if");
        
    Option optFileOut = Option.builder()
      .required()
      .hasArg()
      .argName("file")
      .withDescription("KMyMoney file (out)")
      .longOpt("kmymoney-out-file")
      .create("of");
        
    Option optFromAcctID = Option.builder()
      .required()
      .hasArg()
      .argName("acctid")
      .withDescription("Account-ID to be booked from")
      .longOpt("from-account-id")
      .create("facct");
      
    Option optToAcctID = Option.builder()
      .required()
      .hasArg()
      .argName("acctid")
      .withDescription("Account-ID to be booked to")
      .longOpt("to-account-id")
      .create("tacct");
      
    Option optAmount = Option.builder()
      .required()
      .hasArg()
      .argName("amount")
      .withDescription("Amount")
      .longOpt("amount")
      .create("amt");
              
    Option optQuantity = Option.builder()
      .required()
      .hasArg()
      .argName("quantity")
      .withDescription("Quantity")
      .longOpt("quantity")
      .create("qty");
                
    Option optDatePosted = Option.builder()
      .required()
      .hasArg()
      .argName("datetime")
      .withDescription("Date posted")
      .longOpt("date-posted")
      .create("dtp");
            
    // The convenient ones
    Option optDescr = Option.builder()
      .hasArg()
      .argName("descr")
      .withDescription("Description")
      .longOpt("description")
      .create("dscr");
              
    Option optPye = Option.builder()
      .hasArg()
      .argName("pyeid")
      .withDescription("Payeee-ID to be booked against")
      .longOpt("payee-id")
      .create("pye");
    	      
        
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optFromAcctID);
    options.addOption(optToAcctID);
    options.addOption(optAmount);
    options.addOption(optQuantity);
    options.addOption(optDatePosted);
    options.addOption(optDescr);
    options.addOption(optPye);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));
    
    System.err.println("Account name (from): '" + kmmFile.getAccountByID(fromAcctID).getQualifiedName() + "'");
    System.err.println("Account name (to):   '" + kmmFile.getAccountByID(toAcctID).getQualifiedName() + "'");
    
    // ---
    
    KMyMoneyWritableTransaction trx = kmmFile.createWritableTransaction();
    // Does not work like that: The description/memo on transaction 
    // level is purely internal:
    // trx.setDescription(description);
    trx.setDescription("Generated by GenTrx, " + LocalDateTime.now());

    // ---
    
    KMyMoneyWritableTransactionSplit split1 = trx.createWritableSplit(kmmFile.getAccountByID(fromAcctID));
    split1.setValue(new FixedPointNumber(amount.copy().negate()));
    split1.setShares(new FixedPointNumber(quantity.copy().negate()));

    if ( pyeID != null )
    	split1.setPayeeID(pyeID);
    
    // This is what we actually want:
    split1.setDescription(description);
    
    // ---
    
    KMyMoneyWritableTransactionSplit split2 = trx.createWritableSplit(kmmFile.getAccountByID(toAcctID));
    split2.setValue(new FixedPointNumber(amount));
    split2.setShares(new FixedPointNumber(quantity));

    if ( pyeID != null )
    	split2.setPayeeID(pyeID);
    
    // Cf. above
    split2.setDescription(description);
    
    // ---
    
    trx.setDatePosted(datePosted);
    trx.setDateEntered(LocalDate.now());
    
    // ---
    
    System.out.println("Transaction to write: " + trx.toString());
    kmmFile.writeFile(new File(kmmOutFileName));
    System.out.println("OK");
  }
  
  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args) throws InvalidCommandLineArgsException
  {
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = null;
    try
    {
      cmdLine = parser.parse(options, args);
    }
    catch (ParseException exc)
    {
      System.err.println("Parsing options failed. Reason: " + exc.getMessage());
      throw new InvalidCommandLineArgsException();
    }

    // ---

    // <kmymoney-in-file>
    try
    {
      kmmInFileName = cmdLine.getOptionValue("kmymoney-in-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-in-file>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("KMyMoney file (in): '" + kmmInFileName + "'");
    
    // <kmymoney-out-file>
    try
    {
      kmmOutFileName = cmdLine.getOptionValue("kmymoney-out-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-out-file>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("KMyMoney file (out): '" + kmmOutFileName + "'");
    
    // <from-account-id>
    try
    {
      fromAcctID = new KMMAcctID( cmdLine.getOptionValue("from-account-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <from-account-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Account ID (from): '" + fromAcctID + "' ");
    
    // <to-account-id>
    try
    {
      toAcctID = new KMMAcctID( cmdLine.getOptionValue("to-account-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <to-account-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Account ID (to): '" + toAcctID + "'");
    
    // <amount>
    try
    {
      BigMoney betrag = BigMoney.of(CurrencyUnit.EUR, Double.parseDouble(cmdLine.getOptionValue("amount")));
      amount = new FixedPointNumber(betrag.getAmount());
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <amount>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Amount: " + amount);

    // <quantity>
    try
    {
      quantity = new FixedPointNumber(Double.parseDouble(cmdLine.getOptionValue("quantity")));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <quantity>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Quantity: " + quantity);

    // <date-posted>
    try
    {
      datePosted = LocalDateHelpers.parseLocalDate(cmdLine.getOptionValue("date-posted"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <date-posted>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Date posted: " + datePosted);

    // <description>
    if ( cmdLine.hasOption("description") )
    {
      try
      {
        description = cmdLine.getOptionValue("description");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <description>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      description = "Generated by GenTrx, " + LocalDateTime.now();
    }
    System.err.println("description: '" + description + "'");
    
    // <payee-id>
    if ( cmdLine.hasOption("payee-id") )
    {
      try
      {
        pyeID = new KMMPyeID( cmdLine.getOptionValue("payee-id") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <payee-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      pyeID = null;
    }
    System.err.println("Payee ID: '" + pyeID + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenTrx", options );
  }
}
