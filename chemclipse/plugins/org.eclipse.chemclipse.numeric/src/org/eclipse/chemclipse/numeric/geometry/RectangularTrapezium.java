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
package org.eclipse.chemclipse.numeric.geometry;

public class RectangularTrapezium {

	double width;
	double startHeight;
	double stopHeight;

	/**
	 * This class describes a rectangular trapezium.
	 * 
	 * @param width
	 * @param startHeight
	 * @param stopHeight
	 */
	public RectangularTrapezium(double width, double startHeight, double stopHeight) {
		this.width = width;
		this.startHeight = startHeight;
		this.stopHeight = stopHeight;
	}

	/**
	 * Returns the area of the rectangular trapezium.
	 * 
	 * @return double
	 */
	public double getArea() {

		double lowerHeight = Math.min(startHeight, stopHeight);
		double halfTriangleHeight = Math.abs(startHeight - stopHeight) / 2;
		return width * (lowerHeight + halfTriangleHeight);
	}
}
