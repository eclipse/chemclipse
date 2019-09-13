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

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Peak Detector";
	private static final String PREFIX_CSD = "PeakDetectorCSD.";
	private static final String PREFIX_MSD = "PeakDetectorMSD.";
	private Set<String> backcompatCSD = new HashSet<>();
	private Set<String> backcompatMSD = new HashSet<>();

	public PeakDetectorTypeSupplier() {
		super(CATEGORY);
		try {
			IPeakDetectorSupport support = PeakDetectorCSD.getPeakDetectorSupport();
			for(String processorId : support.getAvailablePeakDetectorIds()) {
				IPeakDetectorSupplier supplier = support.getPeakDetectorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX_CSD + processorId, CSD_DATA_TYPES, this);
				processorSupplier.setName(supplier.getPeakDetectorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
				backcompatCSD.add(processorId);
			}
		} catch(NoPeakDetectorAvailableException e) {
			// no need to worry
		}
		try {
			IPeakDetectorSupport support = PeakDetectorMSD.getPeakDetectorSupport();
			for(String processorId : support.getAvailablePeakDetectorIds()) {
				IPeakDetectorSupplier supplier = support.getPeakDetectorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX_MSD + processorId, MSD_DATA_TYPES, this);
				processorSupplier.setName(supplier.getPeakDetectorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
				backcompatMSD.add(processorId);
			}
		} catch(NoPeakDetectorAvailableException e) {
			// no need to worry
		}
	}

	@Override
	protected String getBackCompatId(String id) {

		if(backcompatCSD.contains(id)) {
			return PREFIX_CSD + id;
		} else if(backcompatMSD.contains(id)) {
			return PREFIX_MSD + id;
		}
		return super.getBackCompatId(id);
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
				if(processSettings instanceof IPeakDetectorSettingsCSD) {
					return getProcessingResult(PeakDetectorCSD.detect(chromatogramSelectionCSD, (IPeakDetectorSettingsCSD)processSettings, processorId, monitor), chromatogramSelection);
				} else {
					return getProcessingResult(PeakDetectorCSD.detect(chromatogramSelectionCSD, processorId, monitor), chromatogramSelection);
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
				if(processSettings instanceof IPeakDetectorSettingsMSD) {
					return getProcessingResult(PeakDetectorMSD.detect(chromatogramSelectionMSD, (IPeakDetectorSettingsMSD)processSettings, processorId, monitor), chromatogramSelection);
				} else {
					return getProcessingResult(PeakDetectorMSD.detect(chromatogramSelectionMSD, processorId, monitor), chromatogramSelection);
				}
			} else {
				return getProcessingInfoError(processorId);
			}
		} else {
			return null;
		}
	}
}
