/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.LowPassPeaksFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class LowPassPeaksFilter extends AbstractPeakFilter<LowPassPeaksFilterSettings> {

	@Override
	public String getName() {

		return "Low Pass Peaks";
	}

	@Override
	public String getDescription() {

		return "Keep the n-lowest peaks";
	}

	@Override
	public Class<LowPassPeaksFilterSettings> getConfigClass() {

		return LowPassPeaksFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, LowPassPeaksFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		int numberLowest = configuration.getNumberLowest();
		List<IPeak> peaksToDelete = XPassPeaksFilter.filterPeaks(peaks, context, numberLowest, false);
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}
}