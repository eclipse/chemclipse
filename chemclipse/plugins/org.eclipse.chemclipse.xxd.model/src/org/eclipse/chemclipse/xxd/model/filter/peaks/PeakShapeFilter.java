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

import java.math.BigDecimal;
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
public class PeakShapeFilter implements IPeakFilter<PeakShapeFilterSettings> {

	@Override
	public String getName() {

		return "Peak Shape Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by leading and tailing)";
	}

	@Override
	public Class<PeakShapeFilterSettings> getConfigClass() {

		return PeakShapeFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, PeakShapeFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

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

	private static <X extends IPeak> void applySelectedOptions(PeakShapeFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak) {

		boolean keepFlag = false;
		if(configuration.getFilterTreatmentOption()==ValueFilterTreatmentOption.KEEP_PEAK) {
			keepFlag = true;
		}
		switch (configuration.getFilterSelectionCriterion()){
		case LEADING_SMALLER_THAN_LIMIT:
			if(keepFlag) {
				if(Double.compare(peak.getPeakModel().getLeading(), configuration.getLeadingValue())>0) {
					processPeak(listener, configuration, peak);
				}
			} else {
				if(Double.compare(peak.getPeakModel().getLeading(), configuration.getLeadingValue())<0) {
					processPeak(listener, configuration, peak);
				}
			}
			break;
		case TAILING_GREATER_THAN_LIMIT:
			if(keepFlag) {
				if(Double.compare(peak.getPeakModel().getTailing(), configuration.getTailingValue())<0) {
					processPeak(listener, configuration, peak);
				}
			} else {
				if(Double.compare(peak.getPeakModel().getTailing(), configuration.getTailingValue())>0) {
					processPeak(listener, configuration, peak);
				}
			}
			break;
		case VALUES_WITHIN_RANGE:
			if(keepFlag) {
				if(!peakProfile(configuration, peak)) {
					processPeak(listener, configuration, peak);
				}
			} else {
				if(peakProfile(configuration, peak)) {
					processPeak(listener, configuration, peak);
				}
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> boolean peakProfile(PeakShapeFilterSettings configuration, X peak) {
		
		return checkLeading(peak, configuration) || checkTailing(peak, configuration);
	}

	private static <X extends IPeak> boolean checkTailing(X peak, PeakShapeFilterSettings configuration) {

		BigDecimal peakTailing = new BigDecimal(peak.getPeakModel().getTailing());
		BigDecimal settingTailing = new BigDecimal(configuration.getTailingValue());
		if(peakTailing.compareTo(settingTailing)<0 || peakTailing.compareTo(settingTailing) == 0) {
			return true;
		} else {
			return false;
		}
	}

	private static <X extends IPeak> boolean checkLeading(X peak, PeakShapeFilterSettings configuration) {

		BigDecimal peakLeading = new BigDecimal(peak.getPeakModel().getLeading());
		BigDecimal settingLeading = new BigDecimal(configuration.getLeadingValue());
		if(peakLeading.compareTo(settingLeading)>0 || peakLeading.compareTo(settingLeading) == 0) {
			return true;
		} else {
			return false;
		}
	}

	private static <X extends IPeak> void processPeak(CRUDListener<X, IPeakModel> listener, PeakShapeFilterSettings configuration, X peak) {

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