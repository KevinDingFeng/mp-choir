package com.shenghesun.user.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shenghesun.common.BaseResponse;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.User;
import com.shenghesun.service.ChoirService;
import com.shenghesun.service.UserService;
import com.shenghesun.util.FileIOUtil;
import com.shenghesun.util.PropertyConfigurer;

@RestController
@RequestMapping("/choir")
public class ChoirController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PropertyConfigurer propertyConfigurer;
	
	@Autowired
	private ChoirService choirService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/demo/{name}",method = RequestMethod.GET)
	@ResponseBody
	public String demoShowName(@PathVariable String name) {
		logger.debug("访问getUserByName,Name={}", name);
		return "name is  " + name;
	}
	
//	@ModelAttribute("entity")
//	public Choir prepare(
//			@RequestParam(value = "id", required = false) Long id,
//			HttpServletRequest req) {
//		String method = req.getMethod();
//		if (id != null && id > 0 && RequestMethod.POST.name().equals(method)) {// 修改表单提交后数据绑定之前执行
//			return choirService.getForUpdate(id);
//		} else if (RequestMethod.POST.name().equals(method)) {// 新增表单提交后数据绑定之前执行
//			return new Choir();
//		} else {
//			return null;
//		}
//	}

	/**
	 * 新增团
	 * @Title: creat 
	 * @Description: TODO 
	 * @param albumArtPaht
	 * @param choir
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年8月13日下午4:46:28
	 **/ 
	@RequestMapping(value = "/create")
	@ResponseBody
	public Object creat(@RequestParam(value = "albumArtFile") MultipartFile albumArtFile,
			Choir choir) {
		BaseResponse response = new BaseResponse();
		try {
			if (albumArtFile != null &&
			        !albumArtFile.isEmpty() &&
			        !"".equals(albumArtFile.getOriginalFilename())) {
			    // 上传专辑封面
			    String coverPath = FileIOUtil.uploadFile(albumArtFile.getOriginalFilename(),
			    		albumArtFile.getInputStream(),
			    		propertyConfigurer.getUploadFilePath(),
			            true);
			    if (StringUtils.isNotEmpty(coverPath)) {
			    	choir.setAlbumArtPaht(coverPath);
			    }
			    Set<User> userSet = new TreeSet<>();
			    Optional<User> userOpt = userService.findById(choir.getUserId());
			    User user = new User();
			    if(userOpt!=null) {
			    	user = userOpt.get();
			    }
			    userSet.add(user);
			    choir.setUsers(userSet);
			    
			    if(choir.getId()!=null) {
			    	Choir ctemp = choirService.getForUpdate(choir.getId());
			    	if(ctemp!=null) {
			    		choir.setVersion(ctemp.getVersion());
			    	}else {
			    		response.setSuccess(false);
			    		return response;
			    	}
			    }
			    
			    choir = choirService.save(choir);
			    
			    choir.setAlbumArtPaht(propertyConfigurer.getShowFilePath()+
			    		choir.getAlbumArtPaht());
			    
			    response.setData(choir);
			}
		} catch (IOException e) {
			logger.error("Exception {} in {} " , e.getMessage() , "creat"); 
		}
		return response;
	}
	
	
	/**
	 * 修改团信息
	 * @Title: updateChoirInfo 
	 * @Description: TODO 
	 * @param choir
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年9月18日下午6:12:02
	 **/ 
	@RequestMapping(value = "/updateChoirInfo")
	@ResponseBody
	public Object updateChoirInfo(Choir choir) {
		BaseResponse response = new BaseResponse();
		try {
			Choir choirDb = choirService.getForUpdate(choir.getId());
			choirDb.setChoirName(choir.getChoirName());
			choirDb.setPopulation(choir.getPopulation());
			choirService.save(choirDb);
			
			response.setData(choirDb);
		} catch (Exception e) {
			logger.error("Exception {} in {} " , e.getMessage() , "updateChoirInfo:"+choir.getId()); 
		}
		return response;
	}
	
	/**
	 * 修改发布演唱任务已经点击
	 * @Title: updateChoirPublishTask 
	 * @Description: TODO 
	 * @param choir
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年9月19日下午3:54:21
	 **/ 
	@RequestMapping(value = "/updateChoirPublishTask")
	@ResponseBody
	public Object updateChoirPublishTask(Long choirId) {
		BaseResponse response = new BaseResponse();
		try {
			Choir choirDb = choirService.getForUpdate(choirId);
			choirDb.setPublishTask(true);
			choirService.save(choirDb);
			
			response.setData(choirDb);
		} catch (Exception e) {
			logger.error("Exception {} in {} " , e.getMessage() , "updateChoirPublishTask:"+choirId); 
		}
		return response;
	}
	
	/**
	 * 获取团信息
	 * @Title: getChoirInfo 
	 * @Description: TODO 
	 * @param choirId
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年9月18日下午5:30:29
	 **/ 
	@RequestMapping(value = "/getChoirInfo")
	@ResponseBody
	public Object getChoirInfo(Long choirId) {
		BaseResponse response = new BaseResponse();
		try {
			Choir choir = choirService.getForUpdate(choirId);
			response.setData(choir);
		} catch (Exception e) {
			logger.error("Exception {} in {} " , e.getMessage() , "getChoirInfo:"+choirId); 
		}
		return response;
	}
}
