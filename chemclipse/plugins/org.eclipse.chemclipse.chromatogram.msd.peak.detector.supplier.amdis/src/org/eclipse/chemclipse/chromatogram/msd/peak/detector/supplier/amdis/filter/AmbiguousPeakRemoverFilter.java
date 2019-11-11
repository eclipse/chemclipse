/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.filter.AmbiguousPeakRemoverFilterSettings.SelectionMethod;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class AmbiguousPeakRemoverFilter implements IPeakFilter<AmbiguousPeakRemoverFilterSettings> {

	private static final String NAME = "Ambiguous Peak Remover";

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public String getDescription() {

		return "Filtering ambiguous peaks in a given retention window ";
	}

	@Override
	public Class<AmbiguousPeakRemoverFilterSettings> getConfigClass() {

		return AmbiguousPeakRemoverFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X> listener, AmbiguousPeakRemoverFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		List<X> peaks = new ArrayList<>(listener.read());
		if(configuration == null) {
			configuration = createNewConfiguration();
		}
		Comparator<X> compareFunction;
		if(configuration.getMethod() == SelectionMethod.AREA) {
			compareFunction = new AreaComparator<>();
			for(X peak : peaks) {
				if(!(peak instanceof IPeakMSD)) {
					throw new IllegalArgumentException("invalid peak type");
				}
			}
		} else {
			compareFunction = new SNRComparator<>();
			for(X peak : peaks) {
				if(!(peak instanceof IChromatogramPeakMSD)) {
					messageConsumer.addWarnMessage(getName(), "SNR compare method is only avaiable for Chromatogram Peaks, skip processing");
					return;
				}
				if(!(peak instanceof IPeakMSD)) {
					throw new IllegalArgumentException("invalid peak type");
				}
			}
		}
		filterDuplicatePeaks(peaks, configuration, listener, compareFunction, monitor);
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		for(IPeak peak : items) {
			IPeakMSD peakMSD = Adapters.adapt(peak, IPeakMSD.class);
			if(peakMSD == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public AmbiguousPeakRemoverFilterSettings createConfiguration(Collection<? extends IPeak> items) throws IllegalArgumentException {

		for(IPeak peak : items) {
			if(peak instanceof IChromatogramPeakMSD) {
				return new AmbiguousPeakRemoverFilterSettings(((IChromatogramPeakMSD)peak).getChromatogram());
			}
		}
		return IPeakFilter.super.createConfiguration(items);
	}

	private static <X extends IPeak> void filterDuplicatePeaks(List<X> peaks, AmbiguousPeakRemoverFilterSettings settings, CRUDListener<? super X> listener, Comparator<X> compareFunction, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, NAME, peaks.size());
		// We first order all peaks by retention time
		Collections.sort(peaks, new Comparator<X>() {

			@Override
			public int compare(X p1, X p2) {

				return getRTDelta(Adapters.adapt(p1, IPeakMSD.class), Adapters.adapt(p2, IPeakMSD.class));
			}
		});
		X lastPeak = null;
		List<X> candidatePeakSet = new ArrayList<>();
		IMassSpectrumComparator comparator = MassSpectrumComparator.getMassSpectrumComparator(settings.getComparatorID());
		for(X peak : peaks) {
			if(lastPeak != null) {
				double deltaSeconds = getRTDelta(peak, lastPeak) / 1000d;
				double deltaMinutes = deltaSeconds / 60d;
				if(deltaMinutes < settings.getRtMaxdistance()) {
					// add it to the set of candidates
					candidatePeakSet.add(peak);
					lastPeak = peak;
				} else {
					// extract peaks from the candidatesSet...
					// and delete extracted ones...
					deletePeaks(extractPeaks(candidatePeakSet, comparator, settings.getMinmatchfactor()), listener, compareFunction);
					lastPeak = null;
				}
			}
			if(lastPeak == null) {
				lastPeak = peak;
				candidatePeakSet.clear();
				candidatePeakSet.add(peak);
			}
			subMonitor.worked(1);
		}
		if(!candidatePeakSet.isEmpty()) {
			// extract peaks from the candidatesSet...
			// and delete extracted ones...
			deletePeaks(extractPeaks(candidatePeakSet, comparator, settings.getMinmatchfactor()), listener, compareFunction);
		}
	}

	private static <T extends IPeak> List<PeakGroup<T>> extractPeaks(List<T> candidatePeakSet, IMassSpectrumComparator comparator, double minMatchFactor) {

		int size = candidatePeakSet.size();
		if(size < 2) {
			// nothing to do then...
			return Collections.emptyList();
		}
		// compare each other and form a group
		List<PeakGroup<T>> peakGroups = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			T candidate = candidatePeakSet.get(i);
			for(int j = 0; j < size; j++) {
				if(j == i) {
					// no need to compare with itself
					continue;
				}
				T comparision = candidatePeakSet.get(j);
				IProcessingInfo<IComparisonResult> info = comparator.compare(Adapters.adapt(comparision, IPeakMSD.class).getExtractedMassSpectrum(), Adapters.adapt(candidate, IPeakMSD.class).getExtractedMassSpectrum());
				if(info != null) {
					IComparisonResult result = info.getProcessingResult();
					if(result != null) {
						float mf = result.getMatchFactor();
						if(mf / 100d > minMatchFactor) {
							PeakGroup<T> group = new PeakGroup<>();
							group.addPeak(candidate, i);
							group.addPeak(comparision, j);
							peakGroups.add(group);
						}
					}
				}
			}
		}
		int groups = peakGroups.size();
		// now join groups that intersect each other
		for(int i = 0; i < groups; i++) {
			PeakGroup<T> current = peakGroups.get(i);
			for(int j = 0; j < groups; j++) {
				if(i == j) {
					continue;
				}
				PeakGroup<T> other = peakGroups.get(j);
				if(current.intersects(other)) {
					current.merge(other);
				}
			}
		}
		for(Iterator<PeakGroup<T>> iterator = peakGroups.iterator(); iterator.hasNext();) {
			PeakGroup<T> peakGroup = iterator.next();
			if(peakGroup.isEmpty()) {
				iterator.remove();
			}
		}
		return peakGroups;
	}

	private static <T extends IPeak> void deletePeaks(List<PeakGroup<T>> list, CRUDListener<? super T> listener, Comparator<T> compareFunction) {

		for(PeakGroup<T> peakGroup : list) {
			T maxPeak = peakGroup.getMaxPeak(compareFunction);
			if(maxPeak != null) {
				Collection<T> values = peakGroup.values();
				for(T peak : values) {
					if(peak == maxPeak) {
						continue;
					}
					listener.delete(peak);
				}
			}
		}
	}

	private static int getRTDelta(IPeak p1, IPeak p2) {

		int rt1 = Adapters.adapt(p1, IPeakMSD.class).getExtractedMassSpectrum().getRetentionTime();
		int rt2 = Adapters.adapt(p1, IPeakMSD.class).getExtractedMassSpectrum().getRetentionTime();
		return rt1 - rt2;
	}

	@Override
	public DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.MSD};
	}
}
