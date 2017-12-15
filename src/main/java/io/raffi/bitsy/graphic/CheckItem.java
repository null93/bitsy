package io.raffi.bitsy.graphic;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import io.raffi.bitsy.*;
import io.raffi.bitsy.menu.*;

public class CheckItem extends CheckboxMenuItem implements ItemListener {
		
	private MenuObserver menuObserver;

	private Resource resource;

	private String resourceName; 

	public CheckItem ( String label, String resourceName, Resource resource ) {
		super ( label );
		this.resource = resource;
		this.menuObserver = MenuObserver.getInstance ();
		this.resourceName = resourceName;
		this.addItemListener ( this );
		try {
			Field field = Resource.class.getField ( resourceName );
	        Boolean currentValue = ( Boolean ) field.get ( this.resource );
	        this.setState ( currentValue );
	    }
	    catch ( Exception e ) {}
	}

	public void itemStateChanged ( ItemEvent event ) {
		try {
			Boolean state = ( ( CheckboxMenuItem ) event.getSource () ).getState ();
			Field field = Resource.class.getField ( this.resourceName );
	        field.set ( this.resource, state );
	        this.resource.save ();
	    }
	    catch ( Exception e ) {}
	    this.menuObserver.render ();
	}

}