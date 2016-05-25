import java.net.*; 
import java.io.*; 

public class EchoServer {

	public static ServerSocket server = null;

	public EchoServer ( int port ) throws Exception {
		
		PrintWriter out = null;
		BufferedReader in = null;
		Socket client = null;
		String input = null;

		try {
			EchoServer.server = new ServerSocket ( port ); 
			Thread listen = new Thread ( new Runnable () {
				public void run () {
					// Loop forever while listening for client connections
					while ( true ) {
						try {
							String input = "";
							Socket client = EchoServer.server.accept ();
							PrintWriter out = new PrintWriter ( client.getOutputStream (), true ); 
							BufferedReader in = new BufferedReader ( new InputStreamReader ( client.getInputStream () ) ); 
							while ( ( input = in.readLine () ) != null ) { 
								System.out.println ( "Server: " + input ); 
								out.println ( input ); 
								if ( input.equals ( "Bye." ) ) { 
									break; 
								}
							}
						}
						catch ( Exception exception ) {}
						finally {
							try {
								out.close (); 
								in.close ();
								client.close (); 
							}
							catch ( Exception exception ) {}
						}
					}
			    }
			});
			listen.start ();
		} 
		catch ( Exception exception ) { 
			System.err.println ( "Fail..." ); 
			System.exit ( 1 ); 
		}
		finally {
			EchoServer.server.close (); 
		}
	}

	public static void main ( String [] args ) throws Exception { 

		EchoServer server = new EchoServer ( 10007 );		

	} 
} 