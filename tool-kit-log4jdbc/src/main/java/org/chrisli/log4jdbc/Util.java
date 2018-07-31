package org.chrisli.log4jdbc;

/**
 * [工具类]
 * 
 * @author Chris li[黎超]
 * @version [版本, 2017-04-12]
 * @see
 */
public class Util {
	/**
	 * [右对齐]
	 * 
	 * @author Chris li[黎超]
	 * @version [版本, 2017-04-12]
	 */
	public static String rightJustify(int fieldSize, String field) {
		if (field == null) {
			field = "";
		}
		StringBuffer output = new StringBuffer();
		for (int i = 0, j = fieldSize - field.length(); i < j; i++) {
			output.append(' ');
		}
		output.append(field);
		return output.toString();
	}
}
