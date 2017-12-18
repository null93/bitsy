package io.raffi.bitsy.setting;

import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;

public class PreviewLength extends SelectItem <Integer> {

	public PreviewLength ( Resource resource ) {
		super ( "Clip Preview Length", "previewLength", resource );
		this.setOptions ( 5, 10, 25, 50, 100 );
		this.setSelected ( resource.previewLength );
	}

}