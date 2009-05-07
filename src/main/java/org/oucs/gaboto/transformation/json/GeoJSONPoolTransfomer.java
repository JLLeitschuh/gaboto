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
package org.oucs.gaboto.transformation.json;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.oucs.gaboto.entities.pool.GabotoEntityPool;
import org.oucs.gaboto.transformation.kml.KMLPoolTransformer;

/**
 * Creates a representation of a KML File in JSON.
 * 
 * @author Arno Mittelbach
 *
 */
public class GeoJSONPoolTransfomer extends KMLPoolTransformer {
  
  private static XMLSerializer toJson;
  
	public String transform(GabotoEntityPool pool) {
	  String transformed = super.transform(pool);
		JSON j = getSerializer().read(transformed.trim()); 	
    return j.toString(1);
    /*
		try{
			return XML.toJSONObject(transformed.trim()).toString();
		} catch(JSONException e){
			PoolTransformationException pte = new PoolTransformationException();
			pte.initCause(e);
			throw pte;
		}
		*/
	}
	private XMLSerializer getSerializer() { 
	  if (toJson == null)
	    toJson = new XMLSerializer();
	  return toJson;
	}
}