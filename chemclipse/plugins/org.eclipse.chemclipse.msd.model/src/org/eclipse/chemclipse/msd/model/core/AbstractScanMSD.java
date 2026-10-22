/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - honor the mark mode
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractScan;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.core.MarkedTraces;
import org.eclipse.chemclipse.msd.model.core.comparator.IonCombinedComparator;
import org.eclipse.chemclipse.msd.model.core.comparator.IonComparatorMode;
import org.eclipse.chemclipse.msd.model.implementation.ImmutableZeroIon;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.util.MarkedTracesSupportMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.support.traces.ITrace;

/**
 * The class {@code AbstractScanMSD} controls the basic operations for all types
 * of mass spectra.<br/>
 * Two kinds of mass spectra inherit from AbstractMassSpectrum:
 * <ol>
 * <li>{@link AbstractRegularMassSpectrum} implements a normal mass spectrum,
 * for example an scan with a retention time, a retention index and a scan
 * number.</li>
 * <li>{@link AbstractCombinedMassSpectrum} implements a mass spectrum, which
 * has been created for example by combination of a mass spectrum range. It has
 * no specific retention time but a start and stop retention time and so on.
 * </li>
 * </ol>
 *
 * @author Philip Wenig
 * @author Alexander Kerner
 * @author Janos Binder
 * @see AbstractChromatogramMSD
 */
public abstract class AbstractScanMSD extends AbstractScan implements IScanMSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or methods.
	 */
	private static final long serialVersionUID = -5705437012632871948L;

	private static final Logger logger = Logger.getLogger(AbstractScanMSD.class);

	private static final float NORMALIZATION_BASE = 100.0f;
	private static final int LIMIT_SIM_MEASUREMENT = 10; // 10 m/z values
	/*
	 * Shared by all scans without ions. It is replaced by a modifiable
	 * list as soon as the first ion is added.
	 */
	private static final List<IIon> NO_IONS = Collections.emptyList();

	private boolean isNormalized = false;
	private float normalizationBase = 0.0f;
	private List<IIon> ionsList;
	private IScanMSD optimizedMassSpectrum;

	protected AbstractScanMSD() {

		super();
		createNewIonList();
	}

	protected AbstractScanMSD(final Collection<? extends IIon> ions) {

		super();
		this.ionsList = new ArrayList<>(ions);
	}

	/**
	 * Creates a new instance of {@code AbstractScanMSD} by creating a shallow copy
	 * of provided {@code templateScan}.
	 * 
	 * @param templateScan
	 *            {@link IScanMSD scan} that is used as a template
	 */
	protected AbstractScanMSD(IScanMSD templateScan) {

		super(templateScan);
		this.ionsList = new ArrayList<>(templateScan.getIons()); // Shallow copy.
		this.isNormalized = templateScan.isNormalized();
		this.normalizationBase = templateScan.getNormalizationBase();
		this.optimizedMassSpectrum = templateScan.getOptimizedMassSpectrum();
	}

	@Override
	public boolean checkIntensityCollisions() {

		return true;
	}

	@Override
	public AbstractScanMSD addIons(List<IIon> ions, boolean addIntensities) {

		/*
		 * Size the list once instead of growing it ion by ion.
		 */
		if(ionsList == NO_IONS) {
			ionsList = new ArrayList<>(ions.size());
		} else if(ionsList instanceof ArrayList<IIon> arrayList) {
			arrayList.ensureCapacity(ionsList.size() + ions.size());
		}
		for(IIon ion : ions) {
			if(ion == null) {
				continue;
			}
			if(addIntensities) {
				addIon(true, ion);
			} else {
				addIon(false, ion);
			}
		}
		return this;
	}

	@Override
	public AbstractScanMSD addIon(boolean addIntensity, IIon ion) {

		/*
		 * Return if the ion is null.
		 */
		if(ion == null) {
			logger.warn("The ion must be not null.");
			return this;
		}

		boolean addNew = true;
		if(checkIntensityCollisions()) {
			for(IIon actualIon : ionsList) {
				if(checkIon(ion, actualIon)) {
					/*
					 * Check whether the intensity should be added or only the higher intensity
					 * should be taken.<br/> Replace the abundance only, if the abundance is higher
					 * than the older one otherwise do nothing
					 */
					if(addIntensity) {
						addIntensities(actualIon, ion);
						addNew = false;
						break;
					} else {
						if(ion.getAbundance() >= actualIon.getAbundance()) {
							addHigherIntensity(actualIon, ion);
							addNew = false;
							break;
						} else {
							addNew = false;
							break;
						}
					}
				}
			}
		}
		/*
		 * Add a new ion.
		 */
		if(addNew) {
			getModifiableIonsList().add(ion);
			setDirty(true);
		}
		return this;
	}

	@Override
	public AbstractScanMSD addIon(IIon ion, boolean checked) {

		if(checked) {
			addIon(ion);
		} else {
			getModifiableIonsList().add(ion);
			setDirty(true);
		}
		return this;
	}

	@Override
	public AbstractScanMSD addIon(IIon ion) {

		return addIon(false, ion);
	}

	@Override
	public AbstractScanMSD removeIon(IIon ion) {

		ionsList.remove(ion);
		return this;
	}

	@Override
	public AbstractScanMSD removeAllIons() {

		clearIons();
		return this;
	}

	@Override
	public AbstractScanMSD removeIon(int ion) {

		/*
		 * Initialize the list of mass over charge rations (ion) to be removed.
		 */
		Set<Integer> ions = new HashSet<>();
		ions.add(ion);
		removeIons(ions);
		return this;
	}

	@Override
	public AbstractScanMSD removeIons(Set<Integer> ions) {

		if(ions == null) {
			// TODO maybe log warning?
			return this;
		}

		List<IIon> ionsToRemove = new ArrayList<>();
		/*
		 * Get the list of ions for removal.
		 */
		for(IIon ion : ionsList) {
			if(removeIon(ions, ion)) {
				ionsToRemove.add(ion);
			}
		}
		removeIonsFromMassSpectrum(ionsToRemove);
		return this;
	}

	@Override
	public AbstractScanMSD removeIons(IMarkedTraces<ITrace> markedIons) {

		if(markedIons == null) {
			// TODO maybe log warning?
			return this;
		}

		Set<Integer> nominalIons = MarkedTracesSupportMSD.getTracesAsInteger(markedIons);
		MarkedTraceModus markedTraceModus = markedIons.getMarkedTraceModus();
		switch(markedTraceModus) {
			case INCLUDE:
				/*
				 * Remove all listed ions.
				 */
				removeIons(nominalIons);
				break;
			case EXCLUDE:
				/*
				 * Remove all except the listed ions.
				 */
				Set<Integer> removeIons = new HashSet<>();
				for(IIon ion : ionsList) {
					int nominal = AbstractIon.getIon(ion.getIon());
					if(!nominalIons.contains(nominal)) {
						removeIons.add(nominal);
					}
				}
				removeIons(removeIons);
				break;
			default:
				/*
				 * Do nothing
				 */
				break;
		}

		return this;
	}

	/**
	 * Use this list only to iterate through the ions of this mass spectrum.<br/>
	 * To add and remove ions, use the methods of this class.
	 */
	@Override
	public List<IIon> getIons() {

		if(ionsList == NO_IONS) {
			return NO_IONS;
		}
		return Collections.unmodifiableList(ionsList);
	}

	public void clearIons() {

		/*
		 * Release the backing array, otherwise its capacity is retained.
		 */
		ionsList.clear();
		if(ionsList instanceof ArrayList<IIon> arrayList) {
			arrayList.trimToSize();
		}
	}

	@Override
	public float getTotalSignal(IMarkedTraces<ITrace> markedIons) {

		float totalSignal = 0;
		/*
		 * If the excluded ions are null, return the total signal.
		 */
		if(markedIons == null || markedIons.isEmpty()) {
			totalSignal = getTotalSignal();
		} else {
			IIon ion;
			Iterator<IIon> iterator = ionsList.iterator();
			while(iterator.hasNext()) {
				ion = iterator.next();
				if(useIon(ion, markedIons)) {
					totalSignal += ion.getAbundance();
				}
			}
		}
		return totalSignal;
	}

	private static boolean useIon(IIon ion, IMarkedTraces<ITrace> filterIons) {

		Set<Integer> ionNominal = MarkedTracesSupportMSD.getTracesAsInteger(filterIons);
		switch(filterIons.getMarkedTraceModus()) {
			case EXCLUDE:
				return ionNominal.contains(AbstractIon.getIon(ion.getIon()));
			case INCLUDE:
				return !ionNominal.contains(AbstractIon.getIon(ion.getIon()));
			default:
				return true;
		}
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0;
		for(int i = 0; i < ionsList.size(); i++) {
			totalSignal += ionsList.get(i).getAbundance();
		}
		return totalSignal;
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal() {

		if(hasIons()) {
			IIonBounds bounds = getIonBounds();
			double startIon = bounds.getLowestIon().getIon();
			double stopIon = bounds.getHighestIon().getIon();
			return getExtractedIonSignal(startIon, stopIon);
		} else {
			return new ExtractedIonSignal(0, 0);
		}
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal(double startIon, double stopIon) {

		ExtractedIonSignal extractedIonSignal;
		if(hasIons()) {
			extractedIonSignal = new ExtractedIonSignal(startIon, stopIon);
			for(IIon ion : getIons()) {
				/*
				 * The ion.getIon() will be tested in the IExtractedIonSignal instance if it is
				 * valid (that means between startIon and stopIon).
				 */
				extractedIonSignal.setAbundance(ion);
			}
			return extractedIonSignal;
		} else {
			return new ExtractedIonSignal(0, 0);
		}
	}

	@Override
	public double getBasePeak() {

		if(hasIons()) {
			return getHighestAbundance().getIon();
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getBasePeakAbundance() {

		if(hasIons()) {
			return getHighestAbundance().getAbundance();
		} else {
			return 0.0f;
		}
	}

	@Override
	public IIon getHighestAbundance() {

		if(hasIons()) {
			Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.ABUNDANCE_FIRST);
			return Collections.max(ionsList, comparator);
		} else {
			return ImmutableZeroIon.INSTANCE;
		}
	}

	@Override
	public IIon getHighestIon() {

		if(hasIons()) {
			Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.MZ_FIRST);
			return Collections.max(ionsList, comparator);
		} else {
			return ImmutableZeroIon.INSTANCE;
		}
	}

	@Override
	public IIon getLowestAbundance() {

		if(hasIons()) {
			Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.ABUNDANCE_FIRST);
			return Collections.min(ionsList, comparator);
		} else {
			return ImmutableZeroIon.INSTANCE;
		}
	}

	@Override
	public IIon getLowestIon() {

		if(hasIons()) {
			Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.MZ_FIRST);
			return Collections.min(ionsList, comparator);
		} else {
			return ImmutableZeroIon.INSTANCE;
		}
	}

	@Override
	public IIonBounds getIonBounds() {

		IIon lowest = null;
		IIon highest = null;
		if(hasIons()) {
			Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.MZ_FIRST);
			lowest = Collections.min(ionsList, comparator);
			highest = Collections.max(ionsList, comparator);
			return new IonBounds(lowest, highest);
		} else {
			return null;
		}
	}

	@Override
	public int getNumberOfIons() {

		return ionsList.size();
	}

	@Override
	public boolean isEmpty() {

		return ionsList.isEmpty();
	}

	@Override
	public IIon getIon(int ion) {

		if(hasIons()) {
			IExtractedIonSignal extractedIonSignal = new ExtractedIonSignal(ion, ion);
			for(IIon actualIon : ionsList) {
				extractedIonSignal.setAbundance(actualIon);
			}
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0) {
				return new Ion(ion, abundance);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// TODO JUnit and optimize
	@Override
	public IIon getIon(double ion) {

		if(hasIons()) {
			for(IIon actualIon : ionsList) {
				if(actualIon.getIon() == ion) {
					return actualIon;
				}
			}
		}
		/*
		 * If there is no such ion.
		 */
		return new Ion(ion, 0.0f);
	}

	@Override
	public IIon getIon(double ion, int precision) {

		if(hasIons()) {
			for(IIon actualIon : ionsList) {
				double accurateIon = AbstractIon.getIon(actualIon.getIon(), precision);
				if(accurateIon == AbstractIon.getIon(ion, precision)) {
					return new Ion(accurateIon, actualIon.getAbundance());
				}
			}
		}
		/*
		 * If there is no such ion.
		 */
		return new Ion(ion, 0.0f);
	}

	@Override
	public void adjustIons(float percentage) {

		/*
		 * Do nothing if out of range.
		 */
		if(percentage < -1.0f || percentage > 1.0f) {
			return;
		}
		/*
		 * If percentage == 0.0f, do nothing.
		 */
		if(percentage == 0.0f) {
			return;
		}
		float abundance;
		for(IIon ion : ionsList) {
			abundance = ion.getAbundance();
			abundance += abundance * percentage;
			ion.setAbundance(abundance);
		}
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		/*
		 * If the total signal is 0 there would be no ion stored in the list.<br/>
		 * That's not what we want.
		 */
		if(totalSignal <= 0.0f || Float.isNaN(totalSignal) || Float.isInfinite(totalSignal)) {
			return;
		}
		/*
		 * Do not cause a division by zero exception :-).
		 */
		if(getTotalSignal() == 0.0f) {
			return;
		}
		float base = 100.0f;
		float correctionFactor = ((base / getTotalSignal()) * totalSignal) / base;
		float abundance;
		for(IIon ion : ionsList) {
			abundance = ion.getAbundance();
			abundance *= correctionFactor;
			if(!ion.setAbundance(abundance)) {
				logger.warn("Invalid abundance: " + abundance);
			}
		}
	}

	@Override
	public IScanMSD getMassSpectrum(IMarkedTraces<ITrace> excludedIons) {

		IScanMSD massSpectrum;
		try {
			massSpectrum = makeDeepCopy();
			massSpectrum.removeIons(excludedIons);
		} catch(CloneNotSupportedException e) {
			massSpectrum = createNewMassSpectrum(excludedIons);
		}
		return massSpectrum;
	}

	/**
	 * Creates a new mass spectrum.
	 * 
	 * @param excludedIons
	 * @return IMassSpectrum
	 */
	private IScanMSD createNewMassSpectrum(IMarkedTraces<ITrace> excludedIons) {

		if(excludedIons == null) {
			excludedIons = new MarkedTraces(MarkedTraceModus.INCLUDE);
		}
		IScanMSD massSpectrum = new ScanMSD();
		IIon ion;
		Set<Integer> excludedIonsNominal = MarkedTracesSupportMSD.getTracesAsInteger(excludedIons);
		for(IIon actualIon : ionsList) {
			int mz = (int)actualIon.getIon();
			if(!excludedIonsNominal.contains(mz)) {
				ion = new Ion(actualIon);
				massSpectrum.addIon(ion);
			}
		}
		return massSpectrum;
	}

	@Override
	public boolean hasIons() {

		return !ionsList.isEmpty();
	}

	@Override
	public void enforceLoadScanProxy() {

		/*
		 * Normally, no action is required. The vendor proxy overrides this method by
		 * default.
		 */
	}

	@Override
	public boolean isNormalized() {

		return isNormalized;
	}

	@Override
	public float getNormalizationBase() {

		return normalizationBase;
	}

	@Override
	public IScanMSD normalize() {

		return normalize(NORMALIZATION_BASE);
	}

	@Override
	public IScanMSD normalize(float base) {

		/*
		 * Return if the base value is lower than zero.
		 */
		if(base <= 0) {
			return this;
		}
		/*
		 * Return if there are no ions stored.
		 */
		if(!hasIons()) {
			return this;
		}
		/*
		 * There is at least 1 ion stored in the list otherwise the code would not have
		 * reached this point.
		 */
		List<IIon> ions = getIons();
		Comparator<IIon> comparator = new IonCombinedComparator(IonComparatorMode.ABUNDANCE_FIRST);
		double highestAbundance = Collections.max(ions, comparator).getAbundance();
		/*
		 * Return if the highest abundance == 0.<br/> If yes a division through 0 would
		 * throw a ArithmeticException.
		 */
		double factor;
		if(highestAbundance == 0) {
			return this;
		} else {
			factor = base / highestAbundance;
			isNormalized = true;
			normalizationBase = base;
		}
		float percentageAbundance;
		for(IIon actualIon : ions) {
			percentageAbundance = (float)(factor * actualIon.getAbundance());
			actualIon.setAbundance(percentageAbundance); // TODO use return value
		}
		return this;
	}

	@Override
	public void setOptimizedMassSpectrum(IScanMSD optimizedMassSpectrum) {

		this.optimizedMassSpectrum = optimizedMassSpectrum;
	}

	protected void setIons(Collection<? extends IIon> ions) {

		this.ionsList = new ArrayList<>(ions);
	}

	@Override
	public IScanMSD getOptimizedMassSpectrum() {

		return optimizedMassSpectrum;
	}

	@Override
	public boolean isMeasurementSIM() {

		if(!ionsList.isEmpty() && ionsList.size() <= LIMIT_SIM_MEASUREMENT) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isTandemMS() {

		int limit = (ionsList.size() > 30) ? 30 : ionsList.size();
		for(int i = 0; i < limit; i++) {
			IIon ion = ionsList.get(i);
			if(ion instanceof IIonMSn ionMSn && ionMSn.getIonTransition() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHighResolutionMS() {

		if(ionsList.size() > 3000) {
			return true;
		} else {
			/*
			 * Detailed check.
			 */
			int counterNominal = 0;
			int counterHighRes = 0;
			int size = ionsList.size();
			int limit = 10;

			if(size <= limit) {
				/*
				 * Check all
				 */
				for(IIon ion : ionsList) {
					String[] parts = Double.toString(ion.getIon()).split("\\.");
					if(parts[1].length() <= 1) {
						counterNominal++;
					} else {
						counterHighRes++;
					}
				}
			} else {
				int modulo = size / 10;
				for(int i = 0; i < ionsList.size(); i++) {
					if(i % modulo == 0) {
						IIon ion = ionsList.get(i);
						String[] parts = Double.toString(ion.getIon()).split("\\.");
						if(parts[1].length() <= 1) {
							counterNominal++;
						} else {
							counterHighRes++;
						}
					}
				}
			}

			return counterHighRes > counterNominal;
		}
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractScanMSD other = (AbstractScanMSD)otherObject;
		return getBasePeak() == other.getBasePeak() && getBasePeakAbundance() == other.getBasePeakAbundance() && getNumberOfIons() == other.getNumberOfIons() && getTotalSignal() == other.getTotalSignal() && isNormalized() == other.isNormalized() && getNormalizationBase() == other.getNormalizationBase();
	}

	@Override
	public int hashCode() {

		return 7 * Double.hashCode(getBasePeak()) + 11 * Float.hashCode(getBasePeakAbundance()) + 13 * Float.hashCode(getNumberOfIons()) + 15 * Float.hashCode(getTotalSignal()) + 13 * Boolean.hashCode(isNormalized) + 11 * Float.hashCode(normalizationBase);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[basePeak=");
		builder.append(getBasePeak());
		builder.append(",basePeakAbundance=");
		builder.append(getBasePeakAbundance());
		builder.append(",numberOfIons=");
		builder.append(getNumberOfIons());
		builder.append(",totalSignal=");
		builder.append(getTotalSignal());
		builder.append(",isNormalized=");
		builder.append(isNormalized());
		if(isNormalized()) {
			builder.append(",normalizationBase=");
			builder.append(getNormalizationBase());
		}
		builder.append(",Ion/Abundance pairs: ");
		Iterator<IIon> iter = ionsList.iterator();
		while(iter.hasNext()) {
			IIon ion = iter.next();
			builder.append(ion.getIon());
			builder.append(":");
			builder.append(ion.getAbundance());
			if(iter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		AbstractScanMSD massSpectrum = (AbstractScanMSD)super.clone();
		massSpectrum.createNewIonList();
		return massSpectrum;
	}

	/**
	 * Adds both intensities from firstIon to secondIon.
	 * 
	 * @param firstIon
	 * @param secondIon
	 */
	private void addIntensities(IIon firstIon, IIon secondIon) {

		setDirty(firstIon.setAbundance(secondIon.getAbundance() + firstIon.getAbundance()));
	}

	/**
	 * Adds the intensity of firstIon to secondIon.
	 * 
	 * @param firstIon
	 * @param secondIon
	 */
	private void addHigherIntensity(IIon firstIon, IIon secondIon) {

		setDirty(firstIon.setAbundance(secondIon.getAbundance()));
	}

	/**
	 * This method is used only to create a new ion list. The list has to be filled
	 * by the implementation specific ions of each implementing class.<br/>
	 * If mass spectrum will be cloned, the ion list will be created as a new
	 * instance. The ions of the object to be cloned will be stored in the new
	 * object again by each implementing class.<br/>
	 * Don't allocate a list up front. Empty scans share {@link #NO_IONS}.
	 * Large chromatograms (e.g. MRM traces) contain millions of scans,
	 * often empty or with a few ions only.
	 */
	private void createNewIonList() {

		ionsList = NO_IONS;
	}

	private List<IIon> getModifiableIonsList() {

		if(ionsList == NO_IONS) {
			ionsList = new ArrayList<>();
		}
		return ionsList;
	}

	/**
	 * Checks if the given ion mass over charge ratio (ion) should be removed.
	 * 
	 * @param ions
	 * @param actualIon
	 * @return boolean
	 */
	private boolean removeIon(Set<Integer> ions, IIon actualIon) {

		boolean remove = false;
		int abstractIon = AbstractIon.getIon(actualIon.getIon());
		exitloop:
		for(int ion : ions) {
			if(abstractIon == ion) {
				remove = true;
				break exitloop;
			}
		}
		return remove;
	}

	/**
	 * Removes all ions stored in the list from the actual mass spectrum.
	 * 
	 * @param ionsToRemove
	 */
	private void removeIonsFromMassSpectrum(List<IIon> ionsToRemove) {

		assert (ionsToRemove != null) : "The ion list must not be null.";
		/*
		 * Remove all retrieved items.
		 */
		for(IIon ion : ionsToRemove) {
			ionsList.remove(ion);
		}
	}

	private boolean checkIon(IIon ion1, IIon ion2) {

		if(ion1 == null || ion2 == null) {
			return false;
		}

		if(Double.compare(ion1.getIon(), ion2.getIon()) != 0) {
			return false;
		}

		// Both MSn: compare transitions too
		if(ion1 instanceof IIonMSn msnIon1 && ion2 instanceof IIonMSn msnIon2) {
			return Objects.equals(msnIon1.getIonTransition(), msnIon2.getIonTransition());
		}

		// Mixed MS1/MSn at same m/z are NOT the same ion
		if((ion1 instanceof IIonMSn) ^ (ion2 instanceof IIonMSn)) {
			return false;
		}

		// Both are MS1 ions at same m/z
		return true;
	}
}