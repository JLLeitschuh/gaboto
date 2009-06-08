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
package net.sf.gaboto.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oucs.gaboto.GabotoConfiguration;
import org.oucs.gaboto.GabotoLibrary;
import org.oucs.gaboto.entities.Building;
import org.oucs.gaboto.entities.College;
import org.oucs.gaboto.entities.GabotoEntity;
import org.oucs.gaboto.entities.pool.GabotoEntityPool;
import org.oucs.gaboto.entities.time.GabotoTimeBasedEntity;
import org.oucs.gaboto.exceptions.EntityAlreadyExistsException;
import org.oucs.gaboto.exceptions.GabotoException;
import org.oucs.gaboto.exceptions.ResourceDoesNotExistException;
import org.oucs.gaboto.model.Gaboto;
import org.oucs.gaboto.model.GabotoFactory;
import org.oucs.gaboto.model.GabotoSnapshot;
import org.oucs.gaboto.model.query.GabotoQuery;
import org.oucs.gaboto.model.query.defined.ListOfTypedEntities;
import org.oucs.gaboto.timedim.TimeInstant;
import org.oucs.gaboto.timedim.TimeSpan;
import org.oucs.gaboto.vocabulary.DC;
import org.oucs.gaboto.vocabulary.OxPointsVocab;

import com.hp.hpl.jena.graph.Node;

@SuppressWarnings("boxing")
public class TestGabotoTimeBasedEntity {

	@BeforeClass
	public static void setUp() throws Exception {
		GabotoLibrary.init(GabotoConfiguration.fromConfigFile());
	}
	
  @Test (expected=IllegalArgumentException.class)
	public void testGetEntityException(){
		GabotoTimeBasedEntity entity = new GabotoTimeBasedEntity(Building.class, 
		    Utils.generateRandomURI(), new TimeSpan(100,10,2,10,10,10));
		entity.getEntity(new TimeInstant(500,0,0));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddPropertyException1() throws Exception{
		GabotoTimeBasedEntity entity = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), new TimeSpan(100,10,2,10,10,10));
		entity.addProperty(new TimeSpan(600,0,0), "abc", "lila");
	}

	@Test (expected=IllegalArgumentException.class)
	public void testAddPropertyException2() throws Exception{
		GabotoTimeBasedEntity entity = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), new TimeSpan(100,10,2,10,10,10));
		entity.addProperty(new TimeSpan(600,0,0), DC.title, "lila");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddPropertyException3() throws Exception { 
		GabotoTimeBasedEntity entity = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), new TimeSpan(100,10,2,10,10,10));
		entity.addProperty(new TimeSpan(100,11,2), "abc", "lila");
	}

	@Test (expected=IllegalArgumentException.class)
	public void testAddPropertyException4() throws Exception { 
		GabotoTimeBasedEntity entity = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), new TimeSpan(100,10,2,10,10,10));
		entity.addProperty("abc", "lila");
	}
	
	@Test
	public void testCreateTimeBasedEntity(){
		TimeSpan ts = new TimeSpan(500,0,0,500,10,10);
		GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), ts);
		
		String name1 = "This is a nice building";
		String name2 = "but not from 700-900"; 
		
		// set different names
		entityTB.addProperty(DC.title, name1);
		entityTB.addProperty(new TimeSpan(700,0,0,200,0,0), DC.title, name2);

		Building entityEarly = (Building) entityTB.getEntity(new TimeInstant(600,null, null));
		Building entityMiddle = (Building)  entityTB.getEntity(new TimeInstant(800,null, null));
		Building entityLate = (Building) entityTB.getEntity(new TimeInstant(950,null, null));

		assertEquals(name1, entityEarly.getName());
		assertEquals(name2, entityMiddle.getName());
		assertEquals(name1, entityLate.getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateTimeBasedEntity2(){
		TimeSpan ts = new TimeSpan(500,0,0,500,10,10);
		GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(), ts);
		entityTB.getEntity(new TimeInstant(1100,null, null));
	}
	
	@Test
	public void testIterator(){
		for(int j = 0; j < 200; j++){
			GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI());
			String name1 = "This is a nice building";
			if(j % 100 == 0)
				System.out.print(".");
			// set different names
			entityTB.addProperty(DC.title, name1);
			
			for(int i = 0; i < 100; i++){
				TimeSpan ts = Utils.getRandomTimespan(0.9,0.9,0.9,0.9,0.9,0.9);
				entityTB.addProperty(ts, DC.title, Utils.generateRandomURI());
			}
			
			Iterator<GabotoEntity> it = entityTB.iterator();
			GabotoEntity entity = it.next();
			while(it.hasNext()){
				GabotoEntity next = it.next();
				assertTrue(entity.getTimeSpan().getEnd().compareTo(next.getTimeSpan().getBegin()) <= 0);
				entity = next;
			}
		}
		System.out.println();
	}
	
	//@Test
	public void BorkedtestLoadingEntitiesSimple() throws GabotoException{
		Gaboto oxp = GabotoFactory.getInMemoryGaboto();
		
		TimeInstant now = TimeInstant.now();
		
		GabotoQuery query = new ListOfTypedEntities(OxPointsVocab.College_URI, now );
		GabotoEntityPool pool = (GabotoEntityPool) query.execute(GabotoQuery.FORMAT_ENTITY_POOL);
		
		for(College c : pool.getEntities(new College())){
			GabotoTimeBasedEntity entityTB = GabotoTimeBasedEntity.loadEntity(c.getUri(), oxp);
			Iterator<GabotoEntity> it = entityTB.iterator();
			while(it.hasNext()){
				GabotoEntity entity = it.next();
				if(entity.getTimeSpan().contains(now)){
					assertEquals(c.getName(), ((College)entity).getName());
				}
			}
		}
	}
	
	@Test
	public void testIterator2() throws Exception{
		
		TimeSpan ts = new TimeSpan(0,4, 2, 1637,9,28);
		GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),ts);

		String name1 = Utils.generateRandomURI();
		entityTB.addProperty(DC.title, name1);
		Iterator<GabotoEntity> it = entityTB.iterator();
		while (it.hasNext()) {
			GabotoEntity entity = it.next();
			assertEquals(entity.getTimeSpan(), entityTB.getTimeSpan());
		}
	}
	
	@Test
	public void testIterator3() throws Exception{
		for(int i = 0; i < 10000; i++){
			GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),Utils.getRandomTimespan());
	
			String name1 = Utils.generateRandomURI();
			entityTB.addProperty(DC.title, name1);
			Iterator<GabotoEntity> it = entityTB.iterator();
			while (it.hasNext()) {
				GabotoEntity entity = it.next();
				assertEquals(entity.getTimeSpan(), entityTB.getTimeSpan());
			}
		}
	}
	
	
	//@Test
	public void tooSlowtestAddEntitySimple() throws GabotoException{
		Gaboto oxp = GabotoFactory.getPersistentGaboto();
		Gaboto oxp_mem = GabotoFactory.getInMemoryGaboto();
		
		for(int i = 0; i < 1000; i++){
			GabotoTimeBasedEntity timeBasedEntity = new GabotoTimeBasedEntity(Building.class, 
			    Utils.generateRandomURI(), Utils.getRandomTimespan());
	
			String name = Utils.generateRandomURI();
			timeBasedEntity.addProperty(DC.title, name);
			oxp.add(timeBasedEntity);
			
			// create snapshot
			GabotoSnapshot snaphot = oxp_mem.getSnapshot(oxp.getGraph(timeBasedEntity.getTimeSpan()));
			Building building = (Building) snaphot.loadEntity(timeBasedEntity.getUri());
			
      assertEquals("Iteration " + 1, building.getName(), name);
			assertEquals("Iteration " + 1, building.getUri(), timeBasedEntity.getUri());
			assertEquals("Iteration " + 1, building.getType(), timeBasedEntity.getType());
			// FIXME Failing
      //assertNotEqual("Iteration " + 1, building.getTimeSpan(), timeBasedEntity.getTimeSpan());
		}
	}
	
	@Test
	public void testAddEntityOverflow() throws EntityAlreadyExistsException, ResourceDoesNotExistException{
		Gaboto oxp = GabotoFactory.getPersistentGaboto();
		Gaboto oxp_mem = GabotoFactory.getInMemoryGaboto();
		
		TimeSpan ts = new TimeSpan(166,2,26,679,11,28);
		GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),ts);

		String name1 = Utils.generateRandomURI();
		entityTB.addProperty(DC.title, name1);
		oxp.add(entityTB);
		
		// create snapshot
		GabotoSnapshot snap = oxp_mem.getSnapshot(oxp.getGraph(entityTB.getTimeSpan()));
		Building b = (Building) snap.loadEntity(entityTB.getUri());
		
		assertEquals(b.getUri(), entityTB.getUri());
		assertEquals(b.getType(), entityTB.getType());
		assertEquals(b.getTimeSpan(), entityTB.getTimeSpan());
		assertEquals(b.getName(), name1);
}
	
	
	//@Test
	public void BorkedTestAddEntityComplex() throws GabotoException{
		Gaboto oxp = GabotoFactory.getPersistentGaboto();
		Gaboto oxp_mem = GabotoFactory.getInMemoryGaboto();
		
		
		for(int i = 0; i < 50; i++){
			GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),Utils.getRandomTimespan());
	
			for(int j = 0; j < 100; j++){
				String name1 = Utils.generateRandomURI();
				try{
					entityTB.addProperty(Utils.getRandomTimespan(1,1,1,1,1,1), DC.title, name1);
				} catch(IllegalArgumentException e){}
			}
			oxp.add(entityTB);
			
			GabotoTimeBasedEntity entityTB2 = GabotoTimeBasedEntity.loadEntity(entityTB.getUri(), oxp_mem);
			
			Iterator<GabotoEntity> it1 = entityTB.iterator();
			Iterator<GabotoEntity> it2 = entityTB2.iterator();
			/*
			System.out.println(entityTB.getTimeSpan());
			System.out.println(entityTB2.getTimeSpan());
			
			System.out.println(entityTB.getTimeSpansSorted());
			System.out.println(entityTB2.getTimeSpansSorted());
			*/
			while(it1.hasNext()){
				assertTrue(it2.hasNext());
				Building b1 = (Building) it1.next();
				Building b2 = (Building) it2.next();
				
				assertEquals(b1.getUri(), b2.getUri());
				assertEquals(b1.getType(), b2.getType());
				assertTrue(b1.getTimeSpan().getBegin().aboutTheSame(b2.getTimeSpan().getBegin()) &&
						   b1.getTimeSpan().getEnd().aboutTheSame(b2.getTimeSpan().getEnd()));
				assertEquals(b1.getName(), b2.getName());
			}
		}
	} 
	
	@Test(expected=ResourceDoesNotExistException.class)
	public void testAddPurgeEntitySimple() throws GabotoException{
		Gaboto oxp = GabotoFactory.getPersistentGaboto();
		Gaboto oxp_mem = GabotoFactory.getInMemoryGaboto();
		
		for(int i = 0; i < 100; i++){
			GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),Utils.getRandomTimespan());
	
			String name1 = Utils.generateRandomURI();
			entityTB.addProperty(DC.title, name1);
			oxp.add(entityTB);
			
			oxp.purge(entityTB.getUri());
			
			// create snapshot
			GabotoSnapshot snap = oxp_mem.getSnapshot(oxp.getGraph(entityTB.getTimeSpan()));
			snap.loadEntity(entityTB.getUri());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPurgeEntityComplex() throws GabotoException{
		Gaboto oxp = GabotoFactory.getPersistentGaboto();
		Gaboto oxp_mem = GabotoFactory.getInMemoryGaboto();
		
		for(int i = 0; i < 50; i++){
			GabotoTimeBasedEntity entityTB = new GabotoTimeBasedEntity(Building.class, Utils.generateRandomURI(),Utils.getRandomTimespan());
	
			for(int j = 0; j < 100; j++){
				String name1 = Utils.generateRandomURI();
				try{
					entityTB.addProperty(Utils.getRandomTimespan(), DC.title, name1);
				} catch(IllegalArgumentException e){}
			}
			
			oxp.add(entityTB);
			oxp.purge(entityTB.getUri());
			
			// search
			Iterator it = oxp_mem.getNamedGraphSet().findQuads(Node.ANY, Node.createURI(entityTB.getUri()), Node.ANY, Node.ANY);
			assertTrue(! it.hasNext());
		}
	} 
}
