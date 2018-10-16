package com.waverider.soldout;

import java.util.Random;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public interface SoldOutEntityUpdateSubscriber {

	public void onNewMessage(SoldOutEntityUpdate entityUpdateMessage);

	public void start(Random random);
	
}
