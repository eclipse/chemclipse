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
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeletePeaksByModelFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksByModelFilter extends AbstractPeakFilter<DeletePeaksByModelFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peaks by Model";
	}

	@Override
	public Class<DeletePeaksByModelFilterSettings> getConfigClass() {

		return DeletePeaksByModelFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeletePeaksByModelFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		PeakModelOption peakModelOption = configuration.getPeakModelOption();
		List<IPeak> peaksToDelete = new ArrayList<>();
		/*
		 * Collect the peaks
		 */
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		for(IPeak peak : chromatogram.getPeaks(chromatogramSelection)) {
			IPeakModel peakModel = peak.getPeakModel();
			switch(peakModelOption) {
				case STRICT:
					if(peakModel.isStrictModel()) {
						peaksToDelete.add(peak);
					}
					break;
				default:
					/*
					 * NON-STRICT
					 */
					if(!peakModel.isStrictModel()) {
						peaksToDelete.add(peak);
					}
					break;
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
}