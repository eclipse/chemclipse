/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeletePeaksByTraceFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksByTraceFilter extends AbstractPeakFilter<DeletePeaksByTraceFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peaks by Trace";
	}

	@Override
	public Class<DeletePeaksByTraceFilterSettings> getConfigClass() {

		return DeletePeaksByTraceFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeletePeaksByTraceFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Set<Integer> traces = getTraces(configuration.getTraces());
		List<IPeak> peaksToDelete = new ArrayList<>();
		/*
		 * Collect the peaks
		 */
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		for(IPeak peak : chromatogram.getPeaks(chromatogramSelection)) {
			if(peak instanceof IPeakMSD peakMSD) {
				if(peakContainsTraces(peakMSD, traces)) {
					peaksToDelete.add(peakMSD);
				}
			}
		}
		/*
		 * Delete the peaks
		 */
		if(!peaksToDelete.isEmpty()) {
			deletePeaks(peaksToDelete, chromatogramSelection);
			resetPeakSelection(chromatogramSelection);
		}
	}

	private Set<Integer> getTraces(String content) {

		Set<Integer> traces = new HashSet<>();
		String[] values = content.trim().split("\\s+");
		for(String value : values) {
			try {
				traces.add(Integer.parseInt(value));
			} catch(NumberFormatException e) {
			}
		}
		//
		return traces;
	}

	private boolean peakContainsTraces(IPeakMSD peakMSD, Set<Integer> traces) {

		if(!traces.isEmpty()) {
			IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
			IPeakMassSpectrum peakMassSpectrum = peakModelMSD.getPeakMassSpectrum();
			IExtractedIonSignal extractedIonSignal = peakMassSpectrum.getExtractedIonSignal();
			/*
			 * Return early instead of checking all traces.
			 */
			for(int trace : traces) {
				if(extractedIonSignal.getAbundance(trace) == 0) {
					return false;
				}
			}
			/*
			 * The above condition didn't occur, hence all traces
			 * are available in the peak mass spectrum.
			 */
			return true;
		}
		//
		return false;
	}
}