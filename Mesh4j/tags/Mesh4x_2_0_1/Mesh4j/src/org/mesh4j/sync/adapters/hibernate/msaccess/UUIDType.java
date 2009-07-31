package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;

import sun.jdbc.odbc.JdbcOdbcInputStream;

public class UUIDType implements EnhancedUserType {

	private static final String CAST_EXCEPTION_TEXT = " cannot be cast to a java.lang.String";

	@Override
	public Object fromXMLString(String xmlValue) {
		return xmlValue;
	}

	@Override
	public String objectToSQLString(Object value) {
		return (String) value;
	}

	@Override
	public String toXMLString(Object value) {
		return (String) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		if (!String.class.isAssignableFrom(cached.getClass())) {
			return null;
		}
		return (String) cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return value.toString();
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (!String.class.isAssignableFrom(x.getClass())) {
			throw new HibernateException(x.getClass().toString()
					+ CAST_EXCEPTION_TEXT);
		} else if (!String.class.isAssignableFrom(y.getClass())) {

			throw new HibernateException(y.getClass().toString()
					+ CAST_EXCEPTION_TEXT);
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		if (!String.class.isAssignableFrom(x.getClass())) {
			throw new HibernateException(x.getClass().toString()
					+ CAST_EXCEPTION_TEXT);
		}
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		StringBuffer sb = new StringBuffer();

		try {
			JdbcOdbcInputStream is = (JdbcOdbcInputStream) rs
					.getBinaryStream(names[0]);
			int i;
			while ((i = is.read()) != -1) {
				sb.append(i);
			}
			String val = sb.toString();
			UUID uuid = UUID.nameUUIDFromBytes(val.getBytes());
			return uuid.toString();
		} catch (Exception e) {
			throw new HibernateException(e);
		}

	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);
			return;
		}

		if (!String.class.isAssignableFrom(value.getClass())) {
			throw new HibernateException(value.getClass().toString()
					+ CAST_EXCEPTION_TEXT);
		}

		st.setBytes(index, value.toString().getBytes());
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		if (!String.class.isAssignableFrom(original.getClass())) {
			throw new HibernateException(original.getClass().toString()
					+ CAST_EXCEPTION_TEXT);
		}
		return original;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class returnedClass() {
		return String.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARBINARY };
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}
