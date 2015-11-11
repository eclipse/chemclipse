/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonIsNullException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

/**
 * The class AbstractMassSpectrum controls the basic operations for all types of
 * mass spectra.<br/>
 * Two kinds of mass spectra inherit from AbstractMassSpectrum.<br/>
 * {@link AbstractRegularMassSpectrum} implements a normal mass spectrum for
 * example an scan with a retention time, a retention index and a scan number.<br/>
 * {@link AbstractCombinedMassSpectrum} implements a mass spectrum, which has
 * been created for example by combination of a mass spectrum range. It has no
 * specific retention time ... but a start and stop retention time and so on.
 * 
 * @author eselmeister
 * @see AbstractChromatogramMSD
 */
public abstract class AbstractScanMSD extends AbstractScan implements IScanMSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final float NORMALIZATION_BASE = 100.0f;
	private static final long serialVersionUID = -5705437012632871946L;
	private static final Logger logger = Logger.getLogger(AbstractScanMSD.class);
	private boolean isNormalized = false;
	private float normalizationBase = 0.0f;
	private List<IIon> ionsList;
	/*
	 * Targets and referenced mass spectra.
	 */
	private Set<IMassSpectrumTarget> targets;
	private IScanMSD optimizedMassSpectrum;

	public AbstractScanMSD() {

		/*
		 * If mass spectrum will be cloned, the ion list will be
		 * created as a new instance in the method
		 * createNewIonList().<br/> The ions of the object to
		 * be cloned will be stored in the new object again by each implementing
		 * class.
		 */
		createNewIonList();
		targets = new HashSet<IMassSpectrumTarget>();
	}

	// -----------------------------IMassSpectrum
	@Override
	public void addIons(List<IIon> ions, boolean addIntensities) {

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
	}

	@Override
	public void addIon(boolean addIntensity, IIon ion) {

		/*
		 * Return if the ion is null.
		 */
		if(ion == null) {
			return;
		}
		boolean addNew = true;
		for(IIon actualIon : ionsList) {
			if(ion.getIon() == actualIon.getIon()) {
				/*
				 * Check whether the intensity should be added or only the
				 * higher intensity should be taken.<br/> Replace the abundance
				 * only, if the abundance is higher than the older one otherwise
				 * do nothing
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
		if(addNew) {
			this.ionsList.add(ion);
			setDirty(true);
		}
	}

	@Override
	public void addIon(IIon ion, boolean checked) {

		if(checked) {
			addIon(ion);
		} else {
			ionsList.add(ion);
			setDirty(true);
		}
	}

	@Override
	public void addIon(IIon ion) {

		addIon(false, ion);
	}

	@Override
	public void removeIon(IIon ion) {

		ionsList.remove(ion);
	}

	@Override
	public void removeAllIons() {

		ionsList.clear();
	}

	@Override
	public void removeIon(int ion) {

		/*
		 * Initialize the list of mass over charge rations (ion) to be removed.
		 */
		Set<Integer> ions = new HashSet<Integer>();
		ions.add(ion);
		removeIons(ions);
	}

	@Override
	public void removeIons(Set<Integer> ions) {

		if(ions == null) {
			return;
		}
		List<IIon> ionsToRemove = new ArrayList<IIon>();
		/*
		 * Get the list of ions for removal.
		 */
		for(IIon ion : ionsList) {
			if(removeIon(ions, ion)) {
				ionsToRemove.add(ion);
			}
		}
		removeIonsFromMassSpectrum(ionsToRemove);
	}

	@Override
	public void removeIons(IMarkedIons excludedIons) {

		if(excludedIons == null) {
			return;
		}
		Set<Integer> ions = excludedIons.getIonsNominal();
		removeIons(ions);
	}

	/**
	 * Use this list only to iterate through the ions of this mass
	 * spectrum.<br/>
	 * To add and remove ions, use the methods of this class.
	 */
	@Override
	public List<IIon> getIons() {

		return ionsList;
	}

	@Override
	public float getTotalSignal(IMarkedIons excludedIons) {

		float totalSignal = 0;
		/*
		 * If the excluded ions are null, return the total signal.
		 */
		if(excludedIons == null) {
			totalSignal = getTotalSignal();
		} else {
			IIon ion;
			Iterator<IIon> iterator = ionsList.iterator();
			Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
			while(iterator.hasNext()) {
				ion = iterator.next();
				if(!excludedIonsNominal.contains(AbstractIon.getIon(ion.getIon()))) {
					totalSignal += ion.getAbundance();
				}
			}
		}
		return totalSignal;
	}

	@Override
	public float getTotalSignal() {

		IIon ion;
		float totalSignal = 0;
		Iterator<IIon> iterator = ionsList.iterator();
		while(iterator.hasNext()) {
			ion = iterator.next();
			totalSignal += ion.getAbundance();
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
				 * The ion.getIon() will be tested in the IExtractedIonSignal
				 * instance if it is valid (that means between startIon and
				 * stopIon).
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
			IonAbundanceComparator comparator = new IonAbundanceComparator();
			return Collections.max(ionsList, comparator);
		} else {
			return null;
		}
	}

	@Override
	public IIon getHighestIon() {

		if(hasIons()) {
			IonValueComparator comparator = new IonValueComparator();
			return Collections.max(ionsList, comparator);
		} else {
			return null;
		}
	}

	@Override
	public IIon getLowestAbundance() {

		if(hasIons()) {
			IonAbundanceComparator comparator = new IonAbundanceComparator();
			return Collections.min(ionsList, comparator);
		} else {
			return null;
		}
	}

	@Override
	public IIon getLowestIon() {

		if(hasIons()) {
			IonValueComparator comparator = new IonValueComparator();
			return Collections.min(ionsList, comparator);
		} else {
			return null;
		}
	}

	@Override
	public IIonBounds getIonBounds() {

		IIon lowest = null;
		IIon highest = null;
		if(hasIons()) {
			IonValueComparator comparator = new IonValueComparator();
			lowest = Collections.min(ionsList, comparator);
			highest = Collections.max(ionsList, comparator);
			return new IonBounds(lowest, highest);
		} else {
			return null;
		}
	}

	public int getNumberOfIons() {

		return ionsList.size();
	}

	@Override
	public IIon getIon(int ion) throws AbundanceLimitExceededException, IonLimitExceededException {

		if(hasIons()) {
			IExtractedIonSignal extractedIonSignal = new ExtractedIonSignal(ion, ion);
			for(IIon actualIon : ionsList) {
				extractedIonSignal.setAbundance(actualIon);
			}
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0) {
				IIon defaultIon = new Ion(ion, abundance);
				return defaultIon;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// TODO JUnit und optimieren
	@Override
	public IIon getIon(double ion) throws AbundanceLimitExceededException, IonLimitExceededException {

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
	public IIon getIon(double ion, int precision) throws AbundanceLimitExceededException, IonLimitExceededException {

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
			try {
				ion.setAbundance(abundance);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		/*
		 * If the total signal is 0 there would be no ion stored in
		 * the list.<br/> That's not what we want.
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
			try {
				ion.setAbundance(abundance);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public IScanMSD getMassSpectrum(IMarkedIons excludedIons) {

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
	private IScanMSD createNewMassSpectrum(IMarkedIons excludedIons) {

		if(excludedIons == null) {
			excludedIons = new MarkedIons();
		}
		IScanMSD massSpectrum = new ScanMSD();
		IIon ion;
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		for(IIon actualIon : ionsList) {
			if(!excludedIonsNominal.contains(actualIon.getIon())) {
				try {
					ion = new Ion(actualIon);
					massSpectrum.addIon(ion);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				} catch(IonIsNullException e) {
					logger.warn(e);
				}
			}
		}
		return massSpectrum;
	}

	// -----------------------------IMassSpectrum
	// ------------------------------------------IMassSpectrumTargets
	// TODO JUnit
	@Override
	public void addTarget(IMassSpectrumTarget massSpectrumTarget) {

		if(massSpectrumTarget != null) {
			enforceLoadScanProxy();
			targets.add(massSpectrumTarget);
		}
	}

	@Override
	public void removeTarget(IMassSpectrumTarget massSpectrumTarget) {

		enforceLoadScanProxy();
		targets.remove(massSpectrumTarget);
	}

	@Override
	public void removeTargets(List<IMassSpectrumTarget> targetsToDelete) {

		targets.removeAll(targetsToDelete);
	}

	@Override
	public void removeAllTargets() {

		enforceLoadScanProxy();
		targets.clear();
	}

	@Override
	public List<IMassSpectrumTarget> getTargets() {

		enforceLoadScanProxy();
		List<IMassSpectrumTarget> targetList = new ArrayList<IMassSpectrumTarget>(targets);
		return targetList;
	}

	// ------------------------------------------IMassSpectrumTargets
	@Override
	public boolean hasIons() {

		if(ionsList.size() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void enforceLoadScanProxy() {

		/*
		 * Normally, no action is required.
		 * The vendor proxy overrides this
		 * method by default.
		 */
	}

	// -----------------------------IMassSpectrumNormalizable
	@Override
	public boolean isNormalized() {

		return isNormalized;
	}

	@Override
	public float getNormalizationBase() {

		return normalizationBase;
	}

	@Override
	public void normalize() {

		normalize(NORMALIZATION_BASE);
	}

	@Override
	public void normalize(float base) {

		/*
		 * Return if the base value is lower than zero.
		 */
		if(base <= 0) {
			return;
		}
		/*
		 * Return if there are no ions stored.
		 */
		if(!hasIons()) {
			return;
		}
		/*
		 * There is at least 1 ion stored in the list otherwise the
		 * code would not have reached this point.
		 */
		List<IIon> ions = getIons();
		double highestAbundance = Collections.max(ions, new IonAbundanceComparator()).getAbundance();
		/*
		 * Return if the highest abundance == 0.<br/> If yes a division through
		 * 0 would throw a ArithmeticException.
		 */
		double factor;
		if(highestAbundance == 0) {
			return;
		} else {
			factor = base / highestAbundance;
			isNormalized = true;
			normalizationBase = base;
		}
		float percentageAbundance;
		for(IIon actualIon : ions) {
			percentageAbundance = (float)(factor * actualIon.getAbundance());
			try {
				actualIon.setAbundance(percentageAbundance);
			} catch(AbundanceLimitExceededException e) {
				// TODO What will be done if this exception is thrown?
				logger.warn(e);
			}
		}
	}

	@Override
	public void setOptimizedMassSpectrum(IScanMSD optimizedMassSpectrum) {

		this.optimizedMassSpectrum = optimizedMassSpectrum;
	}

	@Override
	public IScanMSD getOptimizedMassSpectrum() {

		return optimizedMassSpectrum;
	}

	// -----------------------------IMassSpectrumNormalizable
	// -----------------------------equals, hashCode, toString
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

		return 7 * Double.valueOf(getBasePeak()).hashCode() + 11 * Float.valueOf(getBasePeakAbundance()).hashCode() + 13 * Float.valueOf(getNumberOfIons()).hashCode() + 15 * Float.valueOf(getTotalSignal()).hashCode() + 13 * Boolean.valueOf(isNormalized).hashCode() + 11 * Float.valueOf(normalizationBase).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("basePeak=" + getBasePeak());
		builder.append(",");
		builder.append("basePeakAbundance=" + getBasePeakAbundance());
		builder.append(",");
		builder.append("numberOfIons=" + getNumberOfIons());
		builder.append(",");
		builder.append("totalSignal=" + getTotalSignal());
		builder.append(",");
		builder.append("isNormalized=" + isNormalized());
		builder.append(",");
		builder.append("normalizationBase=" + getNormalizationBase());
		builder.append("]");
		return builder.toString();
	}

	// -----------------------------equals, hashCode, toString
	// -----------------------------clone
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

		try {
			firstIon.setAbundance(secondIon.getAbundance() + firstIon.getAbundance());
			setDirty(true);
		} catch(AbundanceLimitExceededException e) {
			// If an exception will be thrown -> do nothing an keep the old
			// value
			setDirty(false);
		}
	}

	/**
	 * Adds the intensity of firstIon to secondIon.
	 * 
	 * @param firstIon
	 * @param secondIon
	 */
	private void addHigherIntensity(IIon firstIon, IIon secondIon) {

		try {
			firstIon.setAbundance(secondIon.getAbundance());
			setDirty(true);
		} catch(AbundanceLimitExceededException e) {
			// If an exception will be thrown -> do nothing an keep the old
			// value
			setDirty(false);
		}
	}

	/**
	 * This method is used only to create a new ion list. The list has
	 * to be filled by the implementation specific ions of each
	 * implementing class.
	 */
	private void createNewIonList() {

		ionsList = new ArrayList<IIon>(200);
	}

	/**
	 * Checks if the given ion mass over charge ratio (ion) should be
	 * removed.
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
	 * Removes all ions stored in the list from the actual mass
	 * spectrum.
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
	// -----------------------------clone
}
