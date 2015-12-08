/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.parts;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;

public class DeconvConverterUI {

	public static IMultipleSeries setViewSeries(IArraysViewDeconv arraysViewDeconv) {

		IMultipleSeries multiSeries = setWorkflow(arraysViewDeconv);
		return multiSeries;
	}

	public static IMultipleSeries setWorkflow(IArraysViewDeconv arraysViewDeconv) {

		IMultipleSeries multiSeries = new MultipleSeries();
		/*
		 * Set Series
		 */
		if(arraysViewDeconv.size() != 0) {
			/*
			 * Set xScale for all others
			 */
			int size = arraysViewDeconv.size();
			double[] xScaleView = new double[size];
			for(int i = 0; i < size; i++) {
				xScaleView[i] = arraysViewDeconv.getxValues(i).getScale();
			}
			/*
			 * yScale Original Chromatogram
			 */
			if(arraysViewDeconv.sizeYScaleOriginalChromatogram() != 0) {
				double[] yChroma = new double[size];
				for(int i = 0; i < size; i++) {
					yChroma[i] = arraysViewDeconv.getyValuesChromatogram(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, yChroma, DeconvHelper.PROPERTY_DECONVIEW_ORIGINALCHROMA));
			}
			if(arraysViewDeconv.sizeBaseline() != 0) {
				double[] yBaseline = new double[size];
				for(int i = 0; i < size; i++) {
					yBaseline[i] = arraysViewDeconv.getyBaselineValues(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, yBaseline, DeconvHelper.PROPERTY_DECONVIEW_BASELINE));
			}
			if(arraysViewDeconv.sizeNoise() != 0) {
				double[] yNoise = new double[size];
				for(int i = 0; i < size; i++) {
					yNoise[i] = arraysViewDeconv.getyNoiseValues(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, yNoise, DeconvHelper.PROPTERY_DECONVIEW_NOISE));
			}
			if(arraysViewDeconv.sizeSmoothed() != 0) {
				double[] ySmoothed = new double[size];
				for(int i = 0; i < size; i++) {
					ySmoothed[i] = arraysViewDeconv.getySmoothedValues(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, ySmoothed, DeconvHelper.PROPTERY_DECONVIEW_SMOOTHED));
			}
			if(arraysViewDeconv.sizeFirstDeriv() != 0) {
				double[] yFirstDeriv = new double[size];
				for(int i = 0; i < size; i++) {
					yFirstDeriv[i] = arraysViewDeconv.getyFirstDeriv(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, yFirstDeriv, DeconvHelper.PROPTERY_DECONVIEW_FIRSTDERIV));
			}
			if(arraysViewDeconv.sizeSecondDeriv() != 0) {
				double[] ySecondDeriv = new double[size];
				for(int i = 0; i < size; i++) {
					ySecondDeriv[i] = arraysViewDeconv.getySecondDeriv(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, ySecondDeriv, DeconvHelper.PROPTERY_DECONVIEW_SECONDDERIV));
			}
			if(arraysViewDeconv.sizeThirdDeriv() != 0) {
				double[] yThirdDeriv = new double[size];
				for(int i = 0; i < size; i++) {
					yThirdDeriv[i] = arraysViewDeconv.getyThirdDeriv(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, yThirdDeriv, DeconvHelper.PROPTERY_DECONVIEW_THIRDDERIV));
			}
			if(arraysViewDeconv.sizePeakRanges() != 0) {
				double[] peakRanges = new double[size];
				for(int i = 0; i < size; i++) {
					peakRanges[i] = arraysViewDeconv.getPeakValues(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, peakRanges, DeconvHelper.PROPERTY_DECONVIEW_PEAKRANGES));
			}
			if(arraysViewDeconv.sizePeakRangesEndPoints() != 0) {
				double[] peakRangesEnPoint = new double[size];
				for(int i = 0; i < size; i++) {
					peakRangesEnPoint[i] = arraysViewDeconv.getPeakValuesEndPoint(i).getScale();
				}
				multiSeries.add(new Series(xScaleView, peakRangesEnPoint, DeconvHelper.PROPERTY_DECONVIEW_PEAKRANGES_ENDPOINTS));
			}
		}
		return multiSeries;
	}
}
