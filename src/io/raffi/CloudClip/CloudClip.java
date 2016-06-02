package io.raffi.CloudClip;

public class CloudClip {

	public static void main ( String [] args ) {
		// Initialize all the classes in this order
        Preferences settings = Preferences.getInstance ();
        Server server = Server.getInstance ();
        History history = History.getInstance ();
        MenuTray menu = MenuTray.getInstance ();
		ClipboardManager clipboard = ClipboardManager.getInstance ();
		// Connect to established peers
		server.connectToPeers ( settings.getPeers () );
	}

}