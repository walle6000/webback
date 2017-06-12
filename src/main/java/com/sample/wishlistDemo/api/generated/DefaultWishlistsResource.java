
package com.sample.wishlistDemo.api.generated;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.service.WishListDocumentPersistenceService;
import com.sample.service.WishListRedisService;

/**
* Resource class containing the custom logic. Please put your logic here!
*/
@Component("apiWishlistsResource")
@Singleton
public class DefaultWishlistsResource implements com.sample.wishlistDemo.api.generated.WishlistsResource
{
	@javax.ws.rs.core.Context
	private javax.ws.rs.core.UriInfo uriInfo;

	@Autowired
	private WishListRedisService remotePersistenceService;
	
	@Autowired
	private WishListDocumentPersistenceService WishListDocumentPersistenceService;
	
	/* GET / */
	@Override
	public Response get(final YaasAwareParameters yaasAware)
	{
		// place some logic here
		return Response.ok()
			.entity(new java.util.ArrayList<Wishlist>()).build();
	}

	/* POST / */
	@Override
	public Response post(final YaasAwareParameters yaasAware, final Wishlist wishlist)
	{
		// place some logic here
		//remotePersistenceService.createOrUpdateWishList(wishlist);
		WishListDocumentPersistenceService.createOrUpdateWishList(wishlist);
		//Error error = null;
		return Response.created(uriInfo.getAbsolutePath())
			.build();
	}

	/* GET /{wishlistId} */
	@Override
	public Response getByWishlistId(final YaasAwareParameters yaasAware, final java.lang.String wishlistId)
	{
		// place some logic here
		//Wishlist wl = remotePersistenceService.getWishList(wishlistId);
		Wishlist wl = WishListDocumentPersistenceService.getWishList(wishlistId);
		/*return Response.ok()
			.entity(new Wishlist()).build();*/
		return Response.ok()
				.entity(wl).build();
	}

	/* PUT /{wishlistId} */
	@Override
	public Response putByWishlistId(final YaasAwareParameters yaasAware, final java.lang.String wishlistId, final Wishlist wishlist)
	{
		// place some logic here
		//remotePersistenceService.updateWishList(wishlistId,wishlist);
		WishListDocumentPersistenceService.updateWishList(wishlistId, wishlist);
		return Response.ok()
			.build();
	}

	@Override
	public
	Response getByWishlistIdWishlistItems(
			final YaasAwareParameters yaasAware,  final java.lang.String wishlistId)
	{
		// place some logic here
		//List<WishlistItem> Wishlist = remotePersistenceService.getWishlistItemsByWishlistId(wishlistId);
		List<WishlistItem> Wishlist = WishListDocumentPersistenceService.getWishlistItemsByWishlistId(wishlistId);
		/*return Response.ok()
				.entity(new java.util.ArrayList<WishlistItem>()).build();*/
		return Response.ok()
				.entity(Wishlist).build();
		
	}

	@Override
	public
	Response postByWishlistIdWishlistItems(final YaasAwareParameters yaasAware,
			final java.lang.String wishlistId, final WishlistItem wishlistItem){
		// place some logic here
		//remotePersistenceService.postWishlistItemsByWishlistId(wishlistId, wishlistItem);
		WishListDocumentPersistenceService.postWishlistItemsByWishlistId(wishlistId, wishlistItem);
		return Response.ok()
					.build();
	}
	
	/* DELETE /{wishlistId} */
	@Override
	public Response deleteByWishlistId(final YaasAwareParameters yaasAware, final java.lang.String wishlistId)
	{
		// place some logic here
		//remotePersistenceService.deleteWishList(wishlistId);
		WishListDocumentPersistenceService.deleteWishList(wishlistId);
		return Response.ok()
			.build();
	}


}
