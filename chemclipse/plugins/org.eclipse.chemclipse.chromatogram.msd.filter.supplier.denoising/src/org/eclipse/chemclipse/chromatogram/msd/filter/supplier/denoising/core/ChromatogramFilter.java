/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use {@link DenoisingFilterResult} instead of plain {@link List}
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support.Denoising;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result.DenoisingFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.FilterSettings;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.FilterException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	@Override
	public IProcessingInfo<?> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<Object> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		//
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettings) {
				try {
					FilterSettings filterSettings = (FilterSettings)chromatogramFilterSettings;
					TraceSettingUtil ionSettingsUtil = new TraceSettingUtil();
					IMarkedIons ionsToRemove = new MarkedIons(ionSettingsUtil.extractTraces(ionSettingsUtil.deserialize(filterSettings.getIonsToRemove())), MarkedTraceModus.INCLUDE);
					IMarkedIons ionsToPreserve = new MarkedIons(ionSettingsUtil.extractTraces(ionSettingsUtil.deserialize(filterSettings.getIonsToPreserve())), MarkedTraceModus.INCLUDE);
					boolean adjustThresholdTransitions = filterSettings.isAdjustThresholdTransitions();
					int numberOfUsedIonsForCoefficient = filterSettings.getNumberOfUsedIonsForCoefficient();
					int segmentWidth = filterSettings.getSegmentWidth();
					//
					List<ICombinedMassSpectrum> noiseMassSpectra = Denoising.applyDenoisingFilter(chromatogramSelection, ionsToRemove, ionsToPreserve, adjustThresholdTransitions, numberOfUsedIonsForCoefficient, segmentWidth, monitor);
					DenoisingFilterResult filterResult = new DenoisingFilterResult(ResultStatus.OK, "The chromatogram selection has been denoised successfully.", noiseMassSpectra);
					IMeasurementResult<?> measurementResult = new MeasurementResult("MS Denoising Filter", "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising", "This list contains the calculated noise mass spectra.", filterResult);
					chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
					processingInfo.setProcessingResult(filterResult);
					chromatogramSelection.getChromatogram().setDirty(true);
				} catch(FilterException e) {
					processingInfo.setProcessingResult(new DenoisingFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
				}
			}
		}
		return processingInfo;
	}

	// TODO JUnit
	@Override
	public IProcessingInfo<?> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettings filterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
}
