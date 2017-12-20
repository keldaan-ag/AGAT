package eu.ensg.tsi.agat.geotools;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.ensg.tsi.agat.domain.Bound;
import eu.ensg.tsi.agat.domain.Point;

public class VectorReader implements IReader {
	
	@Override
	public Bound getBoundofFile(String filePath, int epsg) {
		double left   = Double.NaN; 
		double right  = Double.NaN; 
		double top    = Double.NaN; 
		double bottom = Double.NaN; 
		File file = new File(filePath);
		Map<String, URL> map = new HashMap<String, URL>();      
		
		try {
			map.put("url", file.toURI().toURL());
			DataStore dataStore = DataStoreFinder.getDataStore(map);
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
			SimpleFeatureCollection collection = featureSource.getFeatures();
			ReferencedEnvelope env = collection.getBounds();
			CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:" + epsg);
			int epsgFile = (int) CRS.lookupEpsgCode(crs, false);
			int epsgSource = (int) CRS.lookupEpsgCode(sourceCRS, false);
			
			if(epsgFile != epsgSource) {
				env.transform(sourceCRS, false);
			}
			
			left   = env.getMinX();
			right  = env.getMaxX();
			top    = env.getMaxY();
			bottom = env.getMinY();
			
		} catch (Exception e) {
			e.printStackTrace();
		}        


		return new Bound( new Point(left,bottom), new Point(right,top));
	}


}
