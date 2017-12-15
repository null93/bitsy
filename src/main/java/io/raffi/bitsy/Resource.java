package io.raffi.bitsy;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 
 */
public class Resource implements Serializable {

	/**
	 * 
	 */
	private transient static Resource instance;
	public transient File output;

	/**
	 * 
	 */
	public Integer maxClips = 100;
	public Integer previewLength = 50;
	public Boolean ignoreDuplicateClips = false;
	public Boolean startOnBoot = false;
	public Boolean clearOnExit = false;
	public ArrayList <String> clips = new ArrayList <String> ();

	/**
	 * 
	 */
	private Resource () {}

	/**
	 * 
	 */
	public void save () {
		try {
			//
			FileOutputStream fileOutputStream = new FileOutputStream ( this.output );
			ObjectOutputStream objectOutputStream = new ObjectOutputStream ( fileOutputStream );
			objectOutputStream.writeObject ( this );
			objectOutputStream.close ();
			fileOutputStream.close ();
		}
		catch ( Exception e ) {
			e.printStackTrace ();
			System.out.println ( e );
		}
	}

	/**
	 * 
	 */
	public static Resource getInstance () {
		//
		File output = new File ( System.getProperty ("user.home") + File.separator + ".bitsy" );
		Resource instance = null;
		//
		if ( Resource.instance != null ) {
			instance = Resource.instance;
		}
		//
		else if ( output.exists () ) {
			try {
				//
				FileInputStream FileInputStream = new FileInputStream ( output );
				ObjectInputStream objectInputStream = new ObjectInputStream ( FileInputStream );
				instance = ( Resource ) objectInputStream.readObject ();
				objectInputStream.close ();
				FileInputStream.close ();
				instance.output = output;
			}
			catch ( Exception e ) {
				e.printStackTrace ();
				System.out.println ( e );
			}
		}
		//
		else {
			instance = new Resource ();
			instance.output = output;
		}
		//
		Resource.instance = instance;
		return instance;
	}

}