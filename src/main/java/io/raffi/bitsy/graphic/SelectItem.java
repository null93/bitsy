package io.raffi.bitsy.graphic;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.util.ArrayList;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.lang.reflect.*;
import java.util.HashMap;

import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;
import io.raffi.bitsy.menu.*;

public class SelectItem <Type> extends Menu implements ItemListener {

	private MenuObserver menuObserver;

	protected Resource resource;

	protected String resourceName;

	protected Type selected;

	protected HashMap <String, Type> options;

	public SelectItem ( String label, String resourceName, Resource resource ) {
		super ( label );
		this.resource = resource;
		this.resourceName = resourceName;
		this.menuObserver = MenuObserver.getInstance ();
		this.options = new HashMap <String, Type> ();
	}

	protected void setOptions ( Type ... options ) {
		this.removeAll ();
		this.options.clear ();
		for ( Type option : options ) {
			CheckboxMenuItem item = new CheckboxMenuItem ( option.toString () );
			item.addItemListener ( this );
			this.options.put ( option.toString (), option );
			this.add ( item );
		}
	}

	protected void setSelected ( Type value ) {
		this.selected = value;
		for ( int i = 0; i < this.getItemCount (); i++ ) {
			CheckboxMenuItem checkbox = ( CheckboxMenuItem ) this.getItem ( i );
			checkbox.setState ( this.selected.toString ().equals ( checkbox.getLabel () ) );
		}
	}

	public void itemStateChanged ( ItemEvent event ) {
		try {
			String item = ( String ) event.getItem ();
			Field field = Resource.class.getField ( this.resourceName );
	        field.set ( this.resource, this.options.get ( item ) );
	        this.resource.save ();
	    }
	    catch ( Exception e ) {
	    	System.out.println ( e );
	    	e.printStackTrace ();
	    }
		this.menuObserver.render ();
	}

}