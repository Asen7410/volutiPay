package com.tr.util;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * EnvironmentUtil
 *
 * @author Asen
 */
public class EnvironmentUtil {

    private static final String DEV = "dev";
    private static final String TEST = "test";
    private static final String PROD = "prod";

    private static class Holder {
        public static final Environment environment = SpringContextHolder.getBean(Environment.class);
    }

    public static String getActiveProfile() {
        String[] profiles = Holder.environment.getActiveProfiles();
        return ArrayUtils.isNotEmpty(profiles) ? profiles[0] : DEV;
    }

    public static boolean isDev() {
        return Objects.equals(getActiveProfile(), DEV);
    }

    public static boolean isTest() {
        return Objects.equals(getActiveProfile(), TEST);
    }

    public static boolean isProd() {
        return Objects.equals(getActiveProfile(), PROD);
    }

    public static boolean isNonProd() {
        return !isProd();
    }

}
