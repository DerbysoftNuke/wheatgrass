package com.derby.nuke.wheatgrass.hibernate;

import com.derby.nuke.wheatgrass.lang.SortProperties;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.TextType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * DataMapUserType
 *
 * @author Drizzt Yang
 * @since 11-4-8 16:22
 */
@SuppressWarnings(["rawtypes","unchecked"])
class DataMapUserType implements UserType, ParameterizedType {
    private static final String CHAR_SET = "utf-8";
	private static final Logger logger = LoggerFactory.getLogger(DataMapUserType.class);
    private static final String VALUE_TYPE = "valueType";
    private static final String KEY_TYPE = "keyType";
	private Class valueClass;
	private Class keyClass;

    @Override
    public int[] sqlTypes() {
        return [TextType.INSTANCE.sqlType()];
    }

    @Override
    public Class<?> returnedClass() {
        return Map.class;
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
        return (value != null) ? new HashMap((Map) value) : null;
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
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("nullSafeGet owner[" + ToStringBuilder.reflectionToString(owner) + "]");
        }
        String text = (String) TextType.INSTANCE.nullSafeGet(rs, names, session, owner);
        if (text == null) {
            return new HashMap();
        }
        SortProperties properties = new SortProperties();
        try {
            properties.load(new ByteArrayInputStream(text.getBytes(CHAR_SET)));
        } catch (Exception e) {
            throw new IllegalStateException("load properties failed");
        }

        Map map = new HashMap();
        for (Object key : properties.keySet()) {
            try {
                map.put(createObject(keyClass, (String) key),
                        createObject(valueClass, properties.getProperty((String)key)));
            } catch (Exception e) {
                logger.error("Init Fail", e);
            }
        }
        return map;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        String text = null;
        if (value != null) {
            Map map = (Map) value;
            if (!map.isEmpty()) {
                SortProperties properties = new SortProperties();
                for (Object key : map.keySet()) {
                    properties.setProperty(key.toString(), map.get(key).toString());
                }
                OutputStream os = new ByteArrayOutputStream();
                try {
                    properties.store(os, CHAR_SET);
                } catch (IOException e) {
                    throw new IllegalStateException("store [" + map + "] to xml failed", e);
                }
                text = os.toString();
            }
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
        String valueType = properties.getProperty(VALUE_TYPE);
        String keyType = properties.getProperty(KEY_TYPE);
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("illegal params setting[" + properties + "]");
        }
        this.valueClass = getClassType(valueType);
        this.keyClass = getClassType(keyType);
    }

    private static Class<?> getClassType(String clazzType) {
        try {
            return Class.forName(clazzType);
        } catch (Exception e) {
            throw new IllegalStateException("Illegal class [" +clazzType + "]", e);
        }
    }

    private static Constructor<?> getConstructor(Class clazzType) {
        try {
        	return clazzType.getConstructor(String.class);
        } catch (Exception eStr) {
            logger.warn("get Constructor with String failed");
            try {
                Constructor<?> c = clazzType.getConstructor(Object.class);
                logger.warn("get Constructor with Object succeed");
                return c;
            } catch (Exception eObj) {
                throw new IllegalStateException("Constructor init failed!", eObj);
            }
        }
    }

    private static Object createObject(Class clazzType, String value) {
        if (String.class.equals(clazzType)) {
            return value;
        }
        if (clazzType.isEnum()) {
            return Enum.valueOf(clazzType, value);
        }
        try {
            return getConstructor(clazzType).newInstance(value);
        } catch (Exception eObj) {
            throw new IllegalStateException("Constructor init failed!", eObj);
        }
    }
}
