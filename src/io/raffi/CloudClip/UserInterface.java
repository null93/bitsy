package io.raffi.CloudClip;

import javax.swing.JDialog;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.text.Utilities;
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
import io.raffi.CloudClip.Graphic.Button;
import io.raffi.CloudClip.Graphic.TextField;
import javax.swing.Icon;
import javax.swing.JOptionPane;

@SuppressWarnings ( "unchecked" )
public class UserInterface {

	public UserInterface () {}

	private static JDialog dialog () {
		// Create a new dialog and set it's properties
		final JDialog dialog = new JDialog ();
		dialog.setAlwaysOnTop ( true );  
		// Return that instance
		return dialog;
	}

	public static Boolean confirmLocalClear () {
		// Initialize the message string
		String message = "Would you like to clear the clipboard?  Please note\nthat it will not b" +
		"e propagated to your synced clipboards.";
		// Initialize and display a new confirmation dialog window
		int result = JOptionPane.showConfirmDialog (
			UserInterface.dialog (),
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

	public static Boolean confirmNetworkClear () {
		// Initialize the message string
		String message = "Would you like to clear across all currently\nconnected clipboards?";
		// Initialize and display a new confirmation dialog window
		int result = JOptionPane.showConfirmDialog (
			UserInterface.dialog (),
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
		int result = JOptionPane.showOptionDialog ( UserInterface.dialog (), panel, "Peer Connection Request", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null );
		if ( result == JOptionPane.YES_OPTION ) {
			if ( UserInterface.validateIP ( address.getText () ) && ( UserInterface.validatePort ( port.getText () ) ) ) {
				return new Tuple ( address.getText (), Integer.parseInt ( port.getText () ) );
			}
		}
		System.out.println ( "Failed because of user input" );
		return null;
	}

	public static Boolean peerConnectionAuthorization ( String address, int port ) {
		// Initialize and display a new confirmation dialog window
		int result = JOptionPane.showConfirmDialog (
			UserInterface.dialog (),
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
		Pattern IP_V4 = Pattern.compile (
			"(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])",
			Pattern.CASE_INSENSITIVE
		);
		Matcher m = IP_V4.matcher ( address );
		if ( m.matches () ) {
			return true;
		}
		return false;
	}

	private static Boolean validatePort ( String portString ) {
		int port = Integer.parseInt ( portString );
		if ( port > 0 && port < 65535 ) {
			return true;
		}
		return false;
	}

}