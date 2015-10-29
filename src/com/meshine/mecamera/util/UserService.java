package com.meshine.mecamera.util;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;

@Rest(rootUrl = Constant.BASE_URL, converters = {
		StringHttpMessageConverter.class, FormHttpMessageConverter.class,
		ByteArrayHttpMessageConverter.class })
public interface UserService {

	@Post("/user/update/headonly/{username}")
	String updateHeadOnly(MultiValueMap<String, Object> data, String username);
	@Post("/user/update/head/{username}")
	String updateHead(MultiValueMap<String, Object> data, String username);
	@Get("/user/update/nickname/{username}/{nickname}")
	String updateNickname(String username,String nickname);
	@Get("/user/update/email/{username}/{email}")
	String updateEmail(String username,String email);
	@Get("/user/update/tel/{username}/{tel}")
	String updateTel(String username,String tel);
	@Get("/user/update/location/{username}/{location}")
	String updateLocation(String username,String location);
	
	
	@Get("/download/head/{username}")
	byte[] getHead(String username);
	
	@Get("/download/headpre/{username}")
	byte[] getHeadPre(String username);
	
	@Get("/user/info/{username}")
	String getUserInfo(String username);

	@Get("/user/check/{username}")
	String checkUser(String username);

	@Post("/user/add/{msg}")
	String addUser(String msg);

	@Post("/user/login/{username}/{password}")
	String login(String username, String password);

	// @Post("/user/upload/{user}")
	// String uploadHead(String username);

	//上传图片
	@Post("/image/upload/{username}")
	String upload(MultiValueMap<String, Object> data, String username);
}
