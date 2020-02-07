/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - copy constrcutor
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

/**
 * @author eselmeister
 */
public class RetentionTimeRange implements IRetentionTimeRange {

	private int startRetentionTime;
	private int stopRetentionTime;

	public RetentionTimeRange(IRetentionTimeRange range) {
		this(range.getStartRetentionTime(), range.getStopRetentionTime());
	}

	/**
	 * Creates a new retention time range.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	public RetentionTimeRange(int startRetentionTime, int stopRetentionTime) {
		if(startRetentionTime < 0) {
			startRetentionTime = 0;
		}
		if(stopRetentionTime < 0) {
			stopRetentionTime = 0;
		}
		if(startRetentionTime > stopRetentionTime) {
			this.startRetentionTime = stopRetentionTime;
			this.stopRetentionTime = startRetentionTime;
		} else {
			this.startRetentionTime = startRetentionTime;
			this.stopRetentionTime = stopRetentionTime;
		}
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	// --------------------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(getClass() != other.getClass()) {
			return false;
		}
		RetentionTimeRange otherRange = (RetentionTimeRange)other;
		return getStartRetentionTime() == otherRange.getStartRetentionTime() && getStopRetentionTime() == otherRange.getStopRetentionTime();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(startRetentionTime).hashCode() + 11 * Integer.valueOf(stopRetentionTime).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startRetentionTime=" + startRetentionTime);
		builder.append(",");
		builder.append("stopRetentionTime=" + stopRetentionTime);
		builder.append("]");
		return builder.toString();
	}
	// --------------------------------------equals, hashCode, toString
}
