package biz.binarysolutions.geonames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.binarysolutions.geonames.data.Reply;
import biz.binarysolutions.geonames.util.PMF;
import biz.binarysolutions.geonames.util.StringUtil;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	
	private static final String URL = 
		"http://api.geonames.org/search?" + 
		"featureClass=P&" + 
		"username=&" + 
		"type=json&" + 
		"name=";

	/**
	 * 
	 * @param pm
	 * @param name
	 * @return
	 */
	private Reply getCachedReply(PersistenceManager pm, String name) {
		return Reply.getByName(pm, name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private Reply getGeonamesReply(String name) {
		
		try {
		    URL url = new URL(URL + name);
		    BufferedReader reader = new BufferedReader(
	    		new InputStreamReader(url.openStream(), "UTF-8"));
		    
		    StringBuffer sb = new StringBuffer();
		    String line;

		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    reader.close();
		    
		    return new Reply(name, sb.toString());

		} catch (Exception e) {
		    // do nothing
		} 
		
		return null;
	}

	/**
	 * 
	 * @param pm
	 * @param reply
	 */
	private void cacheReply(PersistenceManager pm, Reply reply) {

		if (reply != null) {
			pm.makePersistent(reply);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private String getReply(String name) {
		
		String stringReply = null;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Reply reply = getCachedReply(pm, name);
			if (reply == null) {
				reply = getGeonamesReply(name);
				cacheReply(pm, reply);
			} else {
				reply.incrementCount();
			}
			
			if (reply != null) {
				stringReply = reply.getReply();
			}
		} finally {
			pm.close();
		}

		return stringReply;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
		throws IOException {
		
		String name = req.getParameter("name");
		if (! StringUtil.isDefined(name)) {
			return;
		}
		
		String reply = getReply(name);
		if (! StringUtil.isDefined(reply)) {
			return;
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.getWriter().println(reply);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws IOException {
		
		doPost(req, resp);
	}	
}
