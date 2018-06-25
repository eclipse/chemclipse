/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.core;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.internal.calculator.SubtractCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	private static final String DESCRIPTION = "Subtract Filter Chromatogram";

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Subtract the mass spectrum.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			ISupplierFilterSettings subtractFilterSettings = (ISupplierFilterSettings)chromatogramFilterSettings;
			SubtractCalculator subtractCalculator = new SubtractCalculator();
			subtractCalculator.subtractPeakMassSpectraFromChromatogramSelection(chromatogramSelection, subtractFilterSettings);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been subtracted successfully from the chromatogram selection."));
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of:" + ISupplierFilterSettings.class);
		}
		//
		IChromatogramFilterResult chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The subtract filter has been applied successfully.");
		processingInfo.setProcessingResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}
}
