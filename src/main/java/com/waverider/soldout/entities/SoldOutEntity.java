package com.waverider.soldout.entities;

public class SoldOutEntity {

	EntityType entityType;
	
	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	SoldOutEntity(EntityType et){
		entityType = et;
	}
}
