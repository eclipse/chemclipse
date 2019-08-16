/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

public class SegmentValidator {

	/**
	 * There must be at least a minimum amount of crossing (> 50%) for the
	 * segment to be accepted.<br/>
	 * If not, it is assumed that the segment
	 * is part of a peak.<br/>
	 * E.g.: If the segment has 13 scans. There must
	 * be at least 6 crossings for the segment to be accepted.<br/>
	 * length =
	 * 13 - 1 => 12<br/>
	 * length / 2 = 6<br/>
	 * With crossings <= 6 there must
	 * be at least 7 crossings.
	 */
	public boolean acceptSegment(double[] values, double mean) {

		/*
		 * Initialize the starting position.<br/> If the first value is above
		 * the mean value, than set isAbove to true.
		 */
		boolean isAbove = false;
		if(values[0] >= mean) {
			isAbove = true;
		}
		int crossings = 0;
		int length = (values.length - 1);
		/*
		 * Why should i use (values.length - 1)?<br/> Because you have to watch
		 * out for the next value to determine if a crossing will occur.
		 */
		for(int i = 0; i < length; i++) {
			if(isAbove) {
				if(values[i + 1] < mean) {
					crossings++;
					isAbove = false;
				}
			} else {
				if(values[i + 1] > mean) {
					crossings++;
					isAbove = true;
				}
			}
		}
		/*
		 * Decision accept / reject.
		 */
		boolean accept = false;
		if(crossings <= ((length + 1) / 2)) {
			accept = false;
		} else {
			accept = true;
		}
		return accept;
	}
}
