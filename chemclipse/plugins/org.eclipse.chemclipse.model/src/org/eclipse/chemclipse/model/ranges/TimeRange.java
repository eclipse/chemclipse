/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ranges;

/**
 * This time range handles the start/center/stop information e.g.
 * for an expected peak. It's identity is defined by the identifier.
 * The time range should be used for positive ranges only as the
 * validation tries to correct odd values.
 */
public class TimeRange {

	public enum Marker {
		START, //
		MAXIMUM, //
		STOP //
	}

	public static final float MINUTE_FACTOR = 60000.0f;
	//
	private String identifier = "";
	/*
	 * Milliseconds
	 * ---
	 * Maximum is currently not used, but might
	 * be important when processes are used that
	 * need a user selected maximum position.
	 */
	private int start = 0;
	private int maximum = 0;
	private int stop = 0;
	/*
	 * Transient
	 */
	private boolean locked = false;

	/**
	 * Start / Stop in milliseconds.
	 * Auto-correction of start/stop if performed.
	 * 
	 * @param start
	 * @param stop
	 */
	public TimeRange(String identifier, int start, int stop) throws IllegalArgumentException {

		this(identifier, start, calculateCenter(start, stop), stop);
	}

	public TimeRange(TimeRange timeRange) throws IllegalArgumentException {

		this(timeRange.getIdentifier(), timeRange.getStart(), timeRange.getMaximum(), timeRange.getStop());
	}

	/**
	 * Start / Maximum / Stop in milliseconds.
	 * Auto-correction of start/maximum/stop if performed.
	 * 
	 * @param start
	 * @param maximum
	 * @param stop
	 */
	public TimeRange(String identifier, int start, int maximum, int stop) throws IllegalArgumentException {

		/*
		 * Validity checks.
		 */
		if(start < 0 || maximum < 0 || stop < 0) {
			throw new IllegalArgumentException("Start/Maximum/Stop must be >= 0.");
		}
		//
		this.identifier = (identifier != null) ? identifier : "";
		this.start = Math.min(start, stop);
		this.stop = Math.max(start, stop);
		updateMaximum();
	}

	public String getIdentifier() {

		return identifier;
	}

	public int getStart() {

		return start;
	}

	public int getCenter() {

		return calculateCenter(start, stop);
	}

	public int getMaximum() {

		return maximum;
	}

	public int getStop() {

		return stop;
	}

	public void update(int start, int stop) {

		this.start = Math.min(start, stop);
		this.stop = Math.max(start, stop);
		updateMaximum();
	}

	public void update(int start, int maximum, int stop) {

		update(start, stop);
		updateMaximum(maximum);
	}

	/**
	 * Updates the start time.
	 * The start time must be <= stop time, otherwise no update is performed.
	 * 
	 * @param start
	 */
	public void updateStart(int start) {

		if(start <= stop) {
			this.start = start;
			if(start >= maximum) {
				updateMaximum();
			}
		}
	}

	/**
	 * Calculates the maximum, based on start/stop.
	 */
	public void updateMaximum() {

		this.maximum = (int)calculateCenter(start, stop);
	}

	/**
	 * Updates the maximum time.
	 * The maximum time must be >= start time and <= stop time, otherwise no update is performed.
	 * 
	 * @param maximum
	 */
	public void updateMaximum(int maximum) {

		if(maximum >= start && maximum <= stop) {
			this.maximum = maximum;
		}
	}

	/**
	 * Updates the stop time.
	 * The stop time must be >= start time, otherwise no update is performed.
	 * 
	 * @param stop
	 */
	public void updateStop(int stop) {

		if(stop >= start) {
			this.stop = stop;
			if(stop <= maximum) {
				updateMaximum();
			}
		}
	}

	public boolean isLocked() {

		return locked;
	}

	public void setLocked(boolean locked) {

		this.locked = locked;
	}

	private static int calculateCenter(int start, int stop) {

		int min = Math.min(start, stop);
		int max = Math.max(start, stop);
		//
		int center = 0;
		int delta = max - min;
		if(delta != 0) {
			/*
			 * min < max
			 */
			center = delta / 2 + min;
		} else {
			/*
			 * min == max
			 */
			center = min;
		}
		//
		return center;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		TimeRange other = (TimeRange)obj;
		if(identifier == null) {
			if(other.identifier != null)
				return false;
		} else if(!identifier.equals(other.identifier))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "TimeRange [identifier=" + identifier + ", start=" + start + ", maximum=" + maximum + ", stop=" + stop + "]";
	}
}