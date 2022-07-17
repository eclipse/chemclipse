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
import org.eclipse.chemclipse.xxd.model.settings.peaks.AsymmetryFilterSettings;
import org.eclipse.chemclipse.xxd.model.support.TreatmentOption;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class AsymmetryFilter extends AbstractPeakFilter<AsymmetryFilterSettings> {

	private static BiPredicate<Double, Double> ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT_COMPARATOR = (factor, factorSetting) -> (factor < factorSetting);
	private static BiPredicate<Double, Double> ASYMMETRY_FACTOR_GREATER_THAN_LIMIT_COMPARATOR = (factor, factorSetting) -> (factor > factorSetting);

	private static class FactorPredicate<T> {

		private final BiPredicate<Double, T> predicate;
		private final T factorSetting;

		public FactorPredicate(BiPredicate<Double, T> predicate, T factorSetting) {

			super();
			this.predicate = predicate;
			this.factorSetting = factorSetting;
		}

		public FactorPredicate<?> negate() {

			return new FactorPredicate<T>(predicate.negate(), factorSetting);
		}

		public boolean test(double factor) {

			boolean result = predicate.test(factor, factorSetting);
			return result;
		}
	}

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
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, AsymmetryFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		FactorPredicate<?> predicate = getPredicate(configuration);
		TreatmentOption treatmentOption = configuration.getFilterTreatmentOption();
		List<IPeak> peaksToDelete = new ArrayList<>();
		for(IPeak peak : peaks) {
			processPeak(treatmentOption, peak, predicate, peaksToDelete);
			subMonitor.worked(1);
		}
		//
		deletePeaks(peaksToDelete, chromatogramSelection);
		resetPeakSelection(chromatogramSelection);
	}

	private static FactorPredicate<?> getPredicate(AsymmetryFilterSettings configuration) {

		switch(configuration.getAsymmetryCriterion()) {
			case ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT:
				return new FactorPredicate<>(ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT_COMPARATOR, configuration.getAsymmetryFactor());
			case ASYMMETRY_FACTOR_GREATER_THAN_LIMIT:
				return new FactorPredicate<>(ASYMMETRY_FACTOR_GREATER_THAN_LIMIT_COMPARATOR, configuration.getAsymmetryFactor());
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private void processPeak(TreatmentOption treatmentOption, IPeak peak, FactorPredicate<?> predicate, List<IPeak> peaksToDelete) {

		double peakAsymmetryFactor = peak.getPeakModel().getTailing() / peak.getPeakModel().getLeading();
		switch(treatmentOption) {
			case ACTIVATE_PEAK:
				if(predicate.test(peakAsymmetryFactor)) {
					peak.setActiveForAnalysis(true);
				}
				break;
			case DEACTIVATE_PEAK:
				if(predicate.test(peakAsymmetryFactor)) {
					peak.setActiveForAnalysis(false);
				}
				break;
			case KEEP_PEAK:
				if(predicate.negate().test(peakAsymmetryFactor)) {
					peaksToDelete.add(peak);
				}
				break;
			case DELETE_PEAK:
				if(predicate.test(peakAsymmetryFactor)) {
					peaksToDelete.add(peak);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
