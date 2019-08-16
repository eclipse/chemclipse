/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import org.eclipse.swt.graphics.Color;

public interface IColorScheme {

	/**
	 * Returns the size.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the next color.
	 * 
	 * @return Color
	 */
	Color getNextColor();

	/**
	 * Returns the previous color.
	 * 
	 * @return Color
	 */
	Color getPreviousColor();

	/**
	 * Return the color, given by number.
	 * 
	 * @param i
	 * @return Color
	 */
	Color getColor(int i);

	/**
	 * Resets the color scheme to the initial color.
	 */
	void reset();

	/**
	 * Return the current color.
	 * 
	 * @param i
	 * @return Color
	 */
	Color getColor();

	/**
	 * Selects the next color.
	 * It can be fetched on demand by getColor();
	 */
	void incrementColor();
}
