package com.tr.util;

import com.tr.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Asen
 * 本地线程，数据源上下文
 *
 */
public class DataContextHolder {

	private static final Logger log = LoggerFactory.getLogger(DataContextHolder.class);
	
	//线程本地环境
	private static final ThreadLocal<User> userIdLocal = new ThreadLocal<>();

    public static ThreadLocal<User> getUserIdLocal() {
        return userIdLocal;
    }

    public static void userIdLocalClear(){
        userIdLocal.remove();
    }
}
