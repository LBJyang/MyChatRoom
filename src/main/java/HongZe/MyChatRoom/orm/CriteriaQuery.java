/**
 * 
 */
package HongZe.MyChatRoom.orm;

/**
 * Basic Query Criteria.
 * 
 * @author fanyang
 */
public abstract class CriteriaQuery<T> {
	protected final Criteria<T> criteria;

	public CriteriaQuery(Criteria<T> criteria) {
		// TODO Auto-generated constructor stub
		this.criteria = criteria;
	}

	String sql() {
		return criteria.sql();
	}
}
