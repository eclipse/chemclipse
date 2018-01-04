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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
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

	IChromatogramWizardElements getChromatogramWizardElementsMSD();

	IChromatogramWizardElements getChromatogramWizardElementsCSD();

	String getStartIndexName();

	void setStartIndexName(String startIndexName);

	String getStopIndexName();

	void setStopIndexName(String stopIndexName);

	boolean isUseAlreadyDetectedPeaks();

	void setUseAlreadyDetectedPeaks(boolean useAlreadyDetectedPeaks);

	IChromatogramSelectionMSD getChromatogramSelectionMSD();

	void setChromatogramSelectionMSD(IChromatogramSelectionMSD chromatogramSelectionMSD);

	IChromatogramSelectionCSD getChromatogramSelectionCSD();

	void setChromatogramSelectionCSD(IChromatogramSelectionCSD chromatogramSelectionCSD);

	List<IRetentionIndexEntry> getExtractedRetentionIndexEntries();

	void setExtractedRetentionIndexEntries(List<IRetentionIndexEntry> extractedRetentionIndexEntries);

	boolean isRetentionIndexDataValidated();

	void setRetentionIndexDataIsValidated(boolean retentionIndexDataIsValidated);
}
