// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout.entities;

import java.io.Serializable;

public class LiveEvent extends SoldOutEntity implements Serializable {

	private final String eventName;
	private final String eventDate;
	private final String venue;
	private final String city;
	private final String id;
	
	public String getEventName() {
		return eventName;
	}

	public String getEventDate() {
		return eventDate;
	}

	public String getVenue() {
		return venue;
	}

	public String getCity() {
		return city;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public LiveEvent(String eventName, String eventDate, String venue, String city) {
		super(EntityType.LIVE_EVENT);
		
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.venue = venue;
		this.city = city;
		
	
		this.id = eventName+eventDate+venue+city;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8230109647432360046L;

	public String getId() {
		return id;
	}

	
	
}
