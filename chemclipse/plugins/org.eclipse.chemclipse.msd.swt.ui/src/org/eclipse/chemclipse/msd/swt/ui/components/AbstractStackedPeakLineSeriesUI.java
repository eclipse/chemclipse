/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import java.util.List;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Range;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.notifier.IStackedPeakSelectionUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;

/**
 * This class offers a solution to draw chromatographic data like chromatograms,
 * baselines and peaks.<br/>
 * It has four axes: milliseconds and minutes on x, abundance and relative
 * abundance on y.<br/>
 * Childs must only override the method given by {@link ISeriesSetter}.
 * 
 * @author eselmeister
 */
public abstract class AbstractStackedPeakLineSeriesUI extends AbstractLineSeriesUI implements ISeriesSetter, KeyListener, MouseListener, IStackedPeakSelectionUpdateNotifier {

	protected List<IPeakMSD> peakListMSD;

	public AbstractStackedPeakLineSeriesUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
	}

	@Override
	public void update(List<IPeakMSD> peakListMSD, boolean forceReload) {

		this.peakListMSD = peakListMSD;
		if(!isMaster() || (isMaster() && forceReload)) {
			double maxSignal = calculateMaxSignal();
			setMaxSignal(maxSignal);
			setSeries(forceReload);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {

	}

	private void setSeries(boolean forceReload) {

		/*
		 * Delete the current and set the new series.
		 */
		deleteAllCurrentSeries();
		setViewSeries();
		getAxisSet().adjustRange();
		setSecondaryRanges();
		redraw();
	}

	/**
	 * Sets the secondary ranges.
	 */
	private void setSecondaryRanges() {

		assert (getXAxisBottom() != null) : "The minutes instance must be not null.";
		assert (getYAxisRight() != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		/*
		 * Minutes
		 */
		Range range = calculateMinAndMaxRetentionTime();
		getXAxisBottom().setRange(range);
		/*
		 * Relative Abundance Range
		 */
		min = ChartUtil.getRelativeAbundance(getMaxSignal(), 0);
		max = ChartUtil.getRelativeAbundance(getMaxSignal(), calculateMaxSignal());
		range = new Range(min, max);
		getYAxisRight().setRange(range);
	}

	@Override
	public void redrawXAxisBottomScale() {

		assert (getXAxisTop() != null) : "The xAxisTop instance must be not null.";
		assert (getXAxisBottom() != null) : "The xAxisBottom instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set xAxisBottom scale.
		 */
		range = getXAxisTop().getRange();
		min = range.lower / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = range.upper / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		ChartUtil.setRange(getXAxisBottom(), min, max);
	}

	/**
	 * Calculates the max signal from the peak list.
	 * 
	 * @return double
	 */
	private double calculateMaxSignal() {

		double maxSignal = 0;
		for(IPeakMSD peak : peakListMSD) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			double peakSignal = peakModel.getPeakAbundance() + peakModel.getBackgroundAbundance();
			if(peakSignal > maxSignal) {
				maxSignal = peakSignal;
			}
		}
		return maxSignal;
	}

	/**
	 * Calculates the min / max retention time from the peak list.
	 * 
	 * @return double
	 */
	private Range calculateMinAndMaxRetentionTime() {

		int minRetentionTime = Integer.MAX_VALUE;
		int maxRetentionTime = Integer.MIN_VALUE;
		for(IPeakMSD peak : peakListMSD) {
			IPeakModelMSD peakModel = peak.getPeakModel();
			int startRetentionTime = peakModel.getStartRetentionTime();
			int stopRetentionTime = peakModel.getStopRetentionTime();
			//
			if(startRetentionTime < minRetentionTime) {
				minRetentionTime = startRetentionTime;
			}
			//
			if(stopRetentionTime > maxRetentionTime) {
				maxRetentionTime = stopRetentionTime;
			}
		}
		//
		double minRetentionTimeMinutes = minRetentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		double maxRetentionTimeMinutes = maxRetentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		return new Range(minRetentionTimeMinutes, maxRetentionTimeMinutes);
	}
}
