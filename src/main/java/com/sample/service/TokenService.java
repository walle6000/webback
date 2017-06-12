package com.sample.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sample.bean.TokenBean;

@Service
public class TokenService {
	
	@Value("${tokenUrl}")
	private String tokenUrl = "https://api.us.yaas.io/hybris/oauth2/v1/token";
	
	@Value("${client_id}")
	private String client_id = "4UXRJgu1uqOfdfhsjhDQu9e0JFpzh0jm";
	
	@Value("${client_secret}")
	private String client_secret = "4YF7AqDIT5kN2omN";
	
	@Value("${grant_type}")
	private String grant_type = "client_credentials";
	
	@Value("${scope}")
	private String scope = "hybris.tenant=pfi hybris.document_view hybris.document_manage hybris.document_admin";	
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(2).expireAfterWrite(3600, TimeUnit.SECONDS).build();  
	
	private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
	
	public String getToken(){
		/*if("slow".equals("slow")){
			String testToken = "Bearer 021-8b6d2fe3-fffe-47d2-8fe6-0aa5bcf18cdc";
			return testToken;
		}*/
		String token = "";
		try {
			token = cache.get("token", new Callable<String>(){
				@Override
				public String call() throws Exception {
					MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<String, String>();
					requestEntity.add("client_secret", client_secret);
			        requestEntity.add("client_id", client_id);
			        requestEntity.add("scope",scope);
			        requestEntity.add("grant_type", grant_type);
			        TokenBean token = restTemplate.postForObject(tokenUrl, requestEntity, TokenBean.class);
					return token.getToken_type()+" "+token.getAccess_token();
				}
			});
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOG.debug("TokenService - getToken() - token:"+token);
		return token;
	}
	
	public HttpHeaders getHttpHeader(){
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", getToken());
		requestHeaders.add("Content-Type", "application/json");
		return requestHeaders;
	}
}
