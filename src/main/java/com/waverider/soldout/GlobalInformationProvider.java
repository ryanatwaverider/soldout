package com.waverider.soldout;

import com.waverider.soldout.entities.AccessTokenListing;
import com.waverider.soldout.entities.AccessTokenSale;
import com.waverider.soldout.entities.EventAccessToken;
import com.waverider.soldout.entities.TokenOwner;

public interface GlobalInformationProvider {
	
	public AccessTokenListing getRandomListing();
	
	public EventAccessToken getAccessTokenFor(String eventId, String accessTokenId);
	
	public AccessTokenSale purchaseListing(AccessTokenListing listing, TokenOwner buyer);
	
	public void scheduleEvent(Runnable r, long delayInMillis);

	public void publishListing(AccessTokenListing createListing);

}
