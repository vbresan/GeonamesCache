package biz.binarysolutions.geonames.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Elevation {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String lat;
	
	@Persistent
	private String lng;
	
	@Persistent
	private long count;
	
	@Persistent
	private String elevation;

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @param elevation
	 */
	public Elevation(String lat, String lng, String elevation) {

		this.lat = lat;
		this.lng = lng;
		this.elevation = elevation;
		this.count = 1;
	}

	/**
	 * 
	 */
	public void incrementCount() {
		++count;
	}

	/**
	 * 
	 * @return
	 */
	public String getElevation() {
		return elevation;
	}

	/**
	 * 
	 * @param pm
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static Elevation getByLatLng
		(
			PersistenceManager pm, 
			String lat,
			String lng
		) {
		
		Query query = pm.newQuery(Elevation.class);
		query.setFilter("lat == latParam");
		query.setFilter("lng == lngParam");
		query.declareParameters("String latParam, String lngParam");
		
		List<Elevation> elevations = (List<Elevation>) query.execute(lat, lng);
		if (elevations != null && elevations.size() > 0) {
			return elevations.get(0);
		} 
			
		return null;
	}
}
