/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
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
import org.eclipse.nebula.visualization.widgets.datadefinition.IPrimaryArrayWrapper;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

public class ChromatogramHeatmapSupport {

	public Optional<ChromatogramHeatmapData> getHeatmapData(IChromatogram<?> chromatogram) {

		if(chromatogram instanceof IChromatogramMSD) {
			return getHeatmap((IChromatogramMSD)chromatogram);
		} else if(chromatogram instanceof IChromatogramWSD) {
			return getHeatmap((IChromatogramWSD)chromatogram);
		} else {
			return Optional.empty();
		}
	}

	private Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramWSD chromatogram) {

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
			//
			double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			//
			float[] heatmapData = new float[dataHeight * dataWidth * 2];
			int j = 0;
			for(int signalNumber = signals.size() - 1; signalNumber >= 0; signalNumber--) {
				IExtractedSingleWavelengthSignals signal = signals.get(signalNumber);
				for(int scan = startScan; scan <= stopScan; scan++) {
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
				maxAbudance = Float.max(maxAbudance, value);
				minAbudance = Float.min(minAbudance, value);
			}
			//
			IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
			Range axisRangeWidth = new Range(startRetentionTime, stopRetentionTime);
			Range axisRangeHeight = new Range(startWavelength, stopWavelength);
			double minimum = minAbudance;
			double maximum = maxAbudance;
			//
			return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight));
		}
		return Optional.empty();
	}

	private Optional<ChromatogramHeatmapData> getHeatmap(IChromatogramMSD chromatogram) {

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
		//
		double startRetentionTime = chromatogram.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
		double stopRetentionTime = chromatogram.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
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
			maxAbudance = Float.max(maxAbudance, value);
			minAbudance = Float.min(minAbudance, value);
		}
		//
		IPrimaryArrayWrapper arrayWrapper = new FloatArrayWrapper(heatmapData);
		Range axisRangeWidth = new Range(startRetentionTime, stopRetentionTime);
		Range axisRangeHeight = new Range(startIon, stopIon);
		double minimum = minAbudance;
		double maximum = (float)(maxAbudance / (dataWidth / 5.0d));
		//
		return Optional.of(new ChromatogramHeatmapData(arrayWrapper, axisRangeWidth, axisRangeHeight, minimum, maximum, dataWidth, dataHeight));
	}
}
