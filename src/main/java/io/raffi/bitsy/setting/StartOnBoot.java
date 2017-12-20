package io.raffi.bitsy.setting;

import io.raffi.bitsy.graphic.CheckItem;
import io.raffi.bitsy.Resource;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StartOnBoot extends CheckItem {

	public StartOnBoot ( Resource resource ) {
		super ( "Start on Boot", "startOnBoot", resource );
	}

	public Boolean exportResource ( InputStream source, String destination ) {
        try {
            Files.copy ( source, Paths.get ( destination ), StandardCopyOption.REPLACE_EXISTING );
        }
        catch ( Exception e ) {
        	return false;
        }
        return true;
	}

	public void itemStateChanged ( ItemEvent event ) {
		super.itemStateChanged ( event );
		String filename = "io.raffi.bitsy.plist";
		String destination = System.getProperty ("user.home") + "/Library/LaunchAgents/" + filename;
		try {
			if ( this.resource.startOnBoot ) {
				this.exportResource (
					this.getClass ().getResourceAsStream ("/Startup.plist"),
					destination
				);
			}
			else {
				File file = new File ( destination );
				file.delete ();
			}
		}
		catch ( Exception e ) {
			e.printStackTrace ();
		}
	}

}