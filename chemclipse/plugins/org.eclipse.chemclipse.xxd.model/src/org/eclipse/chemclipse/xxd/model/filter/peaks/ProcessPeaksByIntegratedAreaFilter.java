/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterLocalSettings;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class ProcessPeaksByIntegratedAreaFilter implements IPeakFilter<ProcessPeaksByIntegratedAreaFilterSettings> {

	@Override
	public String getName() {

		return "Integrated Area Peak Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by integrated area values";
	}

	@Override
	public Class<ProcessPeaksByIntegratedAreaFilterSettings> getConfigClass() {

		return ProcessPeaksByIntegratedAreaFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ProcessPeaksByIntegratedAreaFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		ProcessPeaksByAreaFilterLocalSettings localSettings = new ProcessPeaksByAreaFilterLocalSettings();
		localSettings.setIntegratedAreaFilterSettings(configuration);
		ProcessPeaksByAreaFilterUtils.parseLocalSettings(localSettings);

		for(X peak : read) {
			double compareAreaValue = peak.getIntegratedArea();
			ProcessPeaksByAreaFilterUtils.checkAreaAndFilterPeak(compareAreaValue, localSettings, listener, peak);
			subMonitor.worked(1);
		}
	}
}
