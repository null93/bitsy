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

public class Server {

	private static Server instance;

	private static ServerSocketFactory serverSocketFactory;

	private static SocketFactory socketFactory;

	private static ServerSocket server;

	private static volatile ArrayList <Connection> peers;

	private Server ( int incomingPort ) throws Exception {
		// Initialize the server socket factory and socket factory
		Server.serverSocketFactory = SSLServerSocketFactory.getDefault ();
		Server.socketFactory = SSLSocketFactory.getDefault ();
		// Initialize the socket and clients array list
		Server.server = new ServerSocket ( incomingPort );
		//Server.server = Server.serverSocketFactory.createServerSocket ( incomingPort );
		Server.peers = new ArrayList <Connection> ();
		// Spawn a new thread to listen for connections and start the thread
		Thread listen = new Thread ( new Runnable () {
			public void run () {
				// Loop forever while listening for client connections
				while ( true ) {
					try {
						// Block until we get a client, and then append to the array
						Socket client = Server.server.accept ();
						Connection connection = new Connection ( client );
						new Thread ( connection ).start ();
						Server.peers.add ( connection );
						System.out.println ( "Accepted connection!" );
						Server.sendAll ( "Hello there!" );
					}
					catch ( Exception exception ) {}
				}
		    }
		});
		listen.start ();

	}

	public static Server getInstance () throws Exception {
		// Check to see if the instance is initialized
		if ( Server.instance == null ) {
			// If it isn't then initialize one using the desired settings
			Server.instance = new Server ( Preferences.Port );
		}
		// Return the Server instance
		return Server.instance;
	}
	
	public synchronized String connect ( String outgoingAddress, int outgoingPort ) {
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
			String hash = Server.hash ( 32 );
			connection.send ( packet.sendHandshake ( hash ) );
			return hash;
		}
		catch ( Exception exception ) {
			System.out.println ( "Failed to connect to peer!" );
		}
		return null;
	}

	public synchronized static void sendAll ( String data ) {
		// Iterate through all of the peer connections
		for ( Connection peer : Server.peers ) {
			// Send this peer the data
			peer.send ( data );
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