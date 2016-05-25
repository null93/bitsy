import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class Connection implements Runnable {

	private Socket socket;

	private PrintWriter outgoing;

	private volatile BufferedReader incoming;

	private String message;

	private Connection.State state = State.OPEN;

	public enum State {
		OPEN, CLOSED;
	}

	public Connection ( Socket socket ) throws Exception {
		// Save the socket connection
		this.socket = socket;
		System.out.println ( socket.getRemoteSocketAddress().toString() );
		try {
			// Initialize the incoming and outgoing data stream
			this.outgoing = new PrintWriter ( this.socket.getOutputStream (), true );
			this.incoming = new BufferedReader ( new InputStreamReader ( this.socket.getInputStream () ) );
			outgoing.flush ();
		}
		// Attempt to catch any exceptions
		catch ( Exception exception ) {
			// Throw our own exception
			throw new Exception ( "Connection with client was interrupted!" );
		}
	}

	public void send ( String data ) {
		this.outgoing.flush ();
		this.outgoing.println ( data );
	}

	public void close () {
		this.state = State.CLOSED;
		try {
			this.outgoing.close ();
			this.incoming.close ();
			this.socket.close ();
		}
		catch ( Exception exception ) {}
	}

	public void run () {
		while ( this.state == State.OPEN ) {
			try {
				while ( ( this.message = this.incoming.readLine () ) != null ) {
					System.out.println ( "The response from server is: " + this.message );
					Server.sendAllBut ( this, this.message );
				}
			}
			catch ( Exception exception ) {}
		}
	}

}