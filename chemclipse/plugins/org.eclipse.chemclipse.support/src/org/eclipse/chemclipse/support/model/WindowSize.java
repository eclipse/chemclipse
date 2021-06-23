/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - remove the window size enum
 *******************************************************************************/
package org.eclipse.chemclipse.support.model;

public class WindowSize {

	private int size;

	private WindowSize(int size) {

		this.size = size;
	}

	/**
	 * The enum constants have been have been faded out.
	 * Keep compatibility of old process methods and settings.
	 * Call this function to get the updated constant when using WindowSize.valueOf(...) and
	 * you assume that an old setting was persisted already.
	 * 
	 * @param value
	 * @return String
	 */
	public static int getAdjustedSetting(String value) {

		switch(value) {
			case "":
			case "NONE":
				return 0;
			case "WIDTH_3":
			case "SCANS_3":
				return 3;
			case "SCANS_5":
			case "WIDTH_5":
				return 5;
			case "SCANS_7":
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
			case "WIDTH_21":
				return 21;
			case "WIDTH_23":
				return 23;
			case "WIDTH_25":
				return 25;
			case "WIDTH_27":
				return 27;
			case "WIDTH_29":
				return 29;
			case "WIDTH_31":
				return 31;
			case "WIDTH_33":
				return 33;
			case "WIDTH_35":
				return 35;
			case "WIDTH_37":
				return 37;
			case "WIDTH_39":
				return 39;
			case "WIDTH_41":
				return 41;
			case "WIDTH_43":
				return 43;
			case "WIDTH_45":
				return 45;
			default:
				return Integer.valueOf(value);
		}
	}

	public int getSize() {

		return size;
	}
}
