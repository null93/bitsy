import io.raffi.CloudClip.Preferences;
import io.raffi.CloudClip.History;
import io.raffi.CloudClip.MenuTray;
import io.raffi.CloudClip.ClipboardManager;
import io.raffi.CloudClip.Server;
import io.raffi.CloudClip.UserInterface;

public class CloudClip {

	public static void main ( String [] args ) throws Exception {
        Preferences settings = Preferences.getInstance ();
        Server server = Server.getInstance ();
        History history = History.getInstance ();
        MenuTray menu = new MenuTray ();
		ClipboardManager clipboard = new ClipboardManager ( history, menu );
		// UserInterface.peerConnectionAuthorization ( "192.168.0.12", 6969 );
	}

}