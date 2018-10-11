package io.raffi.bitsy.action;

import io.raffi.bitsy.graphic.ActionItem;
import io.raffi.bitsy.Resource;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.MenuShortcut;
import java.net.URI;

/**
 * 
 * @version         1.0.1
 * @package         io.raffi.bitsy.action
 * @author          Rafael Grigorian [me@raffi.io]
 * @license 		MIT License
 * @copyright       2018 Rafael Grigorian — All Rights Reserved
 */
public class About extends ActionItem {
	
	private Resource resource;

	public About ( Resource resource ) {
		super ("About");
		super.setShortcut ( new MenuShortcut ( KeyEvent.VK_A, false ) ); 
		this.resource = resource;
	}

	public void actionPerformed ( ActionEvent event ) {
        try {
            Desktop.getDesktop ().browse ( new URI ("https://github.com/rdogg312/bitsy") );
        }
        catch ( Exception e ) {}
	}

}