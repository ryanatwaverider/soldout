package com.waverider.soldout;

import java.util.HashMap;

import com.waverider.soldout.entities.AccessTokenListing;
import com.waverider.soldout.entities.AccessTokenSale;
import com.waverider.soldout.entities.EventAccessToken;
import com.waverider.soldout.entities.LiveEvent;
import com.waverider.soldout.entities.TokenOwner;
import com.waverider.soldout.messages.ActionType;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class SoldOut {
	
	ChronicleWriter tokenChronicler = new ChronicleWriter("EventAccessTokens");
	ChronicleWriter tokenListingsChronicler = new ChronicleWriter("AccessTokenListings");
	
	public SoldOut(){
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		SoldOut so = new SoldOut();
		
		if (args[0].contentEquals("CreateEvent")){
			so.createEvent();
			// TODO publish events, tokens, and listings!
		}
		else {
			so.fetchEventsAndListings();
		}
	}

	private void fetchEventsAndListings() {
		// TODO query the system for existing events and listings
		// also need to query for the full tree of transactions per accessToken
		
		
		
	}

	HashMap<String,LiveEvent> events = new HashMap<String,LiveEvent>();
	HashMap<String,HashMap<String,EventAccessToken>> tokensByEvent = new HashMap<String,HashMap<String,EventAccessToken>>();
	private HashMap<String, AccessTokenListing> listingsByAccessToken = new HashMap<String,AccessTokenListing>();
	

	private void addEvent(LiveEvent event){
		events.put(event.getId(), event);
		tokensByEvent.put(event.getId(), new HashMap<String,EventAccessToken>());
	}
	
	private void createEvent() {
		LiveEvent event = new LiveEvent("Lady Gaga", "2018-11-05", "United Center", "Chicago");
		addEvent(event);
		
		TokenOwner owner = new TokenOwner("Vendor_UC_Chicago");

		String level = "Floor";
		createSeatsForEvent(event,level,2,10, owner, 75.0d);

		level = "Main";
		createSeatsForEvent(event,level,10,10, owner, 50.0d);
		
		level = "Balcony";
		createSeatsForEvent(event,level,5,10, owner, 25.0d);
	}

	private void createSeatsForEvent(LiveEvent event, String level, int rows, int seats, 
			TokenOwner initialOwner, double listingPrice) {
		EventAccessToken token;

		HashMap<String, EventAccessToken> tokens = tokensByEvent.get(event.getId());
		
		for (int row=0;row<rows;row++){
			for (int seat=0;seat<seats;seat++){
				token = new EventAccessToken(event.getId(), row, seat, level, initialOwner);
				
				SoldOutEntityUpdate sou = new SoldOutEntityUpdate(token,ActionType.CREATE_ENTITY);
				tokenChronicler.writeEntity(sou);

				tokens.put(token.getId(),token);
				publishListing(token.createListing(initialOwner, listingPrice));
			}
		}
	}

	private void publishListing(AccessTokenListing listing) {
		listingsByAccessToken.put(listing.getEventAccessTokenId(),listing);
		
		
		SoldOutEntityUpdate sou = new SoldOutEntityUpdate(listing,ActionType.CREATE_ENTITY);
		tokenListingsChronicler.writeEntity(sou);
		
		// TODO publish this information out
	}
	
	
	/**
	 *
	 * @param buyer
	 * @param listing
	 * @param listingId
	 * @return
	 * 
	 */
	private AccessTokenSale purchaseAccessToken(TokenOwner buyer, AccessTokenListing listing, int listingId){
		if (listing.getListingId()==listingId){
			HashMap<String, EventAccessToken> tokensForEvent = tokensByEvent.get(listing.getEventId());
			EventAccessToken token = tokensForEvent.get(listing.getEventAccessTokenId());
			AccessTokenSale sale = token.createSale(buyer,listing);
			
			doMoneyTranfersForSale(sale, token);
			return sale;
		}
		else {
			return null;
		}
	}
	
	private final double VENDOR_SPLIT_PERCENT = 0.5d;

	private void doMoneyTranfersForSale(AccessTokenSale sale, EventAccessToken token) {
		// TODO Auto-generated method stub
		
		// First take would be to give all the money to the seller, but then the magic 
		// comes in
		double lastSalePrice = token.getLastSalePrice();
		double salePrice = sale.getSalePrice();
		
		double profit = salePrice-lastSalePrice;
		double basis = lastSalePrice;
		if (profit>0.0d){
			double vendorAmt = profit*VENDOR_SPLIT_PERCENT; // this is variable
			double sellerAmt = salePrice - vendorAmt;
			transferMoney(sale.getBuyer(),sale.getSeller(),basis+sellerAmt);
			transferMoney(sale.getBuyer(),token.getVendor(),vendorAmt);
		}
		else {
			transferMoney(sale.getBuyer(),sale.getSeller(),salePrice);
		}
		
	}

	private void transferMoney(TokenOwner buyer, TokenOwner seller, double salePrice) {
		// TODO move money between accounts
		// this is a hedera thing
		
	}

}
