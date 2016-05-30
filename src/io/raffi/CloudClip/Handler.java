package io.raffi.CloudClip;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Handler {

	private Preferences preferences;

	private Packet packet;

	private String address;

	private int port;

	public Handler ( String address, int port, String json ) {
		// Save the address and port internally
		this.address = address;
		this.port = port;
		// Attempt to get instances of helper classes
		try {
			// Initialize classes that will be used beyond this scope within this class
			this.preferences = Preferences.getInstance ();
			this.packet = Packet.getInstance ();
		}
		// Catch any exception that might be thrown
		catch ( Exception exception ) {
			System.out.println ( "Could not get instances in Handler class." );
		}
		// First we want to try to parse the json response
		try {
			// Initialize json parser
			JSONParser parser = new JSONParser ();
			// Parse the JSON string
			JSONObject contents = ( JSONObject ) parser.parse ( json );
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
		switch ( request.get ( "type" ).toString () ) {
			// This handles the initial request to be added to external peer list
			case "handshake":
				// Check to see if the user wants to connect with this peer
				if ( UserInterface.peerConnectionAuthorization ( this.address, this.port ) ) {
					System.out.println ( "Accepted Connect Peer" );
				}
				break;
		}
	}

}