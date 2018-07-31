package org.chrisli.log4jdbc.rdbms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * [Oracle关系型数据库特性]
 * 
 * @author Chris li[黎超]
 * @version [版本, 2017-04-12]
 * @see
 */
public class OracleRdbmsSpecifics extends RdbmsSpecifics {

	public OracleRdbmsSpecifics() {
		super();
	}

	public String formatParameterObject(Object object) {
		if (object instanceof Timestamp) {
			return "TO_TIMESTAMP('" + new SimpleDateFormat(DATE_TIME_FORMAT_A).format(object) + "', 'YYYY-MM-DD HH24:MI:SS.FF3')";
		}
		if (object instanceof Date) {
			return "TO_DATE('" + new SimpleDateFormat(DATE_TIME_FORMAT_B).format(object) + "', 'YYYY-MM-DD HH24:MI:SS')";
		}
		return super.formatParameterObject(object);
	}

}
