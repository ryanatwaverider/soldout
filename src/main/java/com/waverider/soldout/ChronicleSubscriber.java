/**
 * (C) Copyright Waverider LLC, 2018
 */
// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public interface ChronicleSubscriber {
	public void onMessage(SoldOutEntityUpdate message);
}
