package io.raffi.bitsy.setting;

import java.awt.event.ItemEvent;
import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;

public class MaxClips extends SelectItem <Integer> {

	public MaxClips ( Resource resource ) {
		super ( "Number of Saved Clips", "maxClips", resource );
		this.setOptions ( 5, 25, 50, 100, 300, 500 );
		this.setSelected ( resource.maxClips );
	}

	public void itemStateChanged ( ItemEvent event ) {
		super.itemStateChanged ( event );
		while ( this.resource.clips.size () > this.resource.maxClips ) {
            this.resource.clips.remove ( this.resource.clips.size () - 1 );
        }
        this.resource.save ();
        super.itemStateChanged ( event );
	}

}