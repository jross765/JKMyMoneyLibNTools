package org.kmymoney.tools.xml.upd;

import java.io.File;
import java.time.LocalDate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.complex.KMMPriceID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.tools.CommandLineTool;
import org.kmymoney.tools.xml.get.sonstige.GetStockAcct;
import org.kmymoney.tools.xml.helper.CmdLineHelper;
import org.kmymoney.tools.xml.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class UpdPrc extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdPrc.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String     kmmInFileName = null;
  private static String     kmmOutFileName = null;

  private static KMMPriceID           prcID = null;
  private static KMMQualifSecCurrID   fromSecCurrID = null;
  private static KMMQualifCurrID      toCurrID = null;
  private static Helper.DateFormat    dateFormat    = null;
  private static LocalDate            date = null;
  
  private static KMyMoneyPrice.Source source = null;
  private static FixedPointNumber    value = null;

  private static KMyMoneyWritablePrice prc = null;

  public static void main( String[] args )
  {
    try
    {
      UpdPrc tool = new UpdPrc ();
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
    // prcID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFileIn = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file (in)")
      .withLongOpt("kmymoney-in-file")
      .create("if");
          
    Option optFileOut = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file (out)")
      .withLongOpt("kmymoney-out-file")
      .create("of");
      
    Option optFromSecCurr= OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("cmdty/curr")
      .withDescription("From-commodity/currency")
      .withLongOpt("from-cmdty-curr")
      .create("f");
    	          
    Option optToCurr = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("curr")
      .withDescription("To-currency")
      .withLongOpt("to-curr")
      .create("t");
    	    
    Option optDateFormat = OptionBuilder
      .hasArg()
      .withArgName("date-format")
      .withDescription("Date format")
      .withLongOpt("date-format")
      .create("df");
    	            
    Option optDate = OptionBuilder
       .isRequired()
       .hasArg()
       .withArgName("date")
       .withDescription("Date")
       .withLongOpt("date")
       .create("dat");
            
    Option optSource = OptionBuilder
      .hasArg()
      .withArgName("source")
      .withDescription("Price source")
      .withLongOpt("source")
      .create("s");
    	    	    
    Option optValue = OptionBuilder
      .hasArg()
      .withArgName("value")
      .withDescription("Price value")
      .withLongOpt("val")
      .create("v");
    
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optFromSecCurr);
    options.addOption(optToCurr);
    options.addOption(optDateFormat);
    options.addOption(optDate);
    options.addOption(optSource);
    options.addOption(optValue);
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

    try 
    {
      prcID = new KMMPriceID(fromSecCurrID, toCurrID, date);
      System.err.println("Price ID: " + prcID.toString());
    }
    catch ( Exception exc )
    {
      System.err.println("Error: Could not instantiate price ID");
      throw new Exception();
    }
    
    try 
    {
      prc = kmmFile.getWritablePriceByID(prcID);
      System.err.println("Price before update: " + prc.toString());
    }
    catch ( Exception exc )
    {
      System.err.println("Error: Could not find/instantiate price with ID '" + prcID + "'");
      // ::TODO
//      throw new PriceNotFoundException();
      throw new NoEntryFoundException();
    }
    
    doChanges(kmmFile);
    System.err.println("Price after update: " + prc.toString());
    
    kmmFile.writeFile(new File(kmmOutFileName));
    
    System.out.println("OK");
  }

  private void doChanges(KMyMoneyWritableFileImpl kmmFile) throws Exception
  {
    if ( source != null )
    {
      System.err.println("Setting source");
      prc.setSource(source);
    }

    if ( value != null )
    {
      System.err.println("Setting value");
      prc.setValue(value);
    }
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args) throws InvalidCommandLineArgsException
  {
    CommandLineParser parser = new GnuParser();
    CommandLine cmdLine = null;
    try
    {
      cmdLine = parser.parse(options, args);
    }
    catch (ParseException exc)
    {
      System.err.println("Parsing options failed. Reason: " + exc.getMessage());
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
    
    // <from-cmdty-curr>
    try
    {
      fromSecCurrID = KMMQualifSecCurrID.parse(cmdLine.getOptionValue("from-cmdty-curr")); 
      System.err.println("from-cmdty-curr: " + fromSecCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <from-cmdty-curr>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <to-curr>
    try
    {
      toCurrID = KMMQualifCurrID.parse(cmdLine.getOptionValue("to-curr")); 
      System.err.println("to-curr: " + toCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <to-curr>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <date-format>
    dateFormat = CmdLineHelper.getDateFormat(cmdLine, "date-format");
    System.err.println("date-format: " + dateFormat);

    // <date>
    try
    {
      date = CmdLineHelper.getDate(cmdLine, "date", dateFormat); 
      System.err.println("date: " + date);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <date>");
      throw new InvalidCommandLineArgsException();
    }

    // <source>
    if ( cmdLine.hasOption("source") ) 
    {
      try
      {
    	source = KMyMoneyPrice.Source.valueOf( cmdLine.getOptionValue("source") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <source>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Source: " + source);

    // <value>
    if ( cmdLine.hasOption("value") ) 
    {
      try
      {
        value = new FixedPointNumber( cmdLine.getOptionValue("value") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <value>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Value: " + value);
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "UpdPrc", options );
  }
}
