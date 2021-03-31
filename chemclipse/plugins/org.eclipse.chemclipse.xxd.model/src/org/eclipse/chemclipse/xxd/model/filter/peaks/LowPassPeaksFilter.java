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
import org.eclipse.chemclipse.xxd.model.settings.peaks.LowPassPeaksFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class LowPassPeaksFilter extends AbstractPeakFilter<LowPassPeaksFilterSettings> {

	@Override
	public String getName() {

		return "Low Pass Peak(s)";
	}

	@Override
	public String getDescription() {

		return "Keep the n-lowest peak(s)";
	}

	@Override
	public Class<LowPassPeaksFilterSettings> getConfigClass() {

		return LowPassPeaksFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, LowPassPeaksFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		int numberLowest = configuration.getNumberLowest();
		XPassPeaksFilter.filterPeaks(listener, messageConsumer, numberLowest, false, monitor);
		//
		resetPeakSelection(listener.getDataContainer());
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}