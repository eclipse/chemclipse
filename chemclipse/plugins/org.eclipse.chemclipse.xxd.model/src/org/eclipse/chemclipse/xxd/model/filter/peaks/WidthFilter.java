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

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class WidthFilter implements IPeakFilter<WidthFilterSettings> {

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
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, WidthFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());

		for(X peak : read) {
			processPeak(configuration, listener, peak, getPredicate(configuration));
			subMonitor.worked(1);
		}
	}
	
	private static WidthPredicate<?> getPredicate(WidthFilterSettings configuration) {

		switch(configuration.getFilterSelectionCriterion()) {
		case WIDTH_SMALLER_THAN_LIMIT:
			return new WidthPredicate<>(WIDTH_SMALLER_THAN_LIMIT_COMPARATOR, (int) (configuration.getWidthValue() * 60000));
		case WIDTH_GREATER_THAN_LIMIT:
			return new WidthPredicate<>(WIDTH_GREATER_THAN_LIMIT_COMPARATOR, (int) (configuration.getWidthValue() * 60000));
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void processPeak(WidthFilterSettings configuration, CRUDListener<X, IPeakModel> listener, X peak, WidthPredicate<?> predicate) {

		int width = peak.getPeakModel().getWidthByInflectionPoints();
		switch(configuration.getFilterTreatmentOption()) {
		case ENABLE_PEAK:
			if(predicate.test(width)) {
				peak.setActiveForAnalysis(true);
				listener.updated(peak);
			}
			break;
		case DEACTIVATE_PEAK:
			if(predicate.test(width)) {
				peak.setActiveForAnalysis(false);
				listener.updated(peak);
			}
			break;
		case KEEP_PEAK:
			if(predicate.negate().test(width))
				listener.delete(peak);
			break;
		case DELETE_PEAK:
			if(predicate.test(width))
				listener.delete(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
