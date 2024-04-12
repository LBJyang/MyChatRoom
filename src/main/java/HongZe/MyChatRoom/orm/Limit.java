/**
 * 
 */
package HongZe.MyChatRoom.orm;

import java.util.List;

/**
 * @param <T>
 * 
 */
public class Limit<T> extends CriteriaQuery<T> {
	public Limit(Criteria<T> criteria, int offset, int maxResults) {
		// TODO Auto-generated constructor stub
		super(criteria);
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be >=0!");
		}
		if (maxResults < 0) {
			throw new IllegalArgumentException("MaxResults must be >=0");
		}
		this.criteria.offset = offset;
		this.criteria.maxResults = maxResults;
	}

	public List<T> list() {
		return this.criteria.list();
	}
}
