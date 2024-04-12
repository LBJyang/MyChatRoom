/**
 * 
 */
package HongZe.MyChatRoom.orm;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;

/**
 * Hold creteria query information.
 * 
 * @author fanyang
 * @param <T> Generic type
 * 
 */
public class Criteria<T> {
	// all the info
	MyTemplate myTemplate;
	List<String> select = null;
	Mapper<T> mapper;
	String where;
	List<String> orderBy = null;
	int offset = 0;
	int maxResults = 0;
	List<Object> whereParams = null;
	Class<T> cls;
	String table = null;

	/**
	 * constructor,get the template.
	 * 
	 * @param myTemplate
	 */
	public Criteria(MyTemplate myTemplate) {
		// TODO Auto-generated constructor stub
		this.myTemplate = myTemplate;
	}

	String sql() {
		StringBuilder sb = new StringBuilder(128);
		sb.append("SELECT ");
		sb.append(select == null ? "*" : String.join(",", select));
		sb.append(" FROM ").append(mapper.tableName);
		if (where != null) {
			sb.append(" WHERE ").append(where);
		}
		if (orderBy != null) {
			sb.append(" ORDER BY ").append(String.join(",", orderBy));
		}
		if (offset >= 0 && maxResults > 0) {
			sb.append(" LIMIT ?, ?");
		}
		return sb.toString();
	}

	Object[] params() {
		List<Object> params = new ArrayList<Object>();
		if (where != null) {
			for (Object object : whereParams) {
				params.add(object == null ? null : object);
			}
		}
		if (offset >= 0 && maxResults > 0) {
			params.add(offset);
			params.add(maxResults);
		}
		return params.toArray();
	}

	List<T> list() {
		String sql = sql();
		Object[] selectParams = params();
		return myTemplate.jdbcTemplate.query(sql, mapper.rowMapper, selectParams);
	}

	T first() {
		this.offset = 0;
		this.maxResults = 1;
		List<T> list = list();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	T unique() {
		this.offset = 0;
		this.maxResults = 2;
		List<T> list = list();
		if (list.isEmpty()) {
			throw new NoResultException("Expected one result but nothing found!");
		}
		if (list.size() > 1) {
			throw new NonUniqueResultException("Expected one result but more than one found!");
		}
		return list.get(0);
	}
}
