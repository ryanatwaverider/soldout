// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout.entities;

import io.protostuff.Tag;

public abstract class SoldOutEntity {

	@Tag(10)
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
