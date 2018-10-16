package com.waverider.soldout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.waverider.soldout.entities.AccessTokenListing;
import com.waverider.soldout.entities.AccessTokenSale;
import com.waverider.soldout.entities.EventAccessToken;
import com.waverider.soldout.entities.TokenOwner;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class UserSimulator implements SoldOutEntityUpdateSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(UserSimulator.class);

	private boolean isBuyer = false;
	private boolean isSellerForProfit = false;
	private boolean isSellerForLoss = false;
	long updateTimer = 30000;
	private TokenOwner tokenOwner;
	private GlobalInformationProvider informationRelayer;
	private String profile;
	private IntStream eventDelays;
	private Random random;

	public UserSimulator(TokenOwner tokenOwner, GlobalInformationProvider informationRelayer, String profile){
		this.tokenOwner = tokenOwner;
		this.informationRelayer = informationRelayer;
		this.profile = profile;
	}

	public void startSimulation(Random random){
		
		
		this.random = random;
		
		if (profile.contentEquals("Speculator")){
//			eventDelays = random.ints(500, 8000, 12000);
			eventDelays = random.ints(2000, 2800);
		}
		else if (profile.contentEquals("Professional")){
//			eventDelays = random.ints(500, 4000, 6000);
			eventDelays = random.ints(500, 1000, 1300);
			isBuyer = true;
			isSellerForProfit = true;
		}
		else if (profile.contentEquals("Attendee")) {
//			eventDelays = random.ints(200, 28000, 42000);
			eventDelays = random.ints(8000, 12000);
			isBuyer = true;
			isSellerForLoss = true;
		}
		delayIterator = eventDelays.iterator();
		
		informationRelayer.scheduleEvent(doSomething,(long)delayIterator.next());
		
	}
	
	enum Event { BUY, SELLFORLOSS, SELLFORPROFIT, CHANGE_LISTING, NONE };
	
	private Event lastEvent;
	protected HashMap<String,EventAccessToken> ownedTokens = new HashMap<String,EventAccessToken>();
	
	private Runnable doSomething = new Runnable(){
		
		public void run(){
			if (isBuyer && (lastEvent!=Event.BUY || 
					(profile.equals("Professional") && ownedTokens.size()<20) ||
					(ownedTokens.size()<5)) && tokenOwner.getWalletBalance()>100.0d){
				AccessTokenListing listing = informationRelayer.getRandomListing();
//				EventAccessToken accessToken = informationRelayer.getAccessTokenFor(listing.getEventId(), listing.getEventAccessTokenId());
				AccessTokenSale sale = informationRelayer.purchaseListing(listing, tokenOwner);
				if (sale!=null){
					lastEvent = Event.BUY;
				}
			}
			else {
				if (ownedTokens.size()>0 && ownedTokens.size()<50){
					int idx = random.nextInt(ownedTokens.size());
					String[] a = new String[ownedTokens.size()];
					ownedTokens.keySet().toArray(a);
					String tokenId = a[idx];
					
					EventAccessToken token = ownedTokens.get(tokenId);
					if (isSellerForProfit){
						informationRelayer.publishListing(token.createListing(tokenOwner, token.getLastSalePrice()*1.2));
						lastEvent = Event.SELLFORPROFIT;
					}
					else if (isSellerForLoss){
						informationRelayer.publishListing(token.createListing(tokenOwner, token.getLastSalePrice()*.8));
						lastEvent = Event.SELLFORLOSS;
					}
				}
			}

			informationRelayer.scheduleEvent(this,(long)delayIterator.next());
		}
	};

	private OfInt delayIterator;

	public void onNewMessage(SoldOutEntityUpdate entityUpdateMessage) {
		switch (entityUpdateMessage.getEntity().getEntityType()){
		case ACCESS_TOKEN_SALE:
			AccessTokenSale accessTokenSale = (AccessTokenSale)entityUpdateMessage.getEntity();
			if (accessTokenSale.getSeller().getIdentity().contentEquals(tokenOwner.getIdentity())){
				ownedTokens.remove(accessTokenSale.getTokenId());
			}
			break;
		case EVENT_ACCESS_TOKEN:
			EventAccessToken et = (EventAccessToken)entityUpdateMessage.getEntity();
			if (et.getCurrentOwner().getIdentity().contentEquals(tokenOwner.getIdentity())){
				ownedTokens.put(et.getId(), et);
				logger.info(tokenOwner.getIdentity() + " Owned tokens size is " + ownedTokens.size());
			}
			default:
				break;
		}
		
	}
	
}
