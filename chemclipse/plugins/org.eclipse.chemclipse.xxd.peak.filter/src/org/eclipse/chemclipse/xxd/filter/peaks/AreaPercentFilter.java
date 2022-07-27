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
import org.eclipse.chemclipse.xxd.filter.peaks.settings.AreaPercentFilterSettings;
import org.eclipse.chemclipse.xxd.filter.support.TreatmentOption;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Range;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class AreaPercentFilter extends AbstractPeakFilter<AreaPercentFilterSettings> {

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

		public boolean test(double areaPercent) {

			boolean result = predicate.test(areaPercent, areaSetting);
			return result;
		}
	}

	@Override
	public String getName() {

		return "Area Percent Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by percentage area values";
	}

	@Override
	public Class<AreaPercentFilterSettings> getConfigClass() {

		return AreaPercentFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, AreaPercentFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		double areaSum = calculateAreaSum(peaks);
		AreaPredicate<?> predicate = getPredicate(configuration);
		TreatmentOption treatmentOption = configuration.getTreatmentOption();
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			double compareAreaValue = calculatePercentageAreaCompareValue(peak, areaSum);
			processPeak(treatmentOption, peak, compareAreaValue, predicate, peaksToDelete);
			subMonitor.worked(1);
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}

	private static double calculateAreaSum(Collection<IPeak> peaks) {

		double areaSum = 0;
		for(IPeak peak : peaks) {
			areaSum = areaSum + peak.getIntegratedArea();
		}
		return areaSum;
	}

	private static double calculatePercentageAreaCompareValue(IPeak peak, double areaSum) {

		if(areaSum != 0) {
			return (100 / areaSum) * peak.getIntegratedArea();
		} else {
			return 0.0d;
		}
	}

	private static AreaPredicate<?> getPredicate(AreaPercentFilterSettings configuration) {

		switch(configuration.getAreaCriterion()) {
			case AREA_LESS_THAN_MINIMUM:
				return new AreaPredicate<>(AREA_LESS_THAN_MINIMUM_COMPARATOR, configuration.getMinimumPercentageAreaValue());
			case AREA_GREATER_THAN_MAXIMUM:
				return new AreaPredicate<>(AREA_GREATER_THAN_MAXIMUM_COMPARATOR, configuration.getMaximumPercentageAreaValue());
			case AREA_NOT_WITHIN_RANGE:
				return new AreaPredicate<>(AREA_NOT_WITHIN_RANGE, Range.closed(configuration.getMinimumPercentageAreaValue(), configuration.getMaximumPercentageAreaValue()));
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static void processPeak(TreatmentOption treatmentOption, IPeak peak, double compareAreaValue, AreaPredicate<?> predicate, List<IPeak> peaksToDelete) {

		switch(treatmentOption) {
			case ACTIVATE_PEAK:
				if(predicate.test(compareAreaValue)) {
					peak.setActiveForAnalysis(true);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(compareAreaValue)) {
					peak.setActiveForAnalysis(false);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(compareAreaValue)) {
					peaksToDelete.add(peak);
				}
				break;
			case DELETE_PEAK:
				if(predicate.test(compareAreaValue)) {
					peaksToDelete.add(peak);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
