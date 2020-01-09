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
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterAreaTypeSelection;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterSelectionCriterion;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterTreatmentOption;
import org.eclipse.chemclipse.xxd.model.support.ProcessPeaksByAreaFilterUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class ProcessPeaksByAreaFilter implements IPeakFilter<ProcessPeaksByAreaFilterSettings> {

	@Override
	public String getName() {

		return "Peak Area Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by integrated or percentage area values";
	}

	@Override
	public Class<ProcessPeaksByAreaFilterSettings> getConfigClass() {

		return ProcessPeaksByAreaFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ProcessPeaksByAreaFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {
		
		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}

		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		ProcessPeaksByAreaFilterSelectionCriterion criterion = configuration.getFilterSelectionCriterion();
		ProcessPeaksByAreaFilterTreatmentOption option = configuration.getFilterTreatmentOption();
		ProcessPeaksByAreaFilterAreaTypeSelection selection = configuration.getFilterAreaTypeSelection();
		
		double[] areaLimits = ProcessPeaksByAreaFilterUtils.getAreaLimits(configuration, selection);
		
		for(X peak : read) {
			double compareAreaValue = ProcessPeaksByAreaFilterUtils.calculateAreaCompareValue(selection, peak, read);
			if(criterion==ProcessPeaksByAreaFilterSelectionCriterion.AREA_LESS_THAN_MINIMUM) {
				compareAreaToLimit(compareAreaValue, areaLimits[0], option, listener, peak, true);
			} else if(criterion==ProcessPeaksByAreaFilterSelectionCriterion.AREA_GREATER_THAN_MAXIMUM) {
				compareAreaToLimit(compareAreaValue, areaLimits[1], option, listener, peak, false);
			} else { // not within range, e.g. noisy peaks and big spikes
				compareAreaToLimit(compareAreaValue, areaLimits[0], option, listener, peak, true);
				compareAreaToLimit(compareAreaValue, areaLimits[1], option, listener, peak, false);
			}
			subMonitor.worked(1);
		}
	}

	private <X extends IPeak> void compareAreaToLimit(double area, double limit, ProcessPeaksByAreaFilterTreatmentOption option, CRUDListener<X, IPeakModel> listener, X peak, boolean min) {
		
		if(min) {
			if(Double.compare(area, limit)<0) {
				processPeaks(listener, option, peak);
			}
		} else { // max
			if(Double.compare(area, limit)>0) {
				processPeaks(listener, option, peak);
			}
		}
	}

	private <X extends IPeak> void processPeaks(CRUDListener<X, IPeakModel> listener, ProcessPeaksByAreaFilterTreatmentOption option, X peak) {
		
		if(option==ProcessPeaksByAreaFilterTreatmentOption.DELETE_PEAK) {
			listener.delete(peak);
		} else { // disable peak
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
		}
	}
}
