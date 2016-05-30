package io.raffi.CloudClip;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Set;

/**
 * History.java - This class takes care of loading, saving, and updating the JSON data file that
 * contains all the currently logged clips.
 * @version     1.0.0
 * @package     CloudClip
 * @category    History
 * @author      Rafael Grigorian
 * @license     GNU Public License <http://www.gnu.org/licenses/gpl-3.0.txt>
 */
@SuppressWarnings ( "unchecked" )
public class History {

	/**
	 * This data member holds the instance of the History class, since this class will be a
	 * singleton.  It is singleton, because it only needs write access to the clips.json file once.
	 * @var 	History 		instance 			The one instance of the History class
	 */
	private static History instance;

	/**
	 * This data member contains the clips array that is defined in the data json file defined by
	 * the preferences class.
	 * @var     JSONObject      data                The JSON data contents with all the saved clips
	 */
	private JSONObject data;

	/**
	 * This constructor makes sure the predefined data file is available and if its not then it
	 * makes it.  If it is it loads it into the internally defined JSONObject data member.
	 * @throws  Exception
	 */
	private History () throws CloudClipException {
		// Check to see if the data directory exists
		File folder = new File ( Preferences.DataFolder );
		if ( !folder.exists () ) {
			// Make it if it doesn't exist
			folder.mkdir ();
		}
		// Initialize the file descriptor
		File history = new File ( Preferences.ClipsDataPath );
		// Check to see if the file exists
		if ( history.exists () && !history.isDirectory () ) {
			// Attempt to load all contents of the clips file
			try {
				// Get the file contents
				String contents = new Scanner ( new File ( Preferences.ClipsDataPath ) )
				.useDelimiter ("\\Z").next ();
				// Initialize json parser
				JSONParser parser = new JSONParser ();
				// Try to parse the json string
				try {
					// Parse the JSON string, and save internally
					this.data = ( JSONObject ) parser.parse ( contents );
				}
				// Attempt to catch any parse exceptions
				catch ( Exception exception ) {
					// Throw custom exception
					throw new CloudClipException (
						"Could not open the history file in: " + Preferences.ClipsDataPath
					);
				}
			}
			// Catch any errors while scanning
			catch ( Exception exception ) {
				// Throw our own exception
				throw new CloudClipException ( "Cannot open file contents for the clips.json file" );
			}
		}
		else {
			// Initialize an empty JSON array
			this.clear ();
		}
	}

	public static History getInstance () throws CloudClipException {
		// Check to see if the instance of this class is already initialized
		if ( History.instance == null ) {
			// The initialize an instance
			History.instance = new History ();
		}
		// Return the History singleton instance
		return History.instance;
	}

	/**
	 * This function returns the current timestamp that is represented by UNIX time in milliseconds.
	 * @return  int                                 UNIX milliseconds timestamp
	 */
	private int timestamp () {
		// Return the UNIX time in milliseconds
		return ( int ) ( System.currentTimeMillis () / 1000L );
	}

	/**
	 * This function saves the current data structure of the clip history into the file defined by
	 * the preferences class.
	 * @return  void
	 */
	private void save () {
		// Check to see if the data directory exists
		File folder = new File ( Preferences.DataFolder );
		if ( !folder.exists () ) {
			// Make it if it doesn't exist
			folder.mkdir ();
		}
		// Try to write to history file
		try {
			// Initialize the print writer
			PrintWriter writer = new PrintWriter ( Preferences.ClipsDataPath, "UTF-8" );
			// Write the JSON object into the file
			writer.println ( this.data.toString () );
			// Close the writer
			writer.close ();
		}
		// Catch any exceptions
		catch ( Exception exception ) {
			// Report to user that there is a non fatal error
			System.out.println ( "Could not save history file in: " + Preferences.ClipsDataPath );
		}
	}

	/**
	 * This function uses the Collections classes sort function to sort the clips based on the
	 * timestamp values binded to it.
	 * @return  void
	 */
	public void sort () {
		// Sort the clips using the collections sort class function
		Collections.sort ( ( JSONArray ) this.data.get ("clips"), new Comparator <JSONObject> () {
			@Override
			public int compare ( JSONObject clip1, JSONObject clip2 ) {
				// Pull the information from both the clips
				String one = clip1.get ("timestamp").toString ();
				String two = clip2.get ("timestamp").toString ();
				// Return the comparison between both of them
				return two.compareTo ( one );
			}
		});
	}

	/**
	 * This function sets a clip into our data structure and saves it into the clips file.  It
	 * handles max number of clips to display and also updates the clips position based on if it was
	 * in the array already.
	 * @param   String          clip                The target clip to set into history and file
	 * @return  void
	 */
	public void set ( String clip ) {
		// Firstly sort the data array
		this.sort ();
		// Trim to the max size of items
		JSONArray clips = ( JSONArray ) this.data.get ("clips");
		while ( clips.size () > Preferences.MaxNumberOfClips ) {
			clips.remove ( clips.size () - 1 );
		}
		// Check if the value already exists in the set
		if ( this.find ( clip ) == -1 ) {
			// Check to see if we need to make more room
			if ( clips.size () >= Preferences.MaxNumberOfClips ) {
				// Make room for the target
				clips.remove ( clips.size () - 1 );
			}
			// Initialize a new JSONObject based on the data
			JSONObject entry = new JSONObject ();
			// Append the data to the object
			entry.put ( "timestamp", this.timestamp () );
			entry.put ( "clip", clip );
			// Append the entry to the clips array
			clips.add ( entry );
		}
		else {
			// Otherwise, we want to update the timestamp
			this.touch ( clip );
		}
		// Save the data
		this.save ();
	}

	/**
	 * This function returns the index of the target clip in the clips array.  If it doesn't exist
	 * them -1 will be returned.
	 * @param   String          target              The clip to match against in our search
	 * @return  int                                 The index of the target string
	 */
	public int find ( String target ) {
		// Pull the clips array from the data JSON object
		JSONArray clips = ( JSONArray ) this.data.get ("clips");
		// Loop through all of the elements within the array
		for ( int i = 0; i < clips.size (); i++ ) {
			// Pull the element using the index
			JSONObject element = ( JSONObject ) clips.get ( i );
			// Check to see if the clips match
			if ( target.equals ( element.get ("clip").toString () ) ) {
				// If it does, then return the index
				return i;
			}
		}
		// Otherwise, by default return -1
		return -1;
	}

	/**
	 * This function, based on the passed clip as the target, traverses through all the clips, and
	 * if it finds a match, updates the timestamp to a brand new one.
	 * @param   String          target              The target clip to match against
	 * @return  void
	 */
	public void touch ( String target ) {
		// Get the clips internally in the JSON root object
		JSONArray clips = ( JSONArray ) this.data.get ("clips");
		// Loop through all the clips in the array
		for ( int i = 0; i < clips.size (); i++ ) {
			// Get the traversing element
			JSONObject element = ( JSONObject ) clips.get ( i );
			// If the target matches the clip
			if ( target.equals ( element.get ("clip").toString () ) ) {
				// Update the timestamp to the current one and return to break out of loop
				element.put ( "timestamp", this.timestamp () );
				return;
			}
		}
	}

	/**
	 * This function clears the clips array and is also used to initialize the data array if no file
	 * is found initially.
	 * @return  void
	 */
	public void clear () {
		// Reset data structure internally
		this.data = new JSONObject ();
		this.data.put ( "clips", new JSONArray () );
		// Save the file
		this.save ();
	}

	/**
	 * This function traverses through the clips and puts them into an array list and returns it.
	 * @return  ArrayList <String>                  The resulting array list to be returned
	 */
	public ArrayList <String> export () {
		// Initialize a new array list of strings
		ArrayList <String> items = new ArrayList <String> ();
		// Sort the data clips
		this.sort ();
		// Traverse through all the clips in the data JSONOnject
		for ( Object item : ( JSONArray ) this.data.get ( "clips" ) ) {
			// Add them to the array list
			items.add ( ( ( JSONObject ) item ).get ("clip").toString () );
		}
		// Return the items array list
		return items;
	}

}