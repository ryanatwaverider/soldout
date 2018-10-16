// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout.entities;

import java.util.ArrayList;

import io.protostuff.Tag;


public class EventAccessToken extends SoldOutEntity {

	public TokenOwner getCurrentOwner() {
		return currentOwner;
	}

	public void setCurrentOwner(TokenOwner currentOwner) {
		this.currentOwner = currentOwner;
	}

	public String getEventId() {
		return eventId;
	}

	public int getRow() {
		return row;
	}

	public int getSeat() {
		return seat;
	}

	public String getArea() {
		return area;
	}

	@Tag(20)
	final private String eventId;
	
	@Tag(21)
	final private int row;

	@Tag(22)
	final private int seat;
	
	@Tag(23)
	final private String area;
	
	@Tag(24)
	final private String id;
	
	@Tag(25)
	private TokenOwner currentOwner;
	
	@Tag(26)
	private int listingIdCount;
	
	@Tag(27)
	private ArrayList<AccessTokenSale> saleChain = new ArrayList<AccessTokenSale>();
	
	
	@Tag(28)
	private long firstSalePrice;
	
	@Tag(29)
	private AccessTokenSale lastSale;	
	
	@Tag(30)
	private final TokenOwner vendor;
	

	public EventAccessToken(String eventId, int row, int seat, String area, TokenOwner initialOwner) {
		super(EntityType.EVENT_ACCESS_TOKEN);
		
		this.eventId = eventId;
		this.row = row;
		this.seat = seat;
		this.area = area;
		
		id = eventId + "_" + area + "_" + row +"_" + seat;
		this.currentOwner = initialOwner;
		
		listingIdCount = 0;
		this.vendor = initialOwner;
		
	}

	public final String getId() {
		return id;
	}
	
	public AccessTokenListing createListing(TokenOwner ownershipKey, long price){
		if (!ownershipKey.getIdentity().equals(currentOwner.getIdentity())){
			return null;
		}
		
		return new AccessTokenListing(listingIdCount++, this.eventId, this.getId(), price);
	}

	public AccessTokenSale createSale(TokenOwner buyer, AccessTokenListing listing) {
		if (buyer.getIdentity().contentEquals(currentOwner.getIdentity())){
			return null;
		}
		
		AccessTokenSale sale = new AccessTokenSale(buyer,currentOwner,listing.getListingPrice(),this.getId(),this.getEventId());
		if (saleChain.isEmpty()){
			firstSalePrice = listing.getListingPrice();
		}
		saleChain.add(sale);
		currentOwner = buyer;
		
		lastSale = sale; // for easy access to determie commissions
		
		return sale;
	}

	public ArrayList<AccessTokenSale> getSaleChain() {
		return saleChain;
	}

	public long getFirstSalePrice() {
		return firstSalePrice;
	}

	public Long getLastSalePrice() {
		if (lastSale==null){
			return null;
		}
		else {
			return lastSale.getSalePrice();
		}
	}

	public TokenOwner getVendor() {
		return vendor;
	}

}
