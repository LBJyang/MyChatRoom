package HongZe.MyChatRoom.orm;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Mapper for class
 * 
 * @author Yang
 * @param <T> any type of class
 */
public class Mapper<T> {

	/**
	 * basic info
	 */
	final Class<T> entityClass;
	final String tableName;

	/**
	 * Properties Info and Map
	 */
	final AccessibleProperty id;
	final List<AccessibleProperty> allProperties;
	final Map<String, AccessibleProperty> allPropertiesMap;
	final List<AccessibleProperty> allInsertableAccessibleProperties;
	final List<AccessibleProperty> allUpdatableAccessibleProperties;
	final Map<String, AccessibleProperty> allUpdatablePropertiesMap;
	final RowMapper<T> rowMapper;

	/**
	 * SQL to the Class
	 */
	final String selectSQL;
	final String updateSQL;
	final String insertSQL;
	final String deleteSQL;

	/**
	 * Give a class,and get a Mapper which has the class's all info and SQLs.
	 * 
	 * @param cla Any class
	 * @throws IntrospectionException
	 */
	public Mapper(Class<T> cla) throws IntrospectionException {
		// TODO Auto-generated constructor stub
		List<AccessibleProperty> all = getProperties(cla);
		AccessibleProperty[] ids = all.stream().filter(AccessibleProperty::isId).toArray(AccessibleProperty[]::new);
		if (ids.length != 1) {
			throw new RuntimeException("Too Many Ids.");
		}
		this.entityClass = cla;
		this.tableName = getTableName(cla);
		this.id = ids[0];
		this.allProperties = all;
		this.allPropertiesMap = buildPropertyMap(this.allProperties);
		this.allInsertableAccessibleProperties = all.stream().filter(AccessibleProperty::isInsertable)
				.collect(Collectors.toList());
		this.allUpdatableAccessibleProperties = all.stream().filter(AccessibleProperty::isUpdatable)
				.collect(Collectors.toList());
		this.allUpdatablePropertiesMap = buildPropertyMap(allUpdatableAccessibleProperties);
		this.rowMapper = new BeanPropertyRowMapper<>(this.entityClass);
		this.selectSQL = "SELECT * FROM " + this.tableName + " WHERE " + this.id.columnName + " = ?";
		this.updateSQL = "UPDATE "
				+ this.tableName + " SET " + String.join(",", this.allUpdatableAccessibleProperties.stream()
						.map(p -> p.columnName + " = ?").toArray(String[]::new))
				+ " WHERE " + this.id.columnName + " = ?";
		this.insertSQL = "INSERT INTO " + this.tableName + "("
				+ String.join(",",
						this.allInsertableAccessibleProperties.stream().map(p -> p.columnName).toArray(String[]::new))
				+ ") VALUES (" + numOfQuestions(this.allInsertableAccessibleProperties.size()) + ")";
		this.deleteSQL = "DELETE FROM " + this.tableName + " WHERE " + this.id.columnName + " = ?";
	}

	private String numOfQuestions(int size) {
		// TODO Auto-generated method stub
		var sb = new StringBuilder();
		sb.append("?");
		while (size > 1) {
			sb.append(",?");
			size--;
		}
		return sb.toString();
	}

	/**
	 * Given a class,get its tablename. if no table annotation or table.name is
	 * empty,return class's simple name.
	 * 
	 * @param cla
	 * @return table name
	 */
	private String getTableName(Class<T> cla) {
		// TODO Auto-generated method stub
		Table table = cla.getAnnotation(Table.class);
		if (table != null && !table.name().isEmpty()) {
			return table.name();
		}
		return cla.getSimpleName();
	}

	private Map<String, AccessibleProperty> buildPropertyMap(List<AccessibleProperty> allProperties2) {
		// TODO Auto-generated method stub
		Map<String, AccessibleProperty> map = new HashMap<String, AccessibleProperty>();
		for (AccessibleProperty accessibleProperty : allProperties2) {
			map.put(accessibleProperty.propertyName.toLowerCase(), accessibleProperty);
		}
		return map;
	}

	/**
	 * Get a Bean's all properties,except class && transient annotation && no setter
	 * 
	 * @param cla
	 * @return
	 * @throws IntrospectionException
	 */
	private List<AccessibleProperty> getProperties(Class<T> cla) throws IntrospectionException {
		// TODO Auto-generated method stub
		List<AccessibleProperty> properties = new ArrayList<AccessibleProperty>();
		BeanInfo info = Introspector.getBeanInfo(cla);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if (pd.getName().equals("class")) {
				continue;
			}
			Method getter = pd.getReadMethod();
			Method setter = pd.getWriteMethod();
			if (getter != null && getter.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if (getter != null && setter != null) {
				properties.add(new AccessibleProperty(pd));
			} else {
				throw new RuntimeException("Property " + pd.getName() + " is not read/write.");
			}
		}
		return properties;
	}
}
