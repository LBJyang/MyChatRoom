package HongZe.MyChatRoom.orm;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represent a Bean property.
 * 
 * @author Yang
 * 
 */
public class AccessibleProperty {

	// Method
	final Method getter;
	final Method setter;
// class type
	final Class<?> propertyType;
//java bean property name
	final String propertyName;
//Table column name;
	final String columnName;

	boolean isId() {
		return this.getter.isAnnotationPresent(Id.class);
	}

	// If a field do not have a Column Annotation,it default be insertable.
	boolean isInsertable() {
		if (isId()) {
			return false;
		}
		Column col = this.getter.getAnnotation(Column.class);
		return col == null || col.insertable();
	}

	boolean isUpdatable() {
		if (isId()) {
			return false;
		}
		Column col = this.getter.getAnnotation(Column.class);
		return col == null || col.updatable();
	}

	/**
	 * check id,whether is annotated with GeneratedValue and GenerationType is
	 * Identity.
	 * 
	 * @return
	 */
	boolean isIdentityId() {
		if (!isId()) {
			return false;
		}
		GeneratedValue gv = this.getter.getAnnotation(GeneratedValue.class);
		if (gv == null) {
			return false;
		}
		GenerationType gt = gv.strategy();
		return gt == GenerationType.IDENTITY;
	}

	/**
	 * Given pd,set the getter\setter\type\name\columnName 0f the property.
	 * 
	 * @param pd
	 */
	public AccessibleProperty(PropertyDescriptor pd) {
		// TODO Auto-generated constructor stub
		this.getter = pd.getReadMethod();
		this.setter = pd.getWriteMethod();
		this.propertyType = pd.getReadMethod().getReturnType();
		this.propertyName = pd.getName();
		this.columnName = getColumnName(pd.getReadMethod(), propertyName);
	}

	// get column name, if there is no annotation description,return propertyName.
	private String getColumnName(Method readMethod, String propertyName) {
		// TODO Auto-generated method stub
		Column col = readMethod.getAnnotation(Column.class);
		if (col == null || col.name().isEmpty()) {
			return propertyName;
		}
		return col.name();
	}
}
