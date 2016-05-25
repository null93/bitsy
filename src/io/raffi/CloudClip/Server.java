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

	private static ServerSocketFactory serverSocketFactory;

	private static SocketFactory socketFactory;

	private static ServerSocket server;

	private static volatile ArrayList <Connection> peers;

	public Server ( int incomingPort ) throws Exception {
		// Initialize the server socket factory and socket factory
		Server.serverSocketFactory = SSLServerSocketFactory.getDefault ();
		Server.socketFactory = SSLSocketFactory.getDefault ();
		// Initialize the socket and clients array list
		Server.server = Server.serverSocketFactory.createServerSocket ( incomingPort );
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

	public synchronized void connect ( String outgoingAddress, int outgoingPort ) {
		try {
			// Create a new socket
			Socket client = Server.socketFactory.createSocket ( outgoingAddress, outgoingPort );
			// Pass it to the Connection class and append to the peers array
			Connection connection = new Connection ( client );
			Server.peers.add ( connection );
			new Thread ( connection ).start ();
		}
		catch ( Exception exception ) {}
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


	public static void main ( String args [] ) throws Exception {
		if ( args [0].equals ("server") ) {
			Server server = new Server ( 10007 );
		}
		else {
			Server server = new Server ( 10006 );
			server.connect ( "localhost", 10007 );
			Server.sendAll ( "Hello from client" );
		}
		
	}

}