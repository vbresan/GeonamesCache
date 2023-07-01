package biz.binarysolutions.geonames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.binarysolutions.geonames.data.Elevation;
import biz.binarysolutions.geonames.data.Reply;
import biz.binarysolutions.geonames.util.PMF;
import biz.binarysolutions.geonames.util.StringUtil;

@SuppressWarnings("serial")
public class AstergdemServlet extends HttpServlet {
	
	private static final String URL = 
		"http://api.geonames.org/astergdem?username=&";
	
	/**
	 * 
	 * @param pm
	 * @param lat
	 * @param lng
	 * @return
	 */
	private Elevation getCachedElevation
		(
			PersistenceManager pm, 
			String lat,
			String lng
		) {
		return Elevation.getByLatLng(pm, lat, lng);
	}

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	private Elevation getGeonamesElevation(String lat, String lng) {
		
		try {
		    URL url = new URL(URL + "lat=" + lat + "&lng=" + lng);
		    BufferedReader reader = new BufferedReader(
	    		new InputStreamReader(url.openStream(), "UTF-8"));
		    
		    StringBuffer sb = new StringBuffer();
		    String line;

		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    reader.close();
		    
		    return new Elevation(lat, lng, sb.toString().trim());

		} catch (Exception e) {
		    // do nothing
		} 
		
		return null;
	}

	/**
	 * 
	 * @param pm
	 * @param elevation
	 */
	private void cacheElevation(PersistenceManager pm, Elevation elevation) {

		if (elevation != null) {
			pm.makePersistent(elevation);
		}
	}

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	private String getElevation(String lat, String lng) {
		
		String stringElevation = null;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Elevation elevation = getCachedElevation(pm, lat, lng);
			if (elevation == null) {
				elevation = getGeonamesElevation(lat, lng);
				cacheElevation(pm, elevation);
			} else {
				elevation.incrementCount();
			}
			
			if (elevation != null) {
				stringElevation = elevation.getElevation();
			}
		} finally {
			pm.close();
		}

		return stringElevation;
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
		throws IOException {

		String lat = req.getParameter("lat");
		String lng = req.getParameter("lng");
		
		if (!StringUtil.isDefined(lat) || !StringUtil.isDefined(lng)) {
			return;
		}

		String elevation = getElevation(lat, lng);
		if (! StringUtil.isDefined(elevation)) {
			return;
		}
		
		resp.setContentType("text/plain");
		resp.getWriter().println(elevation);
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws IOException {
		doPost(req, resp);
	}	
}
