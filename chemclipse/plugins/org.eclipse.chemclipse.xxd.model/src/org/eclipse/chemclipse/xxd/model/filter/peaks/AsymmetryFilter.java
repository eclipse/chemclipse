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
public class AsymmetryFilter implements IPeakFilter<AsymmetryFilterSettings> {

	@Override
	public String getName() {

		return "Asymmetry Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by peak asymmetry factor (As)";
	}

	@Override
	public Class<AsymmetryFilterSettings> getConfigClass() {

		return AsymmetryFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, AsymmetryFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

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

	private static <X extends IPeak> void applySelectedOptions(AsymmetryFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak) {

		boolean keepFlag = false;
		if(configuration.getFilterTreatmentOption()==ValueFilterTreatmentOption.KEEP_PEAK) {
			keepFlag = true;
		}
		double  peakAsymmetryFactor = peak.getPeakModel().getTailing()/peak.getPeakModel().getLeading();
		switch (configuration.getFilterSelectionCriterion()){
		case ASYMMETRY_FACTOR_GREATER_THAN_LIMIT:
			if(keepFlag) {
				if(Double.compare(peakAsymmetryFactor, configuration.getPeakAsymmetryFactor())<0) {
					processPeak(listener, configuration, peak);
				}
			} else {
				if(Double.compare(peakAsymmetryFactor, configuration.getPeakAsymmetryFactor())>0) {
					processPeak(listener, configuration, peak);
				}
			}
			break;
		case ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT:
			if(keepFlag) {
				if(Double.compare(peakAsymmetryFactor, configuration.getPeakAsymmetryFactor())>0) {
					processPeak(listener, configuration, peak);
				}
			} else {
				if(Double.compare(peakAsymmetryFactor, configuration.getPeakAsymmetryFactor())<0) {
					processPeak(listener, configuration, peak);
				}
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void processPeak(CRUDListener<X, IPeakModel> listener, AsymmetryFilterSettings configuration, X peak) {

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
		case DELETE_PEAK:
			listener.delete(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}