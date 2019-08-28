package org.chrisli.log4jdbc.rdbms;

import org.chrisli.log4jdbc.rdbms.base.RdbmsSpecifics;

import java.text.SimpleDateFormat;

/**
 * [MySql关系型数据库特性]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class MySqlRdbmsSpecifics extends RdbmsSpecifics {

    public MySqlRdbmsSpecifics() {
        super();
    }

    public String formatParameterObject(Object object) {
        if (object instanceof java.sql.Time) {
            return "'" + new SimpleDateFormat(TIME_FORMAT).format(object) + "'";
        }
        if (object instanceof java.sql.Date) {
            return "'" + new SimpleDateFormat(DATE_FORMAT).format(object) + "'";
        }
        if (object instanceof java.util.Date) {
            return "'" + new SimpleDateFormat(DATE_TIME_FORMAT_B).format(object) + "'";
        }
        return super.formatParameterObject(object);
    }
}