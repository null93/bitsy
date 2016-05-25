package io.raffi.CloudClip;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * CloudClipException.java - This class is a used internally to catch all errors and describe them,
 * instead of throwing other exceptions.  It is also here to throw usage error exceptions, when we
 * use this program's classes correctly.
 * @version     1.0.0
 * @package     CloudClip
 * @category    CloudClipException
 * @author      Rafael Grigorian
 * @license     GNU Public License <http://www.gnu.org/licenses/gpl-3.0.txt>
 */
@SuppressWarnings ( "serial" )
public class CloudClipException extends Exception {

	/**
	 * This static and immutable string is used to set the text in the terminal to it's default
	 * settings.  It is used when printing exceptions to standard output.
	 * @var     String          RESET               Escaped style for resetting text font
	 * @static
	 * @final
	 */
	protected static final String RESET = "\033[49;39m";

	/**
	 * This static and immutable string is used to set the text in the terminal to a red foreground
	 * setting.  It is used when printing exceptions to standard output.
	 * @var     String          ERROR               Escaped style for setting red text foreground
	 * @static
	 * @final
	 */
	protected static final String ERROR = "\033[31;49m";

	/**
	 * This static immutable string is printed every time an exception is thrown.  It serves as the
	 * header to each message.  It also implements the color styling that is initialized above.
	 * @var     String          header              The header string pre-appended to each exception
	 * @static
	 * @final
	 */
	protected static final String header = ERROR + "CloudClipException: " + RESET;

	/**
	 * This constructor requires the user to input the exception message to be displayed as a
	 * parameter.  It then calls the super class constructor and prints out the formatted thrown
	 * exception message to the user.
	 * @param   String          message             The message to display when exception is thrown
	 */
	protected CloudClipException ( String message ) {
		// Call the super constructor first
		super ( message );
		// Print out the exception's message
		System.out.println ( header + message );
		// Print the stack trace
		StringWriter errors = new StringWriter ();
		super.printStackTrace ( new PrintWriter ( errors ) );
		String stack = errors.toString ();
		System.out.println ( stack.substring ( stack.indexOf ('\n') + 1 ) );
	}

	/**
	 * This constructor does not require any message string to be passed as a parameter.  It simply
	 * alerts user that the exception was thrown and that an unknown exception was encountered.
	 */
	protected CloudClipException () {
		// Call the super constructor first
		super ();
		// Alert user that an unknown exception was thrown
		System.out.println ( header + "Unknown exception was thrown!" );
		// Print the stack trace
		StringWriter errors = new StringWriter ();
		super.printStackTrace ( new PrintWriter ( errors ) );
		String stack = errors.toString ();
		System.out.print ( stack.substring ( stack.indexOf ('\n') + 1 ) );
	}

}