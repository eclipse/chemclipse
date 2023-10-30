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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.WidthFilterSettings;
import org.eclipse.chemclipse.xxd.filter.support.TreatmentOption;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class WidthFilter extends AbstractPeakFilter<WidthFilterSettings> {

	private static BiPredicate<Integer, Integer> WIDTH_SMALLER_THAN_LIMIT_COMPARATOR = (width, widthSetting) -> (width < widthSetting);
	private static BiPredicate<Integer, Integer> WIDTH_GREATER_THAN_LIMIT_COMPARATOR = (width, widthSetting) -> (width > widthSetting);

	private static class WidthPredicate<T> {

		private final BiPredicate<Integer, T> predicate;
		private final T widthSetting;

		public WidthPredicate(BiPredicate<Integer, T> predicate, T widthSetting) {

			super();
			this.predicate = predicate;
			this.widthSetting = widthSetting;
		}

		public WidthPredicate<?> negate() {

			return new WidthPredicate<T>(predicate.negate(), widthSetting);
		}

		public boolean test(int width) {

			boolean result = predicate.test(width, widthSetting);
			return result;
		}
	}

	@Override
	public String getName() {

		return "Width Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks by peak width";
	}

	@Override
	public Class<WidthFilterSettings> getConfigClass() {

		return WidthFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, WidthFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		WidthPredicate<?> predicate = getPredicate(configuration);
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

	private static WidthPredicate<?> getPredicate(WidthFilterSettings configuration) {

		switch(configuration.getWidthCriterion()) {
			case WIDTH_SMALLER_THAN_LIMIT:
				return new WidthPredicate<>(WIDTH_SMALLER_THAN_LIMIT_COMPARATOR, (int)(configuration.getWidth() * IChromatogram.MINUTE_CORRELATION_FACTOR));
			case WIDTH_GREATER_THAN_LIMIT:
				return new WidthPredicate<>(WIDTH_GREATER_THAN_LIMIT_COMPARATOR, (int)(configuration.getWidth() * IChromatogram.MINUTE_CORRELATION_FACTOR));
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static void processPeak(TreatmentOption treatmentOption, IPeak peak, WidthPredicate<?> predicate, List<IPeak> peaksToDelete) {

		int width = peak.getPeakModel().getWidthByInflectionPoints();
		switch(treatmentOption) {
			case ACTIVATE_PEAK:
				if(predicate.test(width)) {
					peak.setActiveForAnalysis(true);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(width)) {
					peak.setActiveForAnalysis(false);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(width)) {
					peaksToDelete.add(peak);
				}
				break;
			case DELETE_PEAK:
				if(predicate.test(width)) {
					peaksToDelete.add(peak);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
