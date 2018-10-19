/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout;

import java.security.spec.InvalidKeySpecException;

public class HederaCommunicator {

	private final SoldOutConfig config;

	public HederaCommunicator(SoldOutConfig config) {
		this.config = config;
	}

	public long getWalletBallance(int walletId) throws InvalidKeySpecException, Exception {
		return config.getAccount(walletId).getBalance();
	}

	public void send(int fromWalletId, int toWalletId, long salePrice) throws InvalidKeySpecException, Exception {
		config.getAccount(fromWalletId).send(config.getAccountId(toWalletId),salePrice);
	}
}
