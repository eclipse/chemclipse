/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

/**
 * @author Philip Wenig
 */
public abstract class AbstractChromatogramFilterCSD implements IChromatogramFilterCSD {

	private static final String DESCRIPTION = "Chromatogram Filter";

	@Override
	public IProcessingInfo<?> validate(IChromatogramSelectionCSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validateChromatogramSelection(chromatogramSelection));
		processingInfo.addMessages(validateFilterSettings(chromatogramFilterSettings));
		return processingInfo;
	}

	// --------------------------------------------private methods
	/**
	 * Validates that chromatogram selection and the stored chromatogram are not
	 * null.
	 * 
	 * @param chromatogramSelection
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validateChromatogramSelection(IChromatogramSelectionCSD chromatogramSelection) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection is not valid.");
		} else {
			if(chromatogramSelection.getChromatogram() == null) {
				processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram is not valid.");
			}
		}
		return processingInfo;
	}

	/**
	 * Validates that the filter settings are not null.
	 * 
	 * @param chromatogramFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validateFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(chromatogramFilterSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings are not valid.");
		}
		return processingInfo;
	}
	// --------------------------------------------private methods
}
