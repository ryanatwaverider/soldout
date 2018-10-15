package com.waverider.soldout.entities;

public class AccessTokenSale extends SoldOutEntity {

	private final TokenOwner buyer;
	private final TokenOwner seller;
	private final double salePrice;

	public AccessTokenSale(TokenOwner buyer, TokenOwner currentOwner, double listingPrice) {
		super(EntityType.ACCESS_TOKEN_SALE);
		
		this.buyer = buyer;
		this.seller = currentOwner;
		this.salePrice = listingPrice;
	}

	public TokenOwner getBuyer() {
		return buyer;
	}

	public TokenOwner getSeller() {
		return seller;
	}

	public double getSalePrice() {
		return salePrice;
	}

}
