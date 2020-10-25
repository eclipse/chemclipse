/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.AdjacentPeakSubtraction;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.InstrumentFile;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.InstrumentType;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.Option;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.Resolution;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.ScanDirection;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.Sensitivity;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.ShapeRequirements;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model.Threshold;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.EnumSelectionRadioButtonsSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SettingsAMDIS extends AbstractProcessSettings {

	@JsonProperty(value = "AMDIS Folder (AMDIS32)", defaultValue = "")
	@JsonPropertyDescription("Select the AMDIS folder, called AMDIS32.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, onlyDirectory = true)
	private File amdisFolder = PreferenceSupplier.getInstallationFolder();
	@JsonProperty(value = "Data Folder (tmp)", defaultValue = "")
	@JsonPropertyDescription("Select the data folder, normally called tmp.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, onlyDirectory = true)
	private File tmpFolder = PreferenceSupplier.getInstallationFolder();
	//
	@JsonProperty(value = "Autodetect low m/z", defaultValue = "YES")
	@EnumSelectionRadioButtonsSettingProperty
	private Option lowMzAuto = Option.YES;
	@JsonProperty(value = "Start m/z", defaultValue = "35")
	@IntSettingsProperty(minValue = 1, maxValue = 1000)
	private int startMZ = 35;
	@JsonProperty(value = "Autodetect high m/z", defaultValue = "YES")
	@EnumSelectionRadioButtonsSettingProperty
	private Option highMzAuto = Option.YES;
	@JsonProperty(value = "Stop m/z", defaultValue = "600")
	@IntSettingsProperty(minValue = 1, maxValue = 1000)
	private int stopMZ = 600;
	//
	@JsonProperty(value = "Omit m/z", defaultValue = "NO")
	@EnumSelectionRadioButtonsSettingProperty
	private Option omitMz = Option.NO;
	@JsonProperty(value = "Up to 8 m/z values separated by a space, 0 to omit TIC.", defaultValue = "0 18 28")
	private String omitedMZ = "0 18 28";
	//
	@JsonProperty(value = "Use solvent tailing", defaultValue = "YES")
	@EnumSelectionRadioButtonsSettingProperty
	private Option useSolventTailing = Option.YES;
	@JsonProperty(value = "Solvent tailing m/z.", defaultValue = "84")
	private String solventTailingMZ = "84";
	@JsonProperty(value = "Use column bleed", defaultValue = "YES")
	@EnumSelectionRadioButtonsSettingProperty
	private Option useColumnBleed = Option.YES;
	@JsonProperty(value = "Column Bleed m/z.", defaultValue = "207")
	private String columnBleedMZ = "207";
	//
	@JsonProperty(value = "Threshold", defaultValue = "MEDIUM")
	@EnumSelectionRadioButtonsSettingProperty
	private Threshold threshold = Threshold.MEDIUM;
	@JsonProperty(value = "Component Width", defaultValue = "12")
	@IntSettingsProperty(minValue = IOnsiteSettings.MIN_PEAK_WIDTH, maxValue = IOnsiteSettings.MAX_PEAK_WIDTH)
	private int componentWidth = 12;
	@JsonProperty(value = "Adjacent Peak Subtract", defaultValue = "NONE")
	@EnumSelectionRadioButtonsSettingProperty
	private AdjacentPeakSubtraction adjactentPeakSubtraction = AdjacentPeakSubtraction.NONE;
	@JsonProperty(value = "Resolution", defaultValue = "MEDIUM")
	@EnumSelectionRadioButtonsSettingProperty
	private Resolution resolution = Resolution.MEDIUM;
	@JsonProperty(value = "Sensitivity", defaultValue = "MEDIUM")
	@EnumSelectionRadioButtonsSettingProperty
	private Sensitivity sensitivity = Sensitivity.MEDIUM;
	@JsonProperty(value = "Shape Requirements", defaultValue = "HIGH")
	@EnumSelectionRadioButtonsSettingProperty
	private ShapeRequirements shapeRequirements = ShapeRequirements.HIGH;
	//
	@JsonIgnore
	private IOnsiteSettings onsiteSettings = null;

	public IOnsiteSettings getOnsiteSettings() {

		if(onsiteSettings == null) {
			/*
			 * Instantiate
			 */
			onsiteSettings = new OnsiteSettings();
			/*
			 * Default CDF settings.
			 */
			onsiteSettings.setValue(IOnsiteSettings.KEY_SCAN_DIRECTION, ScanDirection.NONE.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_INSTRUMENT_FILE, InstrumentFile.CDF.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_INSTRUMENT_TYPE, InstrumentType.QUADRUPOLE.getValue());
			//
			onsiteSettings.setValue(IOnsiteSettings.KEY_LOW_MZ_AUTO, lowMzAuto.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_START_MZ, Integer.toString(startMZ));
			onsiteSettings.setValue(IOnsiteSettings.KEY_HIGH_MZ_AUTO, highMzAuto.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_STOP_MZ, Integer.toString(stopMZ));
			//
			onsiteSettings.setValue(IOnsiteSettings.KEY_OMIT_MZ, omitMz.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_OMITED_MZ, omitedMZ);
			//
			onsiteSettings.setValue(IOnsiteSettings.KEY_USE_SOLVENT_TAILING, useSolventTailing.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_SOLVENT_TAILING_MZ, solventTailingMZ);
			onsiteSettings.setValue(IOnsiteSettings.KEY_USE_COLUMN_BLEED, useColumnBleed.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_COLUMN_BLEED_MZ, columnBleedMZ);
			//
			onsiteSettings.setValue(IOnsiteSettings.KEY_THRESHOLD, threshold.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_PEAK_WIDTH, Integer.toString(componentWidth));
			onsiteSettings.setValue(IOnsiteSettings.KEY_ADJACENT_PEAK_SUBTRACTION, adjactentPeakSubtraction.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_RESOLUTION, resolution.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_SENSITIVITY, sensitivity.getValue());
			onsiteSettings.setValue(IOnsiteSettings.KEY_SHAPE_REQUIREMENTS, shapeRequirements.getValue());
		}
		//
		return onsiteSettings;
	}

	public File getAmdisFolder() {

		return amdisFolder;
	}

	public void setAmdisFolder(File amdisFolder) {

		this.amdisFolder = amdisFolder;
	}

	public File getTmpFolder() {

		return tmpFolder;
	}

	public void setTmpFolder(File tmpFolder) {

		this.tmpFolder = tmpFolder;
	}
}