package com.sample.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.wishlistDemo.JerseyApplication;
import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class WishListRedisService {
	
	private static final Logger LOG = LoggerFactory.getLogger(JerseyApplication.class);

//	@Autowired
//	private JedisPool jedisPool = null;
	
	@Autowired
	private RedisTemplate<Serializable,Serializable> redisTemplate;
	
	public void insertWishList(Wishlist wl){
		LOG.debug("WishListRedisService - insertWishList():" + wl.getId());
		redisTemplate.execute(new RedisCallback<Wishlist>() {
	          public Wishlist doInRedis(RedisConnection connection) throws DataAccessException {
	              connection.set(redisTemplate.getStringSerializer().serialize(getWishlistKey(wl)), redisTemplate.getStringSerializer().serialize(WishlistMapToStr(wl)));
	              return null;
	          }
	      });
		LOG.debug("WishListRedisService - insertWishList():Done.");
		/*Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(getWishlistKey(wl), WishlistMapToStr(wl));
		} finally {
			jedisPool.returnResource(jedis);
		}*/
	}
	
	public void createOrUpdateWishList(Wishlist wl){
		LOG.debug("WishListRedisService - createOrUpdateWishList():" + wl.getId());
		Wishlist needUpdateWl = getWishList(wl.getId());
		if(needUpdateWl.getId()!=null){
			//List<WishlistItem> wlis = needUpdateWl.getItems();
			//wlis.addAll(wl.getItems());
			mergeWishList(needUpdateWl,wl);
			LOG.debug("WishListRedisService - createOrUpdateWishList():Update document.");
			insertWishList(needUpdateWl);
		}else{
			LOG.debug("WishListRedisService - createOrUpdateWishList():Create a new document.");
			insertWishList(wl);
		}
	}
	
	public Wishlist getWishList(String wishlistId){
		LOG.debug("WishListRedisService - getWishList():" + wishlistId);
		Wishlist wl =  redisTemplate.execute(new RedisCallback<Wishlist>() {
           @Override
          public Wishlist doInRedis(RedisConnection connection)throws DataAccessException {
                byte[] key = redisTemplate.getStringSerializer().serialize(getWishlistKey(wishlistId));
                if(connection.exists(key)){
                byte[] value = connection.get(key);
                String wlstr = redisTemplate.getStringSerializer().deserialize(value);
                return WishlistStrMapToWl(wlstr);
          }
          return new Wishlist();
         }
       });
		/*Wishlist wl = new Wishlist();
		Jedis jedis = jedisPool.getResource();
		try {
			String wlstr = jedis.get(getWishlistKey(wishlistId));
			if(wlstr != null){
				wl = WishlistStrMapToWl(wlstr);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}*/
		LOG.debug("WishListRedisService - getWishList():Done and Retrun.");
		return wl;
	}
	
	public void updateWishList(String wishlistId,Wishlist wl){
		deleteWishList(wishlistId);
		insertWishList(wl);
	}
	
	public void deleteWishList(String wishlistId){
		redisTemplate.execute(new RedisCallback<Wishlist>() {
	           @Override
	          public Wishlist doInRedis(RedisConnection connection)throws DataAccessException {
	                byte[] key = redisTemplate.getStringSerializer().serialize(getWishlistKey(wishlistId));
	                if(connection.exists(key)){
	                	connection.del(key);
	                }
	               return null;
	         }
	       });
		/*Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(getWishlistKey(wishlistId));
		} finally {
			jedisPool.returnResource(jedis);
		}*/
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
			insertWishList(wl);
		}
	}
	
	private String getWishlistKey(Wishlist wl) {
		return new StringBuilder("wish.list.").append(wl.getId()).toString();
	}
	
	private String getWishlistKey(String wishlistId) {
		return new StringBuilder("wish.list.").append(wishlistId).toString();
	}
	
	private String WishlistMapToStr(Wishlist wl){
		ObjectMapper mapper = new ObjectMapper();
		String wlstr = "";
		try {
			wlstr = mapper.writeValueAsString(wl);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wlstr;
	}
	
	private Wishlist WishlistStrMapToWl(String wishliststr){
		ObjectMapper mapper = new ObjectMapper();
		Wishlist wl = null;
		try {
			wl = mapper.readValue(wishliststr, Wishlist.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wl;
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
