package com.shenghesun.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.shenghesun.util.PropertyConfigurer;

 /**
  * 静态文件配置
  * @ClassName: MyMvcConfig 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月14日 下午6:30:35  
  */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private PropertyConfigurer propertyConfigurer;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		//System.out.println("静态文件存储目录");
		registry.addResourceHandler(propertyConfigurer.getShowFilePath() + "**").addResourceLocations("file:" + propertyConfigurer.getUploadFilePath());
	}
}
