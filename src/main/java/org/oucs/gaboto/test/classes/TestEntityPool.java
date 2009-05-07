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
package org.oucs.gaboto.test.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oucs.gaboto.GabotoConfiguration;
import org.oucs.gaboto.GabotoLibrary;
import org.oucs.gaboto.beans.Location;
import org.oucs.gaboto.entities.Building;
import org.oucs.gaboto.entities.College;
import org.oucs.gaboto.entities.GabotoEntity;
import org.oucs.gaboto.entities.pool.GabotoEntityPool;
import org.oucs.gaboto.entities.pool.GabotoEntityPoolConfiguration;
import org.oucs.gaboto.entities.pool.filters.EntityFilter;
import org.oucs.gaboto.entities.pool.filters.PropertyEqualsFilter;
import org.oucs.gaboto.entities.pool.filters.PropertyExistsFilter;
import org.oucs.gaboto.exceptions.EntityPoolInvalidConfigurationException;
import org.oucs.gaboto.exceptions.GabotoException;
import org.oucs.gaboto.exceptions.ResourceDoesNotExistException;
import org.oucs.gaboto.model.Gaboto;
import org.oucs.gaboto.model.GabotoFactory;
import org.oucs.gaboto.model.GabotoSnapshot;
import org.oucs.gaboto.timedim.TimeInstant;
import org.oucs.gaboto.vocabulary.OxPointsVocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;

public class TestEntityPool {

	@BeforeClass
	public static void setUp() throws Exception {
		GabotoLibrary.init(GabotoConfiguration.fromConfigFile());
	}
	
	@Test
	public void testEntityPoolCreation() throws GabotoException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		
		GabotoSnapshot snap = op.getSnapshot(new TimeInstant(2000,0,0));
		
		Model m = snap.getModel();
		
		// count buildings
		int nrOfBuildings = 0;
		ResIterator it = m.listResourcesWithProperty(RDF.type, OxPointsVocab.Building);
		while(it.hasNext()){
			nrOfBuildings++;
			it.next();
		}
		
		// count colleges
		int nrOfColleges = 0;
		it = m.listResourcesWithProperty(RDF.type, OxPointsVocab.College);
		while(it.hasNext()){
			nrOfColleges++;
			it.next();
		}

		// create entitypool
		GabotoEntityPool pool = GabotoEntityPool.createFrom(new GabotoEntityPoolConfiguration(snap));
		
		// go through buildings & colleges in pool
		assertEquals(nrOfBuildings, pool.getEntities(new Building()).size());
		assertEquals(nrOfColleges, pool.getEntities(new College()).size());
	}
	
	@Test
	public void testModelSizeEquality() throws GabotoException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		
		// as long as there is no data for the future in the system, this should hold
		Model m = op.getSnapshot(TimeInstant.now()).getModel();
		GabotoEntityPool pool =  GabotoEntityPool.createFrom(new GabotoEntityPoolConfiguration(op,m));
		Model m2 = pool.createJenaModel();
		
		
		StmtIterator it = m.listStatements();
		while(it.hasNext()){
			Statement stmt = it.nextStatement();
			if(! m2.contains(stmt))
				System.out.println("stmt not in m2: " + stmt);
		}
		
		//assertEquals(m.size(),m2.size());
		assertTrue(Math.abs(m.size()-m2.size()) < 10);
	}
	
	@Test
	public void testEntityAddReferencedEntities() throws GabotoException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		
		GabotoSnapshot snap = op.getSnapshot(new TimeInstant(2000,0,0));
		
		GabotoEntityPoolConfiguration config = new GabotoEntityPoolConfiguration(snap);
		config.setAddReferencedEntitiesToPool(false);
		config.addAcceptedType(OxPointsVocab.College_URI);
		
		GabotoEntityPool pool = GabotoEntityPool.createFrom(config);
		
		for(GabotoEntity e : pool.getEntities())
			assertTrue(e instanceof College);
		
		config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.Building_URI);
		config.setAddReferencedEntitiesToPool(false);
		pool = GabotoEntityPool.createFrom(config);
		
		for(GabotoEntity e : pool.getEntities())
			assertTrue(e instanceof Building);
		
		
		config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.College_URI);
		config.setAddReferencedEntitiesToPool(true);
		
		pool = GabotoEntityPool.createFrom(config);
		
		// access some properties
		for(College c : pool.getEntities(new College())){
			c.getPrimaryPlace();
		}
		
		boolean foundCollage = false;
		boolean foundBuilding = false;
		for(GabotoEntity e : pool.getEntities()){
			if(e instanceof Building)
				foundBuilding = true;
			if(e instanceof College)
				foundCollage = true;
		}
		
		assertTrue(foundCollage);
		assertTrue(foundBuilding);
	}
	
	@Test
	public void testEntityFilter() throws GabotoException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		GabotoSnapshot snap = op.getSnapshot(new TimeInstant(2000,0,0));
		
		GabotoEntityPoolConfiguration config = new GabotoEntityPoolConfiguration(snap);
		config.addEntityFilter(new EntityFilter(){

			@Override
			public Class<? extends GabotoEntity> appliesTo(){
				return College.class;
			}
			
			@Override
			public boolean filterEntity(GabotoEntity entity) {
				return false;
			}
		});
		
		GabotoEntityPool pool = GabotoEntityPool.createFrom(config);
		
		assertTrue(pool.getEntities(new College()).isEmpty());
		
		config = new GabotoEntityPoolConfiguration(snap);
		config.addEntityFilter(new EntityFilter(){

			@Override
			public boolean filterEntity(GabotoEntity entity) {
				if(!(entity instanceof College))
					return false;
				return true;
			}
		});
		
		pool = GabotoEntityPool.createFrom(config);
		
		for(GabotoEntity e : pool.getEntities())
			assertTrue(e instanceof College);
	}
	
	
	@Test
	public void testEntityFilter2() throws GabotoException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		GabotoSnapshot snap = op.getSnapshot(TimeInstant.now());
		
		GabotoEntityPoolConfiguration config = new GabotoEntityPoolConfiguration(snap);
		config.addEntityFilter(new EntityFilter(){

			@Override
			public boolean filterEntity(GabotoEntity entity) {
				if(!(entity instanceof College))
					return false;
				return true;
			}
		});
		
		config.addEntityFilter(new EntityFilter(){
			
			@Override
			public Class<? extends GabotoEntity> appliesTo(){
				return College.class;
			}
			
			@Override
			public boolean filterEntity(GabotoEntity entity) {
				College col = (College) entity;
		    	
		    	// reject if no primary place is set
		    	if(null == col.getPrimaryPlace())
		    		return false;
		    	
		    	// load location
		    	Location loc = col.getPrimaryPlace().getLocation();
		    	
		    	// reject if no location is set
		    	if(null == loc)
		    		return false;
		    	
				double lat_oucs = 51.760010;
				double long_oucs = -1.260350;
	
				double lat_diff = Double.valueOf(loc.getPos().split(" ")[0]) - lat_oucs;
				double long_diff = Double.valueOf(loc.getPos().split(" ")[1]) - long_oucs;
				double distance = Math.sqrt(lat_diff*lat_diff + long_diff*long_diff);

		    	// if distance is small enough, allow entity to pass.
		    	if(distance < 0.002)
		    		return true;
		    	
		    	// reject by default
		    	return false;
  
			}
		});
		
		GabotoEntityPool.createFrom(config);
	}
	
	@Test
	public void testEntityTypeFilter() throws EntityPoolInvalidConfigurationException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		GabotoSnapshot snap = op.getSnapshot(TimeInstant.now());
		
		GabotoEntityPoolConfiguration config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.College_URI);
		GabotoEntityPool pool = GabotoEntityPool.createFrom(config);
		for(GabotoEntity e : pool.getEntities())
			assertEquals(OxPointsVocab.College_URI, e.getType());

		config = new GabotoEntityPoolConfiguration(snap);
		config.addUnacceptedType(OxPointsVocab.College_URI);
		pool = GabotoEntityPool.createFrom(config);
		
		boolean bCollege = false;
		boolean bBuilding = false;
		for(GabotoEntity e : pool.getEntities()){
			if(OxPointsVocab.College_URI.equals(e.getType()))
				bCollege = true;
			if(OxPointsVocab.Building_URI.equals(e.getType()))
				bBuilding = true;
		}
		
		assertTrue(bBuilding);
		assertTrue(!bCollege);
	}
	
	@Test
	public void testResourceFilter() throws EntityPoolInvalidConfigurationException, ResourceDoesNotExistException{
		Gaboto op = GabotoFactory.getInMemoryGaboto();
		GabotoSnapshot snap = op.getSnapshot(TimeInstant.now());
		
		GabotoEntityPoolConfiguration config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.College_URI);
		config.addResourceFilter(new PropertyExistsFilter(DC_11.title));
		GabotoEntityPool pool = GabotoEntityPool.createFrom(config);
		assertTrue(pool.getSize() > 0);
		
		config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.College_URI);
		config.addResourceFilter(new PropertyEqualsFilter(DC_11.title, "Somerville College"));
		pool = GabotoEntityPool.createFrom(config);
		assertTrue(pool.getSize() == 1);
		
		Resource col = snap.getResource(pool.getEntities().toArray(new GabotoEntity[1])[0].getUri());
		config = new GabotoEntityPoolConfiguration(snap);
		config.addAcceptedType(OxPointsVocab.College_URI);
		config.addResource(col);
		config.addResourceFilter(new PropertyEqualsFilter(DC_11.title, "Somerville College"));
		pool = GabotoEntityPool.createFrom(config);
		assertTrue(pool.getSize() == 1);

		
	}
}