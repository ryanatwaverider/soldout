package com.waverider.soldout.entities;

import com.waverider.soldout.messages.ActionType;

public class AccessTokenListing extends SoldOutEntity{

	public String getEventAccessTokenId() {
		return eventAccessTokenId;
	}

	public double getListingPrice() {
		return listingPrice;
	}

	public int getListingId() {
		return listingId;
	}
	
	public String getEventId() {
		return eventId;
	}


	private final String eventAccessTokenId;
	private final String eventId;
	private final double listingPrice;
	private final int listingId;

	public AccessTokenListing(int listingId, String eventId, String eventAccessTokenId, double price) {
		super(EntityType.ACCESS_TOKEN_LISTING);
		
		this.eventId=eventId;
		this.eventAccessTokenId = eventAccessTokenId;
		this.listingPrice = price;
		this.listingId = listingId;
	}


}
