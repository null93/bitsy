package io.raffi.bitsy.action;

import io.raffi.bitsy.graphic.ActionItem;
import io.raffi.bitsy.Resource;
import java.awt.MenuShortcut;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

public class About extends ActionItem {
	
	private Resource resource;

	public About ( Resource resource ) {
		super ("About");
		super.setShortcut ( new MenuShortcut ( KeyEvent.VK_A, false ) ); 
		this.resource = resource;
	}

	public void actionPerformed ( ActionEvent event ) {
        try {
            Desktop.getDesktop ().browse ( new URI ("https://github.com/rdogg312/Bitsy") );
        }
        catch ( Exception e ) {}
	}

}