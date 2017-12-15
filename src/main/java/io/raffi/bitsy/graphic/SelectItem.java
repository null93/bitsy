package io.raffi.bitsy.graphic;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.util.ArrayList;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import io.raffi.bitsy.graphic.SelectItem;
import io.raffi.bitsy.Resource;
import io.raffi.bitsy.menu.*;

public class SelectItem <Type> extends Menu implements ItemListener {

	private MenuObserver menuObserver;

	private Resource resource;

	private String resourceName;

	private String selected = "";

	private ArrayList <CheckboxMenuItem> checkboxes = new ArrayList <CheckboxMenuItem> ();

	public SelectItem ( String label, String resourceName, Resource resource ) {
		super ( label );
		this.resource = resource;
		this.resourceName = resourceName;
		this.menuObserver = MenuObserver.getInstance ();
	}

	protected void setOptions ( Type ... options ) {
		this.removeAll ();
		for ( Type option : options ) {
			CheckboxMenuItem item = new CheckboxMenuItem ( option.toString () );
			item.addItemListener ( this );
			this.checkboxes.add ( item );
			this.add ( item );
		}
	}

	protected void setSelected ( String value ) {
		this.selected = value;
		for ( CheckboxMenuItem checkbox : this.checkboxes ) {
			checkbox.setState ( this.selected.equals ( checkbox.getLabel () ) );
		}
	}

	public void itemStateChanged ( ItemEvent event ) {
		// try {
		// 	Boolean state = ( ( CheckboxMenuItem ) event.getSource () ).getState ();
		// 	Field field = Resource.class.getField ( this.resourceName );
	 //        field.set ( this.resource, state );
	 //        this.resource.save ();
	 //    }
	 //    catch ( Exception e ) {}

		String item = ( String ) event.getItem ();
		this.setSelected ( item.toString () );
		this.menuObserver.render ();
	}

}