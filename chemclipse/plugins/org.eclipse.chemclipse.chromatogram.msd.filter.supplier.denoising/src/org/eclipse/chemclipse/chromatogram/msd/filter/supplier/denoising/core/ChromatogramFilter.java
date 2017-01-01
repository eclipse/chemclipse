/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support.Denoising;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result.DenoisingFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result.IDenoisingFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	private IMarkedIons ionsToRemove;
	private IMarkedIons ionsToPreserve;
	private boolean adjustThresholdTransitions;
	private int numberOfUsedIonsForCoefficient;
	private SegmentWidth segmentWidth;

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo = new ChromatogramFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to denoise the chromatogram selection.
		 */
		setFilterSettings(chromatogramFilterSettings);
		IDenoisingFilterResult chromatogramFilterResult;
		try {
			List<ICombinedMassSpectrum> noiseMassSpectra = Denoising.applyDenoisingFilter(chromatogramSelection, ionsToRemove, ionsToPreserve, adjustThresholdTransitions, numberOfUsedIonsForCoefficient, segmentWidth, monitor);
			chromatogramFilterResult = new DenoisingFilterResult(ResultStatus.OK, "The chromatogram selection has been denoised successfully.", noiseMassSpectra);
		} catch(FilterException e) {
			chromatogramFilterResult = new DenoisingFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	// TODO JUnit
	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	// ----------------------------private methods
	private void setFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		/*
		 * Get the excluded ions instance.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			ISupplierFilterSettings settings = (ISupplierFilterSettings)chromatogramFilterSettings;
			/*
			 * Remove the given ions in all cases.
			 */
			IMarkedIons ionsToRemove = settings.getIonsToRemove();
			if(ionsToRemove != null) {
				this.ionsToRemove = ionsToRemove;
			}
			/*
			 * Preserve the given ions in all cases.
			 */
			IMarkedIons ionsToPreserve = settings.getIonsToPreserve();
			if(ionsToPreserve != null) {
				this.ionsToPreserve = ionsToPreserve;
			}
			/*
			 * Adjust threshold transitions.
			 */
			adjustThresholdTransitions = settings.getAdjustThresholdTransitions();
			/*
			 * Number of ions for coefficient.
			 */
			numberOfUsedIonsForCoefficient = settings.getNumberOfUsedIonsForCoefficient();
			/*
			 * SegmentWidth
			 */
			segmentWidth = settings.getSegmentWidth();
		}
	}
}
