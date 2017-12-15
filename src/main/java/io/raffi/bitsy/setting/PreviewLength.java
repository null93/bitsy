package io.raffi.bitsy.setting;

import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;

public class PreviewLength extends SelectItem <Integer> {
	
	private Resource resource;

	private String resourceName; 

	public PreviewLength ( Resource resource ) {
		//
		super ( "Clip Preview Length", "previewLength", resource );
		this.setOptions ( 25, 50, 100 );
	}

}