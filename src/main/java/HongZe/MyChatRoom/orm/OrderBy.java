/**
 * 
 */
package HongZe.MyChatRoom.orm;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * 
 */
public class OrderBy<T> extends CriteriaQuery<T> {
	public OrderBy(Criteria<T> criteria, String orderBy) {
		super(criteria);
		orderBy(orderBy);
		// TODO Auto-generated constructor stub
	}

	public OrderBy<T> orderBy(String orderBy) {
		// TODO Auto-generated method stub
		if (this.criteria.orderBy == null) {
			this.criteria.orderBy = new ArrayList<String>();
		}
		orderBy = checkProp(orderBy);
		this.criteria.orderBy.add(orderBy);
		return this;
	}

	private String checkProp(String orderBy) {
		// TODO Auto-generated method stub
		String prop = null;
		String upper = orderBy.toUpperCase();
		if (upper.endsWith(" DESC")) {
			prop = upper.substring(0, upper.length() - 5).trim();
			return fieldToProperty(prop) + " DESC";
		} else if (upper.endsWith(" ASC")) {
			prop = upper.substring(0, upper.length() - 4).trim();
			return fieldToProperty(prop) + " ASC";
		} else {
			prop = upper.trim();
			return fieldToProperty(prop);
		}
	}

	private String fieldToProperty(String prop) {
		// TODO Auto-generated method stub
		AccessibleProperty ap = this.criteria.mapper.allPropertiesMap.get(prop.toLowerCase());
		if (ap == null) {
			throw new IllegalArgumentException("Illegal property when use order by:" + prop);
		}
		return ap.columnName;
	}

	public OrderBy<T> desc() {
		int last = this.criteria.orderBy.size();
		String s = this.criteria.orderBy.get(last - 1);
		if (!s.toUpperCase().endsWith(" DESC")) {
			if (s.toUpperCase().endsWith(" ASC")) {
				s = s.substring(0, s.length() - 4);
			}
			s = s + " DESC";
		}
		return this;
	}

	public Limit<T> limit(int offset, int maxResults) {
		return new Limit<T>(this.criteria, offset, maxResults);
	}

	public List<T> list() {
		return criteria.list();
	}

	public T first() {
		return criteria.first();
	}
}
