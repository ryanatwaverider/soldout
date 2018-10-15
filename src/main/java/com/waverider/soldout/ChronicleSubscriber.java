package com.waverider.soldout;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public interface ChronicleSubscriber {
	public void onMessage(SoldOutEntityUpdate message);
}
