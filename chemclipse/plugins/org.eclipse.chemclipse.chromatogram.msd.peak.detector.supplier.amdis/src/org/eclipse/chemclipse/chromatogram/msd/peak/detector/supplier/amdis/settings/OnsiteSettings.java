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

import java.util.HashMap;
import java.util.Map;

public class OnsiteSettings implements IOnsiteSettings {

	private Map<String, String> settingsToModify;

	public OnsiteSettings() {
		/*
		 * Create the settings map
		 * and set the default CDF converter
		 * values.
		 */
		settingsToModify = new HashMap<String, String>();
		//
		settingsToModify.put(KEY_SCAN_DIRECTION, VALUE_SCAN_DIRECTION_NONE);
		settingsToModify.put(KEY_INSTRUMENT_FILE, VALUE_INSTRUMENT_FILE_CDF);
		settingsToModify.put(KEY_INSTRUMENT_TYPE, VALUE_INSTRUMENT_TYPE_QUADRUPOLE);
		//
		settingsToModify.put(KEY_LOW_MZ_AUTO, VALUE_YES);
		settingsToModify.put(KEY_HIGH_MZ_AUTO, VALUE_YES);
		settingsToModify.put(KEY_USE_SOLVENT_TAILING, VALUE_YES);
		settingsToModify.put(KEY_SOLVENT_TAILING_MZ, Integer.toString(VALUE_SOLVENT_TAILING_MZ));
		settingsToModify.put(KEY_USE_COLUMN_BLEED, VALUE_YES);
		settingsToModify.put(KEY_COLUMN_BLEED_MZ, Integer.toString(VALUE_COLUMN_BLEED_MZ));
		settingsToModify.put(KEY_OMIT_MZ, VALUE_NO);
		settingsToModify.put(KEY_OMITED_MZ, "");
	}

	@Override
	public String getLine(String line) {

		if(line != null) {
			String[] values = line.split("=");
			if(values.length == 2) {
				String key = values[0];
				if(settingsToModify.containsKey(key)) {
					line = key + "=" + settingsToModify.get(key);
				}
			}
		}
		return line;
	}

	@Override
	public void setValue(String key, String value) {

		settingsToModify.put(key, value);
	}
}
