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
package org.eclipse.chemclipse.model.support;

/**
 * This class can be used to represent a valid background abundance range.<br/>
 * It evaluates the constructor parameters and corrects them to a valid state.
 * 
 * @author eselmeister
 */
public class BackgroundAbundanceRange implements IBackgroundAbundanceRange {

	private float startBackgroundAbundance;
	private float stopBackgroundAbundance;

	/**
	 * Creates a new BackgroundAbundanceRange object. There are some
	 * limitations:<br/>
	 * The startBackgroundAbundance may not be < 0 and > MAX_BACKGROUND_ABUNDANCE. In such
	 * a case it will be set to MIN_BACKGROUND_ABUNDANCE. The stopBackgroundAbundance
	 * should be <= MAX_BACKGROUND_ABUNDANCE and >= 0 otherwise it will be set to
	 * MAX_BACKGROUND_ABUNDANCE.
	 * 
	 * @param startBackgroundAbundance
	 * @param stopBackgroundAbundance
	 */
	public BackgroundAbundanceRange(float startBackgroundAbundance, float stopBackgroundAbundance) {
		if(startBackgroundAbundance < MIN_BACKGROUND_ABUNDANCE || startBackgroundAbundance > MAX_BACKGROUND_ABUNDANCE) {
			startBackgroundAbundance = MIN_BACKGROUND_ABUNDANCE;
		}
		if(stopBackgroundAbundance > MAX_BACKGROUND_ABUNDANCE || stopBackgroundAbundance < MIN_BACKGROUND_ABUNDANCE) {
			stopBackgroundAbundance = MAX_BACKGROUND_ABUNDANCE;
		}
		this.startBackgroundAbundance = startBackgroundAbundance;
		this.stopBackgroundAbundance = stopBackgroundAbundance;
	}

	@Override
	public float getStartBackgroundAbundance() {

		return startBackgroundAbundance;
	}

	@Override
	public float getStopBackgroundAbundance() {

		return stopBackgroundAbundance;
	}

	// -----------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(this == other) {
			return true;
		}
		if(other == null) {
			return false;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		BackgroundAbundanceRange otherRange = (BackgroundAbundanceRange)other;
		return getStartBackgroundAbundance() == otherRange.getStartBackgroundAbundance() && getStopBackgroundAbundance() == otherRange.getStopBackgroundAbundance();
	}

	@Override
	public int hashCode() {

		return 7 * Float.valueOf(getStartBackgroundAbundance()).hashCode() + 11 * Float.valueOf(getStopBackgroundAbundance()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startBackgroundAbundance=" + getStartBackgroundAbundance());
		builder.append(",");
		builder.append("stopBackgroundAbundance=" + getStopBackgroundAbundance());
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
