package org.kmymoney.tools.xml.gen.simple;

import java.io.File;
import java.time.LocalDate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.builder();
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.tools.CommandLineTool;
import org.kmymoney.tools.xml.helper.CmdLineHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.Helper;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenPrc extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GenPrc.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String kmmInFileName = null;
  private static String kmmOutFileName = null;

  private static KMMQualifSecCurrID   fromSecCurrID = null;
  private static KMMQualifCurrID      toCurrID = null;
  private static Helper.DateFormat    dateFormat    = null;
  private static LocalDate            date = null;
  private static FixedPointNumber     value = null;
  private static KMyMoneyPrice.Source source = null;

  public static void main( String[] args )
  {
    try
    {
      GenPrc tool = new GenPrc ();
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
    // invcID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

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
      
    Option optFromSecCurr= Option.builder()
      .required()
      .hasArg()
      .argName("sec/curr")
      .withDescription("From-commodity/currency")
      .longOpt("from-sec-curr")
      .create("f");
          
    Option optToCurr = Option.builder()
      .required()
      .hasArg()
      .argName("curr")
      .withDescription("To-currency")
      .longOpt("to-curr")
      .create("t");
    
    Option optDateFormat = Option.builder()
      .hasArg()
      .argName("date-format")
      .withDescription("Date format")
      .longOpt("date-format")
      .create("df");
            
    Option optDate = Option.builder()
      .required()
      .hasArg()
      .argName("date")
      .withDescription("Date")
      .longOpt("date")
      .create("dat");
          
    Option optValue = Option.builder()
      .required()
      .hasArg()
      .argName("value")
      .withDescription("Value")
      .longOpt("value")
      .create("v");
            
    // The convenient ones
    Option optSource = Option.builder()
      .hasArg()
      .argName("source")
      .withDescription("Source")
      .longOpt("source")
      .create("src");
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optFromSecCurr);
    options.addOption(optToCurr);
    options.addOption(optDateFormat);
    options.addOption(optDate);
    options.addOption(optValue);
    options.addOption(optSource);
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

    KMMPricePairID prcPrID = new KMMPricePairID(fromSecCurrID, toCurrID);
    KMyMoneyWritablePricePair prcPr = kmmFile.getWritablePricePairByID(prcPrID);
    if ( prcPr == null ) {
    	System.err.println("Price pair '" + prcPrID + "' does not exist in KMyMoney file yet.");
    	System.err.println("Will generate it.");
        prcPr = kmmFile.createWritablePricePair(fromSecCurrID, toCurrID);
    } else {
    	System.err.println("Price pair '" + prcPrID + "' already exists in KMyMoney file.");
    	System.err.println("Will take that one.");
    }
    
    KMyMoneyWritablePrice prc = kmmFile.createWritablePrice((KMyMoneyPricePairImpl) prcPr, date);
    // prc.setParentPricePair(prcPr);
    // prc.setDate(date);
    prc.setValue(value);
    prc.setSource(source);
    
    System.out.println("Price to write: " + prc.toString());
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
    
    // <from-sec-curr>
    try
    {
      fromSecCurrID = KMMQualifSecCurrID.parse(cmdLine.getOptionValue("from-sec-curr")); 
      System.err.println("from-sec-curr: " + fromSecCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <from-sec-curr>");
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
    
    // <value>
    try
    {
      value = new FixedPointNumber( Double.parseDouble( cmdLine.getOptionValue("value") ) ) ; 
      System.err.println("value: " + value);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <name>");
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
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      source = KMyMoneyPrice.Source.USER;
    }
    System.err.println("source: " + source);
    
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GenPrc", options );
    
    System.out.println("");
    System.out.println("Valid values for <source>:");
    for ( KMyMoneyPrice.Source elt : KMyMoneyPrice.Source.values() )
      System.out.println(" - " + elt);
  }
}
