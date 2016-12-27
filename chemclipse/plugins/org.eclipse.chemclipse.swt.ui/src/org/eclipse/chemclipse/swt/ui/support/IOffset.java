/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

public interface IOffset {

	/**
	 * Returns the x offset value.
	 * 
	 * @return double
	 */
	double getXOffset();

	/**
	 * Returns the y offset value.
	 * 
	 * @return double
	 */
	double getYOffset();

	/**
	 * Increments the current x offset.
	 */
	void incrementCurrentXOffset();

	/**
	 * Decrements the current x offset.
	 */
	void decrementCurrentXOffset();

	/**
	 * Returns the current x offset.
	 * 
	 * @return double
	 */
	double getCurrentXOffset();

	/**
	 * Increments the current y offset.
	 */
	void incrementCurrentYOffset();

	/**
	 * Decrements the current y offset.
	 */
	void decrementCurrentYOffset();

	/**
	 * Returns the current y offset.
	 * 
	 * @return double
	 */
	double getCurrentYOffset();

	/**
	 * Resets the current offsets to 0.
	 */
	void reset();
}
