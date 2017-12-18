package io.raffi.bitsy.clipboard;

import java.util.ArrayList;
import java.awt.Toolkit;
import java.awt.datatransfer.*;

public class ClipboardObserver extends Thread {

	private static ClipboardObserver instance;

	private ArrayList <ClipboardListener> listeners;

	private Boolean running;

	private String cached;

	protected ClipboardObserver () {
		this.listeners = new ArrayList <ClipboardListener> ();
		this.running = true;
		this.cached = this.read ();
	}

	public static ClipboardObserver getInstance () {
		if ( ClipboardObserver.instance == null ) {
			ClipboardObserver.instance = new ClipboardObserver ();
		}
		return ClipboardObserver.instance;
	}

	public void addClipboardListener ( ClipboardListener listener ) {
		this.listeners.add ( listener );
	}

	public void removeClipboardListener ( ClipboardListener listener ) {
		this.listeners.remove ( listener );
	}

	private void dispatch ( String value ) {
		for ( ClipboardListener listener : this.listeners ) {
			ClipboardEvent event = new ClipboardEvent ( value );
			listener.clipCopyPreformed ( event );
		}
	}

	public String read () {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			return ( String ) clipboard.getData ( DataFlavor.stringFlavor );
		}
		catch ( Exception exception ) {
			return null;
		}
	}

	public void halt () {
		this.running = false;
	}

	public void run () {
		while ( this.running ) {

			String current = this.read ();

			if ( this.cached == null || !this.cached.equals ( current ) ) {
				this.cached = current;
				this.dispatch ( current );
			}

			try {
				this.sleep ( 100 );
			}
			catch ( Exception e ) {}
		}
	}

}