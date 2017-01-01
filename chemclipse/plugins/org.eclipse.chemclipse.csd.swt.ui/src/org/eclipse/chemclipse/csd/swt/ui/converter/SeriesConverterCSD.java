/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.swt.ui.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IScan;
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
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class SeriesConverterCSD {

	/**
	 * Use only static methods.
	 */
	private SeriesConverterCSD() {
	}

	public static ISeries convertPeak(IPeakCSD peak, boolean includeBackground, Sign sign) {

		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeak(peaks, includeBackground, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static ISeries convertPeakMaxPositions(IChromatogramSelectionCSD chromatogramSelection, IOffset offset, Sign sign, boolean activeForAnalysis) throws NoPeaksAvailableException {

		List<IChromatogramPeakCSD> peaks = chromatogramSelection.getChromatogramCSD().getPeaks(chromatogramSelection);
		IMultipleSeries peakSeries = SeriesConverter.convertPeakMaxMarker(peaks, sign, offset, activeForAnalysis);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static ISeries convertSelectedPeak(IPeakCSD peak, boolean includeBackground, Sign sign) {

		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeak(peaks, includeBackground, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static ISeries convertSelectedPeakBackground(IPeakCSD peak, Sign sign) {

		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeakBackground(peaks, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static IMultipleSeries convertPeak(List<IPeakCSD> peaks, boolean includeBackground, Sign sign, IOffset offset) {

		IMultipleSeries peakSeries = new MultipleSeries();
		if(peaks != null) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * Convert each peak to a series.
			 */
			for(IPeakCSD peak : peaks) {
				/*
				 * Continue if the actual peak is null.
				 */
				if(peak == null) {
					continue;
				}
				IPeakModelCSD peakModel = peak.getPeakModel();
				/*
				 * Initialize with zero.
				 */
				int size = peakModel.getRetentionTimes().size();
				double[] xSeries = new double[size];
				double[] ySeries = new double[size];
				int x = 0;
				int y = 0;
				/*
				 * Values.
				 */
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Go through all retention times of the peak.
				 */
				for(int retentionTime : peakModel.getRetentionTimes()) {
					abundance = peakModel.getPeakAbundance(retentionTime);
					/*
					 * Include the background?
					 */
					if(includeBackground) {
						abundance += peakModel.getBackgroundAbundance(retentionTime);
					}
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
					 * Set the values.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				peakSeries.add(new Series(xSeries, ySeries, "Peak"));
			}
		}
		return peakSeries;
	}

	public static ISeries convertIdentifiedScans(IChromatogramSelectionCSD chromatogramSelection, IOffset offset, Sign sign) throws NoIdentifiedScansAvailableException {

		IMultipleSeries identifiedScansSeries = convertIdentifiedScans(chromatogramSelection, sign, offset);
		return identifiedScansSeries.getMultipleSeries().get(0);
	}

	public static List<IScanCSD> getIdentifiedScans(IChromatogramSelectionCSD chromatogramSelection, boolean enforceLoadScanProxy) {

		List<IScanCSD> identifiedScans = new ArrayList<IScanCSD>();
		List<IScan> scans = chromatogramSelection.getChromatogram().getScans();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		//
		for(IScan scan : scans) {
			if(scan instanceof IScanCSD) {
				IScanCSD scanCSD = (IScanCSD)scan;
				if(scanCSD.getTargets().size() > 0) {
					int retentionTime = scanCSD.getRetentionTime();
					if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
						identifiedScans.add(scanCSD);
					}
				}
			}
		}
		return identifiedScans;
	}

	private static IMultipleSeries convertIdentifiedScans(IChromatogramSelectionCSD chromatogramSelection, Sign sign, IOffset offset) throws NoIdentifiedScansAvailableException {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries identifiedScanSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			offset = SeriesConverter.validateOffset(offset);
			List<IScanCSD> identifiedScans = getIdentifiedScans(chromatogramSelection, false);
			int amountIdentifiedScans = identifiedScans.size();
			if(amountIdentifiedScans == 0) {
				throw new NoIdentifiedScansAvailableException();
			}
			/*
			 * Get the retention time and max abundance value for each peak.
			 */
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
			for(IScanCSD identifiedScan : identifiedScans) {
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

	// TODO JUnit
	public static ISeries convertPeakBackground(IPeakCSD peak, Sign sign) {

		List<IPeakCSD> peaks = new ArrayList<IPeakCSD>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeakBackground(peaks, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static IMultipleSeries convertPeakBackground(List<IPeakCSD> peaks, Sign sign, IOffset offset) {

		IMultipleSeries peakBackgroundSeries = new MultipleSeries();
		if(peaks != null) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * Convert each peak to a series.
			 */
			for(IPeakCSD peak : peaks) {
				/*
				 * Continue if the actual peak is null.
				 */
				if(peak == null) {
					continue;
				}
				IPeakModelCSD peakModel = peak.getPeakModel();
				/*
				 * Initialize with zero.
				 */
				int size = peakModel.getRetentionTimes().size();
				double[] xSeries = new double[size];
				double[] ySeries = new double[size];
				int x = 0;
				int y = 0;
				/*
				 * Values.
				 */
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Go through all retention times of the peak.
				 */
				for(int retentionTime : peakModel.getRetentionTimes()) {
					abundance = peakModel.getBackgroundAbundance(retentionTime);
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
					 * Set the values.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				peakBackgroundSeries.add(new Series(xSeries, ySeries, "Background"));
			}
		}
		return peakBackgroundSeries;
	}

	// TODO JUnit
	public static ISeries convertIncreasingInflectionPoints(IPeakCSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelCSD peakModel = peak.getPeakModel();
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
	public static ISeries convertDecreasingInflectionPoints(IPeakCSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelCSD peakModel = peak.getPeakModel();
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
	public static ISeries convertPeakPerpendicular(IPeakCSD peak, boolean includeBackground, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelCSD peakModel = peak.getPeakModel();
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
	public static ISeries convertPeakWidthByInflectionPoints(IPeakCSD peak, boolean includeBackground, float height, Sign sign) {

		ISeries series = null;
		if(peak != null) {
			IPeakModelCSD peakModel = peak.getPeakModel();
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
}
