package io.raffi.CloudClip;

import java.lang.NullPointerException;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * MenuTray.java - This class initializes the tray icon in the system tray and it handles updating
 * it.  It also provides the action preformed function that handles all clicks to the menu items,
 * because this class implements Action listener.
 * @version     1.0.0
 * @package     CloudClip
 * @category    MenuTray
 * @author      Rafael Grigorian
 * @license     GNU Public License <http://www.gnu.org/licenses/gpl-3.0.txt>
 */
@SuppressWarnings ( "serial" )
public class MenuTray implements ActionListener {

	private static MenuTray instance;

	/**
	 * This internal data member contains an instance of the history class, to handle storing clips.
	 * @var     History         history             Instance of the History class for storing clips.
	 */
	private History history;

	/**
	 * This data member holds the instance of the preferences class and is used to read the
	 * settings.
	 * @var 	Preferences 	preferences 		The preferences singleton instance reference
	 */
	private Preferences preferences;

	/**
	 * This internal data member holds the instance to the popup menu that we created for the system
	 * tray.  It is used throughout the class.
	 * @var     PopupMenu       popup               Instance of the PopupMenu for tray icon
	 */
	private PopupMenu popup;

	/**
	 * This hash map is linked so it preserves order.  It holds the menu items binded with the
	 * original clip.
	 * @var     LinkedHashMap   clips               <String, MenuItem>
	 */
	private LinkedHashMap <String, MenuItem> clips;

	private ActionListener clipListener;

	private ActionListener disconnectListener;

	private Packet packet;


	/**
	 * This constructor initializes the tray icon in the system tray and populates it with the
	 * initial data.  It also throws a custom exception if we fail to add to system tray or if the
	 * command is not supported.
	 * @throws  CloudClipException
	 */
	private MenuTray () {
		// Save the history object, preferences object, and the packet object
		this.history = History.getInstance ();
		this.preferences = Preferences.getInstance ();
		this.packet = Packet.getInstance ();
		// Initialize and define the clip listener
		this.clipListener = new ActionListener () {
    		public void actionPerformed ( ActionEvent event ) {
    			// Extract the target menu item
				MenuItem target = ( MenuItem ) event.getSource ();
    			// Copy the contents to the clipboard
				String value = MenuTray.getInstance ().extract ( target );
				ClipboardManager.write ( target.getActionCommand () );
			}
		};
		// Initialize and define the disconnect listener
		this.disconnectListener = new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Try to do this, since the peer could have already disconnected
				try {
	    			// Extract the target menu item, and get the hash string
					MenuItem target = ( MenuItem ) event.getSource ();
					String hash = target.getActionCommand ();
					// Get the preferences, menu tray, history, and packet instance
					Preferences preferences = Preferences.getInstance ();
					Packet packet = Packet.getInstance ();
					MenuTray menu = MenuTray.getInstance ();
					History history = History.getInstance ();
					// Ask user if they really want to disconnect
					if ( UserInterface.confirmDisconnectPeerConnection () ) {
						// Remove the peer from the list
						preferences.removePeer ( hash );
						// Update the menu
						menu.update ( history.export () );
						// Send disconnect packet to peer
						Server.sendTo ( hash, packet.disconnect () );
						// Close the connection
						Server.close ( hash );
					}
				}
				// Catch null pointer exception, if the connection is already null
				catch ( NullPointerException exception ) {}
			}
		};
		// Get the icon resource path
		String url = Preferences.IconPath;
		// Set the icon
		Image icon = Toolkit.getDefaultToolkit ().getImage ( getClass ().getResource ( url ) );
		// Initialize the clips hash map
		this.clips = new LinkedHashMap <String, MenuItem> ();
		// Check to see that the SystemTray class is supported in this version of java
		if ( SystemTray.isSupported () ) {
			// Initialize the system tray
			SystemTray tray = SystemTray.getSystemTray ();
			// Initialize the popup menu
			this.popup = new PopupMenu ();
			// Load the tray icon using the predefined icon
			TrayIcon trayIcon = new TrayIcon ( icon );
			// Create all the menu icons to the options hash map
			this.update ( this.history.export () );
			// Bind the tray icon with the popup menu
			trayIcon.setPopupMenu ( this.popup );
			// Try to add the tray icon to the system tray
			try {
				// Add the tray icon to the tray
				tray.add ( trayIcon );
			}
			// Catch any exceptions
			catch ( Exception exception ) {}
		}
	}

	/**
	 * This static class is in charge of only making one instance of this class since it is a
	 * designed using the singleton design pattern.
	 * @return 	MenuTray 							The singleton instance
	 * @static
	 */
	public static MenuTray getInstance () {
		// See if there are any instances initialized
		if ( MenuTray.instance == null ) {
			// If there are none, then initialize one
			MenuTray.instance = new MenuTray ();
		}
		// Return that instance
		return MenuTray.instance;
	}

	/**
	 * This function extracts the original clip string that was binded to the linked hash map clips,
	 * and it returns it based on matching the menu item instance address.  If it is not found, then
	 * null is returned.
	 * @param   MenuItem        target              The target menu item to match against
	 * @return  String                              Returns the clip in original form
	 */
	private String extract ( MenuItem target ) {
		// Initialize an iterator for the clips hash map
		Iterator <Entry <String, MenuItem>> iterator = this.clips.entrySet ().iterator ();
		// Loop through all entries within the options hash map
		while ( iterator.hasNext () ) {
			// Initialize an entry pair
			Entry <String, MenuItem> pair = iterator.next ();
			// Check if the target string matches
			if ( target == pair.getValue () ) {
				// Return that pairs key
				return pair.getKey ();
			}
			// Remove the iterator
			iterator.remove ();
		}
		// By default return null
		return null;
	}

	/**
	 * This function updates the menu items based on the array list of string that are passed to it.
	 * These strings are in the proper order.
	 * @param   ArrayList <String>      items       List of clips in manager to display, in order
	 * @return  void
	 */
	public synchronized void update ( ArrayList <String> items ) {
		// Clear the clips array
		this.clips.clear ();
		// Loop through all the exported items
		int index = 0;
		for ( String item : items ) {
			// Initialize the current menu item
			MenuItem menuItem;
			// If the index is less than or equal to 9, then add a shortcut
			if ( index <= 9 ) {
				// Initialize the item, and increment the index
				MenuShortcut shortcut = new MenuShortcut ( ( char ) index + 48 );
				menuItem = new MenuItem ( ClipboardManager.shorten ( item ), shortcut );
				index++;
			}
			// Otherwise, initialize normally
			else {
				// Initialize the menu item without shortcut
				menuItem = new MenuItem ( ClipboardManager.shorten ( item ) );
			}
			// Put it into the menu
			this.clips.put ( item, menuItem );
			// Add the action listener
			menuItem.addActionListener ( this.clipListener );
		}
		// Remove all items from the popup menu
		this.popup.removeAll ();
		// Initialize an iterator for the clips hash map
		Iterator <Entry <String, MenuItem>> iterator = this.clips.entrySet ().iterator ();
		// Loop through all entries within the options hash map
		while ( iterator.hasNext () ) {
			// Initialize an entry pair
			Entry <String, MenuItem> pair = iterator.next ();
			this.popup.add ( pair.getValue () );
			pair.getValue ().setActionCommand ( pair.getKey () );
		}
		// Check to see if there is any clips
		if ( items.size () == 0 ) {
			// Create a disabled menu item
			MenuItem item = new MenuItem ( "Nothing here yet ..." );
			item.setEnabled ( false );
			this.popup.add ( item );
		}
		// Create all the menu icons to the options hash map
		MenuItem quit = new MenuItem ( "Quit", new MenuShortcut ( KeyEvent.VK_Q ) );
		MenuItem about = new MenuItem ( "About" );
		MenuItem preferences = new MenuItem ( "Preferences", new MenuShortcut ( KeyEvent.VK_P ) );
		MenuItem connect = new MenuItem ( "Connect", new MenuShortcut ( KeyEvent.VK_C ) );
		// Add the options to the popup menu
		this.popup.addSeparator ();
		// Get the number of peers from settings
		JSONArray peers = this.preferences.getPeers ();
		// If there is at lease one peer add this menu
		if ( peers.size () > 0 ) {
			// Initialize the disconnect menu
			Menu disconnect = new Menu ( "Disconnect" );
			// Loop through all the peers
			for ( Object peerObject : peers ) {
				// Add the item to the menu
				JSONObject peer = ( JSONObject ) peerObject;
				String address = peer.get ( "address" ).toString ();
				String port = peer.get ( "port" ).toString ();
				MenuItem peerItem = new MenuItem ( address + ":" + port );
				peerItem.setActionCommand ( peer.get ( "hash" ).toString () );
				peerItem.addActionListener ( this.disconnectListener );
				disconnect.add ( peerItem );
			}
			// Add the disconnect menu into the popup menu
			this.popup.add ( disconnect );
			// Initialize the two options we have fro clearing
			MenuItem networkClear = new MenuItem ( "Network" );
			MenuItem localClear = new MenuItem ( "Local" );
			Menu clear = new Menu ( "Clear" );
			// Add the clear options to clear menu and bind listeners to them
			clear.add ( localClear );
			clear.add ( networkClear );
			localClear.addActionListener ( this );
			networkClear.addActionListener ( this );
			// Append the menu to the popup menu
			this.popup.add ( clear );
		}
		else {
			MenuItem clear = new MenuItem ( "Clear" );
			this.popup.add ( clear );
			clear.addActionListener ( this );
		}
		// Add the rest of the menu items
		this.popup.addSeparator ();
		this.popup.add ( preferences );
		this.popup.add ( connect );
		this.popup.add ( about );
		this.popup.addSeparator ();
		this.popup.add ( quit );
		// Add an action listener to the items
		quit.addActionListener ( this );
		about.addActionListener ( this );
		preferences.addActionListener ( this );
		connect.addActionListener ( this );
	}

	/**
	 * This function is here to handle when the menu items are clicked.  It serves the appropriate
	 * functions that relate to the menu item that was clicked.
	 * @var     ActionEvent     event               The caught event form the action listener
	 * @return  void
	 */
	public void actionPerformed ( ActionEvent event ) {
		// Extract the target menu item
		MenuItem target = ( MenuItem ) event.getSource ();
		// Switch Between the sources value
		switch ( target.getActionCommand () ) {
			// Handle the quit action
			case "Quit":
				// Close the socket server and all peer connections
				Server.close ();
				// Exit application
				System.exit ( 0 );
				break;
			// Handle the preferences action
			case "Preferences":
				Preferences.getInstance ().setVisible ( true );
				break;
			// Handle the clear action
			case "Clear":
			case "Local":
				// Ask user if they are sure they want to clear
				if ( UserInterface.confirmLocalClear () ) {
					// Clear the history and update the menu items
					this.history.clear ();
					this.update ( this.history.export () );
				}
				break;
			case "Network":
				// Ask user if they are sure they want to clear
				if ( UserInterface.confirmNetworkClear () ) {
					// Clear the history and update the menu items
					this.history.clear ();
					this.update ( this.history.export () );
					// Send the packet to all connections
					Server.sendAll ( this.packet.networkClear () );
				}
				break;
			// Handle the connect peer action
			case "Connect":
				// Ask user to input information about peer
				Tuple peer = UserInterface.peerConnectionInitialization ();
				// If the information was passed and was valid
				if ( peer != null ) {
					// Initialize the hash variable so it was in this scope
					String hash = null;
					// Attempt to connect to peer
					try {
						// Get the server instance
						Server server = Server.getInstance ();
						// Connect and get that hash
						hash = server.connect (
							peer.first ().toString (),
							( int ) peer.second ()
						);
					}
					// Catch any errors and ignore them
					catch ( Exception exception ) {}
					// Check to see if the hash is null and was uninitialized
					if ( hash == null ) {
						// Create a new hash
						hash = Server.hash ( 32 );
					}
					// Add this request to the requests list
					this.preferences.addRequest (
						peer.first ().toString (),
						( int ) peer.second (),
						hash
					);
				}
				break;
			// Handle the about action
			case "About":
				System.out.println ( "Displaying about menu" );
				break;
		}
	}

}