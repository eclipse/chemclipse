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
package org.eclipse.chemclipse.swt.ui.series;

// TODO JUnit
/**
 * @author eselmeister
 */
public interface IDataRange {

	/**
	 * Returns the lowest x value.
	 * 
	 * @return double
	 */
	double getXMin();

	/**
	 * Returns the highest x value.
	 * 
	 * @return double
	 */
	double getXMax();

	/**
	 * Returns the lowest y value.
	 * 
	 * @return double
	 */
	double getYMin();

	/**
	 * Returns the highest y value.
	 * 
	 * @return double
	 */
	double getYMax();
}
