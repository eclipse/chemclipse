/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexCalculator;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;

public class RetentionIndexWizardElements extends ChromatogramWizardElements {

	private String[] availableStandards = RetentionIndexCalculator.getStandards();
	private boolean useExistingRetentionIndexFile = false;
	private String filterPathRetentionIndexFile; // used for File Dialog
	private String pathRetentionIndexFile = "";
	private boolean useMassSpectrometryData = true;
	private String startIndexName = "";
	private String stopIndexName = "";
	private boolean useAlreadyDetectedPeaks = false;
	//
	private IChromatogramSelection<IPeak, IChromatogram<IPeak>> chromatogramSelection;
	private ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
	//
	private boolean retentionIndexDataIsValidated = false;

	public List<String> getSelectedIndices() {

		List<String> selectedIndices = new ArrayList<>();
		//
		boolean addIndex = false;
		for(String standard : availableStandards) {
			if(standard.equals(startIndexName)) {
				/*
				 * E.g.: "C6 (Hexane)"
				 */
				addIndex = true;
			} else if(standard.equals(stopIndexName)) {
				/*
				 * E.g.: "C27 (Heptacosane)"
				 */
				selectedIndices.add(standard);
				addIndex = false;
			}
			/*
			 * Add the index if it's within the selection.
			 */
			if(addIndex) {
				selectedIndices.add(standard);
			}
		}
		//
		return selectedIndices;
	}

	public String[] getAvailableStandards() {

		return availableStandards;
	}

	public boolean isUseExistingRetentionIndexFile() {

		return useExistingRetentionIndexFile;
	}

	public void setUseExistingRetentionIndexFile(boolean useExistingRetentionIndexFile) {

		this.useExistingRetentionIndexFile = useExistingRetentionIndexFile;
	}

	public String getFilterPathCalibrationFile() {

		return filterPathRetentionIndexFile;
	}

	public void setFilterPathCalibrationFile(String filterPathCalibrationFile) {

		this.filterPathRetentionIndexFile = filterPathCalibrationFile;
	}

	public String getPathRetentionIndexFile() {

		return pathRetentionIndexFile;
	}

	public void setPathRetentionIndexFile(String pathRetentionIndexFile) {

		this.pathRetentionIndexFile = pathRetentionIndexFile;
	}

	public boolean isUseMassSpectrometryData() {

		return useMassSpectrometryData;
	}

	public void setUseMassSpectrometryData(boolean useMassSpectrometryData) {

		this.useMassSpectrometryData = useMassSpectrometryData;
	}

	public String getStartIndexName() {

		return startIndexName;
	}

	public void setStartIndexName(String startIndexName) {

		this.startIndexName = startIndexName;
	}

	public String getStopIndexName() {

		return stopIndexName;
	}

	public void setStopIndexName(String stopIndexName) {

		this.stopIndexName = stopIndexName;
	}

	public boolean isUseAlreadyDetectedPeaks() {

		return useAlreadyDetectedPeaks;
	}

	public void setUseAlreadyDetectedPeaks(boolean useAlreadyDetectedPeaks) {

		this.useAlreadyDetectedPeaks = useAlreadyDetectedPeaks;
	}

	public IChromatogramSelection<IPeak, IChromatogram<IPeak>> getChromatogramSelection() {

		return chromatogramSelection;
	}

	public void setChromatogramSelection(IChromatogramSelection<IPeak, IChromatogram<IPeak>> chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	public ISeparationColumnIndices getSeparationColumnIndices() {

		return separationColumnIndices;
	}

	public void setSeparationColumnIndices(ISeparationColumnIndices separationColumnIndices) {

		this.separationColumnIndices = separationColumnIndices;
	}

	public boolean isRetentionIndexDataValidated() {

		return retentionIndexDataIsValidated;
	}

	public void setRetentionIndexDataIsValidated(boolean retentionIndexDataIsValidated) {

		this.retentionIndexDataIsValidated = retentionIndexDataIsValidated;
	}
}
