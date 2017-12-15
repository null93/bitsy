package io.raffi.bitsy.setting;

import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;

public class MaxClips extends SelectItem <Integer> {
	
	private Resource resource;

	private String resourceName; 

	public MaxClips ( Resource resource ) {
		//
		super ( "Number of saved clips", "maxClips", resource );
		this.setOptions ( 5, 25, 50, 100, 300, 500 );
	}

}