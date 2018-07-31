package org.chrisli.log4jdbc;

import org.chrisli.log4jdbc.logger.Slf4jProxyLogDelegator;

/**
 * [代理日志委托者工厂]
 * 
 * @author Chris li[黎超]
 * @version [版本, 2017-04-12]
 * @see
 */
public class ProxyLogFactory {

	private ProxyLogFactory() {
	}

	/**
	 * 支持Spring注入自定义日志代理
	 */
	private static ProxyLogDelegator logger;

	public static ProxyLogDelegator getProxyLogDelegator() {
		if (logger == null) {
			/**
			 * 默认支持SLF4J日志处理
			 */
			logger = new Slf4jProxyLogDelegator();
		}
		return logger;
	}
}
