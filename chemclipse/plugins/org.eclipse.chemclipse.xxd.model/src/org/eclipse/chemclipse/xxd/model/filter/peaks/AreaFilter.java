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
import java.util.function.BiPredicate;

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

import com.google.common.collect.Range;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class AreaFilter implements IPeakFilter<AreaFilterSettings> {

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
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, AreaFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		for(X peak : read) {
			processPeak(listener, configuration, peak, getPredicate(configuration));
			subMonitor.worked(1);
		}
	}

	private static AreaPredicate<?> getPredicate(AreaFilterSettings configuration) {

		switch(configuration.getFilterSelectionCriterion()) {
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

	private static <X extends IPeak> void processPeak(CRUDListener<X, IPeakModel> listener, AreaFilterSettings configuration, X peak, AreaPredicate<?> predicate) {

		switch(configuration.getFilterTreatmentOption()) {
			case ENABLE_PEAK:
				if(predicate.test(peak.getIntegratedArea())) {
					peak.setActiveForAnalysis(true);
					listener.updated(peak);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(peak.getIntegratedArea())) {
					peak.setActiveForAnalysis(false);
					listener.updated(peak);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(peak.getIntegratedArea()))
					listener.delete(peak);
				break;
			case DELETE_PEAK:
				if(predicate.test(peak.getIntegratedArea()))
					listener.delete(peak);
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
