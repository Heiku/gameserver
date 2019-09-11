package com.ljh.gamedemo.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * Spring工具类，用于注入bean
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取 ApplicationContext
     *
     * @return  applicationContext
     */
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    /**
     * 通过name 获取 Bean
     *
     * @param name      bean name
     * @return          bean
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }


    /**
     * 通过class获取Bean
     *
     * @param clazz     class
     * @param <T>       T
     * @return          bean
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }


    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name      name
     * @param clazz     class
     * @param <T>       T
     * @return          bean
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

}
