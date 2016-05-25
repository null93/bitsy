package io.raffi.CloudClip;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

/**
 * ClipboardManager.java - This class polls for changes in the system clipboard, and if any changes
 * occur that are of type string, then it updates the History class and also updates all the menu
 * items in the popup menu.
 * @version     1.0.0
 * @package     CloudClip
 * @category    ClipboardManager
 * @author      Rafael Grigorian
 * @license     GNU Public License <http://www.gnu.org/licenses/gpl-3.0.txt>
 */
public class ClipboardManager extends Thread {

	/**
	 * This data member holds an instance of the History class, we will use it to set the values
	 * when new strings are found in the clipboard and also to extract the currently available
	 * strings in the clipboard manager.  It is static so we can access it in the run () function.
	 * @var     History         history             An instance of the History class
	 * @static
	 */
	private static History history;

	/**
	 * This data member holds an instance of the MenuTray class, we will use it to set the values
	 * when new strings are found in the clipboard. It is static so we can access it in the run ()
	 * function.
	 * @var     MenuTray        menu                An instance of the History class
	 * @static
	 */
	private static MenuTray menu;

	/**
	 * This data member saves last read string from the clipboard.  It is used when we poll for
	 * changes within the run function.
	 * @var     String          current             The last string read from clipboard
	 */
	private String current;

	/**
	 * This constructor takes in the history object and the menu object and it saves it internally.
	 * It also reads the current string in the system clipboard, as well as starts the thread so we
	 * can poll for changes in the clipboard.
	 * @param   History         history             Passed instance of the History class to save
	 * @param   MenuTray        menu                Passed instance of the MenuTray class to save
	 */
	public ClipboardManager ( History history, MenuTray menu ) {
		// Set the current string in the clipboard
		this.current = ClipboardManager.read ();
		// Save the history and menu instances internally
		ClipboardManager.history = history;
		ClipboardManager.menu = menu;
		// Start the thread
		this.start ();
	}

	/**
	 * This function is a static and takes in a string that we will be setting into the system
	 * clipboard.  If any exceptions are thrown we will try to ignore them.
	 * @param   String          data                This is the string we want to save to clipboard
	 * @void    void
	 * @static
	 */
	public static void write ( String data ) {
		// Get the system clipboard instance
		Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		// Try to set the contents
		try {
			// Set the content of the clipboard to be of the passed data string
			clipboard.setContents ( new StringSelection ( data ), null );
		}
		// If any exception arises, ignore it
		catch ( Exception exception ) {}
	}

	/**
	 * This function reads the current string in the system clipboard and returns it.  If any errors
	 * occur and exceptions thrown, we will try to ignore them.
	 * @return  String                              The current string in the system clipboard
	 * @static
	 */
	public static String read () {
		// Get the system clipboard instance
		Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		// Attempt to read the system clipboard
		try {
			// Return the string flavor from the clipboard
			return ( String ) clipboard.getData ( DataFlavor.stringFlavor );
		}
		// If any exception arises, ignore it
		catch ( Exception exception ) {}
		// By default return null
		return null;
	}
	/**
	 * This function is static and takes in a string, and tries to clean it so it would be better
	 * presented in the popup menu.  It is displayed as the value in the menu item instances.
	 * @param   String          target              The target string to shorten and clean
	 * @return  String                              The cleaned preview string for the menu item
	 * @static
	 */
	public static String shorten ( String target ) {
		// Clean the string so we don't have to display a bunch of whitespace
		target = target.replaceAll ( "\\s+", " " );
		target = target.trim ();
		// Cut off long strings, based on the preferences properties
		String str = target.substring ( 0, Math.min ( target.length (), Preferences.ClipCutoff ) );
		// If the string is really long, we want to tack on a ... on the end of it
		if ( target.length () > Preferences.ClipCutoff ) {
			str += "...";
		}
		// Return the resulting string
		return str;
	}

	/**
	 * This class is here because we implement the Thread class and in this class we want to monitor
	 * any changes to the system clipboard by polling the system clipboard for changes.
	 * @return  void
	 */
	public void run () {
		// Loop forever
		while ( true ) {
			// Get the current string saved in the clipboard
			String contents = ClipboardManager.read ();
			// Check to see that the contents are not null and does not match last saved value
			if ( !this.current.equals ( contents ) && contents != null ) {
				// Save the current string in the clipboard into the last seen value
				this.current = ClipboardManager.read ().toString ();
				// Save this value using the manager
				ClipboardManager.history.set ( this.current );
				// Export all the values form the History class
				ArrayList <String> items = ClipboardManager.history.export ();
				// Update it using the menu class
				ClipboardManager.menu.update ( items );
			}
			// Attempt to put the thread to sleep
			try {
				// Sleep for a short period of time
				ClipboardManager.sleep ( 100 );
			}
			// IF any exceptions occur, we will ignore them
			catch ( Exception exception ) {}
		}
	}

}