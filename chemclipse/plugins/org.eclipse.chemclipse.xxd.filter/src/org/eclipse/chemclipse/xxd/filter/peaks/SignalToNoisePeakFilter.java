/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.SignalToNoisePeakFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class SignalToNoisePeakFilter extends AbstractPeakFilter<SignalToNoisePeakFilterSettings> {

	@Override
	public String getName() {

		return "Signal to Noise";
	}

	@Override
	public Class<SignalToNoisePeakFilterSettings> getConfigClass() {

		return SignalToNoisePeakFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, SignalToNoisePeakFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = new SignalToNoisePeakFilterSettings();
		}
		//
		float maxSignalToNoise = configuration.getMaxSignalToNoise();
		float minSignalToNoise = configuration.getMinSignalToNoise();
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(peak instanceof IChromatogramPeak chromatogramPeak) {
				float sn = chromatogramPeak.getSignalToNoiseRatio();
				if(Float.isFinite(sn)) {
					if((Float.isFinite(maxSignalToNoise) && maxSignalToNoise > 0 && sn > maxSignalToNoise) || (Float.isFinite(minSignalToNoise) && minSignalToNoise > 0 && sn < minSignalToNoise)) {
						peaksToDelete.add(peak);
					}
				}
			}
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}
}
