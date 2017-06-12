package com.sample.wishlistDemo.api.generated;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;


public final class DefaultWishlistsResourceTest extends com.sample.wishlistDemo.api.generated.AbstractResourceTest
{
	/**
	 * Server side root resource /wishlists,
	 * evaluated with some default value(s).
	 */
	private static final String ROOT_RESOURCE_PATH = "/wishlists";

	/* get() /wishlists */
	@Test
	public void testGet()
	{
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");

		final Response response = target.request().get();

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}

	/* post(entity) /wishlists */
	@Test
	public void testPostWithWishlist()
	{
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("");
//		final Wishlist entityBody =
//		new Wishlist();
		final Wishlist entityBody = getMockWishlist();
		final javax.ws.rs.client.Entity<Wishlist> entity =
		javax.ws.rs.client.Entity.entity(entityBody,"application/json");

		final Response response = target.request().post(entity);

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.CREATED.getStatusCode(), response.getStatus());
	}

	/* get() /wishlists/wishlistId */
	@Test
	public void testGetByWishlistId()
	{
		/*final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/wishlistId");*/
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/user_10");
		
		final Response response = target.request().get();

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}

	/* put(entity) /wishlists/wishlistId */
	@Test
	public void testPutByWishlistIdWithWishlist()
	{
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/user_10");
		final Wishlist entityBody = getMockWishlist();
		final javax.ws.rs.client.Entity<Wishlist> entity =
		javax.ws.rs.client.Entity.entity(entityBody,"application/json");

		final Response response = target.request().put(entity);

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}

	/* get() /{wishlistId}/wishlistItems */
	@Test
	public void testgetByWishlistIdWishlistItems()
	{
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/user_10/wishlistItems");

		final Response response = target.request().get();

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}
	
	/* post() /{wishlistId}/wishlistItems */
	@Test
	public void testpostByWishlistIdWishlistItems()
	{
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/user_10/wishlistItems");

		final WishlistItem entityBody = getMockWishlistItem();
		final javax.ws.rs.client.Entity<WishlistItem> entity =
		javax.ws.rs.client.Entity.entity(entityBody,"application/json");
		
		final Response response = target.request().post(entity);

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}
	
	/* delete() /wishlists/wishlistId */
	@Test
	public void testDeleteByWishlistId()
	{
		//final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/wishlistId");
		final WebTarget target = getRootTarget(ROOT_RESOURCE_PATH).path("/user_10");

		final Response response = target.request().delete();

		Assert.assertNotNull("Response must not be null", response);
		Assert.assertEquals("Response does not have expected response code", Status.OK.getStatusCode(), response.getStatus());
	}

	@Override
	protected ResourceConfig configureApplication()
	{
		final ResourceConfig application = new ResourceConfig();
		application.register(DefaultWishlistsResource.class);
		return application;
	}
	
	private Wishlist getMockWishlist(){
		Wishlist entityBody = new Wishlist();
		entityBody.setId("user_10");
		entityBody.setOwner("Allen");
		entityBody.setTitle("Allen_wish");
		entityBody.setCreatedAt(new Date());
		WishlistItem item = new WishlistItem();
		item.set_id("product2");
		item.setProduct("computer");
		item.setAmount(2);
		item.setCreatedAt(new Date());
		List<WishlistItem> wli = new ArrayList<WishlistItem>();
		wli.add(item);
		entityBody.setItems(wli);
		return entityBody;
	}
	
	private WishlistItem getMockWishlistItem(){
		WishlistItem item = new WishlistItem();
		item.setProduct("iphone");
		item.setAmount(1);
		return item;
	}
}
