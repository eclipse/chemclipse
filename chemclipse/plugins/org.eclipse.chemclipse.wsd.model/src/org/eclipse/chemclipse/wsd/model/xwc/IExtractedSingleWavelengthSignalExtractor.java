/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;

public interface IExtractedSingleWavelengthSignalExtractor {

	/**
	 * 
	 * @param chromatogramSelection
	 * @return List of signals for marked wavelengths, signals are sorted according to wavelength
	 *         first is the shortest wavelength and last is longest wavelength.
	 *         if parameter join signal is to true signal for each wavelength is return together, otherwise intervals are saved separately
	 */
	List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection);

	/**
	 * 
	 * @return List of signals for marked wavelengths, signals are sorted according to wavelength
	 *         first is the shortest wavelength and last is longest wavelength.
	 *         if parameter join signal is to true signal for each wavelength is return together, otherwise intervals are saved separately
	 */
	List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals();

	/**
	 * signal start and finish in interval between startScan and stopScan
	 * 
	 * @param startScan
	 * @param stopScan
	 * @return List of signals for marked wavelengths, signals are sorted according to wavelength
	 *         first is the shortest wavelength and last is longest wavelength.
	 *         if parameter join signal is to true signal for each wavelength is return together, otherwise intervals are saved separately
	 */
	List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(int startScan, int stopScan);

	/**
	 * return list of signals for all marked wavelength
	 * each signal start and finish in interval between startScan and stopScan
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param markedWavelengths
	 * @return List of signals for marked wavelengths, signals are sorted according to wavelength
	 *         first is the shortest wavelength and last is longest wavelength.
	 *         if parameter join signal is to true signal for each wavelength is return together, otherwise intervals are saved separately
	 */
	List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(int startScan, int stopScan, IMarkedWavelengths markedWavelengths);

	/**
	 * return list of signals for all marked wavelength
	 * each signal start and finish in interval between startScan and stopScan
	 * 
	 * @param markedWavelengths
	 * @return List of signals for marked wavelengths, signals are sorted according to wavelength
	 *         first is the shortest wavelength and last is longest wavelength.
	 *         if parameter join signal is to true signal for each wavelength is return together, otherwise intervals are saved separately
	 */
	List<IExtractedSingleWavelengthSignals> getExtractedWavelengthSignals(IMarkedWavelengths markedWavelengths);

	/**
	 * signal start and finish in interval between startScan and stopScan
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param markedWavelength
	 * @return signal
	 */
	Optional<IExtractedSingleWavelengthSignals> getExtractWavelengthContinuousSignal(int startScan, int stopScan, IMarkedWavelength markedWavelength);

	/**
	 * 
	 * signal start and finish in interval between startScan and stopScan
	 * 
	 * @param markedWavelength
	 * @return signal
	 */
	Optional<IExtractedSingleWavelengthSignals> getExtractWavelengthContinuousSignal(IMarkedWavelength markedWavelength);

	/**
	 * if set true, signal, which has some wavelength, will be storage together and missing signal will be interpolated
	 * otherwise if signal contains discontinuity, it will be split.
	 * 
	 * @return
	 */
	boolean isJoinSignal();

	/**
	 * if set true, signal, which has some wavelength, will be storage together and missing signal will be interpolated
	 * otherwise if signal contains discontinuity, it will be split.
	 * 
	 * @param joinSignal
	 */
	void setJoinSignal(boolean joinSignal);
}
