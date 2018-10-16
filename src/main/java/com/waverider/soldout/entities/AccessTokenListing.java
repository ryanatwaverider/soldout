package com.waverider.soldout.entities;

import com.waverider.soldout.messages.ActionType;

import io.protostuff.Tag;

public class AccessTokenListing extends SoldOutEntity{

	public String getEventAccessTokenId() {
		return eventAccessTokenId;
	}

	public long getListingPrice() {
		return listingPrice;
	}

	public int getListingId() {
		return listingId;
	}
	
	public String getEventId() {
		return eventId;
	}


	@Tag(200)
	private final String eventAccessTokenId;

	@Tag(201)
	private final String eventId;
	
	@Tag(202)
	private final long listingPrice;
	
	@Tag(203)
	private final int listingId;

	public AccessTokenListing(int listingId, String eventId, String eventAccessTokenId, long price) {
		super(EntityType.ACCESS_TOKEN_LISTING);
		
		this.eventId=eventId;
		this.eventAccessTokenId = eventAccessTokenId;
		this.listingPrice = price;
		this.listingId = listingId;
	}


}
