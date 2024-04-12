/**
 * 
 */
package HongZe.MyChatRoom.orm;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Where<T> extends CriteriaQuery<T> {

	public Where(Criteria<T> criteria, String clause, Object... params) {
		super(criteria);
		this.criteria.where = clause;
		this.criteria.whereParams = new ArrayList<>();
		for (Object object : params) {
			this.criteria.whereParams.add(object);
		}
	}

	public OrderBy<T> orderBy(String orderBy) {
		return new OrderBy<T>(criteria, orderBy);
	}

	public Limit<T> limit(int maxResults) {
		return limit(0, maxResults);
	}

	public Limit<T> limit(int offset, int maxResults) {
		return new Limit<T>(criteria, offset, maxResults);
	}

	public List<T> list() {
		return criteria.list();
	}

	public T unique() {
		return criteria.unique();
	}

	public T first() {
		return criteria.first();
	}
}
