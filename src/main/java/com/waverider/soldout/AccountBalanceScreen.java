/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.waverider.soldout.entities.AccessTokenSale;
import com.waverider.soldout.entities.TokenOwner;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class AccountBalanceScreen extends JFrame implements SoldOutEntityUpdateSubscriber {
	
	private HashMap<String,JLabel> balanceLabels = new HashMap<String,JLabel>();
	
	ArrayList<Color> colors = new ArrayList<Color>();
	
	
	private Font font = new Font("Arial", Font.BOLD, 16);
	public AccountBalanceScreen(Collection<TokenOwner> tokenOwners) {
		super("Account Balances");
		
		colors.add(Color.LIGHT_GRAY);
		colors.add(Color.RED);
		colors.add(Color.ORANGE);
		colors.add(Color.GREEN);
		colors.add(Color.YELLOW);
		colors.add(Color.cyan);
		colors.add(Color.PINK);

		
		JPanel ownerPanel = new JPanel();
		ownerPanel.setLayout(new GridLayout(tokenOwners.size(),2,2,2));
		for (TokenOwner token : tokenOwners) {
//			JLabel colorLabel = new JLabel("   ");
//			colorLabel.setOpaque(true);
//			colorLabel.setBackground(colors.get(token.getWalletId()));
//			ownerPanel.add(colorLabel);

			JLabel lbl = new JLabel(token.getIdentity());
			lbl.setOpaque(true);
			lbl.setFont(font);
			ownerPanel.add(lbl);
			lbl.setBackground(colors.get(token.getWalletId()));
//			lbl.setForeground(Color.BLUE);

			JLabel balanceLabel = new JLabel("H:"+token.getWalletBalance());
			balanceLabel.setFont(font);
			balanceLabel.setBackground(colors.get(token.getWalletId()));
//			balanceLabel.setForeground(Color.BLUE);
			balanceLabel.setOpaque(true);


			balanceLabels.put(token.getIdentity(),balanceLabel);
			ownerPanel.add(balanceLabel);
			
		}
		
		this.setContentPane(ownerPanel);
		
	}

	@Override
	public void onNewMessage(SoldOutEntityUpdate entityUpdateMessage) {
		// TODO Auto-generated method stub
		TokenOwner to;
		JLabel label;
		
		
		switch (entityUpdateMessage.getEntity().getEntityType()) {
		case TOKEN_OWNER:
			to = (TokenOwner)entityUpdateMessage.getEntity();
			label = balanceLabels.get(to.getIdentity());
			label.setText("H:"+to.getWalletBalance());
			break;
		case ACCESS_TOKEN_SALE:
			AccessTokenSale sale = (AccessTokenSale)entityUpdateMessage.getEntity();
			to = sale.getBuyer();
			label = balanceLabels.get(to.getIdentity());
			label.setText("H:"+to.getWalletBalance());

			to = sale.getSeller();
			label = balanceLabels.get(to.getIdentity());
			label.setText("H:"+to.getWalletBalance());
			
		}
	}

	@Override
	public void start(Random random) {
		// TODO Auto-generated method stub
		
	}
	

}
