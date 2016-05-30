package io.raffi.CloudClip;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JComboBox;
import javax.swing.JComboBox;
import java.awt.Toolkit;
import java.lang.Runtime;
import javax.swing.ImageIcon;
import Graphic.Button;
import Graphic.TextField;
import javax.swing.Icon;
import javax.swing.JOptionPane;

@SuppressWarnings ( "unchecked" )
public class UserInterface {
	
	private UserInterface () {}

	public static Boolean confirmClear () {
		// Initialize the message string
		String message = "Would you like to clear the clipboard?  Please note\nthat it will not b" +
		"e propagated to your synced clipboards";
		// Initialize and display a new confirmation dialog window
		int result = JOptionPane.showConfirmDialog (
			null,
			message,
			"CloudClip - Clear Clipboard Confirmation",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.PLAIN_MESSAGE
		);
		// Check to see if the yes option was clicked
		if ( result == JOptionPane.YES_OPTION ) {
			// Return true since yes was clicked
			return true;
		}
		// By default return false
		return false;
	}

	public static Tuple peerConnectionInitialization () {
		Object [] options = { "Connect", "Cancel" };
        TextField address = new TextField ( "IP Address", 300, 50, 10 );
        TextField port = new TextField ( "Port", 300, 50, 10 );
        JPanel panel = new JPanel ( new GridLayout ( 0, 1 ) );
        JLabel label = new JLabel ( "" );
        label.setFont (label.getFont ().deriveFont (10.0f));
        panel.add ( address );
        panel.add ( label );
        panel.add ( port );
        int result = JOptionPane.showOptionDialog ( null, panel, "Peer Connection Request", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null );
        if ( result == JOptionPane.YES_OPTION ) {
        	if ( UserInterface.validateIP ( address.getText () ) && ( UserInterface.validatePort ( port.getText () ) ) ) {
        		return new Tuple ( address.getText (), Integer.parseInt ( port.getText () ) );
        	}
        }
        return null;
	}

	public static Boolean peerConnectionAuthorization ( String address, int port ) throws Exception {
		// Initialize and display a new confirmation dialog window
		int result = JOptionPane.showConfirmDialog (
			null,
			"A peer at " + address + " wants to connect via port number " + port + ".  Would you " +
			"like to pair with this peer?",
			"CloudClip - Peer Connection Authorization Request",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.PLAIN_MESSAGE
		);
		// Check to see if the yes option was clicked
		if ( result == JOptionPane.YES_OPTION ) {
			// Return true since yes was clicked
			return true;
		}
		// By default return false
		return false;
	}

	private static Boolean validateIP ( String address ) {
	    try {
	    	InetAddress inet = InetAddress.getByName( address );
	    	return inet.getHostAddress ().equals ( address ) && inet instanceof Inet4Address;
	    }
	    catch ( Exception exception ) {
	    	return false;
	    }
	}

	private static Boolean validatePort ( String portString ) {
		int port = Integer.parseInt ( portString );
		if ( port > 49151 && port < 65535 ) {
			return true;
		}
		return false;
	}

}