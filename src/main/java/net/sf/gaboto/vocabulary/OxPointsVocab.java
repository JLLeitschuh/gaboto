/* $Id: $ */
package net.sf.gaboto.vocabulary; 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
 
/**
 * Vocabulary definitions from ontologies/OxPoints.owl. 
 * 
 * @author Auto-generated by net.sf.gaboto.generation.VocabularyGenerator 
 */
public class OxPointsVocab {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    public static OntModel MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = MODEL.createResource( NS );
    
    /** @see net.sf.gaboto.generation.VocabularyGenerator#writeObjectProperties() */
    /** <p>Defines that two entities are associated with one another (N:M)</p> */
    public static final 
    String associatedWith_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#associatedWith";
    public static final 
    ObjectProperty associatedWith = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#associatedWith" );
    
    /** <p>The number of cars that can park</p> */
    public static final 
    String capacity_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#capacity";
    public static final 
    ObjectProperty capacity = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#capacity" );
    
    /** <p>Describes that an entity has a homepage for its it people</p> */
    public static final 
    String hasITHomepage_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasITHomepage";
    public static final 
    ObjectProperty hasITHomepage = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasITHomepage" );
    
    /** <p>Library Homepage</p> */
    public static final 
    String hasLibraryHomepage_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLibraryHomepage";
    public static final 
    ObjectProperty hasLibraryHomepage = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLibraryHomepage" );
    
    /** <p>Describes that an entity has a specific location</p> */
    public static final 
    String hasLocation_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLocation";
    public static final 
    ObjectProperty hasLocation = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLocation" );
    
    /** <p>The naming scheme for entities introduced by Estates</p> */
    public static final 
    String hasOBNCode_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOBNCode";
    public static final 
    ObjectProperty hasOBNCode = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOBNCode" );
    
    /** <p>The naming scheme for entities introduced by OLIS</p> */
    public static final 
    String hasOLISCode_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOLISCode";
    public static final 
    ObjectProperty hasOLISCode = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOLISCode" );
    
    /** <p>Links to OpenStreetMap entities</p> */
    public static final 
    String hasOSMIdentifier_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOSMIdentifier";
    public static final 
    ObjectProperty hasOSMIdentifier = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOSMIdentifier" );
    
    /** <p>The naming scheme for entities introduced by OUCS</p> */
    public static final 
    String hasOUCSCode_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOUCSCode";
    public static final 
    ObjectProperty hasOUCSCode = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOUCSCode" );
    
    /** <p>Defines the primary place for an entity</p> */
    public static final 
    String hasPrimaryPlace_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasPrimaryPlace";
    public static final 
    ObjectProperty hasPrimaryPlace = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasPrimaryPlace" );
    
    /** <p>Describes that an entity has a weblearn</p> */
    public static final 
    String hasWeblearn_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasWeblearn";
    public static final 
    ObjectProperty hasWeblearn = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasWeblearn" );
    
    /** <p>Describes that an entity has a homepage</p> */
    public static final 
    String homepage_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#homepage";
    public static final 
    ObjectProperty homepage = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#homepage" );
    
    /** <p>The Place is depicted within the Image</p> */
    public static final 
    String inImage_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#inImage";
    public static final 
    ObjectProperty inImage = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#inImage" );
    
    /** <p>Describes that a unit occupies a place</p> */
    public static final 
    String occupies_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#occupies";
    public static final 
    ObjectProperty occupies = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#occupies" );
    
    /** <p>Describes that a place is physically located within another place</p> */
    public static final 
    String physicallyContainedWithin_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#physicallyContainedWithin";
    public static final 
    ObjectProperty physicallyContainedWithin = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#physicallyContainedWithin" );
    
    /** <p>The type of library sublocation (e.g. book-stack, reading-room)</p> */
    public static final 
    String sublocationType_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#sublocationType";
    public static final 
    ObjectProperty sublocationType = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#sublocationType" );
    
    /** <p>Defines that an entity is a subset of another entity (1:N)</p> */
    public static final 
    String subsetOf_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#subsetOf";
    public static final 
    ObjectProperty subsetOf = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#subsetOf" );
    
    /** <p>The functions for which a room is used.</p> */
    public static final 
    String usedFor_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#usedFor";
    public static final 
    ObjectProperty usedFor = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#usedFor" );
    
    public static final 
    String uses_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#uses";
    public static final 
    ObjectProperty uses = MODEL.createObjectProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#uses" );
    
    
    /** @see net.sf.gaboto.generation.VocabularyGenerator#writeDatatypeProperties() */ 
    /** <p>Describe the height of an image</p> */
    public static final 
    String height_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#height";
    public static final 
    DatatypeProperty height = MODEL.createDatatypeProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#height" );
    
    /** <p>Describe the width of an image</p> */
    public static final 
    String width_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#width";
    public static final 
    DatatypeProperty width = MODEL.createDatatypeProperty( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#width" );
    
    
    /** @see net.sf.gaboto.generation.VocabularyGenerator#writeAnnotationProperties() */
    
    /** @see net.sf.gaboto.generation.VocabularyGenerator#writeOntClasses() */
    public static final 
    String Building_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Building";
    public static final 
    OntClass Building = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Building" );
    
    public static final 
    String Carpark_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Carpark";
    public static final 
    OntClass Carpark = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Carpark" );
    
    public static final 
    String College_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#College";
    public static final 
    OntClass College = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#College" );
    
    public static final 
    String Department_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Department";
    public static final 
    OntClass Department = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Department" );
    
    public static final 
    String Division_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Division";
    public static final 
    OntClass Division = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Division" );
    
    public static final 
    String DrainCover_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#DrainCover";
    public static final 
    OntClass DrainCover = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#DrainCover" );
    
    public static final 
    String Entrance_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Entrance";
    public static final 
    OntClass Entrance = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Entrance" );
    
    public static final 
    String Faculty_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Faculty";
    public static final 
    OntClass Faculty = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Faculty" );
    
    public static final 
    String Group_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Group";
    public static final 
    OntClass Group = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Group" );
    
    public static final 
    String Image_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Image";
    public static final 
    OntClass Image = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Image" );
    
    /** <p>Describes any kind of library.</p> */
    public static final 
    String Library_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Library";
    public static final 
    OntClass Library = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Library" );
    
    /** <p>Describes any kind of museum.</p> */
    public static final 
    String Museum_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Museum";
    public static final 
    OntClass Museum = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Museum" );
    
    public static final 
    String OxpEntity_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#OxpEntity";
    public static final 
    OntClass OxpEntity = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#OxpEntity" );
    
    public static final 
    String Place_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Place";
    public static final 
    OntClass Place = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Place" );
    
    public static final 
    String Room_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Room";
    public static final 
    OntClass Room = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Room" );
    
    public static final 
    String ServiceDepartment_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#ServiceDepartment";
    public static final 
    OntClass ServiceDepartment = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#ServiceDepartment" );
    
    public static final 
    String Site_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Site";
    public static final 
    OntClass Site = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Site" );
    
    /** <p>Describes a library with a parent.</p> */
    public static final 
    String SubLibrary_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#SubLibrary";
    public static final 
    OntClass SubLibrary = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#SubLibrary" );
    
    public static final 
    String Unit_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Unit";
    public static final 
    OntClass Unit = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Unit" );
    
    public static final 
    String WAP_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#WAP";
    public static final 
    OntClass WAP = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#WAP" );
    
    public static final 
    String Website_URI = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Website";
    public static final 
    OntClass Website = MODEL.createClass( "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Website" );
    
    
    /** @see net.sf.gaboto.generation.VocabularyGenerator#writeOntIndividuals() */
    
}
