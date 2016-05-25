import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private static ServerSocket server;

	private static volatile ArrayList <Connection> peers;

	public Server ( int incomingPort ) throws Exception {
		// Initialize the socket and clients array list
		Server.server = new ServerSocket ( incomingPort );
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
						Server.sendAll ( "Hello from Peer #01!" );
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
			Socket client = new Socket ( outgoingAddress, outgoingPort );
			// Pass it to the Connection class and append to the peers array
			Server.peers.add ( new Connection ( client ) );
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

	public static void main ( String args [] ) throws Exception {
		if ( args [0].equals ("server") ) {
			Server server = new Server ( 3454 );
			server.connect ( "localhost", 10007 );
		}
		else {
			Server server = new Server ( 4477 );
			server.connect ( "localhost", 4476 );
			System.out.println ( "hello" );
		}
		
	}

}