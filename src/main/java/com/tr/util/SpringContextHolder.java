package com.tr.util;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SpringContextHolder
 *
 * @author Asen
 */
@Order(0)
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean, Ordered {

    private static ApplicationContext context;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.context = context;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.context = null;
    }

    public static final ApplicationContext context() {
        synchronized (context) {
            return context;
        }
    }

    public static final boolean containsBean(String name) {
        return context().containsBean(name);
    }

    public static final <T> T getBean(Class<T> requiredType) {
        return context().getBean(requiredType);
    }

    public static final <T> T getBean(String beanName, Class<T> type) {
        return context().getBean(beanName, type);
    }

    public static final Object getBean(String beanName) {
        return context().getBean(beanName);
    }

    public static final Object getBean(String beanName, Object... args) {
        return context().getBean(beanName, args);
    }

    public static final <T> List<T> getBeans(Class<T> requiredType) {
        ApplicationContext context = context();
        String[] beanNames = context.getBeanNamesForType(requiredType);
        if (ArrayUtils.isEmpty(beanNames)) {
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<T>(beanNames.length);
        for (String beanName : beanNames) {
            beans.add(context.getBean(beanName, requiredType));
        }
        return beans;
    }

}
