package io.raffi.CloudClip;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JFrame;

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

	protected static int MaxNumberOfClips = 50;

	protected static int ClipCutoff = 40;

	private Preferences () throws Exception {
		// Initialize the JFrame
		super ( "CloudClip Preferences" );

		// Get the operating system
		String os = System.getProperty ( "os.name" );
		// Based on the operating system change the static definitions
		if ( os.contains ( "windows" ) ) {
			Preferences.IconPath = "Windows.Icon.png";
		}

		// Initialize the
		File directory = new File ( DataFolder );
		File settings = new File ( SettingsDataPath );
		if ( !directory.exists () ) {
			directory.mkdir ();
		}
		if ( !settings.exists () ) {
			settings.createNewFile ();
			this.contents = new JSONObject ();
			this.contents.put ( "max_number_of_clips", Preferences.MaxNumberOfClips );
			this.contents.put ( "ClipCutoff", Preferences.ClipCutoff );
			this.contents.put ( "peers", new JSONArray () );
			this.contents.put ( "requests", new JSONArray () );
			this.save ();
		}
		else {
			// Get the file contents
			String contents = new Scanner ( new File ( Preferences.SettingsDataPath ) )
			.useDelimiter ("\\Z").next ();
			// Initialize json parser
			JSONParser parser = new JSONParser ();
			// Try to parse the json string
			try {
				// Parse the JSON string, and save internally
				this.contents = ( JSONObject ) parser.parse ( contents );
			}
			// Attempt to catch any parse exceptions
			catch ( Exception exception ) {
				// Throw custom exception
				throw new CloudClipException (
					"Could not open the settings file in: " + Preferences.SettingsDataPath
				);
			}
		}
	}

	public static Preferences getInstance () throws Exception {
		if ( Preferences.instance == null ) {
			Preferences.instance = new Preferences ();
		}
		return Preferences.instance;
	}

	public void save () {
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
		catch ( Exception exception ) {
			// Report to user that there is a non fatal error
			System.out.println ( "Could not save settings file in: " + Preferences.SettingsDataPath );
		}
	}

}