package io.raffi.CloudClip;

import java.io.File;
import javax.swing.JFrame;

@SuppressWarnings ( "serial" )
public class Preferences extends JFrame {

	private static Preferences instance = null;

	private static String Home = System.getProperty ( "user.home" );

	private static String Support = "/Library/Application Support/CloudClip/";

	protected static String DataFolder = Home + Support;

	protected static String ClipsDataPath = Home + Support + "clips.json";
	
	protected static String SettingsDataPath = Home + Support + "settings.json";

	protected static int MaxNumberOfClips = 50;

	protected static int ClipCutoff = 40;

	private Preferences () throws Exception {
		super ( "CloudClip Preferences" );
		File directory = new File ( DataFolder );
		File settings = new File ( SettingsDataPath );
		if ( !directory.exists () ) {
			directory.mkdir ();
		}
		if ( !settings.exists () ) {
			settings.createNewFile ();
		}
	}

	public static Preferences getInstance () throws Exception {
		if ( Preferences.instance == null ) {
			Preferences.instance = new Preferences ();
		}
		return Preferences.instance;
	}

}