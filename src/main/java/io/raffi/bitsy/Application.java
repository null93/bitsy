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

        if ( this.resource.ignoreDuplicateClips && this.resource.clips.contains ( value ) ) {
            this.resource.clips.remove ( value );
            this.resource.clips.add ( 0, value );
        }
        else {
            while ( this.resource.clips.size () >= this.resource.maxClips ) {
                this.resource.clips.remove ( this.resource.clips.size () - 1 );
            }
            this.resource.clips.add ( 0, value );
        }

        this.resource.save ();
        MenuObserver.getInstance ().render ();
    }

}