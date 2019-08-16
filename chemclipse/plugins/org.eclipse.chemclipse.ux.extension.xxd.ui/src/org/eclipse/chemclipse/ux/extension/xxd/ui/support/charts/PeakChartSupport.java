/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class PeakChartSupport {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0");

	public ILineSeriesData getPeaks(List<? extends IPeak> peaks, boolean includeBackground, boolean mirrored, Color color, String id) {

		ISeriesData seriesData = getPeakSeriesData(peaks, includeBackground, mirrored, id);
		return getLineSeriesData(seriesData, color, true);
	}

	public ILineSeriesData getPeak(IPeak peak, boolean includeBackground, boolean mirrored, Color color, String id) {

		ISeriesData seriesData = getPeakSeriesData(peak, includeBackground, mirrored, id);
		return getLineSeriesData(seriesData, color, true);
	}

	public ILineSeriesData getIncreasingTangent(IPeak peak, boolean includeBackground, boolean mirrored, Color color, String postfix) {

		ISeriesData seriesData = getIncreasingInflectionData(peak, includeBackground, mirrored, postfix);
		return getLineSeriesData(seriesData, color, false);
	}

	public ILineSeriesData getDecreasingTangent(IPeak peak, boolean includeBackground, boolean mirrored, Color color, String postfix) {

		ISeriesData seriesData = getDecreasingInflectionData(peak, includeBackground, mirrored, postfix);
		return getLineSeriesData(seriesData, color, false);
	}

	public ILineSeriesData getPeakPerpendicular(IPeak peak, boolean includeBackground, boolean mirrored, Color color, String postfix) {

		ISeriesData seriesData = getPeakPerpendicularData(peak, includeBackground, mirrored, postfix);
		return getLineSeriesData(seriesData, color, false);
	}

	public ILineSeriesData getPeakWidth(IPeak peak, boolean includeBackground, float height, boolean mirrored, Color color, String postfix) {

		ISeriesData seriesData = getPeakWidthByInflectionData(peak, includeBackground, height, mirrored, postfix);
		return getLineSeriesData(seriesData, color, false);
	}

	public ILineSeriesData getPeakBaseline(IPeak peak, boolean mirrored, Color color, String id) {

		ISeriesData seriesData = getPeakBaselineData(peak, mirrored, id);
		return getLineSeriesData(seriesData, color, false);
	}

	public ILineSeriesData getPeakBackground(IPeak peak, boolean mirrored, Color color, String id) {

		ISeriesData seriesData = getPeakBaselineData(peak, mirrored, id);
		return getLineSeriesData(seriesData, color, true);
	}

	private ILineSeriesData getLineSeriesData(ISeriesData seriesData, Color color, boolean enableArea) {

		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setEnableArea(enableArea);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ISeriesData getPeakSeriesData(List<? extends IPeak> peaks, boolean includeBackground, boolean mirrored, String id) {

		int size = peaks.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		int index = 0;
		for(IPeak peak : peaks) {
			IPeakModel peakModel = peak.getPeakModel();
			int retentionTime = peakModel.getRetentionTimeAtPeakMaximum();
			xSeries[index] = retentionTime;
			if(includeBackground) {
				ySeries[index] = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getPeakAbundance(retentionTime);
			} else {
				ySeries[index] = peakModel.getPeakAbundance(retentionTime);
			}
			//
			if(mirrored) {
				ySeries[index] = ySeries[index] * -1;
			}
			//
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakSeriesData(IPeak peak, boolean includeBackground, boolean mirrored, String id) {

		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int retentionTime : retentionTimes) {
			//
			xSeries[index] = retentionTime;
			if(includeBackground) {
				ySeries[index] = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getPeakAbundance(retentionTime);
			} else {
				ySeries[index] = peakModel.getPeakAbundance(retentionTime);
			}
			//
			if(mirrored) {
				ySeries[index] = ySeries[index] * -1;
			}
			//
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getIncreasingInflectionData(IPeak peak, boolean includeBackground, boolean mirrored, String postfix) {

		String id = "Increasing Tangent" + postfix;
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			try {
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				/*
				 * Where does the increasing tangent crosses the baseline.
				 */
				IPoint intersection = Equations.calculateIntersection(increasing, baseline);
				double x;
				/*
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[0] = x;
				//
				if(includeBackground) {
					ySeries[0] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[0] = intersection.getY();
				}
				//
				if(mirrored) {
					ySeries[0] = ySeries[0] * -1;
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
				//
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
				//
				if(mirrored) {
					ySeries[1] = ySeries[1] * -1;
				}
			} catch(SolverException e) {
				//
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getDecreasingInflectionData(IPeak peak, boolean includeBackground, boolean mirrored, String postfix) {

		String id = "Decreasing Tangent" + postfix;
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
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
				//
				//
				if(mirrored) {
					ySeries[0] = ySeries[0] * -1;
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
				//
				//
				if(mirrored) {
					ySeries[1] = ySeries[1] * -1;
				}
			} catch(SolverException e) {
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakPerpendicularData(IPeak peak, boolean includeBackground, boolean mirrored, String postfix) {

		String id = "Peak Perpendicular" + postfix;
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			xSeries[0] = peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints();
			if(includeBackground) {
				ySeries[0] = peakModel.getBackgroundAbundance(peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints());
			} else {
				ySeries[0] = 0.0d;
			}
			//
			if(mirrored) {
				ySeries[0] = ySeries[0] * -1;
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
				//
				if(mirrored) {
					ySeries[1] = ySeries[1] * -1;
				}
			} catch(SolverException e) {
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakWidthByInflectionData(IPeak peak, boolean includeBackground, float height, boolean mirrored, String postfix) {

		String id = "Peak Width" + postfix;
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
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
					//
					if(mirrored) {
						ySeries[0] = ySeries[0] * -1;
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
					//
					if(mirrored) {
						ySeries[1] = ySeries[1] * -1;
					}
				} catch(SolverException e) {
				}
			}
			id = "Peak" + postfix + " Width at " + decimalFormat.format(height * 100) + "%";
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakBaselineData(IPeak peak, boolean mirrored, String id) {

		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		int index = 0;
		for(int retentionTime : peakModel.getRetentionTimes()) {
			xSeries[index] = retentionTime;
			ySeries[index] = peakModel.getBackgroundAbundance(retentionTime);
			//
			if(mirrored) {
				ySeries[index] = ySeries[index] * -1;
			}
			//
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
