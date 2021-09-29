/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.AbstractChromatogramCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.CalculatorStrategy;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class CalculatorSettings extends AbstractChromatogramCalculatorSettings implements IRetentionIndexFilterSettings {

	@JsonProperty(value = "Calibration File", defaultValue = "")
	@JsonPropertyDescription("Select the file that contains the retention time / index entries.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, extensionNames = {"AMDIS (*.cal)", "AMDIS (*.CAL)"}, validExtensions = {"*.cal", "*.CAL"}, onlyDirectory = false)
	private File calibrationFile;
	@JsonProperty(value = "Calculator Strategy", defaultValue = "FILES")
	@JsonPropertyDescription(value = "The strategy defines the data source, that shall be used for RI calculation.")
	private CalculatorStrategy calculatorStrategy = CalculatorStrategy.FILES;
	@JsonProperty(value = "Use Default Column", defaultValue = "true")
	@JsonPropertyDescription("In case of no match, the default column is used.")
	private boolean useDefaultColumn = true;
	@JsonProperty(value = "Process Referenced Chromatograms", defaultValue = "true")
	@JsonPropertyDescription("Referenced chromatgrams will be also processed.")
	private boolean processReferencedChromatograms = true;
	@JsonIgnore
	private List<String> retentionIndexFiles = new ArrayList<>();

	public File getCalibrationFile() {

		return calibrationFile;
	}

	public void setCalibrationFile(File calibrationFile) {

		this.calibrationFile = calibrationFile;
	}

	public CalculatorStrategy getCalculatorStrategy() {

		return calculatorStrategy;
	}

	public void setCalculatorStrategy(CalculatorStrategy calculatorStrategy) {

		this.calculatorStrategy = calculatorStrategy;
	}

	public boolean isUseDefaultColumn() {

		return useDefaultColumn;
	}

	public void setUseDefaultColumn(boolean useDefaultColumn) {

		this.useDefaultColumn = useDefaultColumn;
	}

	public boolean isProcessReferencedChromatograms() {

		return processReferencedChromatograms;
	}

	public void setProcessReferencedChromatograms(boolean processReferencedChromatograms) {

		this.processReferencedChromatograms = processReferencedChromatograms;
	}

	@Override
	public List<String> getRetentionIndexFiles() {

		if(calibrationFile != null) {
			String file = calibrationFile.getAbsolutePath();
			if(!retentionIndexFiles.contains(file)) {
				retentionIndexFiles.add(file);
			}
		}
		return retentionIndexFiles;
	}

	@Override
	public void setRetentionIndexFiles(List<String> retentionIndexFiles) {

		this.retentionIndexFiles = retentionIndexFiles;
	}
}
