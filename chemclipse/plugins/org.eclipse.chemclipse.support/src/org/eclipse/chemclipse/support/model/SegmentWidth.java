/*******************************************************************************
 * Copyright (c) 2010, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - remove enums
 *******************************************************************************/
package org.eclipse.chemclipse.support.model;

public class SegmentWidth {

	private int width;

	private SegmentWidth(int segmentWidth) {

		this.width = segmentWidth;
	}

	public int getWidth() {

		return width;
	}

	/**
	 * The enum constants have been have been faded out.
	 * Keep compatibility of old process methods and settings.
	 * Call this function to get the updated constant when using SegmentWidth.valueOf(...) and
	 * you assume that an old setting was persisted already.
	 * 
	 * @param value
	 * @return String
	 */
	public static int getAdjustedSetting(String value) {

		switch(value) {
			case "":
				return 0;
			case "WIDTH_5":
				return 5;
			case "WIDTH_7":
				return 7;
			case "WIDTH_9":
				return 9;
			case "WIDTH_11":
				return 11;
			case "WIDTH_13":
				return 13;
			case "WIDTH_15":
				return 15;
			case "WIDTH_17":
				return 17;
			case "WIDTH_19":
				return 19;
			default:
				return Integer.valueOf(value);
		}
	}

	public static int getLower(int segmentWidth) {

		int lower = 0;
		int[] widths = {5, 7, 9, 11, 13, 15, 17, 19};
		for(int w : widths) {
			int current = w;
			if(current < segmentWidth) {
				if(lower == 0 || current > lower) {
					lower = w;
				}
			}
		}
		return lower;
	}
}
