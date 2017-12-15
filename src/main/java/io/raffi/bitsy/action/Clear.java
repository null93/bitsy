package io.raffi.bitsy.action;

import io.raffi.bitsy.graphic.ActionItem;
import io.raffi.bitsy.Resource;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Clear extends ActionItem {
	
	private Resource resource;

	public Clear ( Resource resource ) {
		super ("Clear");
		super.setShortcut ( new MenuShortcut ( KeyEvent.VK_C, false ) ); 
		this.resource = resource;
	}

	public void actionPerformed ( ActionEvent event ) {
        this.resource.clips.clear ();
        this.resource.save ();
        super.actionPerformed ( event );
	}

}