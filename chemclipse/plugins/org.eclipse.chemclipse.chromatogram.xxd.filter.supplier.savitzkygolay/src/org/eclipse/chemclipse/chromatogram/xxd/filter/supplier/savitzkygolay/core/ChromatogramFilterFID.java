/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;

public class ChromatogramFilterFID extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionCSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo = new ChromatogramFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		//
		ISupplierFilterSettings supplierFilterSettings;
		SavitzkyGolayProcessor processor = new SavitzkyGolayProcessor();
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			supplierFilterSettings = (ISupplierFilterSettings)chromatogramFilterSettings;
		} else {
			supplierFilterSettings = PreferenceSupplier.getSupplierFilterSettings();
		}
		//
		IChromatogramFilterResult chromatogramFilterResult = processor.smooth(chromatogramSelection, false, supplierFilterSettings, monitor);
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getSupplierFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}
}
