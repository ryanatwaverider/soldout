/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout.messages;

import com.waverider.soldout.entities.EntityType;
import com.waverider.soldout.entities.SoldOutEntity;

import io.protostuff.Tag;

public class SoldOutEntityUpdate {

	@Tag(2)
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
	
	@Tag(3)
	private long sequenceNumber;

	@Tag(4)
	private SoldOutEntity entity;

	public SoldOutEntityUpdate(SoldOutEntity entity, ActionType actionType){
		this.entity = entity;
		this.actionType = actionType;
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
