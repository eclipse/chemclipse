/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

/**
 * The interface IMassSpectrum extends Serializable to enable an automated
 * storage of mass spectra on the disk.<br/>
 * It would be not efficient to load all mass spectra (e.g. scans of a
 * chromatogram) into the memory. In such a case, java would run out of memory.
 * <br/>
 * That's not what we want. The storage of mass spectra is controlled by
 * AbstractChromatogram and its subclasses.<br/>
 * <br/>
 * Class structure of mass spectra:<br/>
 * <br/>
 * [AbstractMassSpectrum]<br/>
 * implements (IMassSpectrum)<br/>
 * |<br/>
 * --------------------------------------------------------<br/>
 * | |<br/>
 * [AbstractRegularMassSpectrum] [AbstractCombinedMassSpectrum]<br/>
 * implements (IRegularMassSpectrum) implements (ICombinedMassSpectrum)<br/>
 * |<br/>
 * ----------------------------------------------------------------------------
 * ------------------------------------ | | | [AbstractPeakMassSpectrum]
 * [AbstractSupplierMassSpectrum] [AbstractRegularLibraryMassSpectrum]<br/>
 * implements (IPeakMassSpectrum) implements (ISupplierMassSpectrum) implements
 * (IRegularLibraryMassSpectrum)<br/>
 * | |<br/>
 * -----------------------------------------
 * ------------------------------------
 * --------------------------------------------<br/>
 * | | | | | |<br/>
 * [AgilentMassSpectrum] [NetCDFMassSpectrum] [MzXMLMassSpectrum]<br/>
 * [AMDISLibraryMassSpectrum]<br/>
 * [PeakSubstanceLibraryMassSpectrum]<br/>
 * [...]<br/>
 * 
 * @author Philip Wenig
 * @author Alexander Kerner
 * @see AbstractChromatogramMSD
 */
public interface IScanMSD extends IScan, IMassSpectrumCloneable, IMassSpectrumNormalizable, IIonProvider {

	/**
	 * Returns the total ion current (TIC).<br/>
	 * All ions which are stored in excludedIons will not be
	 * considered.<br/>
	 * If no ions are stored, 0 will be returned.
	 * 
	 * @return float - total ion current
	 */
	float getTotalSignal(IMarkedIons excludedIons);

	/**
	 * Returns an IExtractedIonSignal object.<br/>
	 * The lowest and highest ion borders will be calculated dynamically.
	 * 
	 * @return IExtractedIonSignal
	 */
	IExtractedIonSignal getExtractedIonSignal();

	/**
	 * Returns an IExtractedIonSignal object with the given scan range.
	 * 
	 * @return IExtractedIonSignal
	 */
	IExtractedIonSignal getExtractedIonSignal(double startIon, double stopIon);

	/**
	 * Returns the base peak. The base peak is the peak with the highest
	 * abundance.<br/>
	 * If no ions are stored, 0 will be returned.
	 * 
	 * @return float BasePeak
	 */
	double getBasePeak();

	/**
	 * Returns the abundance of the base peak.<br/>
	 * If no ions are stored, 0 will be returned.
	 * 
	 * @return float BasePeakAbundance
	 */
	float getBasePeakAbundance();

	/**
	 * Returns the lowest ion ion of the mass spectrum.<br/>
	 * If no ions are stored, the ImmutableZeroIon will be returned.
	 * 
	 * @return IIon
	 */
	IIon getLowestIon();

	/**
	 * Returns the lowest abundance ion of the mass spectrum.<br/>
	 * If no ions are stored, the ImmutableZeroIon will be returned.
	 * 
	 * @return IIon
	 */
	IIon getLowestAbundance();

	/**
	 * Returns the highest ion ion of the mass spectrum.<br/>
	 * If no ions are stored, the ImmutableZeroIon will be returned.
	 * 
	 * @return IIon
	 */
	IIon getHighestIon();

	/**
	 * Returns the highest abundance ion of the mass spectrum.<br/>
	 * If no ions are stored, the ImmutableZeroIon will be returned.
	 * 
	 * @return IIon
	 */
	IIon getHighestAbundance();

	/**
	 * Returns the lowest and the highest ion (ion) of the actual scan.<br/>
	 * If no ions are stored, null will be returned.
	 * 
	 * @return {@link IIonBounds}
	 */
	IIonBounds getIonBounds();

	/**
	 * Add the list of ions to the current mass spectrum.<br/>
	 * Use add intensities if you would like to add the intensity if there is
	 * still a ion with the same ion value.<br/>
	 * If addIntensities is false, the higher intensity will be taken if there
	 * is still a ion with the same ion value.
	 * 
	 * @param ions
	 * @param addIntensities
	 * @return {@code this}
	 */
	IScanMSD addIons(List<IIon> ions, boolean addIntensities);

	/**
	 * Adds a new ion to the mass spectrum.<br/>
	 * You have an option "checked" to decide whether you like to add a mass
	 * spectrum with or without check to the mass spectrum.<br/>
	 * Checked == false means, that it will not be checked, if there is still a
	 * ion of same ion in the database.<br/>
	 * If there is a check, a ion with same ion can be added to the
	 * database, but if the abundance of the new ion is higher than the old one,
	 * the new abundance will be added.
	 * 
	 * @param ion
	 * @param checked
	 * @return {@code this}
	 */
	IScanMSD addIon(IIon ion, boolean checked);

	/**
	 * Adds a ion to the mass spectrum.<br/>
	 * Use this method in all standard cases.
	 * 
	 * @param ion
	 * @return {@code this}
	 */
	IScanMSD addIon(IIon ion);

	/**
	 * If addIntensity is true, the intensity of the given ion will be
	 * added to the ion with the same ion value if exists.<br/>
	 * If the ion not exists, the ion will be added normally. If
	 * addIntensity is false, the higher of both abundance values (ion
	 * and still existent ion) will be stored. The
	 * 
	 * @param addIntensity
	 * @param ion
	 * @return {@code this}
	 */
	IScanMSD addIon(boolean addIntensity, IIon ion);

	/**
	 * Removes a ion from the mass spectrum.
	 * 
	 * @param ion
	 * @return {@code this}
	 */
	IScanMSD removeIon(IIon ion);

	/**
	 * Removes all ions from the actual mass spectrum.
	 * 
	 * @return {@code this}
	 */
	IScanMSD removeAllIons();

	/**
	 * Removes the ions stored in {@link IMarkedIons} from
	 * the actual mass spectrum. Think of stored mass over charge ratios (ion):
	 * 42.7, 43.3, 43.4.<br/>
	 * When removeIon(43) is called, they will be removed depending on
	 * the rounding algorithm in AbstractIon.getIon(float ion).
	 * 
	 * @param excludedIons
	 * @return {@code this}
	 */
	IScanMSD removeIons(IMarkedIons markedIons);

	/**
	 * Removes all ions from the actual mass spectrum with the given
	 * ion.<br/>
	 * Think of stored mass over charge ratios (ion): 42.7, 43.3, 43.4.<br/>
	 * When removeIon(43) is called, they will be removed depending on
	 * the rounding algorithm in AbstractIon.getIon(float ion).
	 * 
	 * @param ion
	 * @return {@code this}
	 */
	IScanMSD removeIon(int ion);

	/**
	 * Removes all ion with the given mass over charge ratio (ion).
	 * Think of stored mass over charge ratios (ion): 42.7, 43.3, 43.4.<br/>
	 * When removeIon(43) is called, they will be removed depending on
	 * the rounding algorithm in AbstractIon.getIon(float ion).
	 * 
	 * @param ions
	 * @return {@code this}
	 */
	IScanMSD removeIons(Set<Integer> ions);

	/**
	 * Returns the IIon object for the given ion value.<br/>
	 * BE CAREFUL, YOU CAN'T MODIFY THE ABUNDANCE OF THE MASS FRAGMENT WITH THE
	 * RETURNED INSTANCE.<br/>
	 * If no ion is stored, null will be returned.<br/>
	 * If a mass spectrum contains high resolution data, the abundance of the
	 * mass over charge values will be added.<br/>
	 * <br/>
	 * Example:<br/>
	 * 45.1 ion, 72893.3 abundance<br/>
	 * => 45,72893.3 will be returned<br/>
	 * <br/>
	 * 45.123 ion, 72893.3 abundance<br/>
	 * 45.33 ion, 493.3 abundance<br/>
	 * 45.487 ion, 89.5 abundance<br/>
	 * => 45,73476.1 will be returned<br/>
	 * 
	 * @param ion
	 * @throws AbundanceLimitExceededException
	 * @throws IonLimitExceededException
	 * @return IIon
	 */
	IIon getIon(int ion) throws AbundanceLimitExceededException, IonLimitExceededException;

	/**
	 * Returns the ion with the given exact ion value.
	 * 
	 * @param ion
	 * @return IIon
	 * @throws AbundanceLimitExceededException
	 * @throws IonLimitExceededException
	 */
	IIon getIon(double ion) throws AbundanceLimitExceededException, IonLimitExceededException;

	/**
	 * Returns the ion with the given accurate ion value.
	 * Precision see AbstractIon.getIon(float ion, int precision);
	 * {@link AbstractIon}
	 * 
	 * @param ion
	 * @param precision
	 * @return IIon
	 * @throws AbundanceLimitExceededException
	 * @throws IonLimitExceededException
	 */
	IIon getIon(double ion, int precision) throws AbundanceLimitExceededException, IonLimitExceededException;

	/**
	 * Adjust all ions of the chromatogram to the adjusted signal
	 * height.<br/>
	 * The adjustment is given as a percentage value.<br/>
	 * All values must be between -1.0f(-100%) and 0.0f(0%) and 1.0f(100%).<br/>
	 * 
	 * @param percentage
	 */
	void adjustIons(float percentage);

	/**
	 * Returns a copy of the actual mass spectrum corrected by the given
	 * {@link IMarkedIons}.<br/>
	 * The copy does not consists of ions stored in the excluded mass
	 * fragment list.
	 * 
	 * @param excludedIons
	 * @return IMassSpectrum
	 */
	IScanMSD getMassSpectrum(IMarkedIons excludedIons);

	/**
	 * Returns false, if no ions are stored in this mass spectrum.
	 * 
	 * @return boolean
	 */
	boolean hasIons();

	/**
	 * Specialized classes could be of type proxy.
	 * This method enforces the proxy to load its data completely.
	 */
	void enforceLoadScanProxy();

	/**
	 * Sets the optimized mass spectrum for this scan.
	 * 
	 * @param optimizedMassSpectrum
	 */
	void setOptimizedMassSpectrum(IScanMSD optimizedMassSpectrum);

	/**
	 * Returns the optimized mass spectrum.
	 * It could be null if no optimized MS has been set.
	 * 
	 * @return
	 */
	IScanMSD getOptimizedMassSpectrum();

	/**
	 * Returns whether this is a SIM (<= 10 m/z values) or SCAN measurement.
	 * 
	 * @return boolean
	 */
	boolean isMeasurementSIM();

	/**
	 * Returns true if the mass spectrum contains ion transitions.
	 * 
	 * @return boolean
	 */
	boolean isTandemMS();

	/**
	 * Returns true if the mass spectrum contains high resolution data.
	 * 
	 * @return boolean
	 */
	boolean isHighResolutionMS();

	/**
	 * Check each ion for possible collisions with other ions before adding it.
	 * This comes at a huge performance toll and may not be required for MALDI.
	 * 
	 * @return boolean
	 */
	boolean checkIntensityCollisions();
}
