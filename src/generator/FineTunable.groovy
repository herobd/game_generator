package generator

/**
 * @author Brian Davis
 *
 * Interface indicating that the implementing object is a type that supports the fine tune operations
 * 
 */
interface FineTunable extends Cloneable
{
	/**
	 * Number of tunable integer parameters
     * 
     *
	 * @return a number of params
	 */
	int getNumParams()

	/**
	 * Changes the parameter (integer indexed into the tree) by the given amount
	 * @param param the indexed parameter
	 * @param amount the amount to tweak the parameter by
	 */
	void changeParam(int param, int amount)
}
