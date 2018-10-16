// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

	private ArrayList<SoldOutEntityUpdateSubscriber> updateSubscribers = new ArrayList<SoldOutEntityUpdateSubscriber>();
	private OfInt randomIter;
	private IntStream randomInts;

	private UserInterface userInterface;

	
	
	
	public SoldOut() throws InvocationTargetException, InterruptedException{
		scheduler = Executors.newSingleThreadScheduledExecutor();

		random = new Random(System.currentTimeMillis());
		
		randomInts = random.ints(0, 100);
		randomIter = randomInts.iterator();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				userInterface = new UserInterface(SoldOut.this);
				userInterface.pack();
				
			}
		});
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//			}
//		});
		updateSubscribers.add(userInterface);

	}

	public static void main(String[] args) throws IOException, InterruptedException, InvocationTargetException {
		try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }

		SoldOutConfig config = new SoldOutConfig();
		HederaCommunicator communicator = new HederaCommunicator(config);
		SoldOut so = new SoldOut();
		LiveEvent event = so.createEvent();
		so.createSeatsAndListings(event, communicator);
		so.createTokenOwners(communicator);
		
		
		
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
		for (SoldOutEntityUpdateSubscriber sim : updateSubscribers){
			sim.start(random);
		}
	}

	private void createTokenOwners(HederaCommunicator communicator) {
		// TODO Auto-generated method stub
		TokenOwner to = new TokenOwner("Alice",1,communicator);
		tokenOwners.put(to.getIdentity(),to);
		UserSimulator sim = new UserSimulator(to,this,"Speculator");
		updateSubscribers.add(sim);
		
		to = new TokenOwner("TicketScam",2,communicator);
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Professional");
		updateSubscribers.add(sim);

		to = new TokenOwner("Bob",3,communicator);
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Attendee");
		updateSubscribers.add(sim);

		to = new TokenOwner("ScalperJoe",4,communicator);
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Professional");
		updateSubscribers.add(sim);

		to = new TokenOwner("Betty",5,communicator);
		tokenOwners.put(to.getIdentity(),to);
		sim = new UserSimulator(to,this,"Speculator");
		updateSubscribers.add(sim);
		
		
		AccountBalanceScreen accountBalanceScreen = new AccountBalanceScreen(tokenOwners.values());
		updateSubscribers.add(accountBalanceScreen);
		
		accountBalanceScreen.pack();
		accountBalanceScreen.setLocation(300, 20);
		accountBalanceScreen.setVisible(true);

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
	
	private void createSeatsAndListings(LiveEvent event, HederaCommunicator communicator) {
		
		TokenOwner owner = new TokenOwner("Vendor_UC_Chicago",0,communicator);
		theVendor = owner;
		tokenOwners.put(owner.getIdentity(),owner);

		String level = "Floor";
		createSeatsForEvent(event,level,maxRowCount,2,10, owner, 75);
		level = "Main";
		createSeatsForEvent(event,level,maxRowCount,4,10, owner, 50);
		level = "Balcony";
		createSeatsForEvent(event,level, maxRowCount,3,10, owner, 25);
	}

	int maxRowCount=0;
	private void createSeatsForEvent(LiveEvent event, String level,int firstRow, int rows, int seats, 
			TokenOwner initialOwner, long listingPrice) {
		EventAccessToken token;

		HashMap<String, EventAccessToken> tokens = tokensByEvent.get(event.getId());
		
		for (int row=firstRow;row<rows+firstRow;row++){
			maxRowCount++;
			for (int seat=0;seat<seats;seat++){
				token = new EventAccessToken(event.getId(), row, seat, level, initialOwner);
				
				
				sendUpdateToSubscribers(token,ActionType.CREATE_ENTITY);
				//SoldOutEntityUpdate sou = new SoldOutEntityUpdate(token,ActionType.CREATE_ENTITY);
				//accessTokenChronicler.writeEntity(sou);

				tokens.put(token.getId(),token);
				publishListing(token.createListing(initialOwner, listingPrice));
			}
		}
	}

	public void publishListing(AccessTokenListing listing) {
		listingsByAccessToken.put(listing.getEventAccessTokenId(),listing);
		
		
		//SoldOutEntityUpdate sou = new SoldOutEntityUpdate(listing,ActionType.CREATE_ENTITY);
		//listingsChronicler.writeEntity(sou);
		
		HashMap<String, EventAccessToken> tokens = tokensByEvent.get(listing.getEventId());
		EventAccessToken accessToken = tokens.get(listing.getEventAccessTokenId());
		
		logger.info(accessToken.getCurrentOwner().getIdentity() + " Just published listing for: " + listing.getEventAccessTokenId() + 
				" at price " + listing.getListingPrice());
		
		
		sendUpdateToSubscribers(listing,ActionType.CREATE_ENTITY);
	}
	
	private final double VENDOR_SPLIT_PERCENT = 0.5d;

	private Random random;

	private boolean doMoneyTranfersForSale(AccessTokenSale sale, EventAccessToken token) {
		// First take would be to give all the money to the seller, but then the magic 
		// comes in
		long lastSalePrice = token.getLastSalePrice();
		long salePrice = sale.getSalePrice();
		
		long profit = salePrice-lastSalePrice;
		long basis = lastSalePrice;
		if (profit>0.0d){
			long vendorAmt = (long)((double)profit*VENDOR_SPLIT_PERCENT); // this is variable
			long sellerAmt = salePrice - vendorAmt;
			if(!transferMoney(sale.getBuyer(),sale.getSeller(),basis+sellerAmt))
			{
				return false;
			}
//			try {
//				Thread.sleep(1001);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			return transferMoney(sale.getBuyer(),token.getVendor(),vendorAmt);
		}
		else {
			return transferMoney(sale.getBuyer(),sale.getSeller(),salePrice);
		}
		
	}

	private boolean transferMoney(TokenOwner buyer, TokenOwner seller, long salePrice) {
		return buyer.send(seller,salePrice);
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
		
		for (SoldOutEntityUpdateSubscriber sim : updateSubscribers){
			sim.onNewMessage(message);
		}
	}

	public AccessTokenListing getRandomListing() {
		Set<String> keys = listingsByAccessToken.keySet();
		String[] strings = new String[keys.size()];
		keys.toArray(strings);
		
		Integer index = random.nextInt(keys.size());//randomIter.next();
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
			
			if(!doMoneyTranfersForSale(sale, token)){
				logger.warn(sale.getBuyer().getIdentity() + " could not transfer money");
				return null;
			}
			listingsByAccessToken.remove(listing.getEventAccessTokenId());

			sendUpdateToSubscribers(sale,ActionType.CREATE_ENTITY);
			sendUpdateToSubscribers(token,ActionType.UPDATE_ENTITY);
			
			sendUpdateToSubscribers(listing,ActionType.DESTROY_ENTITY);
			
			logger.info(sale.getBuyer().getIdentity() +  " Just bought: " + listing.getEventAccessTokenId() + " at price " + listing.getListingPrice() + 
					" from " + sale.getSeller().getIdentity());
			logger.info(sale.getBuyer().getIdentity() + " Balance is now " + sale.getBuyer().getWalletBalance());
			logger.info(sale.getSeller().getIdentity() + " Balance is now " + sale.getSeller().getWalletBalance());
			logger.info("Vendor named " + token.getVendor().getIdentity() + " balance is now " + token.getVendor().getWalletBalance());
			return sale;
	}

	private void sendUpdateToSubscribers(SoldOutEntity entity, ActionType action) {
		SoldOutEntityUpdate soeu = new SoldOutEntityUpdate(entity,action);
		for (SoldOutEntityUpdateSubscriber sim : updateSubscribers){
			sim.onNewMessage(soeu);
		}
	}

	public void scheduleEvent(Runnable r, long delayInMillis) {
		//logger.info("scheduling in " + delayInMillis);
		scheduler.schedule(r, delayInMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public AccessTokenListing getListingFor(String accessTokenId) {
		return listingsByAccessToken.get(accessTokenId);
	}

}