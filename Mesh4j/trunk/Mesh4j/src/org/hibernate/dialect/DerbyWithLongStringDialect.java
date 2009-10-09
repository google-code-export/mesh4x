package org.hibernate.dialect;

import java.sql.Types;

/**
 * This class is a fix to the varchar limit in Derby. Derby allow the length of a varchar column
 * to be at most 32k. For bigger values, there is an unlimited varchar type, called "long varchar".
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-1501 
 *
 */
public class DerbyWithLongStringDialect extends DerbyDialect {
	
	public DerbyWithLongStringDialect() {
		super();
		registerColumnType(Types.VARCHAR, 32672, "varchar($l)");
        registerColumnType(Types.VARCHAR, Integer.MAX_VALUE, "long varchar");
	}

}
