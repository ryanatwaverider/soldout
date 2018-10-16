package com.waverider.soldout.entities;

import io.protostuff.Tag;

public class TokenOwner extends SoldOutEntity  {

	public String getIdentity() {
		return identity;
	}

	@Tag(100)
	final private String identity;
	
	@Tag(101)
	long walletBalance;

	public long getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(long walletBalance) {
		this.walletBalance = walletBalance;
	}

	public TokenOwner(String identity) {
		super(EntityType.TOKEN_OWNER);
		this.identity = identity;
		this.walletBalance = 1000;
	}

	public void decrementAccountBy(long salePrice) {
		walletBalance -= salePrice;
	}

	public void incrementAccountBy(long salePrice) {
		walletBalance += salePrice;
	}


}
