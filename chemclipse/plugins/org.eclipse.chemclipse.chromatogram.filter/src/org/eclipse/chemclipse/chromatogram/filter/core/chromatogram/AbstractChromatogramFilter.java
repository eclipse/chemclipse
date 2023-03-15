/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractChromatogramFilter<T extends IPeak, C extends IChromatogram<T>, R> implements IChromatogramFilter<T, C, R> {

	private static final String DESCRIPTION = Messages.chromatogramFilter;

	@Override
	public IProcessingInfo<R> validate(IChromatogramSelection<T, C> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings) {

		IProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validateChromatogramSelection(chromatogramSelection));
		processingInfo.addMessages(validateFilterSettings(chromatogramFilterSettings));
		return processingInfo;
	}

	@Override
	public IProcessingInfo<R> validateChromatogramSelection(IChromatogramSelection<T, C> chromatogramSelection) {

		IProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTION, Messages.chromatogramSelectionInvalid);
		} else {
			if(chromatogramSelection.getChromatogram() == null) {
				processingInfo.addErrorMessage(DESCRIPTION, Messages.invalidChromatogram);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<R> validateFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		IProcessingInfo<R> processingInfo = new ProcessingInfo<>();
		if(chromatogramFilterSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, Messages.invalidFilterSettings);
		}
		return processingInfo;
	}
}