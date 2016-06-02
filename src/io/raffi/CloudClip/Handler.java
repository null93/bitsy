package io.raffi.CloudClip;

import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Handler {

	private Preferences preferences;

	private MenuTray menu;

	private History history;

	private Packet packet;

	private Connection connection;

	private String address;

	private int port;

	public Handler ( Connection connection, String json ) {
		// Save the address and port internally as well as the socket
		this.connection = connection;
		this.address = this.connection.getSocket ().getInetAddress ().getHostAddress ().toString ();
		this.port = this.connection.getSocket ().getLocalPort ();
		// Attempt to get instances of helper classes
		try {
			// Initialize classes that will be used beyond this scope within this class
			this.preferences = Preferences.getInstance ();
			this.menu = MenuTray.getInstance ();
			this.history = History.getInstance ();
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
			case "handshake-request":
				// Check to see if the user wants to connect with this peer
				if ( UserInterface.peerConnectionAuthorization ( this.address, this.port ) ) {
					// Save the connection hash id and assign it to the connection
					String hash = request.get ( "hash" ).toString ();
					this.connection.hash = hash;
					// Add the peer locally to settings file
					this.preferences.addRequest ( this.address, this.port, hash );
					this.preferences.addPeer ( hash );
					// Send the peer your information
					this.connection.send ( this.packet.acceptHandshake ( hash ) );
				}
				break;
			// This handles the the successful response to connect to peer
			case "handshake-accept":
				// Save the connection hash id and save the hash
				String hash = request.get ( "hash" ).toString ();
				// Add this user to the peers list
				this.preferences.addPeer ( hash );
				// Update the menu to be able to disconnect
				this.menu.update ( this.history.export () );
				break;
			case "handshake-reject":


				break;
			// This handles when a clip is sent to us
			case "clip":
				// Extract the data string
				String data = request.get ( "data" ).toString ();
				// Check to see that it is not a repeat
				if ( !ClipboardManager.read ().equals ( data ) && data != null ) {
					// Write the data to the clipboard and update the menu
					ClipboardManager.write ( data );
					this.menu.update ( this.history.export () );
				}
				break;
		}
	}

}