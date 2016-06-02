package io.raffi.CloudClip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.SocketFactory;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Server {

	private static Server instance;

	private static Preferences preferences;

	private static ServerSocketFactory serverSocketFactory;

	private static SocketFactory socketFactory;

	private static ServerSocket server;

	private static volatile ArrayList <Connection> peers;

	private Server ( int incomingPort ) {
		// Get the class instances
		Server.preferences = Preferences.getInstance ();
		// Try to initialize a server socket
		try {
			// Initialize the server socket factory and socket factory
			Server.serverSocketFactory = SSLServerSocketFactory.getDefault ();
			Server.socketFactory = SSLSocketFactory.getDefault ();
			// Initialize the socket and clients array list
			// Server.server = Server.serverSocketFactory.createServerSocket ( incomingPort );
			Server.server = new ServerSocket ( incomingPort );
			Server.peers = new ArrayList <Connection> ();
			// Spawn a new thread to listen for connections and start the thread
			Thread listen = new Thread ( new Runnable () {
				public void run () {
					// Loop forever while listening for client connections
					while ( true ) {
						// Try to accept connections
						try {
							// Block until we get a client, and then append to the array
							Socket client = Server.server.accept ();
							Connection connection = new Connection ( client );
							new Thread ( connection ).start ();
							Server.peers.add ( connection );
						}
						// Catch any exceptions and ignore them
						catch ( Exception exception ) {}
					}
			    }
			});
			// Spawn listening thread and start it
			listen.start ();
		}
		// Catch and ignore any exceptions that are thrown
		catch ( Exception exception ) {}
	}

	public static Server getInstance () {
		// Check to see if the instance is initialized
		if ( Server.instance == null ) {
			// If it isn't then initialize one using the desired settings
			Server.instance = new Server ( Preferences.Port );
		}
		// Return the Server instance
		return Server.instance;
	}
	
	public synchronized String connect ( String outgoingAddress, int outgoingPort ) {
		// Pass to main function, but create hash on the fly
		return this.connect ( outgoingAddress, outgoingPort, Server.hash ( 32 ) );
	}

	public synchronized String connect ( String outgoingAddress, int outgoingPort, String hash ) {
		// Try to connect to a peer connection
		try {
			// Create a new socket
			//Socket client = Server.socketFactory.createSocket ( outgoingAddress, outgoingPort );
			Socket client = new Socket ( outgoingAddress, outgoingPort );
			// Pass it to the Connection class and append to the peers array
			Connection connection = new Connection ( client );
			new Thread ( connection ).start ();
			Server.peers.add ( connection );
			// Send a connection request to the server
			Packet packet = Packet.getInstance ();
			connection.hash = hash;
			connection.send ( packet.sendHandshake () );
			return connection.hash;
		}
		// Catch any exceptions that are thrown and return null as hash
		catch ( Exception exception ) {
			// By default return null
			return null;
		}
	}

	public void connectToPeers ( JSONArray peers ) {
		// Loop through all the peers
		for ( Object peerObject : peers ) {
			// Cast the peer to be of JSON Object
			JSONObject peer = ( JSONObject ) peerObject;
			// Get the IP address and the port number of peer and the connection hash
			String hash = peer.get ( "hash" ).toString ();
			String address = peer.get ( "address" ).toString ();
			int port = Integer.parseInt ( peer.get ( "port" ).toString () );
			// Attempt to connect to them
			this.connect ( address, port, hash );
		}
	}

	public void connectToRequests ( JSONArray requests ) {
		// Loop through all the requests
		for ( Object requestObject : requests ) {
			// Cast the request to be of JSON Object
			JSONObject request = ( JSONObject ) requestObject;
			// Get the IP address and the port number of request and the connection hash
			String hash = request.get ( "hash" ).toString ();
			String address = request.get ( "address" ).toString ();
			int port = Integer.parseInt ( request.get ( "port" ).toString () );
			// Attempt to connect to them
			this.connect ( address, port, hash );
		}
	}

	public synchronized static void sendAll ( String data ) {
		// Iterate through all of the peer connections
		for ( Connection peer : Server.peers ) {
			// Make sure that they are in our peers list
			if ( Server.preferences.isPeer ( peer.hash ) ) {
				// Send this peer the data
				peer.send ( data );
			}
		}
	}

	public synchronized static void sendAllBut ( Connection exclude, String data ) {
		// Iterate through all of the peer connections
		for ( Connection peer : Server.peers ) {
			if ( peer != exclude ) {
				// Send this peer the data
				peer.send ( data );
			}
		}
	}

	public synchronized static void close () {
		try {
			for ( Connection peer : Server.peers ) {
				peer.close ();
			}
			Server.server.close ();
		}
		catch ( Exception exception ) {}
	}

	public static String hash ( int length ) {
		String library = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String hash = "";
		for ( int i = 0; i < length; i++ ) {
			int index = ( int ) ( Math.random () * library.length () );
			hash += library.charAt ( index );
		}
		return hash;
	}

}