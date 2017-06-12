package com.sample.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;


@Service
public class WishListDocumentPersistenceService {
	
	@Value("${documentUrl}")
	private String documentUrl = "https://api.beta.yaas.io/hybris/document/v1/";
	
	@Value("${tenant}")
    private String tenant = "pfi/";
	
	@Value("${identifier}")
	private String identifier = "pfi.interview-yg/";
	
	@Value("${dataType}")
	private String dataType = "wishlist";
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private ObjectMapper objectMapper;
    
	private String BASE_URL = new StringBuffer(documentUrl).append(tenant).append(identifier).append("data/").append(dataType).toString();
	
	private static final Logger LOG = LoggerFactory.getLogger(WishListDocumentPersistenceService.class);
	
	
	@PostConstruct
	public void init(){
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	
	public Wishlist getWishList(String wishlistId){
		LOG.debug("WishListDocumentPersistenceService - getWishList():" + wishlistId);
		HttpHeaders requestHeaders  = tokenService.getHttpHeader();
		StringBuffer url = new StringBuffer(BASE_URL).append("?q=id:").append(wishlistId);
		LOG.debug("WishListDocumentPersistenceService - getWishList() - url:"+url);
		HttpEntity<Wishlist> requestEntity = new HttpEntity<Wishlist>(null, requestHeaders);
		Map<String,String> params = new HashMap<String,String>();
        ResponseEntity<String> re = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, String.class, params);
        LOG.info("WishListDocumentPersistenceService - getWishList() - result:" + re.getBody());
        List<Wishlist> list = new ArrayList<Wishlist>();
        try {
			list = objectMapper.readValue(re.getBody(), new TypeReference<List<Wishlist>>(){} );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return list.size()==0?new Wishlist():list.get(0);
	}
	
	public void createOrUpdateWishList(Wishlist wishlist){
		LOG.debug("WishListDocumentPersistenceService - createOrUpdateWishList():" + wishlist.getId());
		Wishlist needUpdateWl = getWishList(wishlist.getId());
		if(needUpdateWl.getId()!=null){
			//List<WishlistItem> wlis = needUpdateWl.getItems();
			//wlis.addAll(wl.getItems());
			mergeWishList(needUpdateWl,wishlist);
			LOG.debug("WishListRedisService - createOrUpdateWishList():Update a document.");
			//insertWishList(needUpdateWl);
			updateWishList(needUpdateWl.getId(),needUpdateWl);
		}else{
			LOG.debug("WishListRedisService - createOrUpdateWishList():Create a new document.");
			insertWishList(wishlist);
		}
	}
	
	public void insertWishList(Wishlist wishlist){
		HttpHeaders requestHeaders  = tokenService.getHttpHeader();
		LOG.debug("WishListDocumentPersistenceService - insert() - url:"+BASE_URL);
		HttpEntity<Wishlist> requestEntity = new HttpEntity<Wishlist>(wishlist, requestHeaders);
        String result = restTemplate.postForObject(BASE_URL, requestEntity, String.class);
        LOG.info("WishListDocumentPersistenceService - insert() - result:"+result);
	}
	
	public void updateWishList(String wishlistId,Wishlist wishlist){
		HttpHeaders requestHeaders  = tokenService.getHttpHeader();
		StringBuffer url = new StringBuffer(BASE_URL).append("?q=id:").append(wishlistId);
		LOG.debug("WishListDocumentPersistenceService - updateWishList() - url:"+url);
		HttpEntity<Wishlist> requestEntity = new HttpEntity<Wishlist>(wishlist, requestHeaders);
		Map<String,String> params = new HashMap<String,String>();
		ResponseEntity<String> re = restTemplate.exchange(url.toString(), HttpMethod.PUT, requestEntity, String.class, params);
		LOG.info("WishListDocumentPersistenceService - updateWishList() - result:"+re.getBody());
	}
	
	public void deleteWishList(String wishlistId){
		HttpHeaders requestHeaders  = tokenService.getHttpHeader();
		StringBuffer url = new StringBuffer(BASE_URL).append("?q=id:").append(wishlistId);
		LOG.debug("WishListDocumentPersistenceService - deleteWishList() - url:"+url);
		HttpEntity<Wishlist> requestEntity = new HttpEntity<Wishlist>(null, requestHeaders);
		Map<String,String> params = new HashMap<String,String>();
		ResponseEntity<String> re = restTemplate.exchange(url.toString(), HttpMethod.DELETE, requestEntity, String.class, params);
		LOG.info("WishListDocumentPersistenceService - deleteWishList() - result:"+re.getBody());
	}
	
	public List<WishlistItem> getWishlistItemsByWishlistId(String wishlistId){
		Wishlist wl = getWishList(wishlistId);
		return wl.getItems()==null?new ArrayList<WishlistItem>():wl.getItems();
	}
	
	
	public void postWishlistItemsByWishlistId(String wishlistId,WishlistItem wishlistItem){
		Wishlist wl = getWishList(wishlistId);
		List<WishlistItem> wli = wl.getItems();
		if(wli != null){
			wli.add(wishlistItem);
			updateWishList(wl.getId(),wl);
		}
	}
	
	private void mergeWishList(Wishlist updatedOne,Wishlist newOne){
		if(updatedOne.getItems()!=null&&newOne.getItems()!=null&&newOne.getItems().size()!=0){
			WishlistItem newItem = newOne.getItems().get(0);
			List<WishlistItem> updatedItems = updatedOne.getItems();
			for(WishlistItem item : updatedItems){
				if(item.get_id().equals(newItem.get_id())){
					item.setAmount(newItem.getAmount());
					item.setCreatedAt(newItem.getCreatedAt());
					return;
				}
			}//end for
			updatedItems.add(newItem);
		}//end if
	}
}
		