package io.raffi.CloudClip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@SuppressWarnings ( { "serial", "unchecked" } )
public class Connection implements Runnable {

	private Socket socket;

	private PrintWriter outgoing;

	private volatile BufferedReader incoming;

	private String message;

	public String hash = null;

	private Connection.State state = State.OPEN;

	public enum State {
		OPEN, CLOSED;
	}

	public Connection ( Socket socket ) {
		// Save the socket connection
		this.socket = socket;
		System.out.println ( socket.getRemoteSocketAddress ().toString () );
		try {
			// Initialize the incoming and outgoing data stream
			this.outgoing = new PrintWriter ( this.socket.getOutputStream (), true );
			this.incoming = new BufferedReader ( new InputStreamReader ( this.socket.getInputStream () ) );
			outgoing.flush ();
		}
		// Attempt to catch any exceptions
		catch ( Exception exception ) {}
	}

	public void send ( String data ) {
		// Parse the packet
		JSONObject json = Packet.parse ( data );
		// Append the connection hash to it
		json.put ( "hash", this.hash );
		// Flush the buffer and print to it
		this.outgoing.flush ();
		this.outgoing.println ( json.toString () );
	}

	public void close () {
		// Mark this connection as closed, to kill the thread that listens to input
		this.state = State.CLOSED;
		// Try to close all files
		try {
			// Close the incoming / outgoing files as well as the socket
			this.outgoing.close ();
			this.incoming.close ();
			this.socket.close ();
		}
		// Ignore any exceptions
		catch ( Exception exception ) {}
	}

	public Socket getSocket () {
		// Return the socket for this connection
		return this.socket;
	}

	public void run () {
		// Keep running until we mark the connection as closed
		while ( this.state == State.OPEN ) {
			// Try to read the message that was sent to us
			try {
				// Read all the lines until there is nothing to read
				while ( ( this.message = this.incoming.readLine () ) != null ) {
					// For debugging purposes, print out the message
					System.out.println ( "The response from server is: " + this.message );
					// Spawn a new handler instance to handle the message
					new Handler ( this, this.message );
				}
			}
			// Catch any exceptions that are thrown and ignore them
			catch ( Exception exception ) {}
		}
	}

}