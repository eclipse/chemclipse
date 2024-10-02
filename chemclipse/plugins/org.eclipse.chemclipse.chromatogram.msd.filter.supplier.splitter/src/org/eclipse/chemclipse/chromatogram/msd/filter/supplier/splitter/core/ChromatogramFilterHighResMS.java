/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsHighResMS;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.support.HighResolutionSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.traces.TraceFactory;
import org.eclipse.chemclipse.support.traces.TraceHighResMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterHighResMS extends AbstractChromatogramFilterMSD {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> validation = validate(chromatogramSelection, chromatogramFilterSettings);
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validation);
		//
		if(!processingInfo.hasErrorMessages()) {
			HeaderField headerField = getHeaderField(chromatogramFilterSettings);
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
				/*
				 * Split selected traces.
				 */
				Set<TraceHighResMSD> specificTraces = getSpecificTraces(chromatogramFilterSettings);
				if(!specificTraces.isEmpty()) {
					List<IChromatogramMSD> chromatograms = HighResolutionSupport.extractHighResolutionData(chromatogramMSD, headerField, specificTraces);
					for(IChromatogramMSD chromatogramReferenceMSD : chromatograms) {
						chromatogramMSD.addReferencedChromatogram(chromatogramReferenceMSD);
					}
				}
			}
			//
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram was splitted into MS/MS reference chromatograms."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsHighResMS splitterSettings = PreferenceSupplier.getFilterSettingsHighResMS();
		return applyFilter(chromatogramSelection, splitterSettings, monitor);
	}

	private int getBinning(IChromatogramFilterSettings chromatogramFilterSettings) {

		int binning = 0;
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			binning = filterSettingsHighResMS.getBinning();
		}
		//
		return binning;
	}

	private HeaderField getHeaderField(IChromatogramFilterSettings chromatogramFilterSettings) {

		HeaderField headerField = HeaderField.DATA_NAME;
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			headerField = filterSettingsHighResMS.getHeaderField();
		}
		//
		return headerField;
	}

	/*
	 */
	private Set<TraceHighResMSD> getSpecificTraces(IChromatogramFilterSettings chromatogramFilterSettings) {

		int ppm = getBinning(chromatogramFilterSettings);
		Set<TraceHighResMSD> specificTransitions = new HashSet<>();
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			String content = filterSettingsHighResMS.getSpecificTraces();
			for(TraceHighResMSD traceHighResMSD : TraceFactory.parseTraces(content, TraceHighResMSD.class)) {
				/*
				 * Auto-adjust binning if no value was set yet.
				 */
				if(traceHighResMSD.getPPM() == 0) {
					traceHighResMSD.setPPM(ppm);
				}
				//
				specificTransitions.add(traceHighResMSD);
			}
		}
		//
		return specificTransitions;
	}
}