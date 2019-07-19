/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes to supplier, merge with MSD
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupplierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupportCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.PeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Peak Identifier";
	private static final String PREFIX_CSD = "PeakIdentifierCSD.";
	private static final String PREFIX_MSD = "PeakIdentifierMSD.";
	private Set<String> backcompatCSD = new HashSet<>();
	private Set<String> backcompatMSD = new HashSet<>();

	public PeakIdentifierTypeSupplier() {
		super(CATEGORY);
		try {
			IPeakIdentifierSupportCSD support = PeakIdentifierCSD.getPeakIdentifierSupport();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierCSD supplier = support.getIdentifierSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, CSD_DATA_TYPES);
				processorSupplier.setName(supplier.getIdentifierName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
				backcompatCSD.add(processorId);
			}
		} catch(NoIdentifierAvailableException e) {
			// nothing to do
		}
		try {
			IPeakIdentifierSupportMSD support = PeakIdentifierMSD.getPeakIdentifierSupport();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierMSD supplier = support.getIdentifierSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, MSD_DATA_TYPES);
				processorSupplier.setName(supplier.getIdentifierName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
				backcompatMSD.add(processorId);
			}
		} catch(NoIdentifierAvailableException e) {
			// nothing to do
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		boolean startsWithCSD = processorId.startsWith(PREFIX_CSD);
		boolean startsWithMSD = processorId.startsWith(PREFIX_MSD);
		if(startsWithCSD || backcompatCSD.contains(processorId)) {
			if(startsWithCSD) {
				processorId = processorId.substring(PREFIX_CSD.length());
			}
			if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
				if(processSettings instanceof IPeakIdentifierSettingsCSD) {
					return getProcessingResult(PeakIdentifierCSD.identify(chromatogramSelectionCSD, (IPeakIdentifierSettingsCSD)processSettings, processorId, monitor), chromatogramSelection);
				} else {
					return getProcessingResult(PeakIdentifierCSD.identify(chromatogramSelectionCSD, processorId, monitor), chromatogramSelection);
				}
			} else {
				return getProcessingInfoError(processorId);
			}
		} else if(startsWithMSD || backcompatMSD.contains(processorId)) {
			if(startsWithMSD) {
				processorId = processorId.substring(PREFIX_MSD.length());
			}
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				if(processSettings instanceof IPeakIdentifierSettingsMSD) {
					return getProcessingResult(PeakIdentifierMSD.identify(chromatogramSelectionMSD, (IPeakIdentifierSettingsMSD)processSettings, processorId, monitor), chromatogramSelection);
				} else {
					return getProcessingResult(PeakIdentifierMSD.identify(chromatogramSelectionMSD, processorId, monitor), chromatogramSelection);
				}
			} else {
				return getProcessingInfoError(processorId);
			}
		} else {
			return null;
		}
	}
}
