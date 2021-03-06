package com.bean;

import com.common.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: user
 * @Date: 2018/5/10 16:30
 * @Description: 生产Bean的工厂类
 */
public class BeanFactory {

    public static Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    public static Map<String, Object> beansMap;

    private static ApplicationContext Appctx;

    private static ApplicationContext xmlPathApplication;

    private static XmlPathBean xmlPathBean;

    //当进行本地测试的时候使用此加载模块
    static {
        try {
            xmlPathApplication = new ClassPathXmlApplicationContext(Constants.CORE_XML_PATH);
            try {
                xmlPathBean = xmlPathApplication.getBean(XmlPathBean.class);
            } catch (NoSuchBeanDefinitionException e) {
                logger.warn("{}", e.getMessage());
            }
            if (xmlPathBean != null) {
                Appctx = new ClassPathXmlApplicationContext(xmlPathBean.xmlPathList.
                        toArray(new String[xmlPathBean.xmlPathList.size()]));
            } else {
                Appctx = xmlPathApplication;
            }
            Map<String, Object> map = Appctx.getBeansOfType(Object.class);
            String beanNames[] = Appctx.getBeanDefinitionNames();
            Map<String, Object> beansMap = new HashMap<>();
            for (String key : beanNames) {
                beansMap.put(map.get(key).getClass().getName(), map.get(key));
                beansMap.put(key, map.get(key));
            }
            if (BeanFactory.beansMap == null) {
                BeanFactory.beansMap = beansMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class objClass) {
        Object obj = null;
        try {
            obj = beansMap.get(objClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String id) {
        Object obj = null;
        try {
            obj = beansMap.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    /**
     * @param objClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeans(Class objClass) {
        Map<String, T> map = new HashMap<>();
        for (String key : beansMap.keySet()) {
            if (beansMap.get(key).getClass().getName().equals(objClass.getName())) {
                map.put(key, (T) beansMap.get(key));
            }
        }
        return map;
    }

    private static String getFilePathPrefix() {
        String prefix;
        prefix = BeanFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return prefix;
    }


    public static void main(String[] args) {
        System.out.println(getFilePathPrefix());
    }

}
