package HongZe.MyChatRoom.orm;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;

/**
 * a wrapper for jdbcTemplate
 * 
 * @author Yang
 */
public class MyTemplate {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	final JdbcTemplate jdbcTemplate;

	private Map<Class<?>, Mapper<?>> classMapping;

	/**
	 * Constructor Scan the entity path,find out all the classes with "entity"
	 * annotation. Then,initiate all the classes to the Mappers which get the SQLs
	 * and the properties. Final,put the class and its Mapper to the classMapper.
	 * 
	 * @param jdbcTemplate getJdbcTemplate
	 * @param basePackage  the entity path
	 */
	public MyTemplate(JdbcTemplate jdbcTemplate, String basePackage) {
		// TODO Auto-generated constructor stub
		this.jdbcTemplate = jdbcTemplate;
		List<Class<?>> classes = scanEntities(basePackage);
		Map<Class<?>, Mapper<?>> classMapping = new HashMap<Class<?>, Mapper<?>>();
		for (Class<?> class1 : classes) {
			try {
				logger.info("Find class: " + class1.getName());
				Mapper<?> mapper = new Mapper<>(class1);
				classMapping.put(class1, mapper);
			} catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.classMapping = classMapping;
	}

	/**
	 * @param basePackage the target path
	 * @return all the classes which have the entity annotation.
	 */
	private List<Class<?>> scanEntities(String basePackage) {
		// TODO Auto-generated method stub
		var provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Set<BeanDefinition> beans = provider.findCandidateComponents(basePackage);
		for (BeanDefinition beanDefinition : beans) {
			try {
				classes.add(Class.forName(beanDefinition.getBeanClassName()));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classes;
	}

	public <T> List<T> fetchAll(Class<T> clazz) {
		Mapper<T> mapper = getMapper(clazz);
		List<T> list = jdbcTemplate.query("select * from " + mapper.tableName, mapper.rowMapper);
		return list;
	}

	/**
	 * Get an instance from class by id,return null if not found.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> T fetch(Class<T> clazz, Object id) {
		Mapper<T> mapper = getMapper(clazz);
		logger.info("SQL:" + mapper.selectSQL);
		List<T> list = jdbcTemplate.query(mapper.selectSQL, mapper.rowMapper, new Object[] { id });
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public <T> Mapper<T> getMapper(Class<T> clazz) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Mapper<T> mapper = (Mapper<T>) this.classMapping.get(clazz);
		if (mapper == null) {
			throw new RuntimeException("Target class is not a registered entity: " + clazz.getName());
		}
		return mapper;
	}

	/**
	 * Given class and id,get the instance.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> T get(Class<T> clazz, Object id) {
		T t = fetch(clazz, id);
		if (t == null) {
			throw new EntityNotFoundException(clazz.getName());
		}
		return t;
	}

	/**
	 * insert a bean to db.
	 * 
	 * @param <T>
	 * @param bean
	 */
	public <T> void insert(T bean) {
		Mapper<?> mapper = getMapper(bean.getClass());
		Object[] args = new Object[mapper.allInsertableAccessibleProperties.size()];
		int n = 0;
		int rows;
		for (AccessibleProperty prop : mapper.allInsertableAccessibleProperties) {
			try {
				args[n] = prop.getter.invoke(bean);
				n++;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("SQL:" + mapper.insertSQL);
		if (mapper.id.isIdentityId()) {
			KeyHolder kh = new GeneratedKeyHolder();
			rows = jdbcTemplate.update((conn) -> {
				var ps = conn.prepareStatement(mapper.insertSQL, Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
				return ps;
			}, kh);
			if (rows == 1) {
				try {
					mapper.id.setter.invoke(bean, kh.getKey());
				} catch (InvalidDataAccessApiUsageException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			rows = jdbcTemplate.update(mapper.insertSQL, args);
		}
	}

	/**
	 * update a bean
	 * 
	 * @param <T>
	 * @param bean
	 */
	public <T> void update(T bean) {
		Mapper<?> mapper = getMapper(bean.getClass());
		Object[] args = new Object[mapper.allUpdatableAccessibleProperties.size() + 1];
		int n = 0;
		for (AccessibleProperty prop : mapper.allUpdatableAccessibleProperties) {
			try {
				args[n] = prop.getter.invoke(bean);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}
		try {
			args[n] = mapper.id.getter.invoke(bean);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("SQL:" + mapper.updateSQL);
		jdbcTemplate.update(mapper.updateSQL, args);
	}

	public <T> void delete(Class<T> clazz, Object id) {
		Mapper<T> mapper = getMapper(clazz);
		logger.info("SQL:" + mapper.deleteSQL);
		jdbcTemplate.update(mapper.deleteSQL, id);
	}

	@SuppressWarnings("rawtypes")
	public Select select(String... selectFields) {
		return new Select(new Criteria(this), selectFields);
	}

	public <T> From<T> from(Class<T> entityClass) {
		Mapper<T> mapper = getMapper(entityClass);
		return new From<>(new Criteria<>(this), mapper);
	}
}
