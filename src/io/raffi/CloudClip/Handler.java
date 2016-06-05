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

	public Handler ( Connection connection, String json ) {
		// Save the address and port internally as well as the socket
		this.connection = connection;
		this.address = this.connection.getSocket ().getInetAddress ().getHostAddress ().toString ();
		// Initialize classes that will be used beyond this scope within this class
		this.preferences = Preferences.getInstance ();
		this.menu = MenuTray.getInstance ();
		this.history = History.getInstance ();
		this.packet = Packet.getInstance ();
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
		// Extract the hash from the packet, hash should always be included
		String hash = request.get ( "hash" ).toString ();
		// Bind the hash to the connection
		this.connection.hash = hash;
		// Switch between the request types
		switch ( request.get ( "type" ).toString () ) {
			// This handles the initial request to be added to external peer list
			case "handshake-request":
				// Get the port from the JSON packet
				int port = Integer.parseInt ( request.get ( "port" ).toString () );
				// Check to see if peer is already been established
				Boolean isPeer = this.preferences.isPeer ( hash );
				// Check to see if the user wants to connect with this peer
				if ( isPeer || UserInterface.peerConnectionAuthorization ( this.address, port ) ) {
					// Check to see is peer has been added already ( we don't want duplicates )
					if ( !isPeer ) {
						// Add the peer locally to settings file
						this.preferences.addRequest ( this.address, port, hash );
						this.preferences.addPeer ( hash );
					}
					// Otherwise, update the address and port, in case we changed the port
					else {
						// Update the address and port
						this.preferences.updatePeer ( this.address, port, hash );
					}
					// Save the clips from the packet to the file
					JSONArray peerClips = Packet.parseArray ( request.get ( "clips" ).toString () );
					// Check to see if we want to merge on connect
					if ( Preferences.MergeClipboardsOnConnect ) {
						// Merge the clipboards
						this.history.merge ( peerClips );
					}
					// Send the peer your information
					String clipboardID = request.get ( "clipboard-id" ).toString ();
					this.connection.send ( this.packet.acceptHandshake ( clipboardID ) );
					// Check preferences to see if we should propagate this clipboard to peers
					if ( Preferences.PropagateAllPeers ) {
						// Save the clipboard id to be the last saved one
						this.preferences.setLastClipboard ( clipboardID );
						// Send all peers the connected peers clipboard
						Server.sendAllBut (
							this.connection,
							this.packet.syncClipboard ( clipboardID, peerClips )
						);
					}
					// Update the menu tray
					this.menu.update ( this.history.export () );
				}
				// Otherwise send a rejection packet so everything was clean
				else {
					// Sent the peer the rejection packet
					this.connection.send ( this.packet.rejectHandshake () );
					// Close the connection with the peer
					this.connection.close ();
				}
				break;
			// This handles the the successful response to connect to peer
			case "handshake-accept":
				// Check to see if the peer has already been established
				if ( !this.preferences.isPeer ( hash ) ) {
					// Add this user to the peers list
					this.preferences.addPeer ( hash );
				}
				// Check to see if we want to merge on connect
				if ( Preferences.MergeClipboardsOnConnect ) {
					// Merge the clipboards
					this.history.merge ( Packet.parseArray ( request.get ( "clips" ).toString () ) );
					// Check preferences to see if we should propagate this clipboard to peers
					if ( Preferences.PropagateAllPeers ) {
						// Save the clips from the packet to the file and the clipboard id
						JSONArray peerClips = Packet.parseArray ( request.get ( "clips" ).toString () );
						String clipboardID = request.get ( "clipboard-id" ).toString ();
						// Save the clipboard id to be the last saved one
						this.preferences.setLastClipboard ( clipboardID );
						// Send all peers the connected peers clipboard
						Server.sendAllBut (
							this.connection,
							this.packet.syncClipboard ( clipboardID, peerClips )
						);
					}
				}
				// Update the menu to be able to disconnect
				this.menu.update ( this.history.export () );
				break;
			case "handshake-reject":
				// Remove the connection from the requests list internally
				this.preferences.removeRequest ( hash );
				// Remove the connection from the peer in case peer removed us while we were disconnected
				this.preferences.removePeer ( hash );
				// Update the menu to potentially reflect the removal of the peer
				this.menu.update ( this.history.export () );
				// Close the connection with peer
				this.connection.close ();
				break;
			// This handles when a clip is sent to us
			case "clip":
				// First check to see that the peer is actually a peer
				if ( this.preferences.isPeer ( hash ) ) {
					// Extract the data string
					String data = request.get ( "data" ).toString ();
					// Check to see that it is not a repeat
					if ( !ClipboardManager.read ().equals ( data ) && data != null ) {
						// Check to see how we should sync the clip
						if ( Preferences.PropagateAllPeers ) {
							// Mock a copy of clip for all peers to get
							ClipboardManager.write ( data );
						}
						// Otherwise, just sync to our history
						else {
							// Set the item to be in our history only
							this.history.set ( data );
						}
						// Update the menu
						this.menu.update ( this.history.export () );
					}
				}
				break;
			// This is the case that handles clearing the clipboard across the peer network
			case "clear":
				// First check to see that this peer is part of our peers list
				if ( this.preferences.isPeer ( hash ) ) {
					// Clear the history and update the menu items
					this.history.clear ();
					this.menu.update ( this.history.export () );
				}
				break;
			// This case disconnects a peer connection
			case "disconnect":
				// First check to see that this peer is part of our peers list
				if ( this.preferences.isPeer ( hash ) ) {
					// Remove the peer from the list
					this.preferences.removePeer ( hash );
					// Update the menu tray
					this.menu.update ( this.history.export () );
					// Close this connection
					this.connection.close ();
				}
				break;
			// This case handles when a new clipboard is synced between the peers
			case "clipboard":
				// First check to see if we want to sync this data
				if ( Preferences.PropagateAllPeers ) {
					// Save the clips from the packet to the file and the clipboard id
					JSONArray clips = Packet.parseArray ( request.get ( "data" ).toString () );
					String clipboardID = request.get ( "clipboard-id" ).toString ();
					// Check to see if we synced this clipboard before
					if ( !clipboardID.equals ( this.preferences.getLastClipboard () ) ) {
						// Save the clipboard id to be the last saved one
						this.preferences.setLastClipboard ( clipboardID );
						// Merge the clipboard locally
						this.history.merge ( clips );
						// Send all peers the connected peers clipboard
						Server.sendAllBut (
							this.connection,
							this.packet.syncClipboard ( clipboardID, clips )
						);
					}
				}
		}
	}

}