package io.raffi.CloudClip;

/**
 * Tuple.java -  * This class is merely here to be able to bind two abstract objects together into a single object.
 * This allows us to use this class throughout a various amount of applications without creating new
 * classes.
 * @version     1.0.0
 * @package     CloudClip
 * @category    Tuple
 * @author      Rafael Grigorian
 * @license     GNU Public License <http://www.gnu.org/licenses/gpl-3.0.txt>
 */
public class Tuple <FIRST, SECOND> {

	/**
	 * This is the first abstract instance that is saved.  It is final, so it cannot be changed.
	 * @var     FIRST           first               The first object
	 * @final
	 */
	private final FIRST first;

	/**
	 * This is the second abstract instance that is saved.  It is final, so it cannot be changed.
	 * @var     SECOND          second              The second object
	 * @final
	 */
	private final SECOND second;

	/**
	 * For this constructor we need to just pass the two abstract objects as parameters and save
	 * them internally.
	 * @param   FIRST           first               The first object
	 * @param   SECOND          second              The second object
	 */
	public Tuple ( FIRST first, SECOND second ) {
		// Save the abstract objects internally and finally
		this.first = first;
		this.second = second;
	}

	/**
	 * This is a getter function that returns the first instance ( object ) from the tuple class.
	 * @return  FIRST                               The first abstract object
	 */
	public FIRST first () {
		// Return the abstract object
		return this.first;
	}

	/**
	 * This is a getter function that returns the second instance ( object ) from the tuple class.
	 * @return  SECOND                              The second abstract object
	 */
	public SECOND second () {
		// Return the abstract object
		return this.second;
	}

}