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
import org.eclipse.chemclipse.xxd.model.support.ValueFilterTreatmentOption;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class IntegratedAreaFilter implements IPeakFilter<IntegratedAreaFilterSettings> {

	@Override
	public String getName() {

		return "Integrated Area Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by integrated area values";
	}

	@Override
	public Class<IntegratedAreaFilterSettings> getConfigClass() {

		return IntegratedAreaFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, IntegratedAreaFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());

		for(X peak : read) {
			applySelectedOptions(peak.getIntegratedArea(), configuration, listener, peak);
			checkForKeepOption(listener, configuration, peak);
			subMonitor.worked(1);
		}
	}

	private static <X extends IPeak> void applySelectedOptions(double peakValue, IntegratedAreaFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak) {

		switch (configuration.getFilterSelectionCriterion()) {
		case AREA_LESS_THAN_MINIMUM:
			if(Double.compare(peakValue, configuration.getMinimumAreaValue())<0) {
				processPeak(listener, configuration, peak);
			}
			break;
		case AREA_GREATER_THAN_MAXIMUM:
			if(Double.compare(peakValue, configuration.getMaximumAreaValue())>0) {
				processPeak(listener, configuration, peak);
			}
			break;
		case AREA_NOT_WITHIN_RANGE:
			if(Double.compare(peakValue, configuration.getMinimumAreaValue())<0 || Double.compare(peakValue, configuration.getMaximumAreaValue())>0) {
				processPeak(listener, configuration, peak);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void processPeak(CRUDListener<X, IPeakModel> listener, IntegratedAreaFilterSettings configuration, X peak) {

		switch (configuration.getFilterTreatmentOption()) {
		case ENABLE_PEAK:
			peak.setActiveForAnalysis(true);
			listener.updated(peak);
			break;
		case DEACTIVATE_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		case KEEP_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		case DELETE_PEAK:
			listener.delete(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}

	private static <X extends IPeak> void checkForKeepOption(CRUDListener<X, IPeakModel> listener, IntegratedAreaFilterSettings configuration, X peak) {

		if(configuration.getFilterTreatmentOption()==ValueFilterTreatmentOption.KEEP_PEAK) {
			if(peak.isActiveForAnalysis()==true) {
				listener.delete(peak);
			} else {
				peak.setActiveForAnalysis(true);
				listener.updated(peak);
			}
		}
	}
}
