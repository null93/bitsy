package io.raffi.CloudClip;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@SuppressWarnings ( "unchecked" )
public class Packet {

	private static Packet instance;

	private Packet () {}

	public static JSONObject parse ( String packet ) {
		// Initialize the parser
		JSONParser parser = new JSONParser ();
		// Try to parse the json string
		try {
			// Parse the JSON string, and return it
			return ( JSONObject ) parser.parse ( packet );
		}
		// Attempt to catch any parse exceptions
		catch ( Exception exception ) {}
		// By default return null
		return null;
	}

	public static Packet getInstance () {
		if ( Packet.instance == null ) {
			Packet.instance = new Packet ();
		}
		return Packet.instance;
	}

	public String clientHandshake () {
		JSONObject json = new JSONObject ();
		json.put ( "type", "handshake" );
		json.put ( "hash", this.hash ( 32 ) );
		json.put ( "clips", "..." );
		return json.toString ();
	}

	public String serverHandshake () {
		JSONObject json = new JSONObject ();
		json.put ( "type", "connect" );
		json.put ( "hash", this.hash ( 32 ) );
		json.put ( "clips", "..." );
		return json.toString ();
	}

	public String hash ( int length ) {
		String output = "";
		String library = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		while ( length > 0 ) {
			output += library.charAt ( ( int ) ( Math.random () * library.length () ) );
			length--;
		}
		return output;
	}

}