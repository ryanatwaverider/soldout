package com.waverider.soldout.entities;

import com.waverider.soldout.HederaCommunicator;

import io.protostuff.Tag;

public class TokenOwner extends SoldOutEntity {

	public String getIdentity() {
		return identity;
	}

	@Tag(100)
	final private String identity;

	final private int walletId;
	final private HederaCommunicator communicator;

	public long getWalletBalance() {
		try {
			return communicator.getWalletBallance(walletId);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public TokenOwner(String identity, int walletId, HederaCommunicator communicator) {
		super(EntityType.TOKEN_OWNER);
		this.identity = identity;
		this.walletId = walletId;
		this.communicator = communicator;
	}

	public boolean send(TokenOwner seller, long salePrice) {
		try {
			communicator.send(this.walletId, seller.walletId, salePrice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
