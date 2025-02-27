package org.kmymoney.tools.xml.get.list;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetPrcList extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPrcList.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String                kmmFileName   = null;
  private static KMMQualifSecCurrID    fromSecCurrID = null;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetPrcList tool = new GetPrcList ();
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
//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFile = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file")
      .withLongOpt("kmymoney-file")
      .create("f");
      
    Option optFromSecCurr= OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("sec/curr")
      .withDescription("From-commodity/currency")
      .withLongOpt("from-sec-curr")
      .create("fr");
    	    	          
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optFromSecCurr);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));
    
    List<KMyMoneyPrice> prcList = kmmFile.getPricesByQualifSecCurrID( fromSecCurrID );
    if ( prcList.size() == 0 ) 
    {
    	System.err.println("Found no price with for that security/currency ID.");
    	throw new NoEntryFoundException();
    }

	System.err.println("Found " + prcList.size() + " price(s).");
    for ( KMyMoneyPrice prc : prcList )
    {
    	System.out.println(prc.toString());	
    }
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

    // <kmymoney-file>
    try
    {
      kmmFileName = cmdLine.getOptionValue("kmymoney-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-file>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("KMyMoney file: '" + kmmFileName + "'");
    
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
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetPrcList", options );
  }
}
