package biz.binarysolutions.geonames.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * 
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Reply {
	
	@Persistent
	@PrimaryKey
	private String name;
	
	@Persistent
	private long count;
	
	@Persistent
	private Text reply;
	
	/**
	 * 
	 * @param name
	 * @param reply
	 */
	public Reply(String name, String reply) {
		
		this.name  = name;
		this.count = 1;
		this.reply = new Text(reply);
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
	public String getReply() {
		return reply.getValue();
	}

	/**
	 * 
	 * @param pm
	 * @param name
	 * @return
	 */
	public static Reply getByName(PersistenceManager pm, String name) {
		
		Query query = pm.newQuery(Reply.class);
		query.setFilter("name == nameParam");
		query.declareParameters("String nameParam");
		
		List<Reply> replies = (List<Reply>) query.execute(name);
		if (replies != null && replies.size() > 0) {
			return replies.get(0);
		} 
			
		return null;
	}
}
