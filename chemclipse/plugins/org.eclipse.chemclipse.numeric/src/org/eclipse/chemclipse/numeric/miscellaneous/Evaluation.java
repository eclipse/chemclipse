/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.miscellaneous;

/**
 * Offers some static methods for value evaluation.
 * 
 * @author eselmeister
 */
public class Evaluation {

	/**
	 * Returns if the values are increasing subsequently.<br/>
	 * If the double[] array contains only 1 value, false will be returned.
	 * 
	 * @param values
	 * @return boolean
	 */
	public static boolean valuesAreIncreasing(double[] values) {

		/*
		 * Return false if the array contains less than 2 entries.
		 */
		if(values.length < 2) {
			return false;
		}
		boolean valuesAreIncreasing = true;
		double act = Double.MIN_VALUE;
		exitloop:
		for(double d : values) {
			if(d > act) {
				act = d;
			} else {
				valuesAreIncreasing = false;
				break exitloop;
			}
		}
		return valuesAreIncreasing;
	}

	/**
	 * Checks if the given values in the double array are greater than the given
	 * threshold.
	 * 
	 * @param values
	 * @param threshold
	 * @return
	 */
	public static boolean valuesAreGreaterThanThreshold(double[] values, double threshold) {

		boolean valuesAreGreater = true;
		exitloop:
		for(double d : values) {
			if(d <= threshold) {
				valuesAreGreater = false;
				break exitloop;
			}
		}
		return valuesAreGreater;
	}
}
