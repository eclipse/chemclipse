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
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAsymmetricalPeakShapeFilterUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class ProcessPeaksByAsymmetricalPeakShapeFilter implements IPeakFilter<ProcessPeaksByAsymmetricalPeakShapeFilterSettings> {

	@Override
	public String getName() {

		return "Asymmetrical Peak Shape Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by asymmetrical peak shape (e.g. leading/tailing)";
	}

	@Override
	public Class<ProcessPeaksByAsymmetricalPeakShapeFilterSettings> getConfigClass() {

		return ProcessPeaksByAsymmetricalPeakShapeFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ProcessPeaksByAsymmetricalPeakShapeFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());

		for(X peak : read) {
			ProcessPeaksByAsymmetricalPeakShapeFilterUtils.checkShapeAndFilterPeak(configuration, listener, peak);
			subMonitor.worked(1);
		}
	}
}