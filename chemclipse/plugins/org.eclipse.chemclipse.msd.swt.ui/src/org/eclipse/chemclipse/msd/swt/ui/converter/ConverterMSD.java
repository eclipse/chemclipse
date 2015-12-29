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
package org.eclipse.chemclipse.msd.swt.ui.converter;

// TODO JUnit
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.swt.ui.support.PlotSeries;

public class ConverterMSD {

	private static final Logger logger = Logger.getLogger(ConverterMSD.class);

	/**
	 * Use only the static methods.
	 */
	private ConverterMSD() {
	}

	/**
	 * Returns a list of filtered ions if the setting has been activated and the
	 * number of stored ions is >= the limit of ions to show.
	 * 
	 * @param massSpectrum
	 * @return List
	 */
	public static List<IIon> getFilteredIons(IScanMSD massSpectrum) {

		List<IIon> ions;
		//
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		boolean filterMassSpectrum = preferences.getBoolean(PreferenceSupplier.P_FILTER_MASS_SPECTRUM, PreferenceSupplier.DEF_FILTER_MASS_SPECTRUM);
		int filterLimitIons = preferences.getInt(PreferenceSupplier.P_FILTER_LIMIT_IONS, PreferenceSupplier.DEF_FILTER_LIMIT_IONS);
		int numberOfIons = massSpectrum.getNumberOfIons();
		//
		if(filterMassSpectrum && numberOfIons > filterLimitIons) {
			//
			ions = new ArrayList<IIon>();
			//
			float[] intensities = new float[massSpectrum.getIons().size()];
			int i = 0;
			for(IIon ion : massSpectrum.getIons()) {
				intensities[i++] = ion.getAbundance();
			}
			//
			float maxIntensity = Calculations.getMax(intensities);
			float medianIntensity = Calculations.getMedian(intensities);
			float limitIntensity = medianIntensity + ((maxIntensity - medianIntensity) / 6.0f);
			int moduloValue = numberOfIons / 100;
			//
			int j = 0;
			for(IIon ion : massSpectrum.getIons()) {
				float intensity = ion.getAbundance();
				if(intensity >= limitIntensity) {
					ions.add(ion);
				} else {
					if(moduloValue > 0 && j % moduloValue == 0) {
						ions.add(ion);
					}
				}
				j++;
			}
		} else {
			ions = massSpectrum.getIons();
		}
		return ions;
	}

	/**
	 * Returns the tic signal of the selected ions.<br/>
	 * If chromatogram or selected ions are null, null will be
	 * returned.
	 * 
	 * @param chromatogram
	 * @param selectedIons
	 * @return PlotSeries
	 */
	public static PlotSeries convertChromatogram(IChromatogramMSD chromatogram, IMarkedIons selectedIons) {

		PlotSeries chromatogramSeries = null;
		if(chromatogram != null && selectedIons != null) {
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
				int scans = signals.size();
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				double abundance;
				/*
				 * Retrieve the chromatogram x and y signals.
				 */
				for(IExtractedIonSignal signal : signals.getExtractedIonSignals()) {
					xSeries[x++] = signal.getRetentionTime();
					/*
					 * Get the total abundance of the selected ions.
					 */
					abundance = 0;
					for(int ion : selectedIons.getIonsNominal()) {
						abundance += signal.getAbundance(ion);
					}
					ySeries[y++] = abundance;
				}
				signals = null;
				chromatogramSeries = new PlotSeries(xSeries, ySeries);
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return chromatogramSeries;
	}

	/**
	 * Returns for each selected ion a plot series.
	 * 
	 * @param chromatogram
	 * @param selectedIons
	 * @return List<PlotSeries>
	 */
	public static List<PlotSeries> convertIons(IChromatogramMSD chromatogram, IMarkedIons selectedIons) {

		List<PlotSeries> plotSeriesList = new ArrayList<PlotSeries>();
		PlotSeries ionSeries;
		if(chromatogram != null && selectedIons != null) {
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
				int scans = signals.size();
				/*
				 * Retrieve for each selected ion the ion x and
				 * y signals.
				 */
				for(int ion : selectedIons.getIonsNominal()) {
					double[] xSeries = new double[scans];
					double[] ySeries = new double[scans];
					int x = 0;
					int y = 0;
					/*
					 * Get the ion value for each scan.
					 */
					for(IExtractedIonSignal signal : signals.getExtractedIonSignals()) {
						xSeries[x++] = signal.getRetentionTime();
						ySeries[y++] = signal.getAbundance(ion);
					}
					/*
					 * Create the ion plot series and store it in the
					 * list.
					 */
					ionSeries = new PlotSeries(xSeries, ySeries);
					plotSeriesList.add(ionSeries);
				}
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return plotSeriesList;
	}

	/**
	 * If the given mass spectrum is null, null will be returned.
	 * 
	 * @param massSpectrum
	 * @return {@link PlotSeries}
	 */
	public static PlotSeries convertMassSpectrum(IScanMSD massSpectrum) {

		PlotSeries massSpectrumSeries = null;
		if(massSpectrum != null) {
			IExtractedIonSignal extractedIonSignal = massSpectrum.getExtractedIonSignal();
			/*
			 * Extend the range +- 1 ion value.
			 */
			int startIon = extractedIonSignal.getStartIon() - 1;
			int stopIon = extractedIonSignal.getStopIon() + 1;
			int ions = stopIon - startIon + 1;
			/*
			 * Initialize with zero.
			 */
			double[] xSeries = new double[ions];
			double[] ySeries = new double[ions];
			int x = 0;
			int y = 0;
			for(int ion = startIon; ion <= stopIon; ion++) {
				xSeries[x++] = ion;
				ySeries[y++] = extractedIonSignal.getAbundance(ion);
			}
			massSpectrumSeries = new PlotSeries(xSeries, ySeries);
		}
		return massSpectrumSeries;
	}

	// TODO add background?
	/**
	 * Returns the plot series of a peak.
	 * 
	 * @return PlotSeries
	 */
	public static PlotSeries convertPeak(IChromatogramPeakMSD peak) {

		PlotSeries peakSeries = null;
		if(peak != null) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			/*
			 * Initialize with zero.
			 */
			int size = peakModel.getRetentionTimes().size();
			double[] xSeries = new double[size];
			double[] ySeries = new double[size];
			int x = 0;
			int y = 0;
			for(int retentionTime : peakModel.getRetentionTimes()) {
				xSeries[x++] = retentionTime;
				ySeries[y++] = peakModel.getPeakAbundance(retentionTime);
			}
			peakSeries = new PlotSeries(xSeries, ySeries);
		}
		return peakSeries;
	}

	/**
	 * Returns a list of peak plot series.
	 * 
	 * @return List<PlotSeries>
	 */
	public static List<PlotSeries> convertPeaks(List<IChromatogramPeakMSD> peaks) {

		List<PlotSeries> plotSeries = null;
		PlotSeries peakSeries;
		if(peaks != null) {
			plotSeries = new ArrayList<PlotSeries>();
			for(IChromatogramPeakMSD peak : peaks) {
				peakSeries = convertPeak(peak);
				plotSeries.add(peakSeries);
			}
		}
		return plotSeries;
	}
}
