package com.waverider.soldout.messages;

import com.waverider.soldout.entities.EntityType;
import com.waverider.soldout.entities.SoldOutEntity;

public class SoldOutEntityUpdate {

	private ActionType actionType;
	
	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}


	public SoldOutEntity getEntity() {
		return entity;
	}

	public void setEntity(SoldOutEntity entity) {
		this.entity = entity;
	}


	public static long NEXT_SEQUENCE_NUMBER = 0l;
	
	private long sequenceNumber;

	private SoldOutEntity entity;

	public SoldOutEntityUpdate(SoldOutEntity entity, ActionType actionType){
		this.entity = entity;
		this.setSequence();
	}

	public SoldOutEntityUpdate() {
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(long sequence){
		this.sequenceNumber = sequence;
	}
	
	public void setSequence(){
		this.setSequenceNumber(NEXT_SEQUENCE_NUMBER++);
	}

}
