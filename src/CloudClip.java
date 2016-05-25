import io.raffi.CloudClip.Preferences;
import io.raffi.CloudClip.History;
import io.raffi.CloudClip.MenuTray;
import io.raffi.CloudClip.ClipboardManager;

public class CloudClip {

	public static void main ( String [] args ) throws Exception {
        Preferences settings = Preferences.getInstance ();
        History history = new History ();
        MenuTray menu = new MenuTray ( history );
		ClipboardManager clipboard = new ClipboardManager ( history, menu );
	}

}