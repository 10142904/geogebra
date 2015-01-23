package geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

/**
 * interface for openGL buffers
 * 
 * @author mathieu
 *
 */
public interface GLBuffer {
	
	/**
	 * allocate memory if needed
	 * @param length length
	 */
	public void allocate(int length);
	
	/**
	 * set limit to which we use the buffer
	 * @param length limit
	 */
	public void setLimit(int length);
	
	/**
	 * put value at current buffer position
	 * @param value value
	 */
	public void put(float value);
	
	/**
	 * end of put values
	 */
	public void endOfPut();

	/**
	 * set this buffer with first values of array
	 * 
	 * @param array
	 *            values array
	 * @param length
	 *            length to copy
	 */
	public void set(ArrayList<Float> array, int length);

	/**
	 * 
	 * @return capacity
	 */
	public int capacity();

	/**
	 * 
	 * set float array
	 */
	public void array(float[] ret);

	/**
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty();

	/**
	 * says it's an empty buffer
	 */
	public void setEmpty();

}
