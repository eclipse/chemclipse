/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignals;
import org.eclipse.nebula.visualization.widgets.datadefinition.FloatArrayWrapper;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

public class ChromatogramHeatmapSupport {

	public Optional<ChromatogramHeatmapData> getHeatmap(@SuppressWarnings("rawtypes") IChromatogram chromatogram) {

		if(chromatogram instanceof IChromatogramMSD) {
			return getHeatmap((IChromatogramMSD)chromatogram);
		} else if(chromatogram instanceof IChromatogramWSD) {
			return getHeatmap((IChromatogramWSD)chromatogram);
		} else {
			return Optional.empty();
		}
	}

	public Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramWSD chromatogram) {

		IExtractedSingleWavelengthSignalExtractor extractor = new ExtractedSingleWavelengthSignalExtractor(chromatogram, true);
		List<IExtractedSingleWavelengthSignals> signals = extractor.getExtractedWavelengthSignals();
		if(signals.size() >= 2) {
			//
			double startWavelength = signals.get(0).getWavelength();
			double stopWavelength = signals.get(signals.size() - 1).getWavelength();
			int startScan = 1;
			int stopScan = chromatogram.getNumberOfScans();
			int dataHeight = signals.size(); // y -> wavelength
			int dataWidth = stopScan - startScan + 1; // x -> scans
			double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			float[] heatmapData = new float[dataHeight * dataWidth * 2];
			int j = 0;
			for(int signalNumber = signals.size() - 1; signalNumber >= 0; signalNumber--) {
				IExtractedSingleWavelengthSignals signal = signals.get(signalNumber);
				for(int scan = startScan; scan <= stopScan; scan++) {
					/*
					 * 
					 */
					int i = Math.min(Math.max(signal.getStartScan(), scan), signal.getStopScan());
					heatmapData[j] = signal.getTotalScanSignal(i).getTotalSignal();
					j++;
				}
			}
			float maxAbudance = -Float.MAX_VALUE;
			float minAbudance = Float.MAX_VALUE;
			for(float f : heatmapData) {
				maxAbudance = Float.max(maxAbudance, f);
				minAbudance = Float.min(minAbudance, f);
			}
			return Optional.of(new ChromatogramHeatmapData(new FloatArrayWrapper(heatmapData), new Range(startRetentionTime, stopRetentionTime), new Range(startWavelength, stopWavelength), minAbudance, maxAbudance, dataWidth, dataHeight));
		}
		return Optional.empty();
	}

	public Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramMSD chromatogram) {

		IExtractedIonSignalExtractor extractedIonSignalExtractor;
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
		IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
		//
		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		//
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		//
		int dataWidth = stopScan - startScan + 1; // x -> scans
		int dataHeight = stopIon - startIon + 1; // x -> m/z values
		//
		double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
		double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
		/*
		 * The data height and width must be >= 1!
		 */
		if(dataHeight <= 1 || dataWidth <= 1) {
			return Optional.empty();
		}
		/*
		 * Parse the heatmap data
		 */
		float[] heatmapData = new float[dataWidth * dataHeight * 2];
		/*
		 * Y-Axis: Scans
		 */
		int i = 0;
		for(int ion = stopIon; ion >= startIon; ion--) {
			for(int scan = startScan; scan <= stopScan; scan++) {
				IExtractedIonSignal extractedIonSignal;
				try {
					extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
					/*
					 * XY data
					 */
					heatmapData[i] = extractedIonSignal.getAbundance(ion);
				} catch(NoExtractedIonSignalStoredException e) {
					heatmapData[i] = 0;
				}
				i++;
			}
		}
		float maxAbudance = -Float.MAX_VALUE;
		float minAbudance = Float.MAX_VALUE;
		for(float f : heatmapData) {
			maxAbudance = Float.max(maxAbudance, f);
			minAbudance = Float.min(minAbudance, f);
		}
		maxAbudance = (float)(maxAbudance / (dataWidth / 5.0d));
		return Optional.of(new ChromatogramHeatmapData(new FloatArrayWrapper(heatmapData), new Range(startRetentionTime, stopRetentionTime), new Range(startIon, stopIon), minAbudance, maxAbudance, dataWidth, dataHeight));
	}
}
