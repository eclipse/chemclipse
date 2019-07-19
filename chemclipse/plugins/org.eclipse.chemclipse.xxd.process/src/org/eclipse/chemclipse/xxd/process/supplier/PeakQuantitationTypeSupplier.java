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
 * Christoph Läubrich - add datatypes to supplier
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.exceptions.NoPeakQuantifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakQuantitationTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Peak Quantifier";

	public PeakQuantitationTypeSupplier() {
		super(CATEGORY);
		try {
			IPeakQuantifierSupport support = PeakQuantifier.getPeakQuantifierSupport();
			for(String processorId : support.getAvailablePeakQuantifierIds()) {
				IPeakQuantifierSupplier supplier = support.getPeakQuantifierSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, MSD_CSD_DATA_TYPES);
				processorSupplier.setName(supplier.getPeakQuantifierName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoPeakQuantifierAvailableException e) {
			// no need to worry
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		@SuppressWarnings("unchecked")
		List<IPeak> peaks = (List<IPeak>)chromatogramSelection.getChromatogram().getPeaks();
		if(processSettings instanceof IPeakQuantifierSettings) {
			return getProcessingResult(PeakQuantifier.quantify(peaks, (IPeakQuantifierSettings)processSettings, processorId, monitor), chromatogramSelection);
		} else {
			return getProcessingResult(PeakQuantifier.quantify(peaks, processorId, monitor), chromatogramSelection);
		}
	}
}
