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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import java.util.List;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;

public interface IRetentionIndexWizardElements extends IChromatogramWizardElements {

	float getRetentionIndex(String name);

	String[] getAvailableStandards();

	List<IRetentionIndexEntry> getSelectedRetentionIndexEntries();

	boolean isUseExistingRetentionIndexFile();

	void setUseExistingRetentionIndexFile(boolean useExistingRetentionIndexFile);

	String getFilterPathCalibrationFile();

	void setFilterPathCalibrationFile(String filterPathCalibrationFile);

	String getPathRetentionIndexFile();

	void setPathRetentionIndexFile(String pathRetentionIndexFile);

	boolean isUseMassSpectrometryData();

	void setUseMassSpectrometryData(boolean useMassSpectrometryData);

	String getStartIndexName();

	void setStartIndexName(String startIndexName);

	String getStopIndexName();

	void setStopIndexName(String stopIndexName);

	boolean isUseAlreadyDetectedPeaks();

	void setUseAlreadyDetectedPeaks(boolean useAlreadyDetectedPeaks);

	@SuppressWarnings("rawtypes")
	IChromatogramSelection getChromatogramSelection();

	@SuppressWarnings("rawtypes")
	void setChromatogramSelection(IChromatogramSelection chromatogramSelection);

	ISeparationColumnIndices getSeparationColumnIndices();

	void setSeparationColumnIndices(ISeparationColumnIndices separationColumnIndices);

	boolean isRetentionIndexDataValidated();

	void setRetentionIndexDataIsValidated(boolean retentionIndexDataIsValidated);
}
