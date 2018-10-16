package com.waverider.soldout;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class UserInterface implements SoldOutEntityUpdateSubscriber{

	private final GlobalInformationProvider globalInformationProvider;

	public UserInterface(GlobalInformationProvider gip) {
		globalInformationProvider = gip;
	}

	@Override
	public void onNewMessage(SoldOutEntityUpdate soeu) {
		// TODO Auto-generated method stub
		
	}
}
