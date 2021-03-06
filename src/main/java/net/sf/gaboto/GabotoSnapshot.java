/**
 * Copyright 2009 University of Oxford
 *
 * Written by Arno Mittelbach for the Erewhon Project
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
package net.sf.gaboto;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.gaboto.node.GabotoEntity;
import net.sf.gaboto.node.pool.EntityPool;
import net.sf.gaboto.node.pool.EntityPoolConfiguration;
import net.sf.gaboto.time.TimeSpan;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Snapshots represent a flat extract from the data in Gaboto.
 * 
 * <p>
 * Gaboto works with a data model built on RDF Named Graphs. However, working with named
 * graphs is rather difficult and tool support is limited. Furthermore, the performance of
 * queries against named graphs is usually much less than acceptable. 
 * </p>
 * 
 * <p>
 * {@link GabotoSnapshot}s provide one solution to this problem. They allow you to represent
 * the part of the data you are interested in in one flat RDF graph that you can then easily
 * query against. Furthermore, snapshots can be used to automatically create the java representation of
 * {@link GabotoEntity}s using a {@link EntityPool}.
 * </p>
 * 
 * @author Arno Mittelbach
 * @version 0.1
 */
public class GabotoSnapshot {

	private Model model;
	
	private Gaboto gaboto;

	/**
	 * Creates a new snapshot using the Jena Model and Gaboto system.
	 * 
	 * @param model The Jena Model.
	 * @param gaboto The Gaboto system.
	 */
	public GabotoSnapshot(Model model, Gaboto gaboto) {
		 this.model = model;
		 this.gaboto = gaboto;
	}
	
	/**
	 * Returns the Gaboto model this snapshot is built upon.
	 * 
	 * @return The Gaboto model this snapshot is built upon.
	 */
	public Gaboto getGaboto(){
		return gaboto;
	}
	
	/**
	 * Returns the Jena model this snapshot is built upon.
	 * 
	 * @return The Jena model this snapshot is built upon.
	 */
	public Model getModel(){
		return model;
	}
	
	/**
	 * Returns the number of triples in the underlying model.
	 * 
	 * @return The number of triples in the underlying model.
	 */
	public long size(){
		return model.size();
	}

	/**
	 * Extracts all resources of a given type from the snapshot.
	 * 
	 * @param type The ontology type.
	 * 
	 * @return A collection of resources of the same type.
	 */
	public Collection<Resource> getResourcesOfType(OntClass type) {
		Set<Resource> resources = new HashSet<Resource>();

		ResIterator it = model.listResourcesWithProperty(RDF.type, type);
		while(it.hasNext())
			resources.add(it.nextResource());
		
		return resources;
	}
	
	/**
	 * Extracts additional time information for an entity (represented by its RDF Resource).
	 * 
	 * @param res The Resource.
	 * 
	 * @return The time span or null.
	 */
	public TimeSpan getTimeSpanForEntity(Resource res){
		return getTimeSpanForEntity(res.getURI());
	}
	
	/**
	 * Extracts additional time information for an entity (represented by its URI).
	 * 
	 * @param uri The entity's URI.
	 * 
	 * @return The time span or null.
	 */
	public TimeSpan getTimeSpanForEntity(String uri){
		try {
			return gaboto.getEntitysLifetime(uri);
		} catch (EntityDoesNotExistException e) {
	    return null;
		}
	}

	/**
	 * Extracts a Property object for a given URI.
	 * 
	 * @param propertyURI The property's URI
	 * 
	 * @return The Property.
	 */
	public Property getProperty(String propertyURI) {
		return model.getProperty(propertyURI);
	}

	/**
	 * Returns an RDF Resource.
	 * 
	 * @param uri The resource's URI
	 * 
	 * @return The RDF Resource
	 */
	public Resource getResource(String uri) throws ResourceDoesNotExistException {
		if(! containsResource(uri))
			throw new ResourceDoesNotExistException(uri);
		
		return model.getResource(uri);
	}

	/**
	 * Tests if a resource with the specified URI exists.
	 * 
	 * @param uri The URI identifying the resource.
	 * @return True, if resource exists.
	 */
	public boolean containsResource(String uri) {
		return model.getGraph().contains(Node.createURI(uri), Node.ANY, Node.ANY);
	}

	/**
	 * Tests if a resource exists.
	 * 
	 * @param res The resource.
	 * @return True, if resource exists.
	 */
	public boolean containsResource(Resource res) {
		return containsResource(res.getURI());
	}
	
	public EntityPool loadEntitiesWithProperty(String propURI){
		return loadEntitiesWithProperty(getProperty(propURI));
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}
	
	
	
	public EntityPool loadEntitiesWithProperty(String propURI, boolean value){
		Property prop = getProperty(propURI);
    if(prop == null)
			throw new GabotoRuntimeException("Property not found"); 
		
		return loadEntitiesWithProperty(getProperty(propURI), value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, boolean value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}
	
	
	public EntityPool loadEntitiesWithProperty(String propURI, char value){
		Property prop = getProperty(propURI);
    if(prop == null)
			return new EntityPool(this.gaboto, this);
		
		return loadEntitiesWithProperty(getProperty(propURI), value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, char value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}
	
	public EntityPool loadEntitiesWithProperty(String propURI, double value){
		Property prop = getProperty(propURI);
    if(prop == null)
			return new EntityPool(this.gaboto, this);
		
		return loadEntitiesWithProperty(getProperty(propURI), value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, double value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}	
	
	public EntityPool loadEntitiesWithProperty(String propURI, float value){
		Property prop = getProperty(propURI);
    if(prop == null)
			return new EntityPool(this.gaboto, this);
		
		return loadEntitiesWithProperty(getProperty(propURI), value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, float value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}	
	
	public EntityPool loadEntitiesWithProperty(String propURI, long value){
		Property prop = getProperty(propURI);
    if(prop == null)
			return new EntityPool(this.gaboto, this);
		
		return loadEntitiesWithProperty(getProperty(propURI), value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, long value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}	
	
	public EntityPool loadEntitiesWithProperty(String propURI, String value){
		Property prop = getProperty(propURI);
    if (prop == null)
      throw new GabotoRuntimeException("Property not found: " + propURI);
		return loadEntitiesWithProperty(prop, value);
	}
	
	public EntityPool loadEntitiesWithProperty(String propURI, Object value){
		Property prop = getProperty(propURI);
    if (prop == null)
      throw new GabotoRuntimeException("Property not found: " + propURI);
		return loadEntitiesWithProperty(prop, value);
	}
	
	
	public EntityPool loadEntitiesWithProperty(Property prop, String value){
   return loadEntitiesWithProperty(prop, (Object)value);
	}
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, Object value){
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext())
			resources.add(it.nextResource());

		return loadEntityPoolFromResources(resources);
	}	
	
	/**
	 * Loads entities that have a certain property.
	 * 
	 * @param prop The property
	 * @param value The property's value
	 * @return An entity pool with all entities that have this property.
	 */
	public EntityPool loadEntitiesWithProperty(Property prop, RDFNode value){
    System.err.println("Value " + value);
		Collection<Resource> resources = new HashSet<Resource>();
		ResIterator it = model.listResourcesWithProperty(prop, value);
		while(it.hasNext()) {
		  Resource r = it.nextResource();
      System.err.println("Adding " + r);
			resources.add(r);
		}
		return loadEntityPoolFromResources(resources);
	}	
	
	
	/**
	 * Creates an entity pool from some resources out of this snapshot
	 * 
	 * @param resources The collection of resources
	 * @return The entity pool
	 */
	private EntityPool loadEntityPoolFromResources(Collection<Resource> resources) {
		EntityPoolConfiguration poolConfig = new EntityPoolConfiguration(this);
		poolConfig.setResources(resources);
		poolConfig.setAddReferencedEntitiesToPool(false);
		
    return EntityPool.createFrom(poolConfig);
	}
	
	/**
	 * Loads a specific entity.
	 * 
	 * @param uri The URI of the entity that is to be created.
	 * 
	 * @return The entity.
	 * 
	 */
	public GabotoEntity loadEntity(String uri) {
		Collection<Resource> resCol = new HashSet<Resource>();
		
		Resource res;
    try {
      res = this.getResource(uri);
    } catch (ResourceDoesNotExistException e) {
      throw new EntityDoesNotExistException(uri);
    }
		resCol.add(res);
		
		// create config
		EntityPoolConfiguration config = new EntityPoolConfiguration(this);
		config.setResources(resCol);

		EntityPool pool = EntityPool.createFrom(config);
		
		return pool.getEntity(uri);
	}

	/**
	 * Executes a Construct SPARQL Query.
	 * 
	 * @param query The query to execute.
	 * 
	 * @return The resulting snapshot.
	 */
	public GabotoSnapshot execSPARQLConstruct(String query){
		QueryExecution qexec = QueryExecutionFactory.create( query, getModel() );
		Model m = null;
		try{
			 m = qexec.execConstruct();
		} finally { qexec.close(); }
		
		return new GabotoSnapshot(m, gaboto);
	}
	
	/**
	 * Executes a Describe SPARQL Query.
	 * 
	 * @param query The query to execute.
	 * 
	 * @return The resulting snapshot.
	 */
	public GabotoSnapshot execSPARQLDescribe(String query){
		QueryExecution qexec = QueryExecutionFactory.create( query, getModel() );
		Model m = null;
		try{
			 m = qexec.execDescribe();
		} finally { qexec.close(); }
		
		return new GabotoSnapshot(m, gaboto);
	}
	
	/**
	 * Executes an Ask SPARQL Query.
	 * 
	 * @param query The query to execute.
	 * 
	 * @return The resulting snapshot.
	 */
	public boolean execSPARQLAsk(String query){
		QueryExecution qexec = QueryExecutionFactory.create( query, getModel() );
		boolean result = false;
		try{
			 result = qexec.execAsk();
		} finally { qexec.close(); }
		return result;
	}
	
	/**
	 * Executes a Select SPARQL Query.
	 * 
	 * @param query The query to execute.
	 */
	public void execSPARQLSelect(String query, SPARQLQuerySolutionProcessor processor){
		QueryExecution qexec = QueryExecutionFactory.create( query, getModel() );
		
		try{
			// execute query
			ResultSet results = qexec.execSelect();
		
			// iterate over results
			while(results.hasNext()){
				QuerySolution soln = results.nextSolution();
				
				// process solution
				processor.processSolution(soln);
				
				// continue ?
				if(processor.stopProcessing())
					break;
			}
		} finally { qexec.close(); }
	}
	
	/**
	 * Creates an {@link EntityPool} with a standard configuration from this snapshot.
	 * 
	 * @return An GabotoEntityPool build from this snapshot.
	 */
	public EntityPool buildEntityPool() {
    return EntityPool.createFrom(new EntityPoolConfiguration(this));
	}
	
	/**
	 * Serialise the model.
	 * 
	 * @param os
	 */
	public void write(OutputStream os){
		model.write(os);
	}
	
	/**
	 * Serialise the model.
	 * 
	 * @param os
	 * @param format
	 */
	public void write(OutputStream os, String format){
		model.write(os, format);
	}
}
