/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;

public interface IRetentionIndexWizardElements extends IChromatogramWizardElements {

	String[] getAvailableStandards();

	boolean isUseExistingRetentionIndexFile();

	void setUseExistingRetentionIndexFile(boolean useExistingRetentionIndexFile);

	String getFilterPathCalibrationFile();

	void setFilterPathCalibrationFile(String filterPathCalibrationFile);

	String getPathRetentionIndexFile();

	void setPathRetentionIndexFile(String pathRetentionIndexFile);

	String getStartIndexName();

	void setStartIndexName(String startIndexName);

	String getStopIndexName();

	void setStopIndexName(String stopIndexName);

	boolean isUseAlreadyDetectedPeaks();

	void setUseAlreadyDetectedPeaks(boolean useAlreadyDetectedPeaks);

	IChromatogramMSD getChromatogramMSD();

	void setChromatogramMSD(IChromatogramMSD chromatogramMSD);
}
