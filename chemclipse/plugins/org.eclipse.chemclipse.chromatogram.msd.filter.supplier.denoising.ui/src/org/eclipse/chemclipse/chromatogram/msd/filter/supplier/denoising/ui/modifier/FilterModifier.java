/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.modifier;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result.IDenoisingFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.preferences.IDenoisingEvents;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(FilterModifier.class);
	private static final String description = "Denoising Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising";
	private IEventBroker eventBroker;

	public FilterModifier(IChromatogramSelectionMSD chromatogramSelection, IEventBroker eventBroker) {
		super(chromatogramSelection);
		this.eventBroker = eventBroker;
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)getChromatogramSelection();
			/*
			 * The filter settings.
			 */
			ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
			/*
			 * Set the ions that shall be removed in every case.
			 */
			IMarkedIons ionsToRemove = chromatogramFilterSettings.getIonsToRemove();
			if(PreferenceSupplier.useChromatogramSpecificIons()) {
				PreferenceSupplier.setMarkedIons(ionsToRemove, chromatogramSelection.getSelectedIons().getIonsNominal());
			} else {
				PreferenceSupplier.setMarkedIons(ionsToRemove, PreferenceSupplier.getIonsToRemove());
			}
			/*
			 * Set the ions that shall be preserved in every case.
			 */
			IMarkedIons ionsToPreserve = chromatogramFilterSettings.getIonsToPreserve();
			if(PreferenceSupplier.useChromatogramSpecificIons()) {
				PreferenceSupplier.setMarkedIons(ionsToPreserve, chromatogramSelection.getExcludedIons().getIonsNominal());
			} else {
				PreferenceSupplier.setMarkedIons(ionsToPreserve, PreferenceSupplier.getIonsToPreserve());
			}
			/*
			 * Adjust Threshold transitions.
			 */
			chromatogramFilterSettings.setAdjustThresholdTransitions(PreferenceSupplier.adjustThresholdTransitions());
			/*
			 * The number of used highest ions to calculate the
			 * correlation factor.
			 */
			// chromatogramFilterSettings.setNumberOfUsedIonsForCoefficient(PreferenceSupplier.getNumberOfUsedIonsForCoefficient());
			chromatogramFilterSettings.setNumberOfUsedIonsForCoefficient(1);
			/*
			 * Set the segment width.
			 */
			chromatogramFilterSettings.setSegmentWidth(PreferenceSupplier.getSegmentWidth());
			/*
			 * Apply the filter and show the view.
			 */
			try {
				final IChromatogramFilterProcessingInfo processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, FILTER_ID, monitor);
				IChromatogramFilterResult result = processingInfo.getChromatogramFilterResult();
				if(result instanceof IDenoisingFilterResult) {
					final IDenoisingFilterResult denoisingResult = (IDenoisingFilterResult)result;
					/*
					 * Update the noise mass spectrum view.
					 */
					ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
					/*
					 * Show the interactive view.
					 */
					if(eventBroker != null) {
						List<ICombinedMassSpectrum> noiseMassSpectra = denoisingResult.getNoiseMassSpectra();
						eventBroker.send(IDenoisingEvents.TOPIC_NOISE_MASS_SPECTRA_UPDATE, noiseMassSpectra);
					}
				}
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("Denoising Filter", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
