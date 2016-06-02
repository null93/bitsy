package io.raffi.CloudClip;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JFrame;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import com.bulenkov.iconloader.IconLoader;

@SuppressWarnings ( { "serial", "unchecked" } )
public class Preferences extends JFrame {

	private static Preferences instance = null;

	private JSONObject contents;

	//private static String Home = System.getProperty ( "user.home" );
	//private static String Support = "/Library/Application Support/CloudClip/";
	//protected static String DataFolder = Home + Support;
	protected static String DataFolder = "./";

	//protected static String ClipsDataPath = Home + Support + "clips.json";
	protected static String ClipsDataPath = "./clips.json";

	//protected static String SettingsDataPath = Home + Support + "settings.json";
	protected static String SettingsDataPath = "./settings.json";

	protected static String IconPath = "Mac.Icon.png";

	protected static int Port = 10009;

	protected static int MaxNumberOfClips = 50;

	protected static int ClipCutoff = 40;

	protected static Boolean Sync = true;

	protected static Boolean PropagateAllPeers = false;

	protected static Boolean MergeClipboardsOnConnect = true;

	private Preferences () {
		// Initialize the JFrame
		super ( "CloudClip Preferences" );
	    setSize(500, 500);
	    final JPanel panel = new JPanel(new BorderLayout());
	    getContentPane().add(panel);
	    JPanel bottom = new JPanel(new BorderLayout());
	    panel.add(bottom, BorderLayout.CENTER);
	    JLabel disabledButton = new JLabel(IconLoader.getIcon("example.png"));
	    bottom.add(disabledButton, BorderLayout.CENTER);
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Get the operating system
		String os = System.getProperty ( "os.name" );
		// Based on the operating system change the static definitions
		if ( os.contains ( "Windows" ) ) {
			// Change to be the windows icon system tray size and design
			Preferences.IconPath = "Windows.Icon.png";
		}

		// Initialize the
		File directory = new File ( DataFolder );
		File settings = new File ( SettingsDataPath );
		if ( !directory.exists () ) {
			directory.mkdir ();
		}
		if ( !settings.exists () ) {
			try {
				settings.createNewFile ();
				this.contents = new JSONObject ();
				this.contents.put ( "max_number_of_clips", Preferences.MaxNumberOfClips );
				this.contents.put ( "ClipCutoff", Preferences.ClipCutoff );
				this.contents.put ( "peers", new JSONArray () );
				this.contents.put ( "requests", new JSONArray () );
				this.save ();
			}
			catch ( Exception exception ) {}
		}
		else {
			// Try to extract and parse that data
			try {
				// Get the file contents
				String contents = new Scanner ( new File ( Preferences.SettingsDataPath ) )
				.useDelimiter ("\\Z").next ();
				// Initialize json parser
				JSONParser parser = new JSONParser ();
				// Parse the JSON string, and save internally
				this.contents = ( JSONObject ) parser.parse ( contents );
			}
			// Attempt to catch any exceptions
			catch ( Exception exception ) {}
		}
	}

	/**
	 * This static class is in charge of only making one instance of this class since it is a
	 * designed using the singleton design pattern.
	 * @return 	Preferences 						The singleton instance
	 * @static
	 */
	public static Preferences getInstance () {
		// Check to see if the instance is initialized
		if ( Preferences.instance == null ) {
			// If it isn't then initialize one
			Preferences.instance = new Preferences ();
		}
		// Return an instance of the Preferences class
		return Preferences.instance;
	}

	public void addRequest ( String address, int port, String hash ) {
		// Initialize a new JSON object
		JSONObject request = new JSONObject ();
		// Populate that object with the passed data
		request.put ( "address", address );
		request.put ( "port", port );
		request.put ( "hash", hash );
		// Append that request object into the requests JSON array
		( ( JSONArray ) this.contents.get ( "requests" ) ).add ( request );
		// Save it back into the preferences file
		this.save ();
	}

	public void removeRequest ( String hash ) {
		// Get the contents of the requests array
		JSONArray requests = ( JSONArray ) this.contents.get ( "requests" );
		// Initialize the loop variable
		int index = 0;
		// Loop through all the requests
		for ( Object requestObject : requests ) {
			// Cast as a JSON object
			JSONObject request = ( JSONObject ) requestObject;
			// Check to see if the hash matches
			if ( request.get ( "hash" ).toString ().equals ( hash ) ) {
				// Remove the object from list, save the object back, and save into file
				requests.remove ( index );
				this.contents.put ( "requests", requests );
				this.save ();
				break;
			}
			// Increment the index variable
			index++;
		}
	}

	public void addPeer ( String hash ) {
		// Get the peers list and the requests list
		JSONArray peers = ( JSONArray ) this.contents.get ( "peers" );
		JSONArray requests = ( JSONArray ) this.contents.get ( "requests" );
		// Initialize the counter variable
		int index = 0;
		// Loop through all the requests
		for ( Object requestObject : requests ) {
			JSONObject request = ( JSONObject ) requestObject;
			// If the current request matches the passed one
			if ( request.get ( "hash" ).toString ().equals ( hash ) ) {
				// Add the request to be a peer and remove from the requests list
				peers.add ( request );
				requests.remove ( index );
				this.contents.put ( "peers", peers );
				this.contents.put ( "requests", requests );
				this.save ();
				break;
			}
			// Increment the counter variable
			index++;
		}
	}

	public JSONArray getPeers () {
		return ( JSONArray ) this.contents.get ( "peers" );
	}

	public JSONArray getRequests () {
		return ( JSONArray ) this.contents.get ( "requests" );
	}

	public Boolean isPeer ( String hash ) {
		for ( Object peerObject : ( JSONArray ) this.contents.get ( "peers" ) ) {
			JSONObject peer = ( JSONObject ) peerObject;
			if ( peer.get ( "hash" ).toString ().equals ( hash ) ) {
				return true;
			}
		}
		return false;
	}

	private synchronized void save () {
		// Check to see if the data directory exists
		File folder = new File ( Preferences.DataFolder );
		if ( !folder.exists () ) {
			// Make it if it doesn't exist
			folder.mkdir ();
		}
		// Try to write to history file
		try {
			// Initialize the print writer
			PrintWriter writer = new PrintWriter ( Preferences.SettingsDataPath, "UTF-8" );
			// Write the JSON object into the file
			writer.println ( this.contents.toString () );
			// Close the writer
			writer.close ();
		}
		// Catch any exceptions
		catch ( Exception exception ) {}
	}

}