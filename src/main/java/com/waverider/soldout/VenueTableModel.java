// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.waverider.soldout.entities.AccessTokenListing;
import com.waverider.soldout.entities.EventAccessToken;
import com.waverider.soldout.messages.ActionType;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class VenueTableModel extends AbstractTableModel implements SoldOutEntityUpdateSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(VenueTableModel.class);

	private final GlobalInformationProvider globalInfoProvider;

	public VenueTableModel(GlobalInformationProvider gip) {
		globalInfoProvider = gip;
	}
	
	public Class getColumnClass(int c) {
        return AccessTokenDisplay.class;
//        return String.class;
      }

	@Override
	public int getRowCount() {
		return tokenListsForRow.size();
	}

	@Override
	public int getColumnCount() {
		return maxColumnWidth;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ArrayList<EventAccessToken> rowList = tokenListsForRow.get(rowIndex);
		if (rowList==null) {
			return null;
		}
		
		EventAccessToken token = rowList.get(columnIndex);

		AccessTokenDisplay td = new AccessTokenDisplay();

		AccessTokenListing listing = globalInfoProvider.getListingFor(token.getId());
		if (listing!=null) {
			td.isListing=true;
			td.ownerId=token.getCurrentOwner().getWalletId();
			td.value = listing.getListingPrice();
			return td;
//			return "L: H:" + listing.getListingPrice();
		}

		if (token.getLastSalePrice()!=null) {
			td.isListing=false;
			td.ownerId=token.getCurrentOwner().getWalletId();
			td.value = token.getLastSalePrice();
//			return "S: H:" + token.getLastSalePrice();
		}
		
		return td;
	}
	
	ArrayList<ArrayList<EventAccessToken>> tokenListsForRow = new ArrayList<ArrayList<EventAccessToken>>();
	private int maxColumnWidth = 0;
	

	@Override
	public void onNewMessage(SoldOutEntityUpdate entityUpdateMessage) {

		
		switch (entityUpdateMessage.getEntity().getEntityType()) {
		case EVENT_ACCESS_TOKEN:
			if (entityUpdateMessage.getActionType()==ActionType.CREATE_ENTITY || 
			entityUpdateMessage.getActionType()==ActionType.UPDATE_ENTITY) {
				EventAccessToken eat = (EventAccessToken)entityUpdateMessage.getEntity();
				int rowLen = tokenListsForRow.size();
				while (tokenListsForRow.size()<=eat.getRow()) {
					tokenListsForRow.add(new ArrayList<EventAccessToken>());
				}
				ArrayList<EventAccessToken> seatList = tokenListsForRow.get(eat.getRow());

				while (seatList.size()<=eat.getSeat()) {
					seatList.add(new EventAccessToken(null, 0, 0, null, null));
				}
				seatList.set(eat.getSeat(), eat);
				if (eat.getSeat()>maxColumnWidth) {
					maxColumnWidth  = eat.getSeat()+1;
					fireTableStructureChanged();
				}
				if (rowLen<=eat.getRow()) {
					fireTableRowsInserted(rowLen, tokenListsForRow.size());
				}
				else {
					fireTableRowsUpdated(0, tokenListsForRow.size());
				}
			}
			break;
		case ACCESS_TOKEN_LISTING:
			if (entityUpdateMessage.getActionType()==ActionType.CREATE_ENTITY || 
			entityUpdateMessage.getActionType()==ActionType.UPDATE_ENTITY) {
				// create map of listings....but maybe we can just get it 
				// from the info provider
			}
			else {
//				fireTableDataChanged();
			}
		}
	}

	@Override
	public void start(Random random) {
		// TODO Auto-generated method stub
		
	}

}
