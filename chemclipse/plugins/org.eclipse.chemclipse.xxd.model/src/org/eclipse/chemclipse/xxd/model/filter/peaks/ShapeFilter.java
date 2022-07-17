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
package org.eclipse.chemclipse.xxd.model.filter.peaks;

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
import org.eclipse.chemclipse.xxd.model.settings.peaks.ShapeFilterSettings;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ShapeFilter extends AbstractPeakFilter<ShapeFilterSettings> {

	private static BiPredicate<RangeContainer, Double> LEADING_SMALLER_THAN_LIMIT_COMPARATOR = (container, shapeSetting) -> (container.leading < shapeSetting);
	private static BiPredicate<RangeContainer, Double> TAILING_GREATER_THAN_LIMIT_COMPARATOR = (container, shapeSetting) -> (container.tailing > shapeSetting);
	private static BiPredicate<RangeContainer, RangeContainer> VALUES_WITHIN_RANGE_COMPARATOR_R = (container, shapeSetting) -> (shapeSetting.isWithinRange(container));

	private static class RangeContainer {

		double leading = 0.0d;
		double tailing = 0.0d;

		public RangeContainer(double leading, double tailing) {

			super();
			this.leading = leading;
			this.tailing = tailing;
		}

		public boolean isWithinRange(RangeContainer container) {

			return (leading >= container.leading) && (tailing <= container.tailing);
		}
	}

	private static class ShapePredicate<T> {

		private final BiPredicate<RangeContainer, T> predicate;
		private final T shapeSetting;

		public ShapePredicate(BiPredicate<RangeContainer, T> predicate, T shapeSetting) {

			super();
			this.predicate = predicate;
			this.shapeSetting = shapeSetting;
		}

		public ShapePredicate<?> negate() {

			return new ShapePredicate<T>(predicate.negate(), shapeSetting);
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
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, ShapeFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		TreatmentOption treatmentOption = configuration.getTreatmentOption();
		ShapePredicate<?> predicate = getPredicate(configuration);
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			processPeakSuperRange(treatmentOption, peak, predicate, peaksToDelete);
			subMonitor.worked(1);
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}

	private static ShapePredicate<?> getPredicate(ShapeFilterSettings configuration) {

		switch(configuration.getShapeCriterion()) {
			case LEADING_SMALLER_THAN_LIMIT:
				return new ShapePredicate<>(LEADING_SMALLER_THAN_LIMIT_COMPARATOR, configuration.getLeading());
			case TAILING_GREATER_THAN_LIMIT:
				return new ShapePredicate<>(TAILING_GREATER_THAN_LIMIT_COMPARATOR, configuration.getTailing());
			case VALUES_WITHIN_RANGE:
				return new ShapePredicate<>(VALUES_WITHIN_RANGE_COMPARATOR_R, new RangeContainer(configuration.getLeading(), configuration.getTailing()));
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static void processPeakSuperRange(TreatmentOption treatmentOption, IPeak peak, ShapePredicate<?> predicate, List<IPeak> peaksToDelete) {

		RangeContainer container = new RangeContainer(peak.getPeakModel().getLeading(), peak.getPeakModel().getTailing());
		switch(treatmentOption) {
			case ACTIVATE_PEAK:
				if(predicate.test(container)) {
					peak.setActiveForAnalysis(true);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(container)) {
					peak.setActiveForAnalysis(false);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(container)) {
					peaksToDelete.add(peak);
				}
				break;
			case DELETE_PEAK:
				if(predicate.test(container)) {
					peaksToDelete.add(peak);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
