/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
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

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrumProxy;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.exceptions.NoIdentifiedScansAvailableException;
import org.eclipse.chemclipse.swt.ui.exceptions.NoPeaksAvailableException;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Converts chromatograms, mass spectra, peaks, baselines to valid series
 * instances to draw with e.g. SWTChart.
 * 
 * @author eselmeister
 */
public class SeriesConverterMSD {

	public static final Logger logger = Logger.getLogger(SeriesConverterMSD.class);

	/**
	 * Use only static methods.
	 */
	private SeriesConverterMSD() {
	}

	// --------------------------------------------------IChromatogram
	/**
	 * Returns the series of the given chromatogram selection.<br/>
	 * Extract the selected ions.<br/>
	 * If combine is false, than each ion will be returned as a
	 * distinct series.
	 * 
	 * @param chromatogramSelection
	 * @param selectedIons
	 * @param combine
	 * @param sign
	 * @return List<ISeries>
	 */
	public static IMultipleSeries convertChromatogram(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons selectedIons, boolean combine, Sign sign) {

		IMultipleSeries ionSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals(startScan, stopScan);
				ISeries series;
				/*
				 * Retrieve the chromatogram x and y signals for each mass
				 * fragment.<br/> Should they be combined?
				 */
				if(combine) {
					series = getSelectedIonsAsCombinedSeries(selectedIons, signals, sign);
					ionSeries.add(series);
				} else {
					for(IMarkedIon markedIon : selectedIons) {
						int ion = AbstractIon.getIon(markedIon.getIon());
						series = getSelectedIonAsSeries(ion, markedIon.getMagnification(), signals, sign);
						ionSeries.add(series);
					}
				}
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return ionSeries;
	}

	public static IMultipleSeries convertChromatogram(IChromatogramMSD chromatogram, IMarkedIons selectedIons, boolean combine, Sign sign, int startScan, int stopScan) {

		IMultipleSeries ionSeries = new MultipleSeries();
		if(chromatogram != null) {
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals(startScan, stopScan);
				ISeries series;
				/*
				 * Retrieve the chromatogram x and y signals for each mass
				 * fragment.<br/> Should they be combined?
				 */
				if(combine) {
					series = getSelectedIonsAsCombinedSeries(selectedIons, signals, sign);
					ionSeries.add(series);
				} else {
					for(IMarkedIon markedIon : selectedIons) {
						int ion = AbstractIon.getIon(markedIon.getIon());
						series = getSelectedIonAsSeries(ion, markedIon.getMagnification(), signals, sign);
						ionSeries.add(series);
					}
				}
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return ionSeries;
	}

	// TODO JUnit und optimieren
	public static IMultipleSeries convertChromatogramAccurate(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons selectedIons, boolean combine, Sign sign, int precision) {

		IMultipleSeries ionSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			ScanRange scanRange = new ScanRange(startScan, stopScan);
			ISeries series;
			/*
			 * Retrieve the chromatogram x and y signals for each mass
			 * fragment.<br/> Should they be combined?
			 */
			if(combine) {
				series = getSelectedAccurateIonsAsCombinedSeries(selectedIons, chromatogram, scanRange, sign, precision);
				ionSeries.add(series);
			} else {
				for(IMarkedIon markedIon : selectedIons) {
					series = getSelectedAccurateIonAsSeries(markedIon.getIon(), chromatogram, scanRange, sign, precision);
					ionSeries.add(series);
				}
			}
		}
		return ionSeries;
	}

	public static IMultipleSeries convertChromatogramExact(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons selectedIons, boolean combine, Sign sign) {

		IMultipleSeries ionSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			ScanRange scanRange = new ScanRange(startScan, stopScan);
			ISeries series;
			/*
			 * Retrieve the chromatogram x and y signals for each mass
			 * fragment.<br/> Should they be combined?
			 */
			if(combine) {
				series = getSelectedExactIonsAsCombinedSeries(selectedIons, chromatogram, scanRange, sign);
				ionSeries.add(series);
			} else {
				for(IMarkedIon markedIon : selectedIons) {
					series = getSelectedExactIonAsSeries(markedIon.getIon(), chromatogram, scanRange, sign);
					ionSeries.add(series);
				}
			}
		}
		return ionSeries;
	}

	/**
	 * Returns the ion transition map.
	 * 
	 * @param chromatogramSelection
	 * @param ionTransitions
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertIonTranstionChromatogram(IChromatogramSelectionMSD chromatogramSelection, Set<IIonTransition> ionTransitions) {

		IMultipleSeries ionSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			ScanRange scanRange = new ScanRange(startScan, stopScan);
			ISeries series;
			/*
			 * Retrieve the chromatogram x and y signals for each mass
			 * fragment.<br/> Should they be combined?
			 */
			for(IIonTransition ionTransition : ionTransitions) {
				series = getSelectedIonTransitionAsSeries(ionTransition, chromatogram, scanRange);
				ionSeries.add(series);
			}
		}
		return ionSeries;
	}

	/**
	 * Converts the given chromatogram by the selected ions. The mass
	 * fragments will be shown as a combined value.<br/>
	 * If you would like to show each ion separately, chose the method
	 * "convertChromatogram(IChromatogramSelection chromatogramSelection, IMarkedIons selectedIons, boolean combine, Sign sign)"
	 * .
	 * 
	 * @param chromatogramSelection
	 * @param selectedIons
	 * @param sign
	 * @return ISeries
	 */
	public static ISeries convertChromatogramBySelectedIons(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons selectedIons, Sign sign) {

		IMultipleSeries chromatogramSeries = convertChromatogram(chromatogramSelection, selectedIons, true, sign);
		return chromatogramSeries.getMultipleSeries().get(0);
	}

	/**
	 * Returns a chromatogram selection series without the excluded mass
	 * fragments.
	 * 
	 * @param chromatogramSelection
	 * @param excludedIons
	 * @param sign
	 * @return ISeries
	 */
	public static ISeries convertChromatogramByExcludedIons(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons excludedIons, Sign sign) {

		ISeries ionSeries = null;
		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals(startScan, stopScan);
				ionSeries = getIonSeriesWithoutExcludedIons(excludedIons, signals, sign);
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return ionSeries;
	}

	/**
	 * Returns the peaks of the given chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param sign
	 * @return ISeries
	 */
	public static ISeries convertPeakMaxPositions(IChromatogramSelectionMSD chromatogramSelection, IOffset offset, Sign sign, boolean activeForAnalysis) throws NoPeaksAvailableException {

		List<IChromatogramPeakMSD> chromatogramPeaks = chromatogramSelection.getChromatogramMSD().getPeaks(chromatogramSelection);
		IMultipleSeries peakSeries = SeriesConverter.convertPeakMaxMarker(chromatogramPeaks, sign, offset, activeForAnalysis);
		return peakSeries.getMultipleSeries().get(0);
	}

	/**
	 * Returns the identified scans of the given chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param sign
	 * @return ISeries
	 */
	public static ISeries convertIdentifiedScans(IChromatogramSelectionMSD chromatogramSelection, IOffset offset, Sign sign) throws NoIdentifiedScansAvailableException {

		IMassSpectra massSpectra = getIdentifiedScans(chromatogramSelection, false);
		IMultipleSeries identifiedScansSeries = convertIdentifiedScans(massSpectra, sign, offset);
		return identifiedScansSeries.getMultipleSeries().get(0);
	}

	public static IMassSpectra getIdentifiedScans(IChromatogramSelectionMSD chromatogramSelection, boolean enforceLoadScanProxy) {

		IMassSpectra massSpectra = new MassSpectra();
		List<IScan> scans = chromatogramSelection.getChromatogram().getScans();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		//
		for(IScan scan : scans) {
			if(scan instanceof IVendorMassSpectrum) {
				/*
				 * TODO This method makes huge some problems when using Scan Proxies.
				 * So it's disabled for Scan Proxies at the moment.
				 */
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scan;
				if(massSpectrum instanceof IVendorMassSpectrumProxy) {
					/*
					 * Proxy
					 */
					// if(isTargetMassSpectrum(massSpectrum, startRetentionTime, stopRetentionTime)) {
					// if(enforceLoadScanProxy) {
					// massSpectrum.enforceLoadScanProxy();
					// }
					// massSpectra.addMassSpectrum(massSpectrum);
					// }
				} else {
					/*
					 * Normal
					 */
					if(isTargetMassSpectrum(massSpectrum, startRetentionTime, stopRetentionTime)) {
						massSpectra.addMassSpectrum(massSpectrum);
					}
				}
			}
		}
		return massSpectra;
	}

	private static boolean isTargetMassSpectrum(IVendorMassSpectrum massSpectrum, int startRetentionTime, int stopRetentionTime) {

		if(massSpectrum.getTargets().size() > 0) {
			int retentionTime = massSpectrum.getRetentionTime();
			if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
				return true;
			}
		}
		return false;
	}

	public static IMultipleSeries convertIdentifiedScans(IMassSpectra massSpectra, Sign sign, IOffset offset) throws NoIdentifiedScansAvailableException {

		IMultipleSeries identifiedScanSeries = new MultipleSeries();
		if(massSpectra != null) {
			offset = SeriesConverter.validateOffset(offset);
			if(massSpectra.size() == 0) {
				throw new NoIdentifiedScansAvailableException();
			}
			/*
			 * Get the retention time and max abundance value for each peak.
			 */
			int amountIdentifiedScans = massSpectra.size();
			double[] xSeries = new double[amountIdentifiedScans];
			double[] ySeries = new double[amountIdentifiedScans];
			int x = 0;
			int y = 0;
			double retentionTime;
			double abundance;
			double xOffset;
			double yOffset;
			/*
			 * Iterate through all identified scans of the chromatogram selection.
			 */
			for(IScanMSD identifiedScan : massSpectra.getList()) {
				/*
				 * Retrieve the x and y signal of each peak.
				 */
				retentionTime = identifiedScan.getRetentionTime();
				abundance = identifiedScan.getTotalSignal();
				/*
				 * Sign the abundance as a negative value?
				 */
				xOffset = offset.getCurrentXOffset();
				yOffset = offset.getCurrentYOffset();
				if(sign == Sign.NEGATIVE) {
					abundance *= -1;
					xOffset *= -1;
					yOffset *= -1;
				}
				/*
				 * Set the offset.
				 */
				retentionTime += xOffset;
				abundance += yOffset;
				/*
				 * Store the values in the array.
				 */
				xSeries[x++] = retentionTime;
				ySeries[y++] = abundance;
				/*
				 * Add the peak.
				 */
				identifiedScanSeries.add(new Series(xSeries, ySeries, "Identified Scans"));
			}
		}
		return identifiedScanSeries;
	}

	/**
	 * Returns the chromatogram selections as series by using the selected ions
	 * of the first chosen chromatogram.
	 * 
	 * @param chromatograms
	 * @param sign
	 * @param offset
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertChromatogramsUseSelectedIons(List<IChromatogramSelection> chromatogramSelections, Sign sign, IOffset offset) {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries chromatogramSeries = new MultipleSeries();
		if(chromatogramSelections != null && chromatogramSelections.size() >= 1) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * The first chromatogram is the master.
			 * It determines the retention time range.<br/>
			 * The master determines the scan range size for SWTChart.
			 */
			IChromatogramSelection masterSelection = chromatogramSelections.get(0);
			int masterStartRetentionTime = masterSelection.getStartRetentionTime();
			int masterStopRetentionTime = masterSelection.getStopRetentionTime();
			/*
			 * Extract the marked ions.
			 */
			IMarkedIons selectedIons = null;
			if(masterSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD masterMSD = (IChromatogramSelectionMSD)masterSelection;
				selectedIons = masterMSD.getSelectedIons();
			}
			/*
			 * Check selected ions.
			 */
			if(selectedIons == null) {
				selectedIons = new MarkedIons();
			}
			/*
			 * Iterate through all chromatogram selections.
			 */
			int counter = 0;
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				counter++;
				/*
				 * Get the scan range of the master chromatogram.
				 */
				int startScan = SeriesConverter.getStartScan(masterStartRetentionTime, chromatogram);
				int stopScan = SeriesConverter.getStopScan(masterStopRetentionTime, chromatogram);
				/*
				 * If the start or stop scan is zero, continue with the next chromatogram.
				 */
				if(startScan == 0 || stopScan == 0) {
					continue;
				}
				/*
				 * Total signals
				 */
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
					IMultipleSeries multipleSeries = convertChromatogram(chromatogramSelectionMSD.getChromatogramMSD(), selectedIons, true, sign, startScan, stopScan);
					ISeries series = multipleSeries.getMultipleSeries().get(0);
					/*
					 * Increment the offset.
					 */
					offset.incrementCurrentXOffset();
					offset.incrementCurrentYOffset();
					chromatogramSeries.add(new Series(series.getXSeries(), series.getYSeries(), "[" + counter + "] " + chromatogram.getName() + " > " + selectedIons.getIonsNominal()));
				}
			}
		}
		return chromatogramSeries;
	}

	// --------------------------------------------------IChromatogram
	// --------------------------------------------------IMassSpectrum
	/**
	 * Converts a mass spectrum.<br/>
	 * If the given mass spectrum is null, null will be returned.
	 * 
	 * @return ISeries
	 */
	public static ISeries convertNominalMassSpectrum(IScanMSD massSpectrum, Sign sign) {

		ISeries massSpectrumSeries = null;
		if(massSpectrum != null && massSpectrum.getTotalSignal() > 0.0f) {
			/*
			 * Filter the mass spectrum if there are more ions
			 * than the limit stored. Else import all ions.
			 */
			List<IIon> ionList = ConverterMSD.getFilteredIons(massSpectrum);
			IExtractedIonSignal extractedIonSignal = new ExtractedIonSignal(ionList);
			/*
			 * Extend the range +- 1 ion value.
			 * Initialize with zero.
			 */
			int startIon = extractedIonSignal.getStartIon() - 1;
			int stopIon = extractedIonSignal.getStopIon() + 1;
			int ions = stopIon - startIon + 1;
			//
			double[] xSeries = new double[ions];
			double[] ySeries = new double[ions];
			int x = 0;
			int y = 0;
			/*
			 * Get the abundance for each ion and check if the values should be
			 * negative
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				xSeries[x++] = ion;
				double signal = extractedIonSignal.getAbundance(ion);
				if(sign == Sign.NEGATIVE) {
					signal *= -1;
				}
				ySeries[y++] = signal;
			}
			//
			IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
			boolean filterMassSpectrum = preferences.getBoolean(PreferenceSupplier.P_FILTER_MASS_SPECTRUM, PreferenceSupplier.DEF_FILTER_MASS_SPECTRUM);
			String label = "Nominal MS";
			if(filterMassSpectrum) {
				label += "*";
			}
			massSpectrumSeries = new Series(xSeries, ySeries, label);
		} else {
			/*
			 * If no mass spectrum is available or the mass spectrum is null.
			 */
			massSpectrumSeries = getZeroMassSpectrumSeries();
		}
		return massSpectrumSeries;
	}

	/**
	 * Returns both mass spectra in a multiple series instance.
	 * 
	 * @param massSpectrum
	 * @param mirroredMassSpectrum
	 * @return
	 */
	public static IMultipleSeries convertNominalMassSpectrum(IScanMSD massSpectrum, IScanMSD mirroredMassSpectrum) {

		IMultipleSeries massSpectrumSeries = new MultipleSeries();
		massSpectrumSeries.add(convertNominalMassSpectrum(massSpectrum, Sign.POSITIVE));
		massSpectrumSeries.add(convertNominalMassSpectrum(mirroredMassSpectrum, Sign.NEGATIVE));
		return massSpectrumSeries;
	}

	public static ISeries convertExactMassSpectrum(IScanMSD massSpectrum, Sign sign) {

		ISeries massSpectrumSeries = null;
		if(massSpectrum != null && massSpectrum.getTotalSignal() > 0.0f) {
			/*
			 * Extend the range +- 1 ion value.
			 */
			double startIon = massSpectrum.getLowestIon().getIon() - 1;
			double stopIon = massSpectrum.getHighestIon().getIon() + 1;
			int ions = massSpectrum.getNumberOfIons() + 2;
			/*
			 * Initialize with zero.
			 */
			double[] xSeries = new double[ions];
			double[] ySeries = new double[ions];
			int x = 0;
			int y = 0;
			double signal;
			/*
			 * Start ion
			 */
			xSeries[x++] = startIon;
			ySeries[y++] = 0.0d;
			/*
			 * Get the abundance for each ion and check if the values should be
			 * negative
			 */
			for(IIon ion : massSpectrum.getIons()) {
				xSeries[x++] = ion.getIon();
				signal = ion.getAbundance();
				if(sign == Sign.NEGATIVE) {
					signal *= -1;
				}
				ySeries[y++] = signal;
			}
			/*
			 * Stop ion
			 */
			xSeries[x++] = stopIon;
			ySeries[y++] = 0.0d;
			/*
			 * Create the series.
			 */
			massSpectrumSeries = new Series(xSeries, ySeries, "Exact MS");
		} else {
			/*
			 * If no mass spectrum is available or the mass spectrum is null.
			 */
			massSpectrumSeries = getZeroMassSpectrumSeries();
		}
		return massSpectrumSeries;
	}

	private static ISeries getZeroMassSpectrumSeries() {

		/*
		 * If no mass spectrum is available or the mass spectrum is null.
		 */
		double[] xSeries = {0, 0};
		double[] ySeries = {0, 0};
		return new Series(xSeries, ySeries, "no mass spectrum available");
	}
	// --------------------------------------------------IMassSpectrum
	// --------------------------------------------------IPeak

	// TODO JUnit
	public static ISeries convertIncreasingInflectionPoints(IPeakMSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			double[] xSeries = new double[2];
			double[] ySeries = new double[2];
			try {
				IPoint intersection;
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				double x;
				/*
				 * Where does the increasing tangent crosses the baseline.
				 */
				intersection = Equations.calculateIntersection(increasing, baseline);
				/*
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[0] = intersection.getX();
				if(includeBackground) {
					ySeries[0] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[0] = intersection.getY();
				}
				/*
				 * This is the highest point of the peak, given by the tangents.
				 */
				intersection = Equations.calculateIntersection(increasing, decreasing);
				/*
				 * Take a look if the retention time (X) is greater than the
				 * peaks retention time.<br/> If yes, take the peaks stop
				 * retention time, otherwise the values would be 0 by default.
				 */
				double stopRetentionTime = peakModel.getStopRetentionTime();
				x = intersection.getX() > stopRetentionTime ? stopRetentionTime : intersection.getX();
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
			}
			series = new Series(xSeries, ySeries, "Increasing Tangent");
		}
		return series;
	}

	// TODO JUnit
	public static ISeries convertDecreasingInflectionPoints(IPeakMSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			double[] xSeries = new double[2];
			double[] ySeries = new double[2];
			try {
				IPoint intersection;
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				double x;
				/*
				 * Where does the decreasing tangent crosses the baseline.
				 */
				intersection = Equations.calculateIntersection(decreasing, baseline);
				/*
				 * Take a look if the retention time (X) is greater than the
				 * peaks retention time.<br/> If yes, take the peaks stop
				 * retention time, otherwise the values would be 0 by default.
				 */
				double stopRetentionTime = peakModel.getStopRetentionTime();
				x = intersection.getX() > stopRetentionTime ? stopRetentionTime : intersection.getX();
				xSeries[0] = intersection.getX();
				if(includeBackground) {
					ySeries[0] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[0] = intersection.getY();
				}
				/*
				 * This is the highest point of the peak, given by the tangents.
				 */
				intersection = Equations.calculateIntersection(increasing, decreasing);
				/*
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
			}
			series = new Series(xSeries, ySeries, "Decreasing Tangent");
		}
		return series;
	}

	// TODO JUnit
	public static ISeries convertPeakPerpendicular(IPeakMSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			double[] xSeries = new double[2];
			double[] ySeries = new double[2];
			xSeries[0] = peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints();
			if(includeBackground) {
				ySeries[0] = peakModel.getBackgroundAbundance(peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints());
			} else {
				ySeries[0] = 0.0d;
			}
			try {
				IPoint intersection = Equations.calculateIntersection(peakModel.getIncreasingInflectionPointEquation(), peakModel.getDecreasingInflectionPointEquation());
				/*
				 * Normally a check if the retention time x is outwards of peak
				 * range should not be performed as it must be in peaks
				 * retention time range, it's the maximum.
				 */
				// TODO einfach background includen geht nicht
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)intersection.getX());
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
			}
			series = new Series(xSeries, ySeries, "Peak Perpendicular");
		}
		return series;
	}

	// TODO JUnit
	// height zwischen 0 und 1
	public static ISeries convertPeakWidthByInflectionPoints(IPeakMSD peak, boolean includeBackground, float height, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			double[] xSeries = new double[2];
			double[] ySeries = new double[2];
			double x;
			LinearEquation percentageHeightBaseline = peakModel.getPercentageHeightBaselineEquation(height);
			if(percentageHeightBaseline != null) {
				try {
					IPoint p1 = Equations.calculateIntersection(peakModel.getIncreasingInflectionPointEquation(), percentageHeightBaseline);
					IPoint p2 = Equations.calculateIntersection(peakModel.getDecreasingInflectionPointEquation(), percentageHeightBaseline);
					/*
					 * Take a look if the retention time (X) is lower than the
					 * peaks retention time.<br/> If yes, take the peaks start
					 * retention time, otherwise the values would be 0 by
					 * default.
					 */
					double startRetentionTime = peakModel.getStartRetentionTime();
					x = p1.getX() < startRetentionTime ? startRetentionTime : p1.getX();
					xSeries[0] = p1.getX();
					/*
					 * Left intersection between increasing tangent and width at
					 * percentage height.
					 */
					if(includeBackground) {
						ySeries[0] = p1.getY() + peakModel.getBackgroundAbundance((int)x);
					} else {
						ySeries[0] = p1.getY();
					}
					/*
					 * Take a look if the retention time (X) is greater than the
					 * peaks retention time.<br/> If yes, take the peaks stop
					 * retention time, otherwise the values would be 0 by
					 * default.
					 */
					double stopRetentionTime = peakModel.getStopRetentionTime();
					x = p2.getX() > stopRetentionTime ? stopRetentionTime : p2.getX();
					xSeries[1] = p2.getX();
					/*
					 * Right intersection between increasing tangent and width
					 * at percentage height.
					 */
					if(includeBackground) {
						ySeries[1] = p2.getY() + peakModel.getBackgroundAbundance((int)x);
					} else {
						ySeries[1] = p2.getY();
					}
				} catch(SolverException e) {
				}
			}
			String id = "Peak Width at " + (height * 100) + "%";
			series = new Series(xSeries, ySeries, id);
		}
		return series;
	}

	// --------------------------------------------------IPeak
	// --------------------------------------------------private methods
	// TODO JUnit - optimieren
	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedAccurateIonAsSeries(double ion, IChromatogramMSD chromatogram, ScanRange scanRange, Sign sign, int precision) {

		assert (chromatogram != null) : "Chromatogram must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = scanRange.getWidth();
		int startScan = scanRange.getStartScan();
		int stopScan = scanRange.getStopScan();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		IVendorMassSpectrum massSpectrum;
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			retentionTime = massSpectrum.getRetentionTime();
			abundance = 0.0d;
			try {
				abundance = massSpectrum.getIon(ion, precision).getAbundance();
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		ISeries series = new Series(xSeries, ySeries, "accurate ion: " + AbstractIon.getIon(ion, precision));
		return series;
	}

	// TODO refactor
	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedExactIonAsSeries(double ion, IChromatogramMSD chromatogram, ScanRange scanRange, Sign sign) {

		assert (chromatogram != null) : "Chromatogram must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = scanRange.getWidth();
		int startScan = scanRange.getStartScan();
		int stopScan = scanRange.getStopScan();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		IVendorMassSpectrum massSpectrum;
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			retentionTime = massSpectrum.getRetentionTime();
			abundance = 0.0d;
			try {
				abundance = massSpectrum.getIon(ion).getAbundance();
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		ISeries series = new Series(xSeries, ySeries, "exact ion: " + ion);
		return series;
	}

	private static ISeries getSelectedIonTransitionAsSeries(IIonTransition ionTransition, IChromatogramMSD chromatogram, ScanRange scanRange) {

		assert (ionTransition != null) : "IonTransition must not be null.";
		assert (chromatogram != null) : "Chromatogram must not be null.";
		double retentionTime;
		double abundance;
		int scans = scanRange.getWidth();
		int startScan = scanRange.getStartScan();
		int stopScan = scanRange.getStopScan();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		IVendorMassSpectrum massSpectrum;
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			retentionTime = massSpectrum.getRetentionTime();
			abundance = 0.0d;
			for(IIon ion : massSpectrum.getIons()) {
				IIonTransition transition = ion.getIonTransition();
				if(transition != null && transition.equals(ionTransition)) {
					abundance = ion.getAbundance();
				}
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(ionTransition.getQ1Ion());
		builder.append(" > ");
		builder.append(ionTransition.getQ3Ion());
		builder.append(" @");
		builder.append(ionTransition.getCollisionEnergy());
		builder.append(" r");
		builder.append(ionTransition.getQ1Resolution());
		builder.append(":");
		builder.append(ionTransition.getQ3Resolution());
		ISeries series = new Series(xSeries, ySeries, builder.toString());
		return series;
	}

	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedIonAsSeries(int ion, int magnification, IExtractedIonSignals signals, Sign sign) {

		assert (signals != null) : "IExtractedIonSignals must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = signals.size();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		for(IExtractedIonSignal signal : signals.getExtractedIonSignals()) {
			retentionTime = signal.getRetentionTime();
			abundance = signal.getAbundance(ion) * magnification;
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		ISeries series = new Series(xSeries, ySeries, "ion: " + ion + " x" + magnification);
		return series;
	}

	// TODO refaktor
	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedAccurateIonsAsCombinedSeries(IMarkedIons selectedIons, IChromatogramMSD chromatogram, ScanRange scanRange, Sign sign, int precision) {

		assert (selectedIons != null) : "IMarkedIons must not be null.";
		assert (chromatogram != null) : "Chromatogram must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = scanRange.getWidth();
		int startScan = scanRange.getStartScan();
		int stopScan = scanRange.getStopScan();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all accurate ion signals from the given signal range.
		 */
		IVendorMassSpectrum massSpectrum;
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			retentionTime = massSpectrum.getRetentionTime();
			abundance = 0.0d;
			for(IMarkedIon markedIon : selectedIons) {
				try {
					abundance += massSpectrum.getIon(markedIon.getIon(), precision).getAbundance();
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		/*
		 * Return the label and the series.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("selected accurate ion: ");
		for(IMarkedIon markedIon : selectedIons) {
			builder.append(AbstractIon.getIon(markedIon.getIon(), precision));
			builder.append(" ");
		}
		ISeries series = new Series(xSeries, ySeries, builder.toString());
		return series;
	}

	// TODO refaktor
	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedExactIonsAsCombinedSeries(IMarkedIons selectedIons, IChromatogramMSD chromatogram, ScanRange scanRange, Sign sign) {

		assert (selectedIons != null) : "IMarkedIons must not be null.";
		assert (chromatogram != null) : "Chromatogram must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = scanRange.getWidth();
		int startScan = scanRange.getStartScan();
		int stopScan = scanRange.getStopScan();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all accurate ion signals from the given signal range.
		 */
		IVendorMassSpectrum massSpectrum;
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = chromatogram.getSupplierScan(scan);
			retentionTime = massSpectrum.getRetentionTime();
			abundance = 0.0d;
			for(IMarkedIon markedIon : selectedIons) {
				try {
					abundance += massSpectrum.getIon(markedIon.getIon()).getAbundance();
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		/*
		 * Return the label and the series.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("selected exact ion: ");
		for(IMarkedIon markedIon : selectedIons) {
			builder.append(markedIon.getIon());
			builder.append(" ");
		}
		ISeries series = new Series(xSeries, ySeries, builder.toString());
		return series;
	}

	// TODO refaktor
	/**
	 * Returns the selected ion as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getSelectedIonsAsCombinedSeries(IMarkedIons selectedIons, IExtractedIonSignals signals, Sign sign) {

		assert (selectedIons != null) : "IMarkedIons must not be null.";
		assert (signals != null) : "IExtractedIonSignals must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = signals.size();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		for(IExtractedIonSignal signal : signals.getExtractedIonSignals()) {
			retentionTime = signal.getRetentionTime();
			abundance = 0;
			/*
			 * Take care with double and int values.
			 * The magnification will be calculated too.
			 */
			for(IMarkedIon markedIon : selectedIons) {
				int ion = AbstractIon.getIon(markedIon.getIon());
				abundance += signal.getAbundance(ion) * markedIon.getMagnification();
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		/*
		 * Return the label and the series.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("selected ion: ");
		for(int ion : selectedIons.getIonsNominal()) {
			builder.append(ion);
			builder.append(" ");
		}
		ISeries series = new Series(xSeries, ySeries, builder.toString());
		return series;
	}

	// TODO refaktor
	/**
	 * Returns the selected ion without the extracted signals as a series.
	 * 
	 * @param ion
	 * @param signals
	 * @param sign
	 * @return ISeries
	 */
	private static ISeries getIonSeriesWithoutExcludedIons(IMarkedIons excludedIons, IExtractedIonSignals signals, Sign sign) {

		assert (excludedIons != null) : "IMarkedIons must not be null.";
		assert (signals != null) : "IExtractedIonSignals must not be null.";
		assert (sign != null) : "Sign must not be null.";
		double retentionTime;
		double abundance;
		int scans = signals.size();
		double[] xSeries = new double[scans];
		double[] ySeries = new double[scans];
		int x = 0;
		int y = 0;
		/*
		 * Extract all ion signals from the given signal range.
		 */
		for(IExtractedIonSignal signal : signals.getExtractedIonSignals()) {
			retentionTime = signal.getRetentionTime();
			abundance = signal.getTotalSignal();
			/*
			 * Subtract the excluded signals.
			 */
			for(int ion : excludedIons.getIonsNominal()) {
				abundance -= signal.getAbundance(ion);
			}
			/*
			 * Sign the abundance as a negative value?
			 */
			if(sign == Sign.NEGATIVE) {
				abundance *= -1;
			}
			/*
			 * Store the values in the array.
			 */
			xSeries[x++] = retentionTime;
			ySeries[y++] = abundance;
		}
		/*
		 * Return the label and the series.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("all ion except: ");
		for(int ion : excludedIons.getIonsNominal()) {
			builder.append(ion);
			builder.append(" ");
		}
		ISeries series = new Series(xSeries, ySeries, builder.toString());
		return series;
	}
	// --------------------------------------------------private methods
}
