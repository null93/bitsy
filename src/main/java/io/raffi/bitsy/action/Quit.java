package io.raffi.bitsy.action;

import io.raffi.bitsy.graphic.ActionItem;
import io.raffi.bitsy.Resource;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * 
 * @version         1.0.1
 * @package         io.raffi.bitsy.action
 * @author          Rafael Grigorian [me@raffi.io]
 * @license 		MIT License
 * @copyright       2018 Rafael Grigorian — All Rights Reserved
 */
public class Quit extends ActionItem {
	
	private Resource resource;

	public Quit ( Resource resource ) {
		super ("Quit");
		super.setShortcut ( new MenuShortcut ( KeyEvent.VK_Q, false ) ); 
		this.resource = resource;
	}

	public void actionPerformed ( ActionEvent event ) {
        if ( this.resource.clearOnExit ) {
            this.resource.clips.clear ();
            this.resource.save ();
        }
        System.exit ( 0 );
	}

}