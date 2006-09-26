package de.lmu.ifi.dbs.utilities.optionhandling;

/**
 * Represents a List-Size GlobalParameterConstraint. The size of the
 * ListParameter to be tested has to be equal to the specified constraint size.
 * 
 * @author Steffi Wanka
 * 
 */
public class LengthConstraint implements GlobalParameterConstraint {

	/**
	 * ListParameter to be tested
	 */
	private ListParameter list;

	/**
	 * constraint list size
	 */
	private IntParameter length;

	/**
	 * Creates a List-Size GlobalParameterConstraint, i.e. the size of the given
	 * ListParameter hat to be equal to the given constraint list size.
	 * 
	 * @param v
	 *            the ListParameter to be tested.
	 * @param i
	 *            constraint list size.
	 */
	public LengthConstraint(ListParameter v, IntParameter i) {
		list = v;
		length = i;
	}

	/**
	 * Checks is the size of the ListParameter is equal to the specified
	 * constraint list size. If not a parameter exception is thrown.
	 * 
	 */
	public void test() throws ParameterException {

		if (list.getListSize() != Integer.parseInt(length.getValue())) {
			throw new WrongParameterValueException(
					"Parameter Constraint Error!\nThe size of the ListParameter \""
							+ list.getName() + "\" must be " + length + "!\n");
		}
	}

}
