/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Jan Holy - initial API and implementation, code copied from ChromatogramHeatmapUI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.chemclipse.tsd.model.core.IScanTSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignals;
import org.eclipse.nebula.visualization.widgets.datadefinition.FloatArrayWrapper;
import org.eclipse.nebula.visualization.widgets.datadefinition.IPrimaryArrayWrapper;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

public class ChromatogramHeatmapSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramHeatmapSupport.class);
	/*
	 * IntensityGraphFigure
	 * The intensity graph figure is only capable to display a array of max 10 M data points.
	 */
	private static final int MAX_ARRAY_SIZE = 10000000;

	public Optional<ChromatogramHeatmapData> getHeatmapData(IChromatogram<?> chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		/*
		 * Validation
		 * The validation must be not lower than 1.
		 */
		try {
			scaleIntensityMin = (scaleIntensityMin < 1.0d) ? 1.0d : scaleIntensityMin;
			scaleIntensityMax = (scaleIntensityMax < 1.0d) ? 1.0d : scaleIntensityMax;
			//
			if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
				return getHeatmap(chromatogramMSD, scaleIntensityMin, scaleIntensityMax);
			} else if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
				return getHeatmap(chromatogramWSD, scaleIntensityMin, scaleIntensityMax);
			} else if(chromatogram instanceof IChromatogramTSD chromatogramTSD) {
				return getHeatmap(chromatogramTSD, scaleIntensityMin, scaleIntensityMax);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		//
		return Optional.empty();
	}

	private Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramWSD chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		IExtractedSingleWavelengthSignalExtractor extractor = new ExtractedSingleWavelengthSignalExtractor(chromatogram, true);
		List<IExtractedSingleWavelengthSignals> signals = extractor.getExtractedWavelengthSignals();
		if(signals.size() >= 2) {
			//
			int startScan = 1;
			int stopScan = chromatogram.getNumberOfScans();
			//
			double startWavelength = signals.get(0).getWavelength();
			double stopWavelength = signals.get(signals.size() - 1).getWavelength();
			//
			int dataHeight = signals.size(); // y -> wavelength
			int dataWidth = stopScan - startScan + 1; // x -> scans
			int divisor = calculateDivisor(dataWidth, dataHeight);
			dataHeight /= divisor;
			//
			double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			//
			float[] heatmapData = new float[dataHeight * dataWidth * 2];
			int j = 0;
			for(int signalNumber = signals.size() - 1; signalNumber >= 0; signalNumber--) {
				IExtractedSingleWavelengthSignals signal = signals.get(signalNumber);
				for(int scan = startScan; scan <= stopScan; scan++) {
					/*
					 * Skip values on demand.
					 */
					if(divisor > 1 && scan % divisor != 0) {
						continue;
					}
					/*
					 * XY data
					 */
					int i = Math.min(Math.max(signal.getStartScan(), scan), signal.getStopScan());
					heatmapData[j] = signal.getTotalScanSignal(i).getTotalSignal();
					j++;
				}
			}
			//
			float maxAbudance = -Float.MAX_VALUE;
			float minAbudance = Float.MAX_VALUE;
			for(float value : heatmapData) {
				minAbudance = Float.min(minAbudance, value);
				maxAbudance = Float.max(maxAbudance, value);
			}
			//
			IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
			Range axisRangeWidth = new Range(startRetentionTime, stopRetentionTime);
			Range axisRangeHeight = new Range(startWavelength, stopWavelength);
			double minimum = minAbudance * scaleIntensityMin;
			double maximum = maxAbudance / scaleIntensityMax;
			//
			return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight));
		}
		return Optional.empty();
	}

	private Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramMSD chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		IExtractedIonSignalExtractor extractor = new ExtractedIonSignalExtractor(chromatogram);
		IExtractedIonSignals signals = extractor.getExtractedIonSignals();
		//
		int startScan = signals.getStartScan();
		int stopScan = signals.getStopScan();
		//
		int startIon = signals.getStartIon();
		int stopIon = signals.getStopIon();
		//
		int dataWidth = stopScan - startScan + 1; // x -> scans
		int dataHeight = stopIon - startIon + 1; // x -> m/z values
		int divisor = calculateDivisor(dataHeight, dataWidth);
		dataWidth /= divisor;
		//
		double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		/*
		 * The data height and width must be >= 1!
		 */
		if(dataHeight <= 1 || dataWidth <= 1) {
			return Optional.empty();
		}
		//
		float[] heatmapData = new float[dataWidth * dataHeight * 2];
		//
		int i = 0;
		for(int ion = stopIon; ion >= startIon; ion--) {
			for(int scan = startScan; scan <= stopScan; scan++) {
				/*
				 * Skip values on demand.
				 */
				if(divisor > 1 && scan % divisor != 0) {
					continue;
				}
				//
				try {
					/*
					 * XY data
					 */
					IExtractedIonSignal extractedIonSignal = signals.getExtractedIonSignal(scan);
					heatmapData[i] = extractedIonSignal.getAbundance(ion);
				} catch(NoExtractedIonSignalStoredException e) {
					heatmapData[i] = 0;
				}
				i++;
			}
		}
		//
		float maxAbudance = -Float.MAX_VALUE;
		float minAbudance = Float.MAX_VALUE;
		for(float value : heatmapData) {
			minAbudance = Float.min(minAbudance, value);
			maxAbudance = Float.max(maxAbudance, value);
		}
		//
		IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
		Range axisRangeWidth = new Range(startRetentionTime, stopRetentionTime);
		Range axisRangeHeight = new Range(startIon, stopIon);
		double minimum = minAbudance * scaleIntensityMin;
		double maximum = maxAbudance / scaleIntensityMax;
		//
		return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight));
	}

	private Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramTSD chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		if(chromatogram.isType2()) {
			return getHeatmapType2(chromatogram, scaleIntensityMin, scaleIntensityMax);
		} else {
			return getHeatmapType1(chromatogram, scaleIntensityMin, scaleIntensityMax);
		}
	}

	private Optional<ChromatogramHeatmapData> getHeatmapType1(IChromatogramTSD chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		List<IScanTSD> scansTSD = getScansTSD(chromatogram.getScans());
		int startScan = 1;
		int stopScan = scansTSD.size();
		int width = extractSignalLength(scansTSD);
		int startSignal = 1;
		int stopSignal = width;
		//
		int dataWidth = stopSignal - startSignal + 1; // x -> drift values
		int dataHeight = stopScan - startScan + 1; // x -> scans
		int divisor = calculateDivisor(dataWidth, dataHeight);
		dataHeight /= divisor;
		//
		double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		/*
		 * The data height and width must be >= 1!
		 */
		if(dataHeight <= 1 || dataWidth <= 1) {
			return Optional.empty();
		}
		//
		float[] heatmapData = new float[dataWidth * dataHeight * 2];
		//
		int i = 0;
		int startJ = 0;
		int stopJ = scansTSD.size() - 1;
		int startK = 0;
		int stopK = width - 1;
		//
		for(int j = stopJ; j >= startJ; j--) {
			/*
			 * Skip values on demand.
			 */
			if(divisor > 1 && j % divisor != 0) {
				continue;
			}
			//
			IScanTSD scanTSD = scansTSD.get(j);
			for(int k = startK; k <= stopK; k++) {
				float[] signals = scanTSD.getSignals();
				if(k < signals.length) {
					heatmapData[i] = scanTSD.getSignals()[k];
				} else {
					heatmapData[i] = 0.0f;
				}
				i++;
			}
		}
		//
		float maxAbudance = -Float.MAX_VALUE;
		float minAbudance = Float.MAX_VALUE;
		for(float value : heatmapData) {
			minAbudance = Float.min(minAbudance, value);
			maxAbudance = Float.max(maxAbudance, value);
		}
		//
		IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
		Range axisRangeWidth = new Range(startSignal, stopSignal);
		Range axisRangeHeight = new Range(startRetentionTime, stopRetentionTime);
		double minimum = minAbudance * scaleIntensityMin;
		double maximum = maxAbudance / scaleIntensityMax;
		//
		String axisLabelX = chromatogram.getLabelAxisX();
		String axisLabelY = chromatogram.getLabelAxisY();
		return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight, axisLabelX, axisLabelY));
	}

	private Optional<ChromatogramHeatmapData> getHeatmapType2(IChromatogramTSD chromatogram, double scaleIntensityMin, double scaleIntensityMax) {

		List<IScanTSD> scansTSD = getScansTSD(chromatogram.getScans());
		int startScan = 1;
		int stopScan = scansTSD.size();
		int height = extractSignalLength(scansTSD);
		int startSignal = 1;
		int stopSignal = height;
		//
		int dataWidth = stopScan - startScan + 1;
		int dataHeight = stopSignal - startSignal + 1;
		int divisor = calculateDivisor(dataWidth, dataHeight);
		dataHeight /= divisor;
		//
		double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		/*
		 * The data height and width must be >= 1!
		 */
		if(dataHeight <= 1 || dataWidth <= 1) {
			return Optional.empty();
		}
		//
		float[] heatmapData = new float[dataWidth * dataHeight * 2];
		//
		int i = 0;
		int startJ = 0;
		int stopJ = height - 1;
		int startK = 0;
		int stopK = scansTSD.size() - 1;
		//
		for(int j = stopJ; j >= startJ; j--) {
			/*
			 * Skip values on demand.
			 */
			if(divisor > 1 && j % divisor != 0) {
				continue;
			}
			//
			for(int k = startK; k <= stopK; k++) {
				IScanTSD scanTSD = scansTSD.get(k);
				float[] signals = scanTSD.getSignals();
				if(j < signals.length) {
					heatmapData[i] = scanTSD.getSignals()[j];
				} else {
					heatmapData[i] = 0.0f;
				}
				i++;
			}
		}
		//
		float maxAbudance = -Float.MAX_VALUE;
		float minAbudance = Float.MAX_VALUE;
		for(float value : heatmapData) {
			minAbudance = Float.min(minAbudance, value);
			maxAbudance = Float.max(maxAbudance, value);
		}
		//
		IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
		Range axisRangeWidth = new Range(startRetentionTime, stopRetentionTime);
		Range axisRangeHeight = new Range(startSignal, stopSignal);
		double minimum = minAbudance * scaleIntensityMin;
		double maximum = maxAbudance / scaleIntensityMax;
		//
		String axisLabelX = chromatogram.getLabelAxisX();
		String axisLabelY = chromatogram.getLabelAxisY();
		return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight, axisLabelX, axisLabelY));
	}

	private List<IScanTSD> getScansTSD(List<IScan> scans) {

		List<IScanTSD> scansTSD = new ArrayList<>();
		for(IScan scan : scans) {
			if(scan instanceof IScanTSD scanTSD) {
				scansTSD.add(scanTSD);
			}
		}
		//
		return scansTSD;
	}

	private int extractSignalLength(List<IScanTSD> scansTSD) {

		int width = Integer.MIN_VALUE;
		for(IScanTSD scanTSD : scansTSD) {
			width = Math.max(width, scanTSD.getSignals().length);
		}
		//
		return width;
	}

	/**
	 * Calculates the divisor to match the MAX_ARRAY_CONSTRAINT on the
	 * variable int value.
	 * 
	 * @param fix
	 * @param variable
	 * @return int
	 */
	private int calculateDivisor(int fix, int variable) {

		return calculateDivisor(1, fix, variable);
	}

	private int calculateDivisor(int divisor, int fix, int variable) {

		if(fix * variable <= MAX_ARRAY_SIZE) {
			return divisor;
		} else {
			divisor *= 2;
			return calculateDivisor(divisor, fix, variable / divisor);
		}
	}
}
