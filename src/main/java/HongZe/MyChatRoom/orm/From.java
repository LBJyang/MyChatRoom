/**
 * 
 */
package HongZe.MyChatRoom.orm;

import java.util.List;

/**
 * set the table,where method returns Where Class,where you can set the query
 * params.
 */
public class From<T> extends CriteriaQuery<T> {

	public From(Criteria<T> criteria, Mapper<T> mapper) {
		super(criteria);
		// TODO Auto-generated constructor stub
		this.criteria.mapper = mapper;
		this.criteria.cls = mapper.entityClass;
		this.criteria.table = mapper.tableName;
	}

	/**
	 * @param clause
	 * @param args
	 * @return
	 */
	public Where<T> where(String clause, Object... args) {
		return new Where<>(criteria, clause, args);
	}

	public OrderBy<T> orderBy(String orderBy) {
		return new OrderBy<T>(criteria, orderBy);
	}

	public Limit<T> limit(int offset, int maxResults) {
		return new Limit<T>(criteria, offset, maxResults);
	}

	public Limit<T> limit(int maxResults) {
		return limit(0, maxResults);
	}

	public List<T> list() {
		return criteria.list();
	}

	public T first() {
		return criteria.first();
	}

	public T unique() {
		return criteria.unique();
	}
}
