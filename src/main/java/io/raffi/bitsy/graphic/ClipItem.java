package io.raffi.bitsy.graphic;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import io.raffi.bitsy.*;
import io.raffi.bitsy.menu.*;

public class ClipItem extends MenuItem implements ActionListener {

	private String value;

	private MenuObserver menuObserver;

	public ClipItem ( String value ) {
		super ( value );
		this.value = value;
		super.setLabel ( this.generatePreview ( value ) );
		this.addActionListener ( this );
		this.menuObserver = MenuObserver.getInstance ();
	}

	private String generatePreview ( String value ) {
		Resource resource = Resource.getInstance ();
		Integer limit = resource.previewLength;
		String preview = value.replaceAll ( "[ \t\r\n]+", " " ).trim ();
	 	preview = preview.substring ( 0, Math.min ( resource.previewLength - 1, preview.length () - 1 ) );
	 	return preview + "\u2026";
	}

	public void actionPerformed ( ActionEvent event ) {
		// Copy clip value into system clipboard
		StringSelection selection = new StringSelection ( this.value );
		Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		clipboard.setContents ( selection, null );
		this.menuObserver.render ();
	}

}