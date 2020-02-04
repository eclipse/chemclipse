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
import org.eclipse.chemclipse.xxd.model.support.PeakShapeFilterSelectionCriterion;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Range;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class ShapeFilter implements IPeakFilter<ShapeFilterSettings> {

	private static BiPredicate<Double, Double> LEADING_SMALLER_THAN_LIMIT_COMPARATOR = (value, shapeSetting) -> (value < shapeSetting);
	private static BiPredicate<Double, Double> TAILING_GREATER_THAN_LIMIT_COMPARATOR = (value, shapeSetting) -> (value > shapeSetting);
	private static BiPredicate<RangeContainer, Range<Double>> VALUES_WITHIN_RANGE_COMPARATOR = (container, shapeSetting) -> (shapeSetting.contains(container.leading) && (shapeSetting.contains(container.tailing)));

	private static class ShapePredicate<T> {

		private final BiPredicate<Double, T> predicate;
		private final T shapeSetting;

		public ShapePredicate(BiPredicate<Double, T> predicate, T shapeSetting) {

			super();
			this.predicate = predicate;
			this.shapeSetting = shapeSetting;
		}

		public ShapePredicate<?> negate() {

			return new ShapePredicate<T>(predicate.negate(), shapeSetting);
		}

		public boolean test(double value) {

			boolean result = predicate.test(value, shapeSetting);
			return result;
		}
	}

	private static class RangeContainer {

		double leading;
		double tailing;

		public RangeContainer(double leading, double tailing) {
			super();
			this.leading = leading;
			this.tailing = tailing;
		}
	}

	private static class RangePredicate<T> {

		private final BiPredicate<RangeContainer, T> predicate;
		private final T shapeSetting;

		public RangePredicate(BiPredicate<RangeContainer, T> predicate, T shapeSetting) {

			super();
			this.predicate = predicate;
			this.shapeSetting = shapeSetting;
		}

		public RangePredicate<?> negate() {

			return new RangePredicate<T>(predicate.negate(), shapeSetting);
		}

		public boolean test(RangeContainer values) {

			boolean result = predicate.test(values, shapeSetting);
			return result;
		}
	}

	@Override
	public String getName() {

		return "Shape Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by leading and tailing";
	}

	@Override
	public Class<ShapeFilterSettings> getConfigClass() {

		return ShapeFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ShapeFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());

		for(X peak : read) {
			if(configuration.getFilterSelectionCriterion() == PeakShapeFilterSelectionCriterion.VALUES_WITHIN_RANGE) {
				processPeakConsideringRange(configuration, listener, peak, getRangePredicate(configuration));
			} else {
				processPeak(configuration, listener, peak, getPredicate(configuration));
			}
			subMonitor.worked(1);
		}
	}

	private static RangePredicate<?> getRangePredicate(ShapeFilterSettings configuration) {

		return new RangePredicate<>(VALUES_WITHIN_RANGE_COMPARATOR, Range.closed(configuration.getLeadingValue(), configuration.getTailingValue()));
	}

	private static ShapePredicate<?> getPredicate(ShapeFilterSettings configuration) {

		switch(configuration.getFilterSelectionCriterion()) {
		case LEADING_SMALLER_THAN_LIMIT:
			return new ShapePredicate<>(LEADING_SMALLER_THAN_LIMIT_COMPARATOR, configuration.getLeadingValue());
		case TAILING_GREATER_THAN_LIMIT:
			return new ShapePredicate<>(TAILING_GREATER_THAN_LIMIT_COMPARATOR, configuration.getTailingValue());
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void processPeakConsideringRange(ShapeFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak, RangePredicate<?> rangePredicate) {

		RangeContainer container = new RangeContainer(peak.getPeakModel().getLeading(), peak.getPeakModel().getTailing());
		switch(configuration.getFilterTreatmentOption()) {
		case ENABLE_PEAK:
			if(rangePredicate.test(container)) {
				peak.setActiveForAnalysis(true);
				listener.updated(peak);
			}
			break;
		case DEACTIVATE_PEAK:
			if(rangePredicate.test(container)) {
				peak.setActiveForAnalysis(false);
				listener.updated(peak);
			}
			break;
		case KEEP_PEAK:
			if(rangePredicate.negate().test(container))
				listener.delete(peak);
			break;
		case DELETE_PEAK:
			if(rangePredicate.test(container))
				listener.delete(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}

	private static <X extends IPeak> void processPeak(ShapeFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak, ShapePredicate<?> predicate) {

		double shapeValue = 0;
		if(configuration.getFilterSelectionCriterion()==PeakShapeFilterSelectionCriterion.LEADING_SMALLER_THAN_LIMIT) {
			shapeValue = peak.getPeakModel().getLeading();
		} else {
			shapeValue = peak.getPeakModel().getTailing();
		}
		switch(configuration.getFilterTreatmentOption()) {
		case ENABLE_PEAK:
			if(predicate.test(shapeValue)) {
				peak.setActiveForAnalysis(true);
				listener.updated(peak);
			}
			break;
		case DEACTIVATE_PEAK:
			if(predicate.test(shapeValue)) {
				peak.setActiveForAnalysis(false);
				listener.updated(peak);
			}
			break;
		case KEEP_PEAK:
			if(predicate.negate().test(shapeValue))
				listener.delete(peak);
			break;
		case DELETE_PEAK:
			if(predicate.test(shapeValue))
				listener.delete(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}