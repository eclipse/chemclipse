/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - improvement update process
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.AreaFilterSettings;
import org.eclipse.chemclipse.xxd.filter.support.TreatmentOption;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Range;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class AreaFilter extends AbstractPeakFilter<AreaFilterSettings> {

	private static BiPredicate<Double, Double> AREA_LESS_THAN_MINIMUM_COMPARATOR = (peakArea, areaSetting) -> (peakArea < areaSetting);
	private static BiPredicate<Double, Double> AREA_GREATER_THAN_MAXIMUM_COMPARATOR = (peakArea, areaSetting) -> (peakArea > areaSetting);
	private static BiPredicate<Double, Range<Double>> AREA_NOT_WITHIN_RANGE = (peakArea, areaSetting) -> (!areaSetting.contains(peakArea));

	private static class AreaPredicate<T> {

		private final BiPredicate<Double, T> predicate;
		private final T areaSetting;

		public AreaPredicate(BiPredicate<Double, T> predicate, T areaSetting) {

			super();
			this.predicate = predicate;
			this.areaSetting = areaSetting;
		}

		public AreaPredicate<?> negate() {

			return new AreaPredicate<T>(predicate.negate(), areaSetting);
		}

		public boolean test(double integratedArea) {

			boolean result = predicate.test(integratedArea, areaSetting);
			return result;
		}
	}

	@Override
	public String getName() {

		return "Area Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by peak area values";
	}

	@Override
	public Class<AreaFilterSettings> getConfigClass() {

		return AreaFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, AreaFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		AreaPredicate<?> predicate = getPredicate(configuration);
		TreatmentOption treatmentOption = configuration.getTreatmentOption();
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			processPeak(treatmentOption, peak, predicate, peaksToDelete);
			subMonitor.worked(1);
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}

	private static AreaPredicate<?> getPredicate(AreaFilterSettings configuration) {

		switch(configuration.getAreaCriterion()) {
			case AREA_LESS_THAN_MINIMUM:
				return new AreaPredicate<>(AREA_LESS_THAN_MINIMUM_COMPARATOR, configuration.getMinimumAreaValue());
			case AREA_GREATER_THAN_MAXIMUM:
				return new AreaPredicate<>(AREA_GREATER_THAN_MAXIMUM_COMPARATOR, configuration.getMaximumAreaValue());
			case AREA_NOT_WITHIN_RANGE:
				return new AreaPredicate<>(AREA_NOT_WITHIN_RANGE, Range.closed(configuration.getMinimumAreaValue(), configuration.getMaximumAreaValue()));
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private void processPeak(TreatmentOption treatmentOption, IPeak peak, AreaPredicate<?> predicate, List<IPeak> peaksToDelete) {

		switch(treatmentOption) {
			case ACTIVATE_PEAK:
				if(predicate.test(peak.getIntegratedArea())) {
					peak.setActiveForAnalysis(true);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(peak.getIntegratedArea())) {
					peak.setActiveForAnalysis(false);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(peak.getIntegratedArea())) {
					peaksToDelete.add(peak);
				}
				break;
			case DELETE_PEAK:
				if(predicate.test(peak.getIntegratedArea())) {
					peaksToDelete.add(peak);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
