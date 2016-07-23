/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.AbstractViewChromatogramUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.exceptions.NoPeaksAvailableException;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

public class LabeledPeakChromatogramUI extends AbstractViewChromatogramUI {

	private ILineSeries activePeaksSeries;
	private List<String> activePeakLabels;
	private ILineSeries inactivePeaksSeries;
	private List<String> inactivePeakLabels;
	//
	private IOffset offset;
	private TargetExtendedComparator targetComparator;

	public LabeledPeakChromatogramUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesMassScale());
		offset = new Offset(0.0d, 0.0d);
		targetComparator = new TargetExtendedComparator();
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			/*
			 * Get the data
			 */
			List<? extends IPeak> peaks = null;
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				peaks = chromatogramSelectionMSD.getChromatogramMSD().getPeaks(chromatogramSelectionMSD);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
				peaks = chromatogramSelectionCSD.getChromatogramCSD().getPeaks(chromatogramSelectionCSD);
			}
			/*
			 * Chromatogram
			 */
			ISeries seriesChromatogram = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
			addSeries(seriesChromatogram);
			ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, seriesChromatogram.getId());
			lineSeries.setXSeries(seriesChromatogram.getXSeries());
			lineSeries.setYSeries(seriesChromatogram.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(Colors.RED);
			/*
			 * Peaks
			 */
			if(peaks != null) {
				try {
					/*
					 * Active Peaks
					 */
					boolean activeForAnalysis = true;
					activePeakLabels = extractLabels(peaks, activeForAnalysis);
					IMultipleSeries multipleSeries = SeriesConverter.convertPeakMaxMarker(peaks, Sign.POSITIVE, offset, activeForAnalysis);
					ISeries seriesPeak = multipleSeries.getMultipleSeries().get(0);
					activePeaksSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, seriesPeak.getId());
					activePeaksSeries.setXSeries(seriesPeak.getXSeries());
					activePeaksSeries.setYSeries(seriesPeak.getYSeries());
					activePeaksSeries.setLineStyle(LineStyle.NONE);
					activePeaksSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
					activePeaksSeries.setSymbolSize(5);
					activePeaksSeries.setLineColor(Colors.GRAY);
					activePeaksSeries.setSymbolColor(Colors.DARK_GRAY);
				} catch(NoPeaksAvailableException e) {
					/*
					 * Do nothing.
					 * Just don't add the series.
					 */
				}
				/*
				 * Inactive Peaks
				 */
				try {
					boolean activeForAnalysis = false;
					inactivePeakLabels = extractLabels(peaks, activeForAnalysis);
					IMultipleSeries multipleSeries = SeriesConverter.convertPeakMaxMarker(peaks, Sign.POSITIVE, offset, activeForAnalysis);
					ISeries seriesPeak = multipleSeries.getMultipleSeries().get(0);
					inactivePeaksSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, seriesPeak.getId());
					inactivePeaksSeries.setXSeries(seriesPeak.getXSeries());
					inactivePeaksSeries.setYSeries(seriesPeak.getYSeries());
					inactivePeaksSeries.setLineStyle(LineStyle.NONE);
					inactivePeaksSeries.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
					inactivePeaksSeries.setSymbolSize(5);
					inactivePeaksSeries.setLineColor(Colors.GRAY);
					inactivePeaksSeries.setSymbolColor(Colors.GRAY);
				} catch(NoPeaksAvailableException e) {
					/*
					 * Do nothing.
					 * Just don't add the series.
					 */
				}
			}
		}
	}

	@Override
	protected void initialize() {

		super.initialize();
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			public void paintControl(PaintEvent e) {

				paintPeakIdentificationNames(e);
			}

			public boolean drawBehindSeries() {

				return true;
			}
		});
	}

	private List<String> extractLabels(List<? extends IPeak> peaks, boolean activeForAnalysis) {

		List<String> labels = new ArrayList<String>();
		if(peaks != null) {
			for(IPeak peak : peaks) {
				/*
				 * Continue if the actual peak is null.
				 */
				if(peak == null) {
					continue;
				}
				/*
				 * Active for analysis?
				 */
				if(peak.isActiveForAnalysis() == activeForAnalysis) {
					/*
					 * Add an empty string if no identification is set,
					 * cause printing the labels depends on the position
					 * in the list.
					 */
					List<IPeakTarget> peakTargets = peak.getTargets();
					if(peakTargets.size() == 0) {
						labels.add("");
					} else {
						/*
						 * Get retention time and abundance.
						 */
						Collections.sort(peakTargets, targetComparator);
						IPeakTarget peakTarget = peakTargets.get(0);
						String name = peakTarget.getLibraryInformation().getName();
						labels.add(name);
					}
				}
			}
		}
		return labels;
	}

	private void paintPeakIdentificationNames(PaintEvent e) {

		/*
		 * Active peaks
		 */
		if(activePeakLabels != null && activePeaksSeries != null) {
			printLabels(activePeakLabels, activePeaksSeries, e);
		}
		/*
		 * Inactive peaks
		 */
		if(inactivePeakLabels != null && inactivePeaksSeries != null) {
			printLabels(inactivePeakLabels, inactivePeaksSeries, e);
		}
	}

	private void printLabels(List<String> labels, ILineSeries lineSeries, PaintEvent e) {

		for(int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			Point point = lineSeries.getPixelCoordinates(i);
			/*
			 * Draw the label
			 */
			Point labelSize = e.gc.textExtent(label);
			int x = (int)(point.x - labelSize.x / 2d);
			int y = point.y - (point.y / 4);
			e.gc.drawText(label, x, y, true);
		}
	}
}
