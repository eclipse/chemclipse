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
package org.eclipse.chemclipse.numeric.statistics;

/**
 * Determines the window size for the moving average calculation.
 */
public enum WindowSize {
	/*
	 * SCANS was used before and might be used in the settings.
	 * Find a way to safely handle old values.
	 */
	WIDTH_3(3), // SCANS_3
	WIDTH_5(5), // SCANS_5
	WIDTH_7(7), // SCANS_7
	WIDTH_9(9), //
	WIDTH_11(11), //
	WIDTH_13(13), //
	WIDTH_15(15), //
	WIDTH_17(17), //
	WIDTH_19(19), //
	WIDTH_21(21), //
	WIDTH_23(23), //
	WIDTH_25(25), //
	WIDTH_27(27), //
	WIDTH_29(29), //
	WIDTH_31(31), //
	WIDTH_33(33), //
	WIDTH_35(35), //
	WIDTH_37(37), //
	WIDTH_39(39), //
	WIDTH_41(41), //
	WIDTH_43(43), //
	WIDTH_45(45);

	private int size;

	private WindowSize(int size) {
		this.size = size;
	}

	/**
	 * Some enum constants have been refactored.
	 * Call this method to get the updated constant when using WindowSize.valueOf(...) and
	 * you assume that an old setting was persisted already.
	 * 
	 * @param value
	 * @return String
	 */
	public static String getAdjustedSetting(String value) {

		if("SCANS_3".equals(value)) {
			return "WIDTH_3";
		} else if("SCANS_5".equals(value)) {
			return "WIDTH_5";
		} else if("SCANS_7".equals(value)) {
			return "WIDTH_3";
		} else {
			return value;
		}
	}

	public int getSize() {

		return size;
	}

	public static String[][] getElements() {

		String[][] elements = new String[values().length][2];
		int counter = 0;
		for(WindowSize windowSize : values()) {
			elements[counter][0] = Integer.toString(windowSize.getSize());
			elements[counter][1] = windowSize.name();
			counter++;
		}
		return elements;
	}
}
