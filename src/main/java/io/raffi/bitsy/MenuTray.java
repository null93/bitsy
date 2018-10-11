package io.raffi.bitsy;

import io.raffi.bitsy.action.*;
import io.raffi.bitsy.graphic.ClipItem;
import io.raffi.bitsy.menu.*;
import io.raffi.bitsy.Resource;
import io.raffi.bitsy.setting.*;
import java.awt.SystemTray;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.MenuShortcut;
import javax.swing.ImageIcon;
import java.awt.AWTException;
import java.awt.MenuShortcut;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class MenuTray extends PopupMenu implements MenuListener {

	private SystemTray systemTray;
	private TrayIcon trayIcon;
	private Image image;
	private Resource resource;

	public MenuTray ( Resource resource ) {
		super ();
		this.resource = resource;
		this.systemTray = SystemTray.getSystemTray ();
		String iconFilename = this.isDarkMode () ? "logo_dark.png" : "logo.png";
		this.image = new ImageIcon (
			Thread.currentThread ()
				.getContextClassLoader ()
				.getResource ( "image/" + iconFilename )
		).getImage ();
		this.trayIcon = new TrayIcon ( this.image );
		this.trayIcon.setImageAutoSize ( true );
		this.refreshMenu ();
		this.trayIcon.setPopupMenu ( this );
		MenuObserver.getInstance ().addMenuListener ( this );
		try {
			this.systemTray.add ( this.trayIcon );
		}
		catch ( Exception e ) {}
	}

	private boolean isDarkMode () {
		try {
			final Process proc = Runtime.getRuntime ().exec (
				new String [] {
					"defaults",
					"read",
					"-g",
					"AppleInterfaceStyle"
				}
			);
			proc.waitFor ( 100, TimeUnit.MILLISECONDS );
			return proc.exitValue () == 0;
		}
		catch ( Exception exception ) {
			return false;
		}
	}

	private void createClips () {
		Resource resource = Resource.getInstance ();
		if ( resource.clips.size () <= 0 ) {
			MenuItem noClips = new MenuItem ("No clips found");
			noClips.setEnabled ( false );
			this.add ( noClips );
		}
		else {
			int i = 0;
			for ( String clip : resource.clips ) {
				ClipItem clipItem = new ClipItem ( clip );
				if ( i < 10 ) {
					clipItem.setShortcut ( new MenuShortcut ( KeyEvent.VK_0 + i++, false ) );
				}
				this.add ( clipItem );
			}
		}
	}

	private void createSettings () {
		this.add ( new PreviewLength ( this.resource ) );
		this.add ( new MaxClips ( this.resource ) );
		this.add ( new ResortRepeated ( this.resource ) );
		this.add ( new SelectedToTop ( this.resource ) );
		this.add ( new StartOnBoot ( this.resource ) );
		this.add ( new ClearOnExit ( this.resource ) );
	}

	private void createActions () {
		this.add ( new About ( this.resource ) );
		this.add ( new Clear ( this.resource ) );
		this.add ( new Quit ( this.resource ) );
	}

	public void refreshMenu () {
		this.removeAll ();
		this.createClips ();
		this.addSeparator ();
		this.createSettings ();
		this.addSeparator ();
		this.createActions ();
	}

}
