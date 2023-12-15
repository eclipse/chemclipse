/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvement update process
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.MinimumTracesFilterSettings;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class MinimumTracesFilter extends AbstractPeakFilter<MinimumTracesFilterSettings> {

	@Override
	public String getName() {

		return "Minimum Number of m/z Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks that are below a given number of ions in the m/z";
	}

	@Override
	public Class<MinimumTracesFilterSettings> getConfigClass() {

		return MinimumTracesFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, MinimumTracesFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		int minIons = configuration.getMinNumberOfIons();
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(peak instanceof IPeakMSD peakMSD) {
				IPeakMassSpectrum massSpectrum = peakMSD.getExtractedMassSpectrum();
				if(massSpectrum.getIons().size() < minIons) {
					peaksToDelete.add(peak);
				}
			} else {
				throw new IllegalArgumentException("Invalid Peak");
			}
			subMonitor.worked(1);
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}

	@Override
	public DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.MSD};
	}
}
