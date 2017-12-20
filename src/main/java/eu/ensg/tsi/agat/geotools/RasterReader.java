package eu.ensg.tsi.agat.geotools;

import java.io.File;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.referencing.CRS;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.ensg.tsi.agat.domain.Bound;
import eu.ensg.tsi.agat.domain.Point;

public class RasterReader implements IReader {

	@Override
	public Bound getBoundofFile(String filePath, int epsg) {
		double left   = Double.NaN; 
		double right  = Double.NaN; 
		double top    = Double.NaN; 
		double bottom = Double.NaN; 
		File file = new File(filePath);

		try {
			AbstractGridFormat format     = GridFormatFinder.findFormat(file);
			GridCoverage2DReader reader   = format.getReader(file);	
			GridCoverage2D coverage       = (GridCoverage2D) reader.read(null);
			CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
			BoundingBox envelope          = coverage.getEnvelope2D().toBounds(crs);
			
			left   = envelope.getMinX();
			right  = envelope.getMaxX();
			top    = envelope.getMaxY();
			bottom = envelope.getMinY();
			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:" + epsg);
			int epsgFile = (int) CRS.lookupEpsgCode(crs, false);
			int epsgSource = (int) CRS.lookupEpsgCode(sourceCRS, false);
			
			if(epsgFile != epsgSource) {
				coverage.getEnvelope2D().toBounds(sourceCRS);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return new Bound( new Point(left,bottom), new Point(right,top));
	}

}
