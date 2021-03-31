/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.xxd.model.settings.peaks.HighPassPeaksFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class HighPassPeaksFilter extends AbstractPeakFilter<HighPassPeaksFilterSettings> {

	@Override
	public String getName() {

		return "High Pass Peak(s)";
	}

	@Override
	public String getDescription() {

		return "Keep the n-highest peak(s)";
	}

	@Override
	public Class<HighPassPeaksFilterSettings> getConfigClass() {

		return HighPassPeaksFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, HighPassPeaksFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		int numberHighest = configuration.getNumberHighest();
		XPassPeaksFilter.filterPeaks(listener, messageConsumer, numberHighest, true, monitor);
		//
		resetPeakSelection(listener.getDataContainer());
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}