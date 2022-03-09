/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

public class RetentionIndex implements IRetentionIndex, Comparable<IRetentionIndex> {

	public static final int MIN_RETENTION_TIME = 0;
	public static final int MAX_RETENTION_TIME = Integer.MAX_VALUE;
	private int retentionTime = 0;
	public static final float MIN_INDEX = 0.0f;
	public static final float MAX_INDEX = Float.MAX_VALUE;
	private float index = 0.0f;
	private String name = "";

	public RetentionIndex() {
	}

	public RetentionIndex(final int retentionTime, final float index) {
		setCkeckedRetentionTime(retentionTime);
		setCheckedIndex(index);
	}

	public RetentionIndex(final int retentionTime, final float index, final String name) {
		this(retentionTime, index);
		this.name = name;
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	/**
	 * The retention time range should be scalable. eselmeister 19.11.2008
	 */
	@Override
	public void setRetentionTime(final int retentionTime) {

		setCkeckedRetentionTime(retentionTime);
	}

	/**
	 * Checks the retention time to min and max values and sets it if valid.
	 * 
	 * @param retentionTime
	 */
	// @edu.umd.cs.findbugs.annotations.SuppressWarnings("INT_VACUOUS_COMPARISON")
	private void setCkeckedRetentionTime(final int retentionTime) {

		/*
		 * Findbugs finds a "INT_VACUOUS_COMPARISON" bug here, but it's ok.<br/>
		 */
		if(retentionTime >= MIN_RETENTION_TIME && retentionTime <= MAX_RETENTION_TIME) {
			this.retentionTime = retentionTime;
		}
	}

	@Override
	public float getIndex() {

		return index;
	}

	@Override
	public void setIndex(final float index) {

		setCheckedIndex(index);
	}

	/**
	 * Checks the index to min and max values and sets it if valid.
	 * 
	 * @param index
	 */
	private void setCheckedIndex(final float index) {

		if(index >= MIN_INDEX && index <= MAX_INDEX) {
			this.index = index;
		}
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(final String name) {

		this.name = name;
	}

	/**
	 * Compares the index of two retention index entries. Returns the following
	 * values: a.compareTo(b) 0 a == b : 2000 == 2000 -1 a < b : 1000 < 2000 +1
	 * a > b : 3000 > 2000
	 */
	@Override
	public int compareTo(final IRetentionIndex other) {

		return (int)(this.index - other.getIndex());
	}

	@Override
	public boolean equals(final Object otherObject) {

		// Are both objects identical. Return true if yes.
		if(this == otherObject) {
			return true;
		}
		// Is the other object not instantiated?
		if(otherObject == null) {
			return false;
		}
		// If the classes are not the same, the objects could not be the same.
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		// otherObject is not null and must be a RetentionIndex
		RetentionIndex other = (RetentionIndex)otherObject;
		// Are the members index, retentionTime, name the same?
		return index == other.index && retentionTime == other.retentionTime && name.equals(other.name);
	}

	@Override
	public int hashCode() {

		// index, retentionTime, name
		return 7 * Double.valueOf(index).hashCode() + 9 * Integer.valueOf(retentionTime).hashCode() + 11 * name.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("index=" + this.index);
		builder.append(",");
		builder.append("retentionTime=" + this.retentionTime);
		builder.append(",");
		builder.append("name=" + this.name);
		builder.append("]");
		return builder.toString();
	}
}
