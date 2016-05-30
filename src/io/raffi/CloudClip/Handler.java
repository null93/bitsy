package io.raffi.ClipCloud;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Handler {

	private Preferences preferences;

	private Packet packet;

	public Handler ( String json ) {
		// Initialize classes that will be used beyond this scope within this class
		this.preferences = Preferences.getInstance ();
		this.packet = Packet.getInstance ();
		// First we want to try to parse the json response
		try {
			// Parse the JSON string
			JSONObject contents = ( JSONObject ) parser.parse ( contents );
			// Assign function call to take care of request
			this.assign ( contents );
		}
		// If anything occurs, simply print to console
		catch ( Exception exception ) {
			// Print generic error to console
			System.out.println ( "Handler could not parse JSON message: " + json.toString () );
		}
	}

	private void assign ( JSONObject request ) {
		// Switch between the request types
		switch ( request.get ( "type" ) ) {
			// This handles the initial request to be added to external peer list
			case "handshake":
				// Check to see if the user wants to connect with this peer
				if ( UserInterface.peerConnectionAuthorization () ) {
					System.out.println ( "Accepted Connect Peer" );
				}
				break;
		}
	}

}