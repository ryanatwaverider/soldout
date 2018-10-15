package com.waverider.soldout.entities;

import java.util.ArrayList;


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

	final private String eventId;
	final private int row;
	final private int seat;
	final private String area;
	final private String id;
	private TokenOwner currentOwner;
	private int listingIdCount;
	
	private ArrayList<AccessTokenSale> saleChain = new ArrayList<AccessTokenSale>();
	private double firstSalePrice;
	private AccessTokenSale lastSale;	
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
	
	public AccessTokenListing createListing(TokenOwner ownershipKey, double price){
		if (!ownershipKey.getIdentity().equals(currentOwner.getIdentity())){
			return null;
		}
		
		return new AccessTokenListing(listingIdCount++, this.eventId, this.getId(), price);
	}

	public AccessTokenSale createSale(TokenOwner buyer, AccessTokenListing listing) {
		if (buyer.getIdentity().contentEquals(currentOwner.getIdentity())){
			return null;
		}
		
		AccessTokenSale sale = new AccessTokenSale(buyer,currentOwner,listing.getListingPrice());
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

	public double getFirstSalePrice() {
		return firstSalePrice;
	}

	public Double getLastSalePrice() {
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
