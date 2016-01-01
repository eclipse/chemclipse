/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import org.eclipse.swt.graphics.Color;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IMarker {

	/**
	 * Sets the marker string, e.g. "V" for selected scan or "T" for peaks.
	 * 
	 * @param marker
	 */
	void setMarker(String marker);

	/**
	 * Returns the actual marker.
	 * 
	 * @return String
	 */
	String getMarker();

	/**
	 * Returns the background color.
	 * 
	 * @return Color
	 */
	Color getBackgroundColor();

	/**
	 * Returns the foreground color.
	 * 
	 * @return Color
	 */
	Color getForegroundColor();
}
