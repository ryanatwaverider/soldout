/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout.entities;

import io.protostuff.Tag;

public class AccessTokenSale extends SoldOutEntity {

	@Tag(20)
	private final TokenOwner buyer;
	
	@Tag(21)
	private final TokenOwner seller;
	
	@Tag(22)
	private final long salePrice;
	
	@Tag(23)
	private final String tokenId;
	
	@Tag(24)
	private final String eventId;

	public String getTokenId() {
		return tokenId;
	}

	public String getEventId() {
		return eventId;
	}

	public AccessTokenSale(TokenOwner buyer, TokenOwner currentOwner, long listingPrice, String tokenId, String eventId) {
		super(EntityType.ACCESS_TOKEN_SALE);
		
		this.buyer = buyer;
		this.seller = currentOwner;
		this.salePrice = listingPrice;
		this.tokenId = tokenId;
		this.eventId = eventId;
	}

	public TokenOwner getBuyer() {
		return buyer;
	}

	public TokenOwner getSeller() {
		return seller;
	}

	public long getSalePrice() {
		return salePrice;
	}

}
