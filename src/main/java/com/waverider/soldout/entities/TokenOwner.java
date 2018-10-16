package com.waverider.soldout.entities;

import com.waverider.soldout.HederaCommunicator;

import io.protostuff.Tag;

public class TokenOwner extends SoldOutEntity {

	public String getIdentity() {
		return identity;
	}

	@Tag(100)
	final private String identity;

	private long walletBallance;
	final private int walletId;
	final private HederaCommunicator communicator;

	public long getWalletBalance() {
		return this.walletBallance;
	}

	public TokenOwner(String identity, int walletId, HederaCommunicator communicator) {
		super(EntityType.TOKEN_OWNER);
		this.identity = identity;
		this.walletId = walletId;
		this.communicator = communicator;
		int count = 0;
		do{
			try {
				Thread.sleep(1001);
				this.walletBallance = communicator.getWalletBallance(walletId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			count ++;
		} while(this.walletBallance == 0 && count < 4);
	}

	public boolean send(TokenOwner seller, long salePrice) {
		if(this.walletBallance<salePrice){
			return false;
		}
		try {
			communicator.send(this.walletId, seller.walletId, salePrice);
			this.walletBallance -= salePrice;
			seller.walletBallance += salePrice;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
