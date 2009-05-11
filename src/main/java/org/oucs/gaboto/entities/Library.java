package org.oucs.gaboto.entities;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oucs.gaboto.entities.pool.EntityExistsCallback;
import org.oucs.gaboto.entities.pool.GabotoEntityPool;
import org.oucs.gaboto.entities.utils.SimpleLiteralProperty;
import org.oucs.gaboto.entities.utils.SimpleURIProperty;
import org.oucs.gaboto.model.GabotoSnapshot;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


/**
 *<p>This class was automatically generated by Gaboto<p>
 */
public class Library extends Unit {
	private String oLISCode;
	private Website libraryHomepage;


	private static Map<String, List<Method>> indirectPropertyLookupTable;
	static{
		indirectPropertyLookupTable = new HashMap<String, List<Method>>();
		List<Method> list;

		try{
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getType(){
		return "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#Library";
	}

	@SimpleLiteralProperty(
		value = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOLISCode",
		datatypeType = "javaprimitive",
		javaType = "String"
	)
	public String getOLISCode(){
		return this.oLISCode;
	}

	@SimpleLiteralProperty(
		value = "http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOLISCode",
		datatypeType = "javaprimitive",
		javaType = "String"
	)
	public void setOLISCode(String oLISCode){
		this.oLISCode = oLISCode;
	}

	@SimpleURIProperty("http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLibraryHomepage")
	public Website getLibraryHomepage(){
		if(! this.isDirectReferencesResolved())
			this.resolveDirectReferences();
		return this.libraryHomepage;
	}

	@SimpleURIProperty("http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLibraryHomepage")
	public void setLibraryHomepage(Website libraryHomepage){
		if( null != libraryHomepage)
			this.removeMissingReference( libraryHomepage.getUri() );
		this.libraryHomepage = libraryHomepage;
	}







	public void loadFromSnapshot(Resource res, GabotoSnapshot snapshot, GabotoEntityPool pool) {
		super.loadFromSnapshot(res, snapshot, pool);
		Statement stmt;

		stmt = res.getProperty(snapshot.getProperty("http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasOLISCode"));
		if(null != stmt && stmt.getObject().isLiteral())
			this.setOLISCode(((Literal)stmt.getObject()).getString());

		stmt = res.getProperty(snapshot.getProperty("http://ns.ox.ac.uk/namespace/oxpoints/2009/02/owl#hasLibraryHomepage"));
		if(null != stmt && stmt.getObject().isResource()){
			Resource missingReference = (Resource)stmt.getObject();
			EntityExistsCallback callback = new EntityExistsCallback(){
				public void entityExists(GabotoEntityPool pool, GabotoEntity entity) {
					setLibraryHomepage((Website)entity);
				}
			};
			this.addMissingReference(missingReference, callback);
		}

	}
	protected List<Method> getIndirectMethodsForProperty(String propertyURI){
		List<Method> list = super.getIndirectMethodsForProperty(propertyURI);
		if(null == list)
			return indirectPropertyLookupTable.get(propertyURI);
		
		else{
			List<Method> tmp = indirectPropertyLookupTable.get(propertyURI);
			if(null != tmp)
				list.addAll(tmp);
		}
		return list;
	}

}