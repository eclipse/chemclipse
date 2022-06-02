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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.LowPassFilterSettings;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class LowPassPeakFilter implements IPeakFilter<LowPassFilterSettings> {

	@Override
	public String getName() {

		return "Low Pass Ions";
	}

	@Override
	public Class<LowPassFilterSettings> getConfigClass() {

		return LowPassFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, LowPassFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		//
		int number = configuration.getNumberLowest();
		//
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		for(X peak : read) {
			if(peak instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak;
				IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
				XPassFilter.applyLowPass(peakModelMSD.getPeakMassSpectrum(), number);
				subMonitor.worked(1);
			}
		}
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		for(IPeak peak : items) {
			if(!(peak instanceof IPeakMSD)) {
				return false;
			}
		}
		return true;
	}
}
