/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components;

import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Range;

public abstract class AbstractProfileScanUI extends AbstractLineSeriesUI implements IChromatogramSelectionWSDUpdateNotifier {

	/*
	 * Subclasses may override setViewSeries().
	 */
	private IChromatogramSelectionWSD chromatogramSelection;

	public AbstractProfileScanUI(Composite parent, int style) {
		super(parent, style, new ProfileSpectrumAxisTitles());
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		this.chromatogramSelection = chromatogramSelection;
		/*
		 * If the current view is not a master, reload the data on each
		 * update.<br/> If the ui is in master mode and force reload is set,
		 * load the series.
		 */
		if(!isMaster() || (isMaster() && forceReload)) {
			double maxSignal = this.chromatogramSelection.getSelectedScan().getTotalSignal();
			setMaxSignal(maxSignal);
			setSeries(forceReload);
		}
	}

	public IChromatogramSelectionWSD getChromatogramSelection() {

		return chromatogramSelection;
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

	/**
	 * Sets the secondary ranges.
	 */
	private void setSecondaryRanges() {

		assert (chromatogramSelection != null) : "The chromatogramSelection instance must be not null.";
		double min, max;
		Range range;
		/*
		 * dalton
		 */
		IMultipleSeries multipleLineSeries = getMultipleSeries();
		min = multipleLineSeries.getXMin();
		max = multipleLineSeries.getXMax();
		if(min < max) {
			range = new Range(min, max);
			getXAxisTop().setRange(range);
		}
		/*
		 * Relative Abundance Range
		 */
		min = ChartUtil.getRelativeAbundance(getMaxSignal(), multipleLineSeries.getYMin());
		max = ChartUtil.getRelativeAbundance(getMaxSignal(), multipleLineSeries.getYMax());
		if(min < max) {
			range = new Range(min, max);
			getYAxisRight().setRange(range);
		}
	}

	/**
	 * Sets the chromatogram series.<br/>
	 * Subclasses may override this method to draw specific chromatographic
	 * values.
	 */
	private void setSeries(boolean forceReload) {

		assert (chromatogramSelection != null) : "The chromatogramSelection instance must be not null.";
		/*
		 * Delete the current and set the new series.
		 */
		deleteAllCurrentSeries();
		setViewSeries();
		getAxisSet().adjustRange();
		setSecondaryRanges();
		redraw();
	}
}
