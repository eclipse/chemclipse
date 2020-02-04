/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich, Dr. Philip Wenig, Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class SignalToNoisePeakFilter implements IPeakFilter<SignalToNoisePeakFilterConfig> {

	@Override
	public String getName() {

		return "Signal to Noise";
	}

	@Override
	public Class<SignalToNoisePeakFilterConfig> getConfigClass() {

		return SignalToNoisePeakFilterConfig.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, SignalToNoisePeakFilterConfig configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		if(configuration == null) {
			configuration = new SignalToNoisePeakFilterConfig();
		}
		float maxSignalToNoise = configuration.getMaxSignalToNoise();
		float minSignalToNoise = configuration.getMinSignalToNoise();
		for(X peak : listener.read()) {
			if(peak instanceof IChromatogramPeak) {
				float sn = ((IChromatogramPeak)peak).getSignalToNoiseRatio();
				if(Float.isFinite(sn)) {
					if((Float.isFinite(maxSignalToNoise) && maxSignalToNoise > 0 && sn > maxSignalToNoise) || (Float.isFinite(minSignalToNoise) && minSignalToNoise > 0 && sn < minSignalToNoise)) {
						listener.delete(peak);
					}
				}
			}
		}
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		for(IPeak peak : items) {
			if(!(peak instanceof IChromatogramPeak)) {
				return false;
			}
		}
		return true;
	}
}
