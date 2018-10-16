package com.waverider.soldout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.waverider.soldout.entities.AccessTokenListing;
import com.waverider.soldout.entities.AccessTokenSale;
import com.waverider.soldout.entities.EventAccessToken;
import com.waverider.soldout.entities.LiveEvent;
import com.waverider.soldout.entities.SoldOutEntity;
import com.waverider.soldout.entities.TokenOwner;
import com.waverider.soldout.messages.ActionType;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

import io.protostuff.LinkedBuffer;

public class SoldOut implements ChronicleSubscriber, GlobalInformationProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(SoldOut.class);

	private ScheduledExecutorService scheduler;

	ChronicleWriter accessTokenChronicler = new ChronicleWriter("EventAccessTokens");
	ChronicleWriter listingsChronicler = new ChronicleWriter("AccessTokenListings");
	LinkedBuffer lb = LinkedBuffer.allocate(4096);

	private ArrayList<UserSimulator> simulators = new ArrayList<UserSimulator>();

	private IntStream listingSearchInts;

	private OfInt randomIter;
	
	
	public SoldOut(){
		scheduler = Executors.newSingleThreadScheduledExecutor();

		random = new Random(System.currentTimeMillis());
		
		listingSearchInts = random.ints(1000, 0, 100);
		randomIter = listingSearchInts.iterator();

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SoldOut so = new SoldOut();
		LiveEvent event = so.createEvent();
		so.createSeatsAndListings(event);
		so.createTokenOwners();
		
		
		so.startSimulation();
		
		while (true){
			Thread.sleep(5000);
		}
//		if (args[0].contentEquals("CreateEvent")){
			
//		}
//		else {
//			so.fetchEventsAndListings(event);
//		}
	}

	

	private void startSimulation() {
		for (UserSimulator sim : simulators){
			sim.startSimulation(random);
		}
	}

	private void createTokenOwners() {
		// TODO Auto-generated method stub
		TokenOwner to = new TokenOwner("Alice");
		tokenOwners.put(to.getIdentity(),to);
		UserSimulator sim = new UserSimulator(to,this,"Speculator");
		simulators.add(sim);
		
		to = new TokenOwner("TicketScam");
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Professional");
		simulators.add(sim);

		to = new TokenOwner("Bob");
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Attendee");
		simulators.add(sim);

		to = new TokenOwner("ScalperJoe");
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Professional");
		simulators.add(sim);

	}

	private void fetchEventsAndListings(LiveEvent event) throws IOException {
		// TODO query the system for existing events and listings
		// also need to query for the full tree of transactions per accessToken
		accessTokenChronicler.runReadLoopToEnd(this);
		
		
	}

	HashMap<String,TokenOwner> tokenOwners = new HashMap<String,TokenOwner>();
	
	HashMap<String,LiveEvent> events = new HashMap<String,LiveEvent>();
	HashMap<String,HashMap<String,EventAccessToken>> tokensByEvent = new HashMap<String,HashMap<String,EventAccessToken>>();
	private HashMap<String, AccessTokenListing> listingsByAccessToken = new HashMap<String,AccessTokenListing>();

	private TokenOwner theVendor;
	

	private void addEvent(LiveEvent event){
		events.put(event.getId(), event);
		tokensByEvent.put(event.getId(), new HashMap<String,EventAccessToken>());
	}
	
	private LiveEvent createEvent(){
		LiveEvent event = new LiveEvent("Lady Gaga", "2018-11-05", "United Center", "Chicago");
		addEvent(event);
		return event;
	}
	
	private void createSeatsAndListings(LiveEvent event) {
		
		TokenOwner owner = new TokenOwner("Vendor_UC_Chicago");
		theVendor = owner;
		tokenOwners.put(owner.getIdentity(),owner);

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
				//accessTokenChronicler.writeEntity(sou);

				tokens.put(token.getId(),token);
				publishListing(token.createListing(initialOwner, listingPrice));
			}
		}
	}

	public void publishListing(AccessTokenListing listing) {
		listingsByAccessToken.put(listing.getEventAccessTokenId(),listing);
		
		
		SoldOutEntityUpdate sou = new SoldOutEntityUpdate(listing,ActionType.CREATE_ENTITY);
		//listingsChronicler.writeEntity(sou);
		
		logger.info("Just published listing for: " + listing.getEventAccessTokenId() + " at price " + listing.getListingPrice());
		
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

	private Random random;

	private void doMoneyTranfersForSale(AccessTokenSale sale, EventAccessToken token) {
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
		buyer.decrementAccountBy(salePrice);
		seller.incrementAccountBy(salePrice);
		
	}

	public void onMessage(SoldOutEntityUpdate message) {
		// TODO Auto-generated method stub
		SoldOutEntity soe = message.getEntity();
		
		
		switch (soe.getEntityType()){
		case EVENT_ACCESS_TOKEN:
			EventAccessToken accessToken = (EventAccessToken)message.getEntity();
			HashMap<String, EventAccessToken> tokens = tokensByEvent.get(accessToken.getEventId());
			if (message.getActionType()==ActionType.CREATE_ENTITY || message.getActionType()==ActionType.UPDATE_ENTITY){
				tokens.put(accessToken.getId(), accessToken);
			}
			else {
				tokens.remove(accessToken.getId());
			}
			break;
		case ACCESS_TOKEN_LISTING:
			AccessTokenListing listing = (AccessTokenListing)message.getEntity();
			if (message.getActionType()==ActionType.CREATE_ENTITY||message.getActionType()==ActionType.UPDATE_ENTITY){
				listingsByAccessToken.put(listing.getEventAccessTokenId(), listing);
			}
			else {
				listingsByAccessToken.remove(listing.getEventAccessTokenId());
			}
			break;
		case ACCESS_TOKEN_SALE:
			break;
		case LIVE_EVENT:
			break;
		case TOKEN_OWNER:
			break;
		}
		
		for (UserSimulator sim : simulators){
			sim.onNewMessage(message);
		}
	}

	public AccessTokenListing getRandomListing() {
		Set<String> keys = listingsByAccessToken.keySet();
		String[] strings = new String[keys.size()];
		keys.toArray(strings);
		Integer index = randomIter.next();
		String key;
		if (index<keys.size()){
			key = strings[index];
		}
		else {
			key = strings[0];
		}
		return listingsByAccessToken.get(key);
	}

	public EventAccessToken getAccessTokenFor(String eventId, String accessTokenId) {
		HashMap<String, EventAccessToken> tokens = tokensByEvent.get(eventId);
		
		return tokens.get(accessTokenId);
	}

	public AccessTokenSale purchaseListing(AccessTokenListing listing, TokenOwner buyer) {
			HashMap<String, EventAccessToken> tokensForEvent = tokensByEvent.get(listing.getEventId());
			EventAccessToken token = tokensForEvent.get(listing.getEventAccessTokenId());
			AccessTokenSale sale = token.createSale(buyer,listing);
			if (sale==null){
				return null;
			}
			
			doMoneyTranfersForSale(sale, token);
			updateSimulators(sale);
			updateSimulators(token);
			
			logger.info(sale.getBuyer().getIdentity() +  " Just bought: " + listing.getEventAccessTokenId() + " at price " + listing.getListingPrice() + 
					" from " + sale.getSeller().getIdentity());
			logger.info(sale.getBuyer().getIdentity() + " Balance is now " + sale.getBuyer().getWalletBalance());
			logger.info(sale.getSeller().getIdentity() + " Balance is now " + sale.getSeller().getWalletBalance());
			logger.info("Vendor named " + token.getVendor().getIdentity() + " balance is now " + token.getVendor().getWalletBalance());
			return sale;
	}

	private void updateSimulators(SoldOutEntity entity) {
		SoldOutEntityUpdate soeu = new SoldOutEntityUpdate(entity,ActionType.CREATE_ENTITY);
		for (UserSimulator sim : simulators){
			sim.onNewMessage(soeu);
		}
	}

	public void scheduleEvent(Runnable r, long delayInMillis) {
		//logger.info("scheduling in " + delayInMillis);
		scheduler.schedule(r, delayInMillis, TimeUnit.MILLISECONDS);
	}

}