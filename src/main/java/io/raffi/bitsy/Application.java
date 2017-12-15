package io.raffi.bitsy; 

import io.raffi.bitsy.clipboard.ClipboardEvent;
import io.raffi.bitsy.clipboard.ClipboardListener;
import io.raffi.bitsy.clipboard.ClipboardObserver;
import io.raffi.bitsy.menu.MenuObserver;
import javax.swing.SwingUtilities;

public class Application extends MenuTray implements ClipboardListener {

    private Resource resource;

    public Application ( Resource resource ) {
        super ( resource );
        this.resource = resource;
    }

    public static void main ( String [] args ) throws Exception {
        SwingUtilities.invokeAndWait ( new Runnable () {
         
            public void run () {
                Application application = new Application ( Resource.getInstance () );
                ClipboardObserver clipboardObserver = ClipboardObserver.getInstance ();
                clipboardObserver.addClipboardListener ( application );
                clipboardObserver.start ();
            }

        });
    }

    public void clipCopyPreformed ( ClipboardEvent event ) {
        String value = event.getValue ();
        this.resource.clips.add ( value );
        this.resource.save ();
        MenuObserver.getInstance ().render ();
    }

}