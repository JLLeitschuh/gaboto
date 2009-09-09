/**
 * Copyright 2009 University of Oxford
 *
 * Written by Tim Pizey for the Erewhon Project
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  - Neither the name of the University of Oxford nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            14-Apr-2003
 * Filename           $RCSfile: schemagen.java,v $
 * Revision           $Revision: 1.61 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2009/04/24 12:52:50 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

package net.sf.gaboto.generation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.xerces.util.XMLChar;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import com.hp.hpl.jena.vocabulary.DAML_OIL;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * 
 * Copied wholesale, as schemagen is not designed for extension.
 * 
 * Differences to Jena: 
 * 
 * MODEL is public. 
 * 
 * Date is not in headers so regenerated files are not trivially different. 
 * 
 * CVS removed from headers.
 * 
 * DEFAULT_CLASS_TEMPLATE defined rather than shared DEFAULT_TEMPLATE 
 * 
 * DEFAULT_PROP_TEMPLATE defined rather than shared DEFAULT_TEMPLATE 
 * 
 * Comment and indentation changes.
 * 
 * @since 26 June 2009
 * 
 */
public class VocabularyGenerator {

  /**
   * <p>
   * A vocabulary generator, that will consume an ontology or other vocabulary
   * file, and generate a Java file with the constants from the vocabulary
   * compiled in. Designed to be highly flexible and customisable.
   * </p>
   * 
   * @author Ian Dickinson
   */
  // Constants
  // ////////////////////////////////
  /** The namespace for the configuration model is {@value} */
  public static final String NS = "http://jena.hpl.hp.com/2003/04/schemagen#";

  /** The default location of the configuration model is {@value} */
  public static final String DEFAULT_CONFIG_URI = "file:schemagen.rdf";

  /** The default marker string for denoting substitutions is {@value} */
  public static final String DEFAULT_MARKER = "%";

  /** Default template for writing out value declarations */
  public static final String DEFAULT_TEMPLATE = 
      "public static final %nl%    %valclass% %valname% = MODEL.%valcreator%( \"%valuri%\" );";

  /** Default template for writing out class declarations */
  public static final String DEFAULT_CLASS_TEMPLATE = 
      "public static final %nl%    String %valname%_URI = \"%valuri%\";%nl%" + 
      "    " + 
      "public static final %nl%    %valclass% %valname% = MODEL.%valcreator%( \"%valuri%\" );";
  
  /** Default template for writing out property declarations */
  public static final String DEFAULT_PROP_TEMPLATE = 
     "public static final %nl%    String %valname%_URI = \"%valuri%\";%nl%" + 
     "    " + 
     "public static final %nl%    %valclass% %valname% = MODEL.%valcreator%( \"%valuri%\" );";
  
  /** Default template for writing out individual declarations */
  public static final String DEFAULT_INDIVIDUAL_TEMPLATE = 
    "public static final %valclass% %valname% = MODEL.%valcreator%( \"%valuri%\", %valtype% );";

  /** Default template for the file header */
  public static final String DEFAULT_HEADER_TEMPLATE = "/* $Id: $ */%nl%" + "%package% %nl%" + "%imports% %nl%"
          + "/**%nl%" 
          + " * Vocabulary definitions from %sourceURI%. %nl%"
          + " * %nl%"
          + " * @author Auto-generated by net.sf.gaboto.generation.VocabularyGenerator %nl%" + " */";

  /** Default line length for comments before wrap */
  public static final int COMMENT_LENGTH_LIMIT = 80;

  /* Constants for the various options we can set */

  /**
   * Select an alternative config file; use <code>-c &lt;filename&gt;</code> on
   * command line
   */
  protected static final Object OPT_CONFIG_FILE = new Object();

  /**
   * Turn off all comment output; use <code>--nocomments</code> on command line;
   */
  protected static final Object OPT_NO_COMMENTS = new Object();

  /**
   * Nominate the URL of the input document; use <code>-i &lt;URL&gt;</code> on
   * command line;
   */
  protected static final Object OPT_INPUT = new Object();

  /**
   * Specify that the language of the source is DAML+OIL; use
   * <code>--daml</code> on command line;
   */
  protected static final Object OPT_LANG_DAML = new Object();

  /**
   * Specify that the language of the source is OWL (the default); use
   * <code>--owl</code> on command line;
   */
  protected static final Object OPT_LANG_OWL = new Object();

  /**
   * Specify that the language of the source is RDFS; use <code>--rdfs</code> on
   * command line;
   */
  protected static final Object OPT_LANG_RDFS = new Object();

  /**
   * Specify that destination file; use <code>-o &lt;fileName&gt;</code> on
   * command line;
   */
  protected static final Object OPT_OUTPUT = new Object();

  /** Specify the file header; use <code>--header "..."</code> on command line; */
  protected static final Object OPT_HEADER = new Object();

  /** Specify the file footer; use <code>--footer "..."</code> on command line; */
  protected static final Object OPT_FOOTER = new Object();

  /**
   * Specify the uri of the configuration root node; use
   * <code>--root &lt;URL&gt;</code> on command line
   */
  protected static final Object OPT_ROOT = new Object();

  /**
   * Specify the marker string for substitutions, default is '%'; use
   * <code>-m "..."</code> on command line;
   */
  protected static final Object OPT_MARKER = new Object();

  /**
   * Specify the packagename; use <code>--package &lt;packagename&gt;</code> on
   * command line;
   */
  protected static final Object OPT_PACKAGENAME = new Object();

  /**
   * Use ontology terms in preference to vanilla RDF; use
   * <code>--ontology</code> on command line;
   */
  protected static final Object OPT_ONTOLOGY = new Object();

  /**
   * The name of the generated class; use <code>-n &lt;classname&gt;</code> on
   * command line;
   */
  protected static final Object OPT_CLASSNAME = new Object();

  /**
   * Additional decoration for class header (such as implements); use
   * <code>--classdec &lt;classname&gt;</code> on command line;
   */
  protected static final Object OPT_CLASSDEC = new Object();

  /**
   * The namespace URI for the vocabulary; use <code>-a &lt;uri&gt;</code> on
   * command line;
   */
  protected static final Object OPT_NAMESPACE = new Object();

  /**
   * Additional declarations to add at the top of the class; use
   * <code>--declarations &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_DECLARATIONS = new Object();

  /**
   * Section declaration for properties section; use
   * <code>--propSection &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_PROPERTY_SECTION = new Object();

  /**
   * Section declaration for class section; use
   * <code>--classSection &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_CLASS_SECTION = new Object();

  /**
   * Section declaration for individuals section; use
   * <code>--individualsSection &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_INDIVIDUALS_SECTION = new Object();

  /**
   * Option to suppress properties in vocab file; use
   * <code>--noproperties &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_NOPROPERTIES = new Object();

  /**
   * Option to suppress classes in vocab file; use
   * <code>--noclasses &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_NOCLASSES = new Object();

  /**
   * Option to suppress individuals in vocab file; use
   * <code>--noindividuals &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_NOINDIVIDUALS = new Object();

  /**
   * Option for no file header; use <code>--noheader &lt;...&gt;</code> on
   * command line;
   */
  protected static final Object OPT_NOHEADER = new Object();

  /**
   * Template for writing out property declarations; use
   * <code>--propTemplate &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_PROP_TEMPLATE = new Object();

  /**
   * Template for writing out class declarations; use
   * <code>--classTemplate &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_CLASS_TEMPLATE = new Object();

  /**
   * Template for writing out individual declarations; use
   * <code>--individualTemplate &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_INDIVIDUAL_TEMPLATE = new Object();

  /**
   * Option for mapping constant names to uppercase; use
   * <code>--uppercase &lt;...&gt;</code> on command line;
   */
  protected static final Object OPT_UC_NAMES = new Object();

  /**
   * Option for including non-local URI's in vocabulary; use
   * <code>--include &lt;uri&gt;</code> on command line;
   */
  protected static final Object OPT_INCLUDE = new Object();

  /**
   * Option for adding a suffix to the generated class name; use
   * <code>--classnamesuffix &lt;uri&gt;</code> on command line;
   */
  protected static final Object OPT_CLASSNAME_SUFFIX = new Object();

  /**
   * Option for the presentation syntax (encoding) of the file; use
   * <code>-e <i>encoding</i></code> on command line; use
   * <code>sgen:encoding</code> in config file
   */
  protected static final Object OPT_ENCODING = new Object();

  /** Option to show the usage message; use --help on command line */
  protected static final Object OPT_HELP = new Object();

  /**
   * Option to generate an output file with DOS (\r\n) line endings. Default is
   * Unix line endings.
   */
  protected static final Object OPT_DOS = new Object();

  /**
   * Option to generate to force the model to perform inference, off by default.
   */
  protected static final Object OPT_USE_INF = new Object();

  /**
   * Option to exclude instances of classes in the allowed namespaces, where the
   * individuals themselves are in other namespaces; use
   * <code>--strictIndividuals</code> on command line; use
   * <code>sgen:strictIndividuals</code> in config file
   */
  protected static final Object OPT_STRICT_INDIVIDUALS = new Object();

  /** Option to include the ontology source code in the generated file */
  protected static final Object OPT_INCLUDE_SOURCE = new Object();

  /** Option to turn off strict checking in .a() */
  protected static final Object OPT_NO_STRICT = new Object();

  /**
   * List of Java reserved keywords, see <a href=
   * "http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html"
   * >this list</a>.
   */
  public static final String[] JAVA_KEYWORDS = { "abstract", "continue", "for", "new", "switch", "assert", "default",
      "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements",
      "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
      "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class",
      "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };

  // Static variables
  // ////////////////////////////////

  private static List<String> KEYWORD_LIST;
  static {
    KEYWORD_LIST = Arrays.asList(JAVA_KEYWORDS);
  }

  // Instance variables
  // ////////////////////////////////

  /** The list of command line arguments */
  protected List<String> m_cmdLineArgs;

  /** The root of the options in the config file */
  protected Resource m_root;

  /** The model that contains the configuration information */
  protected Model m_config = ModelFactory.createDefaultModel();

  /** The model that contains the input source */
  protected OntModel m_source;

  /** The output stream we write to */
  protected PrintStream m_output;

  /** Option definitions */
  protected Object[][] m_optionDefinitions = new Object[][] { { OPT_CONFIG_FILE, new OptionDefinition("-c", null) },
      { OPT_ROOT, new OptionDefinition("-r", null) },
      { OPT_NO_COMMENTS, new OptionDefinition("--nocomments", "noComments") },
      { OPT_INPUT, new OptionDefinition("-i", "input") }, { OPT_LANG_DAML, new OptionDefinition("--daml", "daml") },
      { OPT_LANG_OWL, new OptionDefinition("--owl", "owl") },
      { OPT_LANG_RDFS, new OptionDefinition("--rdfs", "rdfs") }, { OPT_OUTPUT, new OptionDefinition("-o", "output") },
      { OPT_HEADER, new OptionDefinition("--header", "header") },
      { OPT_FOOTER, new OptionDefinition("--footer", "footer") },
      { OPT_MARKER, new OptionDefinition("--marker", "marker") },
      { OPT_PACKAGENAME, new OptionDefinition("--package", "package") },
      { OPT_ONTOLOGY, new OptionDefinition("--ontology", "ontology") },
      { OPT_CLASSNAME, new OptionDefinition("-n", "classname") },
      { OPT_CLASSDEC, new OptionDefinition("--classdec", "classdec") },
      { OPT_NAMESPACE, new OptionDefinition("-a", "namespace") },
      { OPT_DECLARATIONS, new OptionDefinition("--declarations", "declarations") },
      { OPT_PROPERTY_SECTION, new OptionDefinition("--propSection", "propSection") },
      { OPT_CLASS_SECTION, new OptionDefinition("--classSection", "classSection") },
      { OPT_INDIVIDUALS_SECTION, new OptionDefinition("--individualsSection", "individualsSection") },
      { OPT_NOPROPERTIES, new OptionDefinition("--noproperties", "noproperties") },
      { OPT_NOCLASSES, new OptionDefinition("--noclasses", "noclasses") },
      { OPT_NOINDIVIDUALS, new OptionDefinition("--noindividuals", "noindividuals") },
      { OPT_PROP_TEMPLATE, new OptionDefinition("--propTemplate", "propTemplate") },
      { OPT_CLASS_TEMPLATE, new OptionDefinition("--classTemplate", "classTemplate") },
      { OPT_INDIVIDUAL_TEMPLATE, new OptionDefinition("--individualTemplate", "individualTemplate") },
      { OPT_UC_NAMES, new OptionDefinition("--uppercase", "uppercase") },
      { OPT_INCLUDE, new OptionDefinition("--include", "include") },
      { OPT_CLASSNAME_SUFFIX, new OptionDefinition("--classnamesuffix", "classnamesuffix") },
      { OPT_NOHEADER, new OptionDefinition("--noheader", "noheader") },
      { OPT_ENCODING, new OptionDefinition("-e", "encoding") }, { OPT_HELP, new OptionDefinition("--help", null) },
      { OPT_DOS, new OptionDefinition("--dos", "dos") },
      { OPT_USE_INF, new OptionDefinition("--inference", "inference") },
      { OPT_STRICT_INDIVIDUALS, new OptionDefinition("--strictIndividuals", "strictIndividuals") },
      { OPT_INCLUDE_SOURCE, new OptionDefinition("--includeSource", "includeSource") },
      { OPT_NO_STRICT, new OptionDefinition("--nostrict", "noStrict") }, };

  /** Stack of replacements to apply */
  protected List<Replacement> m_replacements = new ArrayList<Replacement>();

  /** Output file newline char - default is Unix, override with --dos */
  protected String m_nl = "\n";

  /** Size of indent step */
  protected int m_indentStep = 4;

  /** Set of names used so far */
  protected Set<String> m_usedNames = new HashSet<String>();

  /** Map from resources to java names */
  protected Map<Resource, String> m_resourcesToNames = new HashMap<Resource, String>();

  /** List of allowed namespace URI strings for admissible values */
  protected List<String> m_includeURI = new ArrayList<String>();

  // Constructors
  // ////////////////////////////////

  // External signature methods
  // ////////////////////////////////

  /*
   * Main entry point. See Javadoc for details of the many command line
   * arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) { 
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/DC.owl",
              "-n", "DCVocab",
              "--classnamesuffix", "Vocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/DCVocab.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/Gaboto.owl",
              "-n", "GabotoVocab",
              "--classnamesuffix", "Vocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/GabotoVocab.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/GabotoKML.owl",
              "-n", "GabotoKMLVocab",
              "--classnamesuffix", "Vocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/GabotoKMLVocab.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/Geo.owl",
              "-n", "GeoVocab",
              "--classnamesuffix", "Vocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/GeoVocab.java",
              "--include", "http://www.w3.org/2003/01/geo/wgs84_pos",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/OxPoints.owl",
              "-n", "OxPointsVocab",
              "--classnamesuffix", "Vocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/OxPointsVocab.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/RDFContext.owl",
              "-n", "RDFContext",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/RDFContext.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      // NOTE this one is RDF not OWL 
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/RDFGraph.rdf",
              "-n", "RDFGraph",
              "--rdfs",
              "-a", "http://www.w3.org/2004/03/trix/rdfg-1/",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/RDFGraph.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/Time.owl",
              "-n", "TimeVocab",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/TimeVocab.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      new VocabularyGenerator().go(new String[] 
                                              {
              "-i", "ontologies/VCard.owl",
              "-n", "VCard",
              "-o", "src/main/java/net/sf/gaboto/vocabulary/VCard.java",
              "--ontology",
              "--package", "net.sf.gaboto.vocabulary"
      });      
      
    } else
      new VocabularyGenerator().go(args);
  }

  // Internal implementation methods
  // ////////////////////////////////

  /** Read the configuration parameters and do setup */
  protected void go(String[] args) {
    // save the command line parameters
    m_cmdLineArgs = Arrays.asList(args);

    // check for user requesting help
    if (m_cmdLineArgs.contains(getOpt(OPT_HELP).m_cmdLineForm)) {
      usage();
    }

    // check to see if there's a specified config file
    String configURL = DEFAULT_CONFIG_URI;
    if (hasValue(OPT_CONFIG_FILE)) {
      // check for protocol; add file: if not specified
      configURL = urlCheck(getValue(OPT_CONFIG_FILE));
    }

    // try to read the config URI
    try {
      FileManager.get().readModel(m_config, configURL);
    } catch (Exception e) {
      // if the user left the default config URI in place, it's not an error to
      // fail to read it
      if (!configURL.equals(DEFAULT_CONFIG_URI)) {
        abort("Failed to read configuration from URI " + configURL, e);
      }
    }

    // got the configuration, now we can begin processing
    processInput();
  }

  /** The sequence of steps to process an entire file */
  protected void processInput() {
    determineConfigRoot();
    determineLanguage();
    selectInput();
    selectOutput();
    setGlobalReplacements();

    processHeader();
    writeClassDeclaration();
    writeInitialDeclarations();
    writeProperties();
    writeClasses();
    writeIndividuals();
    writeClassClose();
    processFooter();
    closeOutput();
  }

  /** Determine the root resource in the configuration file */
  protected void determineConfigRoot() {
    if (hasValue(OPT_ROOT)) {
      String rootURI = getValue(OPT_ROOT);
      m_root = m_config.getResource(rootURI);
    } else {
      // no specified root, we assume there is only one with type sgen:Config
      StmtIterator i = m_config.listStatements(null, RDF.type, m_config.getResource(NS + "Config"));
      if (i.hasNext()) {
        m_root = i.nextStatement().getSubject();
      } else {
        // no configuration root, so we invent one
        m_root = m_config.createResource();
      }
    }

    // add any extra uri's that are allowed in the filter
    m_includeURI.addAll(getAllValues(OPT_INCLUDE));
  }

  /** Create the source model after determining which input language */
  protected void determineLanguage() {
    OntModelSpec s = null;
    if (isTrue(OPT_LANG_DAML)) {
      // DAML language specified
      if (isTrue(OPT_USE_INF)) {
        s = OntModelSpec.DAML_MEM_RULE_INF;
      } else {
        s = OntModelSpec.DAML_MEM;
      }
    } else if (isTrue(OPT_LANG_RDFS)) {
      // RDFS language specified
      if (isTrue(OPT_USE_INF)) {
        s = OntModelSpec.RDFS_MEM_RDFS_INF;
      } else {
        s = OntModelSpec.RDFS_MEM;
      }
    } else {
      // owl is the default
      // s = OntModelSpec.getDefaultSpec( ProfileRegistry.OWL_LANG );
      if (isTrue(OPT_USE_INF)) {
        s = OntModelSpec.OWL_MEM_RULE_INF;
      } else {
        s = OntModelSpec.OWL_MEM;
      }
    }

    m_source = ModelFactory.createOntologyModel(s, null);
    m_source.getDocumentManager().setProcessImports(false);

    // turn off strict checking on request
    if (isTrue(OPT_NO_STRICT)) {
      m_source.setStrictMode(false);
    }
  }

  /**
   * Identify the URL that is to be read in and translated to a vocabulary file,
   * and load the source into the source model
   */
  protected void selectInput() {
    if (!hasResourceValue(OPT_INPUT)) {
      usage();
    }

    String input = urlCheck(getValue(OPT_INPUT));
    String syntax = getValue(OPT_ENCODING);

    try {
      FileManager.get().readModel(m_source, input, syntax);
    } catch (JenaException e) {
      abort("Failed to read input source " + input, e);
    }
  }

  /** Identify the file we are to write the output to */
  protected void selectOutput() {
    String outFile = getValue(OPT_OUTPUT);
    if (outFile == null) {
      m_output = System.out;
    } else {
      try {
        File out = new File(outFile);

        if (out.isDirectory()) {
          // create a file in this directory named classname.java
          String fileName = outFile + System.getProperty("file.separator") + getClassName() + ".java";
          out = new File(fileName);
        }

        m_output = new PrintStream(new FileOutputStream(out));
      } catch (Exception e) {
        abort("I/O error while trying to open file for writing: " + outFile, e);
      }
    }

    // check for DOS line endings
    if (isTrue(OPT_DOS)) {
      m_nl = "\r\n";
    }
  }

  /** Process the header at the start of the file, if defined */
  protected void processHeader() {
    String header = hasValue(OPT_HEADER) ? getValue(OPT_HEADER) : DEFAULT_HEADER_TEMPLATE;

    // user can turn of header processing, default is to have it on
    if (!isTrue(OPT_NOHEADER)) {
      writeln(0, substitute(header));
    } else {
      // we have to do the imports at least
      writeln(0, "import com.hp.hpl.jena.rdf.model.ModelFactory;");
      writeln(0, "import com.hp.hpl.jena.rdf.model.Resource;");
      if (isTrue(OPT_ONTOLOGY)) {
        writeln(0, "import com.hp.hpl.jena.ontology.ObjectProperty;");
        writeln(0, "import com.hp.hpl.jena.ontology.OntClass;");
        writeln(0, "import com.hp.hpl.jena.ontology.OntModel;");
        writeln(0, "import com.hp.hpl.jena.ontology.OntModelSpec;");
      }
      if (isTrue(OPT_INCLUDE_SOURCE)) {
        writeln(0, "import java.io.ByteArrayInputStream;");
      }
    }
  }

  /** Process the footer at the end of the file, if defined */
  protected void processFooter() {
    String footer = getValue(OPT_FOOTER);

    if (footer != null) {
      writeln(0, substitute(footer));
    }
  }

  /** The list of replacements that are always available */
  protected void setGlobalReplacements() {
    addReplacementPattern("date", new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date()));
    addReplacementPattern("package", hasValue(OPT_PACKAGENAME) ? ("package " + getValue(OPT_PACKAGENAME) + ";") : "");
    addReplacementPattern("imports", getImports());
    addReplacementPattern("classname", getClassName());
    addReplacementPattern("sourceURI", getResource(OPT_INPUT).getURI());
    addReplacementPattern("nl", m_nl);
  }

  /** Add a pattern-value pair to the list of available patterns */
  protected void addReplacementPattern(String key, String replacement) {
    if (replacement != null && key != null) {
      String marker = getValue(OPT_MARKER);
      marker = (marker == null) ? DEFAULT_MARKER : marker;

      try {
        m_replacements.add(new Replacement(Pattern.compile(marker + key + marker), replacement));
      } catch (PatternSyntaxException e) {
        abort("Malformed regexp pattern " + marker + key + marker, e);
      }
    }
  }

  /** Pop n replacements off the stack */
  protected void pop(int n) {
    for (int i = 0; i < n; i++) {
      m_replacements.remove(m_replacements.size() - 1);
    }
  }

  /** Close the output file */
  protected void closeOutput() {
    m_output.flush();
    m_output.close();
  }

  /** Answer true if the given option is set to true */
  protected boolean isTrue(Object option) {
    return getOpt(option).isTrue();
  }

  /** Answer true if the given option has value */
  protected boolean hasValue(Object option) {
    return getOpt(option).hasValue();
  }

  /** Answer true if the given option has a resource value */
  protected boolean hasResourceValue(Object option) {
    return getOpt(option).hasResourceValue();
  }

  /** Answer the value of the option or null */
  protected String getValue(Object option) {
    return getOpt(option).getValue();
  }

  /** Answer all values for the given options as Strings */
  protected List<String> getAllValues(Object option) {
    List<String> values = new ArrayList<String>();
    OptionDefinition opt = getOpt(option);

    // look in the command line arguments
    for (Iterator<String> i = m_cmdLineArgs.iterator(); i.hasNext();) {
      String s = i.next();
      if (s.equals(opt.m_cmdLineForm)) {
        // next iterator value is the arg value
        values.add(i.next());
      }
    }

    // now look in the config file
    for (StmtIterator i = m_root.listProperties(opt.m_prop); i.hasNext();) {
      Statement s = i.nextStatement();

      if (s.getObject() instanceof Literal) {
        values.add(s.getString());
      } else {
        values.add(s.getResource().getURI());
      }
    }

    return values;
  }

  /** Answer the value of the option or null */
  protected Resource getResource(Object option) {
    return getOpt(option).getResource();
  }

  /** Answer the option object for the given option */
  protected OptionDefinition getOpt(Object option) {
    for (int i = 0; i < m_optionDefinitions.length; i++) {
      if (m_optionDefinitions[i][0] == option) {
        return (OptionDefinition) m_optionDefinitions[i][1];
      }
    }

    return null;
  }

  /** Abort due to exception */
  protected void abort(String msg, Exception e) {
    System.err.println(msg);
    if (e != null) {
      System.err.println(e);
    }
    System.exit(1);
  }

  /** Print usage message and abort */
  protected void usage() {
    System.err.println("Usage:");
    System.err.println("  java jena.schemagen [options ...]");
    System.err.println();
    System.err.println("Commonly used options include:");
    System.err.println("   -i <input> the source document as a file or URL.");
    System.err.println("   -n <name> the name of the created Java class.");
    System.err.println("   -a <uri> the namespace URI of the source document.");
    System.err.println("   -o <file> the file to write the generated class into.");
    System.err.println("   -o <dir> the directory in which the generated Java class is created.");
    System.err.println("            By default, output goes to stdout.");
    System.err.println("   -e <encoding> the encoding of the input document (N3, RDF/XML, etc).");
    System.err.println("   -c <config> a filename or URL for an RDF document containing ");
    System.err.println("               configuration parameters.");
    System.err.println();
    System.err.println("Many other options are available. See the schemagen HOWTO in the ");
    System.err.println("Jena documentation for full details.");
    System.exit(1);
  }

  /** Use the current replacements list to do the subs in the given string */
  protected String substitute(String sIn) {
    String s = sIn;

    for (Iterator<Replacement> i = m_replacements.iterator(); i.hasNext();) {
      Replacement r = i.next();

      s = r.pattern.matcher(s).replaceAll(r.sub);
    }

    return s;
  }

  /** Add the appropriate indent to a buffer */
  protected int indentTo(int i, StringBuffer buf) {
    int indent = i * m_indentStep;
    for (int j = 0; j < indent; j++) {
      buf.append(' ');
    }

    return indent;
  }

  /** Write a blank line, with indent and newline */
  protected void writeln(int indent) {
    writeln(indent, "");
  }

  /** Write out the given string with n spaces of indent, with newline */
  protected void writeln(int indent, String s) {
    write(indent, s);
    m_output.print(m_nl);
  }

  /** Write out the given string with n spaces of indent */
  protected void write(int indentLevel, String s) {
    for (int i = 0; i < (m_indentStep * indentLevel); i++) {
      m_output.print(" ");
    }

    m_output.print(s);
  }

  /** Determine the list of imports to include in the file */
  protected String getImports() {
    StringBuffer buf = new StringBuffer();
    buf.append("import com.hp.hpl.jena.rdf.model.*;");
    buf.append(m_nl);

    if (useOntology()) {
      buf.append("import com.hp.hpl.jena.ontology.*;");
      buf.append(m_nl);
    }

    if (includeSource()) {
      buf.append("import java.io.ByteArrayInputStream;");
      buf.append(m_nl);
    }

    return buf.toString();
  }

  /** Determine the class name of the vocabulary from the URI */
  protected String getClassName() {
    // if a class name is given, just use that
    if (hasValue(OPT_CLASSNAME)) {
      return getValue((OPT_CLASSNAME));
    }

    // otherwise, we generate a name based on the URI
    String uri = getValue(OPT_INPUT);

    // remove any suffixes
    uri = (uri.endsWith("#")) ? uri.substring(0, uri.length() - 1) : uri;
    uri = (uri.endsWith(".daml")) ? uri.substring(0, uri.length() - 5) : uri;
    uri = (uri.endsWith(".owl")) ? uri.substring(0, uri.length() - 4) : uri;
    uri = (uri.endsWith(".rdf")) ? uri.substring(0, uri.length() - 4) : uri;
    uri = (uri.endsWith(".rdfs")) ? uri.substring(0, uri.length() - 5) : uri;
    uri = (uri.endsWith(".n3")) ? uri.substring(0, uri.length() - 3) : uri;
    uri = (uri.endsWith(".xml")) ? uri.substring(0, uri.length() - 4) : uri;

    // now work back to the first non name character from the end
    int i = uri.length() - 1;
    for (; i > 0; i--) {
      if (!Character.isUnicodeIdentifierPart(uri.charAt(i)) && uri.charAt(i) != '-') {
        i++;
        break;
      }
    }

    String name = uri.substring(i);

    // optionally add name suffix
    if (hasValue(OPT_CLASSNAME_SUFFIX)) {
      name = name + getValue(OPT_CLASSNAME_SUFFIX);
    }

    // now we make the name into a legal Java identifier
    return asLegalJavaID(name, true);
  }

  /** Answer true if we are using ontology terms in this vocabulary */
  protected boolean useOntology() {
    return isTrue(OPT_ONTOLOGY);
  }

  /** Answer true if all comments are suppressed */
  protected boolean noComments() {
    return isTrue(OPT_NO_COMMENTS);
  }

  /** Answer true if ontology source code is to be included */
  protected boolean includeSource() {
    return isTrue(OPT_INCLUDE_SOURCE);
  }

  /** Convert s to a legal Java identifier; capitalise first char if cap is true */
  protected String asLegalJavaID(String s, boolean cap) {
    StringBuffer buf = new StringBuffer();
    int i = 0;

    // treat the first character specially - must be able to start a Java ID,
    // may have to up-case
    try {
      for (; !Character.isJavaIdentifierStart(s.charAt(i)); i++) { /**/
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.err.println("Could not identify legal Java identifier start character in '" + s + "', replacing with __");
      return "__";
    }
    buf.append(cap ? Character.toUpperCase(s.charAt(i)) : s.charAt(i));

    // copy the remaining characters - replace non-legal chars with '_'
    for (++i; i < s.length(); i++) {
      char c = s.charAt(i);
      buf.append(Character.isJavaIdentifierPart(c) ? c : '_');
    }

    // check for illegal keyword
    if (KEYWORD_LIST.contains(buf.toString())) {
      buf.append('_');
    }

    return buf.toString();
  }

  /** The opening class declaration */
  protected void writeClassDeclaration() {
    write(0, "public class ");
    write(0, getClassName());
    write(0, " ");

    if (hasValue(OPT_CLASSDEC)) {
      write(0, getValue(OPT_CLASSDEC));
    }

    writeln(0, "{");
  }

  /** The close of the class decoration */
  protected void writeClassClose() {
    writeln(0, "}");
  }

  /** Write the declarations at the head of the class */
  protected void writeInitialDeclarations() {
    writeModelDeclaration();
    writeSource();
    writeNamespace();

    if (hasValue(OPT_DECLARATIONS)) {
      writeln(0, getValue(OPT_DECLARATIONS));
    }
  }

  /** Write the declaration of the model */
  protected void writeModelDeclaration() {
    if (useOntology()) {
      String lang = "OWL";
      if (isTrue(OPT_LANG_DAML)) {
        lang = "DAML";
      } else if (isTrue(OPT_LANG_RDFS)) {
        lang = "RDFS";
      }
      writeln(1, "/** <p>The ontology model that holds the vocabulary terms</p> */");
      writeln(1, "public static OntModel MODEL = ModelFactory.createOntologyModel( OntModelSpec." + lang
              + "_MEM, null );");
    } else {
      writeln(1, "/** <p>The RDF model that holds the vocabulary terms</p> */");
      writeln(1, "public static Model MODEL = ModelFactory.createDefaultModel();");
    }

    writeln(1);
  }

  /** Write the source code of the input model into the file itself */
  protected void writeSource() {
    if (includeSource()) {
      // first save a copy of the source in compact form into a buffer
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      m_source.write(bos, "N3");
      String output = bos.toString();

      // now we embed each line of the source in the output
      writeln(1, "private static final String SOURCE = ");
      boolean first = true;

      StringTokenizer st = new StringTokenizer(output, "\n");
      while (st.hasMoreTokens()) {
        String tok = st.nextToken();
        if (tok.endsWith("\r")) {
          tok = tok.substring(0, tok.length() - 1);
        }
        write(2, first ? "   " : " + ");
        write(0, "\"");
        write(0, protectQuotes(tok));
        writeln(2, "\\n\"");
        first = false;
      }

      // then we reference the string constant when reading the source
      // note that we avoid StringReader due to charset encoding issues
      writeln(1, ";");
      writeln(0, "");
      writeln(1, "/** Read the ontology definition into the source model */ ");
      writeln(1, "static { ");
      writeln(2, "MODEL.read( new ByteArrayInputStream( SOURCE.getBytes() ), null, \"N3\" );");
      writeln(1, "}");
      writeln(0, "");
    }
  }

  /**
   * Protect any double quotes in the given string so that it's a legal Java
   * String
   */
  private String protectQuotes(String s) {
    int nDquote = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '"') {
        nDquote++;
      }
    }

    if (nDquote == 2) {
      // need to protect the begin and end quote chars
      return s.replaceAll("\"", "\\\\\"");
    } else if (nDquote > 2) {
      // embedded quote chars in the string
      // N3 convention is to use triple-quote blocks
      int qStart = s.indexOf('"');
      int qEnd = s.lastIndexOf('"');

      StringBuffer s0 = new StringBuffer(s.length());

      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);

        if (c == '"') {
          // protect embedded " characters, treating the outer pair differently
          // than any inner quotes
          if (i == qStart || i == qEnd) {
            s0.append("\\\"\\\"\\\"");
          } else {
            s0.append("\\\"");
          }
        } else if (c == '\\') {
          // protect embedded \ characters
          s0.append("\\\\");
        } else {
          s0.append(c);
        }
      }

      return s0.toString();
    } else {
      return s;
    }
  }

  /** Write the string and resource that represent the namespace */
  protected void writeNamespace() {
    String nsURI = determineNamespaceURI();

    writeln(1, "/** <p>The namespace of the vocabulary as a string</p> */");
    writeln(1, "public static final String NS = \"" + nsURI + "\";");
    writeln(1);

    writeln(1, "/** <p>The namespace of the vocabulary as a string</p>");
    writeln(1, " *  @see #NS */");
    writeln(1, "public static String getURI() {return NS;}");
    writeln(1);

    writeln(1, "/** <p>The namespace of the vocabulary as a resource</p> */");
    writeln(1, "public static final Resource NAMESPACE = MODEL.createResource( NS );");
    writeln(1);
  }

  /** Determine what the namespace URI for this vocabulary is */
  protected String determineNamespaceURI() {
    // we have a sequence of strategies for determining the ontology namespace
    String ns = getOptionNamespace();
    if (ns == null) {
      ns = getDefaultPrefixNamespace();
    }
    if (ns == null) {
      ns = getOntologyElementNamespace();
    }
    if (ns == null) {
      ns = guessNamespace();
    }

    // did we get one?
    if (ns == null) {
      abort("Could not determine the base URI for the input vocabulary", null);
    }

    m_includeURI.add(ns);
    return ns;
  }

  /** User has set namespace via a schemagen option */
  protected String getOptionNamespace() {
    return hasResourceValue(OPT_NAMESPACE) ? getResource(OPT_NAMESPACE).getURI() : null;
  }

  /** Document has set an empty prefix for the model */
  protected String getDefaultPrefixNamespace() {
    // alternatively, the default namespace may be set in the prefix mapping
    // read from the input document
    String defaultNS = m_source.getNsPrefixURI("");
    if (defaultNS == null) {
      defaultNS = m_source.getBaseModel().getNsPrefixURI("");
    }

    return defaultNS;
  }

  /** Document has an owl:Ontology or daml:Ontology element */
  protected String getOntologyElementNamespace() {
    // if we are using an ontology model, we can get the namespace URI from the
    // ontology element
    String uri = null;

    StmtIterator i = m_source.getBaseModel().listStatements(null, RDF.type, m_source.getProfile().ONTOLOGY());

    if (i.hasNext()) {
      Resource ont = i.nextStatement().getSubject();
      uri = ont.getURI();

      // ensure ends with namespace separator char
      char ch = uri.charAt(uri.length() - 1);
      boolean endsWithNCNameCh = XMLChar.isNCName(ch);
      uri = endsWithNCNameCh ? uri + "#" : uri;
    }

    return uri;
  }

  /** Guess the URI from the most prevalent URI */
  protected String guessNamespace() {
    Map<String, Integer> nsCount = new HashMap<String, Integer>();

    // count all of the namespaces used in the model
    for (StmtIterator i = m_source.listStatements(); i.hasNext();) {
      Statement s = i.next();
      countNamespace(s.getSubject(), nsCount);
      countNamespace(s.getPredicate(), nsCount);
      if (s.getObject().isResource()) {
        countNamespace(s.getResource(), nsCount);
      }
    }

    // now find the maximal element
    String ns = null;
    int max = 0;
    for (Iterator<String> i = nsCount.keySet().iterator(); i.hasNext();) {
      String nsKey = i.next();

      // we ignore the usual suspects
      if (!(OWL.getURI().equals(nsKey) || RDF.getURI().equals(nsKey) || RDFS.getURI().equals(nsKey) || XSD.getURI()
              .equals(nsKey))) {
        // not an ignorable namespace
        int count = nsCount.get(nsKey).intValue();

        if (count > max) {
          // highest count seen so far
          max = count;
          ns = nsKey;
        }
      }
    }

    return ns;
  }

  /** Record a use of the given namespace in the count map */
  private void countNamespace(Resource r, Map<String, Integer> nsCount) {
    if (!r.isAnon()) {
      String ns = r.getNameSpace();

      // increment the count for this namespace
      Integer count = nsCount.containsKey(ns) ? (Integer) nsCount.get(ns) : new Integer(0);
      Integer count1 = new Integer(count.intValue() + 1);

      nsCount.put(ns, count1);
    }
  }

  /** Write the list of properties */
  protected void writeProperties() {
    if (isTrue(OPT_NOPROPERTIES)) {
      return;
    }

    if (hasValue(OPT_PROPERTY_SECTION)) {
      writeln(0, getValue(OPT_PROPERTY_SECTION));
    }

    if (useOntology()) {
      writeObjectProperties();
      writeDatatypeProperties();
      writeAnnotationProperties();

      // we also write out the RDF properties, to mop up any props that are not
      // stated as
      // object, datatype or annotation properties
      writeRDFProperties(true);
    } else {
      writeRDFProperties(false);
    }
  }

  /** Write any object properties in the vocabulary */
  @SuppressWarnings("unchecked")
  protected void writeObjectProperties() {
    write(1, "/** @see net.sf.gaboto.generation.VocabularyGenerator#writeObjectProperties() */\n");
    String template = hasValue(OPT_PROP_TEMPLATE) ? getValue(OPT_PROP_TEMPLATE) : DEFAULT_PROP_TEMPLATE;

    if (!isTrue(OPT_LANG_RDFS)) {
      for (Iterator<? extends RDFNode> i = sorted(m_source.listObjectProperties()); i.hasNext();) {
        writeValue((Resource) i.next(), template, "ObjectProperty", "createObjectProperty", "_PROP");
      }
    }
    write(1, "\n");
  }

  /** Write any datatype properties in the vocabulary */
  @SuppressWarnings("unchecked")
  protected void writeDatatypeProperties() {
    write(1, "/** @see net.sf.gaboto.generation.VocabularyGenerator#writeDatatypeProperties() */ \n");
    String template = hasValue(OPT_PROP_TEMPLATE) ? getValue(OPT_PROP_TEMPLATE) : DEFAULT_PROP_TEMPLATE;

    if (!isTrue(OPT_LANG_RDFS)) {
      for (Iterator<? extends RDFNode> i = sorted(m_source.listDatatypeProperties()); i.hasNext();) {
        writeValue((Resource) i.next(), template, "DatatypeProperty", "createDatatypeProperty", "_PROP");
      }
    }
    write(1, "\n");
  }

  /** Write any annotation properties in the vocabulary */
  @SuppressWarnings("unchecked")
  protected void writeAnnotationProperties() {
    write(1, "/** @see net.sf.gaboto.generation.VocabularyGenerator#writeAnnotationProperties() */\n");
    String template = hasValue(OPT_PROP_TEMPLATE) ? getValue(OPT_PROP_TEMPLATE) : DEFAULT_PROP_TEMPLATE;

    if (!isTrue(OPT_LANG_RDFS)) {
      for (Iterator<? extends RDFNode> i = sorted(m_source.listAnnotationProperties()); i.hasNext();) {
        writeValue((Resource) i.next(), template, "AnnotationProperty", "createAnnotationProperty", "_PROP");
      }
    }
    write(1, "\n");
  }

  /** Write any vanilla RDF properties in the vocabulary */
  protected void writeRDFProperties(boolean useOntProperty) {
    String template = hasValue(OPT_PROP_TEMPLATE) ? getValue(OPT_PROP_TEMPLATE) : DEFAULT_PROP_TEMPLATE;
    String propType = useOntProperty ? "OntProperty" : "Property";

    // select the appropriate properties based on the language choice
    Resource[] props;
    if (isTrue(OPT_LANG_OWL)) {
      props = new Resource[] { OWL.ObjectProperty, OWL.DatatypeProperty, RDF.Property };
    } else if (isTrue(OPT_LANG_DAML)) {
      props = new Resource[] { DAML_OIL.ObjectProperty, DAML_OIL.DatatypeProperty, RDF.Property };
    } else {
      props = new Resource[] { RDF.Property };
    }

    // collect the properties to be written
    List<Resource> propertyResources = new ArrayList<Resource>();
    for (int j = 0; j < props.length; j++) {
      for (StmtIterator i = m_source.listStatements(null, RDF.type, props[j]); i.hasNext();) {
        propertyResources.add(i.nextStatement().getSubject());
      }
    }

    // now write the properties
    for (Iterator<? extends RDFNode> i = sorted(propertyResources); i.hasNext();) {
      writeValue((Resource) i.next(), template, propType, "create" + propType, "_PROP");
    }
  }

  /** Write any classes in the vocabulary */
  protected void writeClasses() {
    if (isTrue(OPT_NOCLASSES)) {
      return;
    }

    if (hasValue(OPT_CLASS_SECTION)) {
      writeln(0, getValue(OPT_CLASS_SECTION));
    }

    if (useOntology()) {
      writeOntClasses();
    } else {
      writeRDFClasses();
    }
  }

  /** Write classes as ontology terms */
  @SuppressWarnings("unchecked")
  protected void writeOntClasses() {
    write(1, "/** @see net.sf.gaboto.generation.VocabularyGenerator#writeOntClasses() */\n");
    String template = hasValue(OPT_CLASS_TEMPLATE) ? getValue(OPT_CLASS_TEMPLATE) : DEFAULT_CLASS_TEMPLATE;

    for (Iterator<? extends RDFNode> i = sorted(m_source.listClasses()); i.hasNext();) {
      writeValue((Resource) i.next(), template, "OntClass", "createClass", "_CLASS");
    }
    write(1, "\n");
  }

  /** Write classes as vanilla RDF terms */
  @SuppressWarnings("unchecked")
  protected void writeRDFClasses() {
    String template = hasValue(OPT_CLASS_TEMPLATE) ? getValue(OPT_CLASS_TEMPLATE) : DEFAULT_TEMPLATE;

    // make sure we're looking for the appropriate type of class
    Resource cls = OWL.Class;
    if (isTrue(OPT_LANG_DAML)) {
      cls = DAML_OIL.Class;
    } else if (isTrue(OPT_LANG_RDFS)) {
      cls = RDFS.Class;
    }

    // collect the classes to list
    List<Resource> classes = m_source.listStatements(null, RDF.type, cls).mapWith(new Map1() {
      @Override
      public Object map1(Object arg0) {
        return ((Statement) arg0).getSubject();
      }
    }).toList();

    for (Iterator<? extends RDFNode> i = sorted(classes); i.hasNext();) {
      writeValue((Resource) i.next(), template, "Resource", "createResource", "_CLASS");
    }
  }

  /** Write any instances (individuals) in the vocabulary */
  protected void writeIndividuals() {
    if (isTrue(OPT_NOINDIVIDUALS)) {
      return;
    }

    if (hasValue(OPT_INDIVIDUALS_SECTION)) {
      writeln(0, getValue(OPT_INDIVIDUALS_SECTION));
    }

    if (useOntology()) {
      writeOntIndividuals();
    } else {
      writeRDFIndividuals();
    }
  }

  /** Write individuals as ontology terms */
  protected void writeOntIndividuals() {
    write(1, "/** @see net.sf.gaboto.generation.VocabularyGenerator#writeOntIndividuals() */\n");
    String template = hasValue(OPT_INDIVIDUAL_TEMPLATE) ? getValue(OPT_INDIVIDUAL_TEMPLATE)
            : DEFAULT_INDIVIDUAL_TEMPLATE;

    for (Iterator<? extends RDFNode> i = selectIndividuals(); i.hasNext();) {
      Individual ind = (Individual) ((Resource) i.next()).as(Individual.class);

      // do we have a local class resource
      Resource cls = ind.getOntClass();
      if (cls == null) {
        cls = OWL.Thing;
      }

      String varName = m_resourcesToNames.get(cls);
      String valType = (varName != null) ? varName : "MODEL.createClass( \"" + cls.getURI() + "\" )";

      // push the individuals type onto the stack
      addReplacementPattern("valtype", valType);
      writeValue(ind, template, "Individual", "createIndividual", "_INSTANCE");
      pop(1);

    }
    write(1, "\n");
  }

  /** Write individuals as vanilla RDF terms */
  protected void writeRDFIndividuals() {
    String template = hasValue(OPT_INDIVIDUAL_TEMPLATE) ? getValue(OPT_INDIVIDUAL_TEMPLATE) : DEFAULT_TEMPLATE;

    for (Iterator<? extends RDFNode> i = selectIndividuals(); i.hasNext();) {
      writeValue((Resource) i.next(), template, "Resource", "createResource", "_INSTANCE");
    }
  }

  /** Answer an iterator over the individuals selected for output */
  protected ExtendedIterator<? extends RDFNode> selectIndividuals() {
    List<Resource> candidates = new ArrayList<Resource>();
    for (StmtIterator i = m_source.listStatements(null, RDF.type, (RDFNode) null); i.hasNext();) {
      Statement candidate = i.nextStatement();

      if (candidate.getObject().isResource()) {
        Resource candObj = candidate.getResource();
        Resource candSubj = candidate.getSubject();

        // note that whether candSubj is included is tested later on by {@link
        // #filter}
        if (!candSubj.isAnon() && isIncluded(candObj)) {
          candidates.add(candSubj);
        }
      }
    }

    return sorted(candidates);
  }

  /**
   * Answer true if the given resource is accepted for presentation in the
   * output, which is true iff it is a URI node, whose namespace is one of the
   * accepted namespaces in {@link #m_includeURI}.
   * 
   * @param r
   *          A resource to test
   * @return True if the resource is to be included in the generated output
   */
  protected boolean isIncluded(Resource r) {
    boolean accepted = false;

    if (!r.isAnon()) {
      String uri = r.getURI();
      for (Iterator<String> j = m_includeURI.iterator(); !accepted && j.hasNext();) {
        accepted = uri.startsWith(j.next());
      }
    }

    return accepted;
  }

  /**
   * Write the value declaration out using the given template, optionally
   * creating comments
   */
  protected void writeValue(Resource r, String template, String valueClass, String creator, String disambiguator) {
    if (!filter(r)) {
      if (!noComments() && hasComment(r)) {
        writeln(1, formatComment(getComment(r)));
      }

      // push the local bindings for the substitution onto the stack
      addReplacementPattern("valuri", r.getURI());
      addReplacementPattern("valname", getValueName(r, disambiguator));
      addReplacementPattern("valclass", valueClass);
      addReplacementPattern("valcreator", creator);

      // write out the value
      writeln(1, substitute(template));
      writeln(1);

      // pop the local replacements off the stack
      pop(4);
    }
  }

  /** Answer true if the given resource has an rdf:comment or daml:comment */
  protected boolean hasComment(Resource r) {
    return r.hasProperty(RDFS.comment) || r.hasProperty(DAML_OIL.comment);
  }

  /** Answer all of the commentary on the given resource, as a string */
  protected String getComment(Resource r) {
    StringBuffer comment = new StringBuffer();

    // collect any RDFS or DAML comments attached to the node
    for (NodeIterator ni = m_source.listObjectsOfProperty(r, RDFS.comment); ni.hasNext();) {
      RDFNode n = ni.nextNode();
      if (n instanceof Literal) {
        comment.append(((Literal) n).getLexicalForm().trim());
      } else {
        throw new RuntimeException("Not a literal: " + n);
      }
    }

    for (NodeIterator ni = m_source.listObjectsOfProperty(r, DAML_OIL.comment); ni.hasNext();) {
      comment.append(((Literal) ni.nextNode()).getLexicalForm().trim());
    }

    return comment.toString();
  }

  /** Format the comment as Javadoc, and limit the line width. */
  protected String formatComment(String comment) {
    StringBuffer buf = new StringBuffer();
    buf.append("/** <p>");

    boolean inSpace = false;
    int pos = buf.length();
    boolean singleLine = true;

    // now format the comment by compacting whitespace and limiting the line
    // length
    // add the prefix to the start of each line
    for (int i = 0; i < comment.length(); i++) {
      char c = comment.charAt(i);

      // compress whitespace
      if (Character.isWhitespace(c)) {
        if (inSpace) {
          continue; // more than one space is ignored
        } else {
          c = ' '; // map all whitespace to 0x20
          inSpace = true;
        }
      } else {
        inSpace = false;
      }

      // escapes?
      if (c == '\\') {
        c = comment.charAt(++i);

        switch (c) {
        case 'n':
          buf.append(m_nl);
          pos = indentTo(1, buf);
          buf.append(" *  ");
          pos += 3;
          singleLine = false;
          break;

        default:
          // add other escape sequences above
          break;
        }
      } else if (c == '<') {
        buf.append("&lt;");
        pos += 4;
      } else if (c == '>') {
        buf.append("&gt;");
        pos += 4;
      } else if (c == '&') {
        buf.append("&amp;");
        pos += 5;
      } else {
        // add the char
        buf.append(c);
        pos++;
      }

      // wrap any very long lines at 120 chars
      if ((pos > COMMENT_LENGTH_LIMIT) && (inSpace)) {
        buf.append(m_nl);
        pos = indentTo(1, buf);
        buf.append(" *  ");
        pos += 3;
        singleLine = false;
      }
    }

    buf.append("</p>");
    buf.append(singleLine ? "" : m_nl);
    indentTo(singleLine ? 0 : 1, buf);
    buf.append(" */");
    return buf.toString();
  }

  /** Answer true if resource r <b>does not</b> show in output */
  protected boolean filter(Resource r) {
    if (r.isAnon()) {
      return true;
    }

    // if we've already processed this resource once, ignore it next time
    if (m_resourcesToNames.containsKey(r)) {
      return true;
    }

    // search the allowed URI's
    for (Iterator<String> i = m_includeURI.iterator(); i.hasNext();) {
      String uri = i.next();
      if (r.getURI().startsWith(uri)) {
        // in
        return false;
      }
    }

    // we allow individuals whose class is not in the included NS's, unless opt
    // strict-individuals is true */
    if (!isTrue(OPT_STRICT_INDIVIDUALS)) {
      for (StmtIterator j = r.listProperties(RDF.type); j.hasNext();) {
        // we search the rdf:types of this resource
        Resource typeRes = j.nextStatement().getResource();

        if (!typeRes.isAnon()) {
          String typeURI = typeRes.getURI();

          // for any type that is in a permitted NS
          for (Iterator<String> i = m_includeURI.iterator(); i.hasNext();) {
            String uri = i.next();
            if (typeURI.startsWith(uri)) {
              // in
              return false;
            }
          }
        }
      }
    }

    // default is out
    return true;
  }

  /** Answer the Java value name for the URI */
  protected String getValueName(Resource r, String disambiguator) {
    // the id name is basically the local name of the resource, possibly in
    // upper case
    String name = isTrue(OPT_UC_NAMES) ? getUCValueName(r) : r.getLocalName();

    // must be legal java
    name = asLegalJavaID(name, false);

    // must not clash with an existing name
    int attempt = 0;
    String baseName = name;
    while (m_usedNames.contains(name)) {
      name = (attempt == 0) ? (name + disambiguator) : (baseName + disambiguator + attempt);
      attempt++;
    }

    // record this name so that we don't use it again (which will stop the
    // vocabulary from compiling)
    m_usedNames.add(name);

    // record the mapping from resource to name
    m_resourcesToNames.put(r, name);

    return name;
  }

  /** Answer the local name of resource r mapped to upper case */
  protected String getUCValueName(Resource r) {
    StringBuffer buf = new StringBuffer();
    String localName = r.getLocalName();
    char lastChar = 0;

    for (int i = 0; i < localName.length(); i++) {
      char c = localName.charAt(i);

      if (Character.isLowerCase(lastChar) && Character.isUpperCase(c)) {
        buf.append('_');
      }
      buf.append(Character.toUpperCase(c));
      lastChar = c;
    }

    return buf.toString();
  }

  /**
   * Return a URI formed from the given string, unchanged if it's already a URI
   * or converted to a file URI otherwise. If not recognisable as a URL, abort.
   */
  protected String urlCheck(String uriOrFile) {
    boolean legal = true;
    String url = uriOrFile;

    // is it a URI already? to check, we make a URL and see what happens!
    try {
      new URL(url);
    } catch (MalformedURLException ignore) {
      legal = false;
    }

    // if not a legal url, assume it's a file
    if (!legal) {
      legal = true;
      String slash = System.getProperty("file.separator");
      url = "file:" + (uriOrFile.startsWith(slash) ? (slash + slash) : "") + uriOrFile;

      try {
        new URL(url);
      } catch (MalformedURLException ignore) {
        legal = false;
      }
    }

    if (!legal) {
      abort("Could not parse " + uriOrFile + " as a legal URL or a file reference. Aborting.", null);
    }

    return url;
  }

  /**
   * Answer an iterator that contains the elements of the given list, but sorted
   * by URI
   */
  @SuppressWarnings("unchecked")
  protected ExtendedIterator sorted(ExtendedIterator i) {
    return sorted(i.toList());
  }

  /**
   * Answer an iterator that contains the elements of the given iterator, but
   * sorted by URI
   */
  protected ExtendedIterator<? extends RDFNode> sorted(List<? extends RDFNode> members) {
    Collections.sort(members, new Comparator<RDFNode>() {
      public int compare(RDFNode n0, RDFNode n1) {
        if (n0.isLiteral() || n1.isLiteral()) {
          if (n0.isLiteral() && n1.isLiteral()) {
            // two literals
            Literal l0 = (Literal) n0;
            Literal l1 = (Literal) n1;
            return l0.getLexicalForm().compareTo(l1.getLexicalForm());
          } else {
            return n0.isLiteral() ? -1 : 1;
          }
        } else {
          Resource r0 = (Resource) n0;
          Resource r1 = (Resource) n1;
          if (r0.isAnon() && r1.isAnon()) {
            // two anonID's - the order is important as long as its consistent
            return r0.getId().toString().compareTo(r1.getId().toString());
          } else if (r0.isAnon()) {
            return -1;
          } else if (r1.isAnon()) {
            return 1;
          } else {
            // two named resources
            return r0.getURI().compareTo(r1.getURI());
          }
        }
      }
    });

    return WrappedIterator.create(members.iterator());
  }

  // ==============================================================================
  // Inner class definitions
  // ==============================================================================

  /** An option that can be set either on the command line or in the RDF config */
  protected class OptionDefinition {
    protected String m_cmdLineForm;
    protected Property m_prop;

    protected OptionDefinition(String cmdLineForm, String name) {
      m_cmdLineForm = cmdLineForm;
      if (name != null) {
        m_prop = m_config.getProperty(NS, name);
      }
    }

    /**
     * Answer true if this option is set to true, either on the command line or
     * in the config model
     * 
     * @return boolean
     */
    protected boolean isTrue() {
      if (m_cmdLineArgs.contains(m_cmdLineForm)) {
        return true;
      }

      if (m_root.hasProperty(m_prop)) {
        return m_root.getRequiredProperty(m_prop).getBoolean();
      }

      return false;
    }

    /**
     * Answer the string value of the parameter if set, or null otherwise. Note
     * command line has precedence.
     * 
     * @return String
     */
    protected String getValue() {
      int index = m_cmdLineArgs.indexOf(m_cmdLineForm);

      if (index >= 0) {
        try {
          return m_cmdLineArgs.get(index + 1);
        } catch (IndexOutOfBoundsException e) {
          System.err.println("Value for parameter " + m_cmdLineForm + " not set! Aborting.");
        }
      }

      if (m_prop != null && m_root.hasProperty(m_prop)) {
        RDFNode val = m_root.getRequiredProperty(m_prop).getObject();
        if (val.isLiteral()) {
          return ((Literal) val).getLexicalForm();
        } else {
          return ((Resource) val).getURI().toString();
        }
      }

      // not set
      return null;
    }

    /**
     * Answer true if the parameter has a value at all.
     * 
     * @return boolean
     */
    protected boolean hasValue() {
      return getValue() != null;
    }

    /**
     * Answer the resource value of the parameter if set, or null otherwise.
     * 
     * @return String
     */
    protected Resource getResource() {
      int index = m_cmdLineArgs.indexOf(m_cmdLineForm);

      if (index >= 0) {
        try {
          return m_config.getResource(m_cmdLineArgs.get(index + 1));
        } catch (IndexOutOfBoundsException e) {
          System.err.println("Value for parameter " + m_cmdLineForm + " not set! Aborting.");
        }
      }

      if (m_prop != null && m_root.hasProperty(m_prop)) {
        return m_root.getRequiredProperty(m_prop).getResource();
      }

      // not set
      return null;
    }

    /**
     * Answer true if the parameter has a value at all.
     * 
     * @return boolean
     */
    protected boolean hasResourceValue() {
      return getResource() != null;
    }
  } // end inner class OptionDefinition

  /** A pairing of pattern and substitution we want to apply to output */
  protected class Replacement {
    protected String sub;
    protected Pattern pattern;

    protected Replacement(Pattern pattern, String sub) {
      this.sub = sub;
      this.pattern = pattern;
    }
  } // end inner class Replacement
}
