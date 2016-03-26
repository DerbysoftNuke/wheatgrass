package com.derby.nuke.wheatgrass.hibernate;

import java.lang.reflect.Constructor
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.ToStringBuilder
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.type.TextType
import org.hibernate.usertype.ParameterizedType
import org.hibernate.usertype.UserType

import com.google.common.base.Joiner

/**
 * 
 * @author Passyt
 * 
 */
class SimpleCollectionType implements UserType, ParameterizedType {

	private static final Log logger = LogFactory.getLog(SimpleCollectionType.class);
	private static final String COLLECTION_TYPE = "collectionType";
	private static final String ITEM_TYPE = "itemType";
	private static final String SPLIT = "split";
	private Class collectionClass;
	private Class itemClass;
	private String split = ",";

	@Override
	public int[] sqlTypes() {
		return [Types.VARCHAR];
	}

	@Override
	public Class<?> returnedClass() {
		return collectionClass;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		if (logger.isDebugEnabled()) {
			logger.debug("assemble owner[" + ToStringBuilder.reflectionToString(owner) + "]");
		}
		return deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) {
			return null;
		} else if (!(Collection.class.isAssignableFrom(value.getClass()))) {
			throw new IllegalArgumentException("the value " + value.getClass().getName() + " must be instance of "
					+ Collection.class.getName());
		} else {
			return clone((Collection<?>) value);
		}
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y) || (x != null && x.equals(y));
	}

	@Override
	public int hashCode(Object value) throws HibernateException {
		return value.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException,
			SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("nullSafeGet owner[" + ToStringBuilder.reflectionToString(owner) + "]");
		}
		String text = (String) TextType.INSTANCE.nullSafeGet(rs, names, session, owner);
		if (StringUtils.isBlank(text)) {
			return createCollectionObj();
		}

		Collection collection = this.createCollectionObj();
		String[] strings = text.split(this.split);
		for (String each : strings) {
			collection.add(this.createItemObj(each));
		}

		return collection;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException,
			SQLException {
		String text = null;
		if (value != null) {
			List<String> datas = new ArrayList<String>();
			for (Object each : (Collection) value) {
				datas.add(each.toString());
			}
			text = Joiner.on(this.split).join(datas);
		}
		TextType.INSTANCE.nullSafeSet(st, text, index, session);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		if (logger.isDebugEnabled()) {
			logger.debug("replace owner[" + ToStringBuilder.reflectionToString(owner) + "]");
		}
		return deepCopy(original);
	}

	@Override
	public void setParameterValues(Properties properties) {
		String collectionType = properties.getProperty(COLLECTION_TYPE);
		String itemType = properties.getProperty(ITEM_TYPE);
		if (collectionType == null || itemType == null) {
			throw new IllegalArgumentException("Illegal params setting [" + properties + "]");
		}

		this.collectionClass = getClassType(collectionType);
		this.itemClass = getClassType(itemType);

		if (!(Collection.class.isAssignableFrom(this.collectionClass))) {
			throw new IllegalArgumentException("Collection type must be assignable from " + Collection.class.getName());
		}

		String split = properties.getProperty(SPLIT);
		if (StringUtils.isNotBlank(split)) {
			this.split = split;
		}
	}

	private Object clone(Collection value) {
		Collection collection = this.createCollectionObj();
		collection.addAll(value);
		return collection;
	}

	private static Class<?> getClassType(String clazzType) {
		try {
			return Class.forName(clazzType);
		} catch (Exception e) {
			throw new IllegalStateException("Illegal class [" + clazzType + "]", e);
		}
	}

	private Collection createCollectionObj() {
		try {
			return (Collection) getConstructor(this.collectionClass).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Constructor init failed!", e);
		}
	}

	private Object createItemObj(String value) {
		if (String.class.equals(this.itemClass)) {
			return value;
		}

		if (this.itemClass.isEnum()) {
			return Enum.valueOf(this.itemClass, value);
		}

		try {
			return getConstructor(this.itemClass, String.class).newInstance(value);
		} catch (Exception eObj) {
			throw new IllegalStateException("Constructor init failed!", eObj);
		}
	}

	private static <T> Constructor<T> getConstructor(Class<T> clazzType, Class<?>... parameterTypes) {
		try {
			return clazzType.getConstructor(parameterTypes);
		} catch (Exception e) {
			throw new IllegalStateException("Constructor init failed!", e);
		}
	}

}
