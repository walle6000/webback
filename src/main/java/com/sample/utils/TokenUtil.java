package com.sample.utils;

import java.io.IOException;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.bean.TokenBean;

public class TokenUtil {
	
	private static String tokenUrl = "https://api.us.yaas.io/hybris/oauth2/v1/token";
	
	private static String client_id = "4UXRJgu1uqOfdfhsjhDQu9e0JFpzh0jm";
	
	private static String client_secret = "4YF7AqDIT5kN2omN";
	
	private static String grant_type = "client_credentials";
	
	private static String scope = "hybris.tenant=pfi hybris.document_view hybris.document_manage hybris.document_admin";	
	
	
	public static String getToken(){
		RestTemplate restTemplate = new RestTemplate();
		/*HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());*/
		MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<String, String>();
		requestEntity.add("client_secret", client_secret);
        requestEntity.add("client_id", client_id);
        requestEntity.add("scope",scope);
        requestEntity.add("grant_type", grant_type);
        //HttpEntity<Map> formEntity = new HttpEntity<Map>(requestEntity, headers);
		String result = restTemplate.postForObject(tokenUrl, requestEntity, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		TokenBean token = new TokenBean();
	    try{
	        token = objectMapper.readValue(result, TokenBean.class);
	    }catch(IOException e) {
	        e.printStackTrace();
	    }
		return token.getAccess_token();
	}
	
	public static void main(String[] args){
		 String token = getToken();
		 System.out.println(token);
	}
	
}
