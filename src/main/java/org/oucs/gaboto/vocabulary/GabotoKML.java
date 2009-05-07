/* CVS $Id: $ */
package org.oucs.gaboto.vocabulary; 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
 
/**
 * Vocabulary definitions from ontologies/gabotoKML.owl 
 * @author Auto-generated by schemagen on 11 Mar 2009 08:56 
 */
public class GabotoKML {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://ns.ox.ac.uk/namespace/gaboto/kml/2009/03/owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>An entity's parent</p> */
    
    public static final String parent_URI = "http://ns.ox.ac.uk/namespace/gaboto/kml/2009/03/owl#parent";
    public static final ObjectProperty parent = m_model.createObjectProperty( "http://ns.ox.ac.uk/namespace/gaboto/kml/2009/03/owl#parent" );
        
    
}