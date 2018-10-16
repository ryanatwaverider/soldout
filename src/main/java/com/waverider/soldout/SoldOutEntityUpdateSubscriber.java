package com.waverider.soldout;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public interface SoldOutEntityUpdateSubscriber {

	public void onNewMessage(SoldOutEntityUpdate entityUpdateMessage);
	
}
