/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
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

// TODO JUnit
public class Offset implements IOffset {

	private double xOffset;
	private double yOffset;
	private double currentXOffset;
	private double currentYOffset;

	/**
	 * Creates a new offset instance.<br/>
	 * The current x and y offset is default 0.
	 * 
	 * @param xOffset
	 * @param yOffset
	 */
	public Offset(double xOffset, double yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * Returns the x offset.
	 * 
	 * @return double
	 */
	public double getXOffset() {

		return xOffset;
	}

	/**
	 * Returns the y offset.
	 * 
	 * @return double
	 */
	public double getYOffset() {

		return yOffset;
	}

	@Override
	public void incrementCurrentXOffset() {

		currentXOffset += xOffset;
	}

	@Override
	public void decrementCurrentXOffset() {

		currentXOffset -= xOffset;
	}

	@Override
	public double getCurrentXOffset() {

		return currentXOffset;
	}

	@Override
	public void incrementCurrentYOffset() {

		currentYOffset += yOffset;
	}

	@Override
	public void decrementCurrentYOffset() {

		currentYOffset -= yOffset;
	}

	@Override
	public double getCurrentYOffset() {

		return currentYOffset;
	}

	@Override
	public void reset() {

		currentXOffset = 0;
		currentYOffset = 0;
	}
}
