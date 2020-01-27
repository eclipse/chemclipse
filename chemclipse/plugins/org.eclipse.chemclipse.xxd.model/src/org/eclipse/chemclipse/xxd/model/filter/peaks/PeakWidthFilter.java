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
import java.util.Comparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class PeakWidthFilter implements IPeakFilter<PeakWidthFilterSettings> {

	@Override
	public String getName() {

		return "Peak Width Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by peak Width";
	}

	@Override
	public Class<PeakWidthFilterSettings> getConfigClass() {

		return PeakWidthFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, PeakWidthFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());

		for(X peak : read) {
			applySelectedOptions(configuration, listener, peak);
			subMonitor.worked(1);
		}
	}

	private static <X extends IPeak> void applySelectedOptions(PeakWidthFilterSettings settings, CRUDListener<X, IPeakModel> listener, X peak) {

		switch (settings.getFilterSelectionCriterion()){
		case WIDTH_SMALLER_THAN_LIMIT:
			if(Integer.compare(peak.getPeakModel().getWidthByInflectionPoints(), getWidthInMilliseconds(settings))<0) {
				deleteOrDisablePeak(listener, settings, peak);
			}
			break;
		case WIDTH_GREATER_THAN_LIMIT:
			if(Integer.compare(peak.getPeakModel().getWidthByInflectionPoints(), getWidthInMilliseconds(settings))>0) {
				deleteOrDisablePeak(listener, settings, peak);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static int getWidthInMilliseconds(PeakWidthFilterSettings settings) {

		return (int) (settings.getWidthValue() * 60000);
	}

	private static <X extends IPeak> void deleteOrDisablePeak(CRUDListener<X, IPeakModel> listener, PeakWidthFilterSettings settings, X peak) {

		switch (settings.getFilterTreatmentOption()) {
		case DELETE_PEAK:
			listener.delete(peak);
			break;
		case DISABLE_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
