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
package org.eclipse.chemclipse.model.baseline;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;

/**
 * This class implements a baseline segment used in {@link IChromatogramMSD} and {@link BaselineModel}.
 * 
 * @author eselmeister
 */
public class BaselineSegment implements IBaselineSegment {

	private static final Logger logger = Logger.getLogger(BaselineSegment.class);
	private int startRetentionTime = 0;
	private float startBackgroundAbundance = 0.0f;
	private int stopRetentionTime = 0;
	private float stopBackgroundAbundance = 0.0f;

	/**
	 * The start retention time must be <= than the stop retention time.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	public BaselineSegment(int startRetentionTime, int stopRetentionTime) {
		if(startRetentionTime > stopRetentionTime) {
			int tmp = startRetentionTime;
			startRetentionTime = stopRetentionTime;
			stopRetentionTime = tmp;
		}
		setStopRetentionTime(stopRetentionTime);
		setStartRetentionTime(startRetentionTime);
	}

	@Override
	public float getStartBackgroundAbundance() {

		return startBackgroundAbundance;
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public float getStopBackgroundAbundance() {

		return stopBackgroundAbundance;
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	@Override
	public void setStartBackgroundAbundance(float startBackgroundAbundance) {

		if(startBackgroundAbundance >= 0) {
			this.startBackgroundAbundance = startBackgroundAbundance;
		}
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		if(startRetentionTime >= 0 && startRetentionTime <= this.stopRetentionTime) {
			this.startRetentionTime = startRetentionTime;
		} else {
			logger.warn("The start retention time must be lower or equal than the stop retention time. " + "start: " + startRetentionTime + " - " + "stop: " + stopRetentionTime);
		}
	}

	@Override
	public void setStopBackgroundAbundance(float stopBackgroundAbundance) {

		if(stopBackgroundAbundance >= 0) {
			this.stopBackgroundAbundance = stopBackgroundAbundance;
		}
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		if(stopRetentionTime >= 0 && stopRetentionTime >= this.startRetentionTime) {
			this.stopRetentionTime = stopRetentionTime;
		} else {
			logger.warn("The stop retention time must be higher or equal than the start retention time. " + "start: " + startRetentionTime + " - " + "stop: " + stopRetentionTime);
		}
	}

	// ---------------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		BaselineSegment otherSegment = (BaselineSegment)other;
		return this.getStartBackgroundAbundance() == otherSegment.getStartBackgroundAbundance() && this.getStartRetentionTime() == otherSegment.getStartRetentionTime() && this.getStopBackgroundAbundance() == otherSegment.getStopBackgroundAbundance() && this.getStopRetentionTime() == otherSegment.getStopRetentionTime();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(startRetentionTime).hashCode() + 11 * Float.valueOf(startBackgroundAbundance).hashCode() + 13 * Integer.valueOf(stopRetentionTime).hashCode() + 11 * Float.valueOf(stopBackgroundAbundance).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startRetentionTime = " + startRetentionTime);
		builder.append(",");
		builder.append("startBackgroundAbundance = " + startBackgroundAbundance);
		builder.append(",");
		builder.append("stopRetentionTime = " + stopRetentionTime);
		builder.append(",");
		builder.append("stopBackgroundAbundance = " + stopBackgroundAbundance);
		builder.append("]");
		return builder.toString();
	}
	// ---------------------------------equals, hashCode, toString

	@Override
	public float getBackgroundAbundance(int retentionTime) {

		Point p1 = new Point(startRetentionTime, startBackgroundAbundance);
		Point p2 = new Point(stopRetentionTime, startBackgroundAbundance);
		return (float)Equations.createLinearEquation(p1, p2).calculateY(retentionTime);
	}
}
