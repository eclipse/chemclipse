/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.FilterException;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.noise.Calculator;
import org.eclipse.chemclipse.msd.model.noise.INoiseSegment;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalsModifier;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class Denoising {

	private static final Logger logger = Logger.getLogger(Denoising.class);
	private static IonNoiseAbundanceComparator ionNoiseAbundanceComparator = new IonNoiseAbundanceComparator(SortOrder.DESC);

	/**
	 * Use only static methods.
	 */
	private Denoising() {

	}

	/**
	 * Tries to remove ions according to noise and to lower the
	 * signals of noise.
	 * 
	 * @param chromatogramSelection
	 * @throws FilterException
	 */
	public static List<ICombinedMassSpectrum> applyDenoisingFilter(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons ionsToRemove, IMarkedIons ionsToPreserve, boolean adjustThresholdTransitions, int numberOfUsedIonsForCoefficient, int segmentWidth, IProgressMonitor monitor) throws FilterException {

		List<ICombinedMassSpectrum> noiseMassSpectra = new ArrayList<>();
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Denoising", 8);
		try {
			/*
			 * Test if there are ions to remove.
			 */
			if(ionsToRemove == null) {
				throw new FilterException("The ions to remove instance was null.");
			}
			subMonitor.worked(1);
			/*
			 * Test if there are ions to preserve.
			 */
			if(ionsToPreserve == null) {
				throw new FilterException("The ions to preserve instance was null.");
			}
			subMonitor.worked(1);
			/*
			 * Get the extracted ion signals and the calculator instance.
			 */
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
			IExtractedIonSignalExtractor extractedIonSignalExtractor;
			try {
				extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			} catch(ChromatogramIsNullException e1) {
				throw new FilterException("The chromatogram must be not null.");
			}
			subMonitor.worked(1);
			/*
			 * A -> Remove the predefined ions.
			 */
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			extractedIonSignals = removeIonsInScanRange(extractedIonSignals, ionsToRemove, monitor);
			subMonitor.worked(1);
			/*
			 * B -> Adjust zero values (see Stein et al.)
			 */
			if(adjustThresholdTransitions) {
				try {
					ExtractedIonSignalsModifier.adjustThresholdTransitions(extractedIonSignals);
				} catch(AnalysisSupportException e) {
					logger.warn(e);
				}
			}
			subMonitor.worked(1);
			/*
			 * C -> Calculate the noise segments and remove the noise sequentially.
			 */
			Calculator calculator = new Calculator();
			List<INoiseSegment> noiseSegments = calculator.getNoiseSegments(extractedIonSignals, ionsToPreserve, segmentWidth, monitor);
			subMonitor.worked(1);
			/*
			 * D -> Iterate through all noise segments and remove the noise between
			 * the segments. Subtract the noise mass spectrum from scan in the
			 * segment
			 */
			noiseMassSpectra.addAll(subtractNoiseMassSpectraFromSegments(extractedIonSignals, noiseSegments, ionsToPreserve, numberOfUsedIonsForCoefficient, monitor));
			subMonitor.worked(1);
			/*
			 * E -> Writes the results back to the chromatogram.
			 */
			DenoiseOperation denoiseOperation = new DenoiseOperation(chromatogramSelection, extractedIonSignals);
			denoiseOperation.addContext(UndoContextFactory.getUndoContext());
			try {
				OperationHistoryFactory.getOperationHistory().execute(denoiseOperation, null, null);
			} catch(ExecutionException e) {
				logger.warn(e);
			}
			subMonitor.worked(1);
		} finally {
			subMonitor.done();
		}
		/*
		 * Return the noise mass spectrum. It will be displayed in a view in the
		 * user interface.
		 */
		return noiseMassSpectra;
	}

	/**
	 * Removes the given ions from the scan range (start/stop scan).
	 * 
	 * @param extractedIonSignals
	 * @param startScan
	 * @param stopScan
	 * @param ionsToRemove
	 * @param monitor
	 */
	private static IExtractedIonSignals removeIonsInScanRange(IExtractedIonSignals extractedIonSignals, IMarkedIons ionsToRemove, IProgressMonitor monitor) {

		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		IExtractedIonSignal extractedIonSignal;
		for(int scan = startScan; scan <= stopScan; scan++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				removeIons(extractedIonSignal, ionsToRemove);
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
		return extractedIonSignals;
	}

	// TODO Refaktor in extracted ion signal auslagern?
	/**
	 * Removes the selected ions from the given extracted ion signal.
	 */
	private static void removeIons(IExtractedIonSignal extractedIonSignal, IMarkedIons selectedIons) {

		for(int ion : selectedIons.getIonsNominal()) {
			extractedIonSignal.setAbundance(ion, 0.0f, true);
		}
	}

	/**
	 * Subtracts the noise mass spectrum from the given scan range.
	 * 
	 * @param extractedIonSignals
	 * @param noiseMassSpectrum
	 * @param startScan
	 * @param stopScan
	 * @param monitor
	 */
	private static void subtractNoiseMassSpectrumFromScanRange(IExtractedIonSignals extractedIonSignals, ICombinedMassSpectrum noiseMassSpectrum, int startScan, int stopScan, int numberOfUsedIonsForCoefficient, IProgressMonitor monitor) {

		IExtractedIonSignal extractedIonSignal;
		for(int scan = startScan; scan <= stopScan; scan++) {
			try {
				extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
				subtractNoiseMassSpectrumFromScan(extractedIonSignal, noiseMassSpectrum, numberOfUsedIonsForCoefficient, monitor);
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Subtracts the noise mass spectrum from the scan.
	 * 
	 * @param extractedIonSignal
	 * @param noiseMassSpectrum
	 * @param monitor
	 */
	private static void subtractNoiseMassSpectrumFromScan(IExtractedIonSignal extractedIonSignal, ICombinedMassSpectrum noiseMassSpectrum, int numberOfUsedIonsForCoefficient, IProgressMonitor monitor) {

		IExtractedIonSignal noiseSignal = noiseMassSpectrum.getExtractedIonSignal();
		float correlationFactor = calculateCoefficient(extractedIonSignal, noiseSignal, numberOfUsedIonsForCoefficient);
		/*
		 * Avoid a DivideByZero exception.
		 */
		if(correlationFactor <= 0) {
			return;
		}
		int startIon = noiseSignal.getStartIon();
		int stopIon = noiseSignal.getStopIon();
		float abundance;
		float subtractAbundance;
		float newAbundance;
		for(int ion = startIon; ion <= stopIon; ion++) {
			abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0) {
				subtractAbundance = correlationFactor * noiseSignal.getAbundance(ion);
				newAbundance = abundance - subtractAbundance;
				if(newAbundance <= 0.0f) {
					extractedIonSignal.setAbundance(ion, 0.0f, true);
				} else {
					extractedIonSignal.setAbundance(ion, newAbundance, true);
				}
			}
		}
	}

	/**
	 * Calculates a noise coefficient.
	 * 
	 * @param extractedIonSignal
	 * @param noiseSignal
	 * @return float
	 */
	private static float calculateCoefficient(IExtractedIonSignal extractedIonSignal, IExtractedIonSignal noiseSignal, int numberOfUsedIonsForCoefficient) {

		int startIon = noiseSignal.getStartIon();
		int stopIon = noiseSignal.getStopIon();
		float abundanceNoise;
		float abundanceScan;
		float coefficient = 0.0f;
		/*
		 * Get the noise ions. The compareTo method allows to sort by
		 * abundance descending.
		 */
		List<IonNoise> entries = new ArrayList<IonNoise>();
		for(int ion = startIon; ion <= stopIon; ion++) {
			entries.add(new IonNoise(ion, noiseSignal.getAbundance(ion)));
		}
		/*
		 * Sort by abundance descending.
		 */
		Collections.sort(entries, ionNoiseAbundanceComparator);
		/*
		 * The correlation factors. The number of used ions tells how
		 * many ions shall be taken into account.
		 */
		List<Float> coefficients = new ArrayList<Float>();
		int counter = 0;
		exitfor:
		for(IonNoise entry : entries) {
			abundanceNoise = noiseSignal.getAbundance(entry.getIon());
			/*
			 * The noise signal abundance must be > 0. It will be in most cases,
			 * as the entries are sorted descending (ion 56 - 1000, ion 43 - 998
			 * ....)
			 */
			if(abundanceNoise > 0.0f) {
				/*
				 * Then, check if there is an appropriate extracted ion signal
				 * abundance for the given ion. If "yes", then calculate the
				 * correlation factor. If "no", move on.
				 */
				abundanceScan = extractedIonSignal.getAbundance(entry.getIon());
				if(abundanceScan > 0.0f) {
					coefficient = abundanceScan / abundanceNoise;
					coefficients.add(coefficient);
					counter++;
					/*
					 * If still the number of used ions have been
					 * taken, skip the for loop.
					 */
					if(counter > numberOfUsedIonsForCoefficient) {
						break exitfor;
					}
				}
			}
		}
		/*
		 * Transform the correlation factors.
		 */
		int size = coefficients.size();
		float[] values = new float[size];
		for(int i = 0; i < size; i++) {
			values[i] = coefficients.get(i);
		}
		return Calculations.getMean(values);
	}

	/**
	 * Subtracts the noise mass spectra from the calculated segments.
	 * 
	 * @return
	 */
	private static List<ICombinedMassSpectrum> subtractNoiseMassSpectraFromSegments(IExtractedIonSignals extractedIonSignals, List<INoiseSegment> noiseSegments, IMarkedIons ionsToPreserve, int numberOfUsedIonsForCoefficient, IProgressMonitor monitor) {

		List<ICombinedMassSpectrum> noiseMassSpectra = new ArrayList<ICombinedMassSpectrum>();
		Calculator calculator = new Calculator();
		int segments = noiseSegments.size();
		int startScan;
		int stopScan;
		List<ICombinedMassSpectrum> segmentNoiseMassSpectra;
		INoiseSegment currentNoiseSegment;
		INoiseSegment followingNoiseSegment;
		boolean firstRun = true;
		for(int segment = 0; segment < segments; segment++) {
			/*
			 * Calculate the start and stop scans for each segment and the noise
			 * mass spectra. And extract the relevant noise mass spectra.
			 */
			segmentNoiseMassSpectra = new ArrayList<ICombinedMassSpectrum>();
			currentNoiseSegment = noiseSegments.get(segment);
			segmentNoiseMassSpectra.add(currentNoiseSegment.getNoiseMassSpectrum());
			if(segment == 0 && firstRun == true) {
				/*
				 * The first segment starts with the beginning of the scan of
				 * the chromatogram selection. It ends with the last scan of the
				 * first noise segment minus
				 * calculateTailingScans(currentNoiseSegment). E.g. the current
				 * noise segment has a length of 13 scans, then 7 scans will be
				 * subtracted, as we don't want to have overlapping segments.
				 */
				startScan = extractedIonSignals.getStartScan();
				stopScan = currentNoiseSegment.getAnalysisSegment().getStopScan() - calculateTailingScans(currentNoiseSegment);
				/*
				 * Set the segment back and the variable first run to false. The
				 * first segment must be taken into account twice. Why? A: from
				 * the beginning of the chromatogram selection to the first
				 * segment B: from the first segment to the second segment, see
				 * else case (following segment).
				 */
				firstRun = false;
				segment--;
			} else if(segment == segments - 1) {
				/*
				 * The last segment starts with the first scan of the last noise
				 * segment. It ends with the last scan of the chromatogram
				 * selection.
				 */
				startScan = currentNoiseSegment.getAnalysisSegment().getStartScan() + calculateLeadingScans(currentNoiseSegment);
				stopScan = extractedIonSignals.getStopScan();
			} else {
				/*
				 * All segments except the first and last segment will be
				 * calculated in the following way: The start scan is the first
				 * scan of the previous element and the stop scan the last scan
				 * of the following noise segment.
				 */
				followingNoiseSegment = noiseSegments.get(segment + 1);
				startScan = currentNoiseSegment.getAnalysisSegment().getStartScan() + calculateLeadingScans(currentNoiseSegment);
				stopScan = followingNoiseSegment.getAnalysisSegment().getStopScan() - calculateTailingScans(followingNoiseSegment);
				segmentNoiseMassSpectra.add(followingNoiseSegment.getNoiseMassSpectrum());
			}
			/*
			 * Calculate the noise mass spectrum and add it to the noise mass
			 * spectra list.
			 */
			ICombinedMassSpectrum noiseMassSpectrum = calculator.getNoiseMassSpectrum(segmentNoiseMassSpectra, ionsToPreserve, monitor);
			/*
			 * Set the scan and retention time values to show in the noise mass
			 * spectrum view.
			 */
			noiseMassSpectrum.setStartScan(startScan);
			noiseMassSpectrum.setStopScan(stopScan);
			noiseMassSpectrum.setStartRetentionTime(extractedIonSignals.getChromatogram().getScan(startScan).getRetentionTime());
			noiseMassSpectrum.setStopRetentionTime(extractedIonSignals.getChromatogram().getScan(stopScan).getRetentionTime());
			noiseMassSpectra.add(noiseMassSpectrum);
			subtractNoiseMassSpectrumFromScanRange(extractedIonSignals, noiseMassSpectrum, startScan, stopScan, numberOfUsedIonsForCoefficient, monitor);
		}
		return noiseMassSpectra;
	}

	/**
	 * If the segment has e.g. a width of 13 scans, the method will return 6. If
	 * the segment has e.g. a width of 14 scans, it will return 7.
	 * 
	 * @param noiseSegment
	 * @return int
	 */
	private static int calculateLeadingScans(INoiseSegment noiseSegment) {

		int width = noiseSegment.getAnalysisSegment().getWidth();
		int result = 0;
		if(width > 0) {
			result = width / 2;
		}
		return result;
	}

	/**
	 * If the segment has e.g. a width of 13 scans, the method will return 7. If
	 * the segment has e.g. a width of 14 scans, it will return 7.
	 * 
	 * @param noiseSegment
	 * @return int
	 */
	private static int calculateTailingScans(INoiseSegment noiseSegment) {

		int width = noiseSegment.getAnalysisSegment().getWidth();
		int result = 0;
		if(width > 0) {
			/*
			 * E.g. 13 % 2 == 1 14 % 2 == 0
			 */
			if(width % 2 == 0) {
				result = width / 2;
			} else {
				result = (width / 2) + 1;
			}
		}
		return result;
	}
}
