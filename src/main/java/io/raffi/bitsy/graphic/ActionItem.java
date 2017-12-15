package io.raffi.bitsy.graphic;

import java.awt.*;
import java.awt.event.*;
import io.raffi.bitsy.menu.*;

public class ActionItem extends MenuItem implements ActionListener {

	private MenuObserver menuObserver;

	public ActionItem ( String title ) {
		super ( title );
		this.menuObserver = MenuObserver.getInstance ();
		this.addActionListener ( this );
	}
	
	public void actionPerformed ( ActionEvent event ) {
		this.menuObserver.render ();
	}

}