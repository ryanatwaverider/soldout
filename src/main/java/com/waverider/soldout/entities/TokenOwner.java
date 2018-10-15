package com.waverider.soldout.entities;

public class TokenOwner extends SoldOutEntity {

	public String getIdentity() {
		return identity;
	}

	final private String identity;
	
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
	}

}
