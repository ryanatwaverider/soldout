package com.waverider.soldout.entities;

import io.protostuff.Tag;

public class TokenOwner extends SoldOutEntity  {

	public String getIdentity() {
		return identity;
	}

	@Tag(100)
	final private String identity;
	
	@Tag(101)
	double walletBalance;

	public double getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(double walletBalance) {
		this.walletBalance = walletBalance;
	}

	public TokenOwner(String identity) {
		super(EntityType.TOKEN_OWNER);
		this.identity = identity;
		this.walletBalance = 5000d;
	}

	public void decrementAccountBy(double salePrice) {
		walletBalance -= salePrice;
	}

	public void incrementAccountBy(double salePrice) {
		walletBalance += salePrice;
	}


}
