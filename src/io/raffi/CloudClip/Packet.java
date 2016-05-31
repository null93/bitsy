package io.raffi.CloudClip;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@SuppressWarnings ( "unchecked" )
public class Packet {

	private static Packet instance;

	private History history;

	private Packet () {
		this.history = History.getInstance ();
	}

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

	/**
	 * This static class is in charge of only making one instance of this class since it is a
	 * designed using the singleton design pattern.
	 * @return 	Packet 								The singleton instance
	 * @static
	 */
	public static Packet getInstance () {
		if ( Packet.instance == null ) {
			Packet.instance = new Packet ();
		}
		return Packet.instance;
	}

	public String sendHandshake ( String hash ) {
		JSONObject json = new JSONObject ();
		json.put ( "type", "handshake-request" );
		json.put ( "hash", hash );
		json.put ( "clips", this.history.getClips () );
		return json.toString ();
	}

	public String acceptHandshake ( String hash ) {
		JSONObject json = new JSONObject ();
		json.put ( "type", "handshake-accept" );
		json.put ( "hash", hash );
		json.put ( "clips", this.history.getClips () );
		return json.toString ();
	}

	public String rejectHandshake ( String hash ) {
		JSONObject json = new JSONObject ();
		json.put ( "type", "handshake-reject" );
		json.put ( "hash", hash );
		return json.toString ();
	}

	public String sendClip ( String clip ) {
		JSONObject json = new JSONObject ();
		json.put ( "type", "clip" );
		json.put ( "data", clip );
		return json.toString ();
	}

}