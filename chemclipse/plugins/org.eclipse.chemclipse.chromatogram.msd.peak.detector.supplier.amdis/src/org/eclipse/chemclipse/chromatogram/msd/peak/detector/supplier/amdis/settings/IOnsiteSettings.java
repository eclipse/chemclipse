/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

public interface IOnsiteSettings {

	String VALUE_YES = "1";
	String VALUE_NO = "0";
	//
	String KEY_LOW_MZ_AUTO = "LOWMZAUTO"; // No = 0, Yes = 1
	String KEY_START_MZ = "LOMASS";
	String KEY_HIGH_MZ_AUTO = "HIGHMZAUTO"; // No = 0, Yes = 1
	String KEY_STOP_MZ = "MXMASS";
	//
	String KEY_OMIT_MZ = "OMITMZ"; // 0 = no, 1 = yes
	String KEY_OMITED_MZ = "OMITEDMZ"; // 0 = TIC ... 58 59 60
	//
	String KEY_USE_SOLVENT_TAILING = "USESTAIL"; // No = 0, Yes = 1
	String KEY_SOLVENT_TAILING_MZ = "STAILMZ"; // 84
	int VALUE_SOLVENT_TAILING_MZ = 84;
	String KEY_USE_COLUMN_BLEED = "USECOLUMNBLEED";// No = 0, Yes = 1
	String KEY_COLUMN_BLEED_MZ = "BLEEDMZ"; // 207
	int VALUE_COLUMN_BLEED_MZ = 207;
	//
	String KEY_THRESHOLD = "THRESHOLD"; // High = 4, Medium = 3, Low = 2, Off = 1
	String VALUE_THRESHOLD_HIGH = "4";
	String VALUE_THRESHOLD_MEDIUM = "3";
	String VALUE_THRESHOLD_LOW = "2";
	String VALUE_THRESHOLD_OFF = "1";
	String[][] THRESHOLD_VALUES = new String[][]{//
			{"High", VALUE_THRESHOLD_HIGH}, //
			{"Medium", VALUE_THRESHOLD_MEDIUM}, //
			{"Low", VALUE_THRESHOLD_LOW}, //
			{"Off", VALUE_THRESHOLD_OFF}//
	};
	//
	String KEY_PEAK_WIDTH = "PEAKWIDTH"; // 12 ... 32
	int VALUE_DEF_PEAK_WIDTH = 12;
	int VALUE_MIN_PEAK_WIDTH = 12;
	int VALUE_MAX_PEAK_WIDTH = 32;
	//
	String KEY_ADJACENT_PEAK_SUBTRACTION = "DECLEVEL"; // Two = 0, One = 1, None = 2
	String VALUE_ADJACENT_PEAK_SUBTRACTION_TWO = "0";
	String VALUE_ADJACENT_PEAK_SUBTRACTION_ONE = "1";
	String VALUE_ADJACENT_PEAK_SUBTRACTION_NONE = "2";
	String[][] ADJACENT_PEAK_SUBTRACTION_VALUES = new String[][]{//
			{"Two", VALUE_ADJACENT_PEAK_SUBTRACTION_TWO}, //
			{"One", VALUE_ADJACENT_PEAK_SUBTRACTION_ONE}, //
			{"None", VALUE_ADJACENT_PEAK_SUBTRACTION_NONE}//
	};
	//
	String KEY_RESOLUTION = "RESOLUTION"; // High = 0, Medium = 1, Low = 2
	String VALUE_RESOLUTION_HIGH = "0";
	String VALUE_RESOLUTION_MEDIUM = "1";
	String VALUE_RESOLUTION_LOW = "2";
	String[][] RESOLUTION_VALUES = new String[][]{//
			{"High", VALUE_RESOLUTION_HIGH}, //
			{"Medium", VALUE_RESOLUTION_MEDIUM}, //
			{"Low", VALUE_RESOLUTION_LOW}//
	};
	//
	String KEY_SENSITIVITY = "SENSIT"; // Very High = 60, High = 30, Medium = 10, Low = 3, Very Low = 1
	String VALUE_SENSITIVITY_VERY_HIGH = "60";
	String VALUE_SENSITIVITY_HIGH = "30";
	String VALUE_SENSITIVITY_MEDIUM = "10";
	String VALUE_SENSITIVITY_LOW = "3";
	String VALUE_SENSITIVITY_VERY_LOW = "1";
	String[][] SENSITIVITY_VALUES = new String[][]{//
			{"Very High", VALUE_SENSITIVITY_VERY_HIGH}, //
			{"High", VALUE_SENSITIVITY_HIGH}, //
			{"Medium", VALUE_SENSITIVITY_MEDIUM}, //
			{"Low", VALUE_SENSITIVITY_LOW}, //
			{"Very Low", VALUE_SENSITIVITY_VERY_LOW}//
	};
	//
	String KEY_SHAPE_REQUIREMENTS = "PEAKSHAPE"; // High = 2, Medium = 1, Low = 0
	String VALUE_SHAPE_REQUIREMENTS_HIGH = "2";
	String VALUE_SHAPE_REQUIREMENTS_MEDIUM = "1";
	String VALUE_SHAPE_REQUIREMENTS_LOW = "0";
	String[][] SHAPE_REQUIREMENTS_VALUES = new String[][]{//
			{"High", VALUE_SHAPE_REQUIREMENTS_HIGH}, //
			{"Medium", VALUE_SHAPE_REQUIREMENTS_MEDIUM}, //
			{"Low", VALUE_SHAPE_REQUIREMENTS_LOW}//
	};
	//
	String KEY_SCAN_DIRECTION = "SCANDIR"; // High to Low = -1, None = 0, Low to High = 1
	String VALUE_SCAN_DIRECTION_HIGH_TO_LOW = "-1";
	String VALUE_SCAN_DIRECTION_NONE = "0";
	String VALUE_SCAN_DIRECTION_LOW_TO_HIGH = "1";
	//
	String KEY_INSTRUMENT_FILE = "INFILE"; // CDF = 2
	String VALUE_INSTRUMENT_FILE_CDF = "2";
	//
	String KEY_INSTRUMENT_TYPE = "INSTYPE"; // Quadrupole = 0, Ion Trap = 1, Magnetic Sector = 2, SIM = 3
	String VALUE_INSTRUMENT_TYPE_QUADRUPOLE = "0";
	String VALUE_INSTRUMENT_TYPE_ION_TRAP = "1";
	String VALUE_INSTRUMENT_TYPE_MAGNETIC_SECTOR = "2";
	String VALUE_INSTRUMENT_TYPE_SIM = "3";

	String getLine(String line);

	void setValue(String key, String value);
}
