package io.raffi.bitsy.menu;

import java.util.*;

public class MenuObserver {
	
	private static MenuObserver instance;

	private ArrayList <MenuListener> listeners;

	private MenuObserver () {
		this.listeners = new ArrayList <MenuListener> ();
	}

	public void addMenuListener ( MenuListener listener ) {
		this.listeners.add ( listener );
	}

	public void removeMenuListener ( MenuListener listener ) {
		this.listeners.remove ( listener );
	}

	public void render () {
		for ( MenuListener listener : this.listeners ) {
			listener.refreshMenu ();
		}
	}

	public static MenuObserver getInstance () {
		if ( MenuObserver.instance == null ) {
			MenuObserver.instance = new MenuObserver ();
		}
		return MenuObserver.instance;
	} 

}