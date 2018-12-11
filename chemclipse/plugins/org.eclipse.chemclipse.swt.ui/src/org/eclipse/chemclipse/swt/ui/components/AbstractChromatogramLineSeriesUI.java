/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components;

import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.notifier.IChromatogramSelectionUpdateNotifier;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ChromatogramRange;
import org.eclipse.chemclipse.swt.ui.series.IChromatogramRange;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Range;

/**
 * This class offers a solution to draw chromatographic data like chromatograms,
 * baselines and peaks.<br/>
 * It has four axes: milliseconds and minutes on x, abundance and relative
 * abundance on y.<br/>
 * Childs must only override the method given by {@link ISeriesSetter}.
 *
 */
public abstract class AbstractChromatogramLineSeriesUI extends AbstractLineSeriesUI implements IChromatogramSelectionUpdateNotifier {

	/*
	 * Subclasses may override setViewSeries().
	 */
	private double yMaxIntensityAdjusted = 0;
	private IChromatogramSelection chromatogramSelection = null;
	private IChromatogramRange previousChromatogramRange = new ChromatogramRange();
	private List<IChromatogramSelection> chromatogramSelections;
	/*
	 * These will be set if mouse clicks will be performed.
	 * Notice: The adjustChromatogramSelection() method is very important to set the right scale in
	 * case the class is an editor.
	 */
	private int xStart, yStart;

	public AbstractChromatogramLineSeriesUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
	}

	public void update(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			updateSelection(chromatogramSelection, true);
		}
	}

	@Override
	public void updateSelection(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		this.chromatogramSelection = chromatogramSelection;
		yMaxIntensityAdjusted = this.chromatogramSelection.getStopAbundance();
		setYMaxIntensityAdjusted(yMaxIntensityAdjusted);
		/*
		 * If the current view is not a master, reload the data on each
		 * update.<br/> If the ui is in master mode and force reload is set,
		 * load the series.
		 */
		if(!isMaster() || (isMaster() && forceReload)) {
			/*
			 * Get the max signal.
			 */
			double maxSignal = this.chromatogramSelection.getChromatogram().getMaxSignal();
			setMaxSignal(maxSignal);
			/*
			 * Scale to the intensity of the master selection.
			 */
			if(!isMaster()) {
				if(PreferenceSupplier.autoAdjustViewIntensityDisplay()) {
					/*
					 * Auto-adjust
					 */
					enableCompress(true);
					setAutoAdjustIntensity(true);
				} else {
					/*
					 * Keep height
					 */
					enableCompress(false);
					setAutoAdjustIntensity(false);
				}
			}
			/*
			 * Set the series.
			 */
			setSeries(forceReload);
			/*
			 * Adjust the y values.
			 */
			if(!isMaster()) {
				if(!PreferenceSupplier.autoAdjustViewIntensityDisplay()) {
					Range range = getYAxisLeft().getRange();
					range.upper = yMaxIntensityAdjusted;
					getYAxisLeft().setRange(range);
					setSecondaryRanges();
				}
			}
		}
	}

	@Override
	public void updateSelection(List<IChromatogramSelection> chromatogramSelections, boolean forceReload) {

		/*
		 * The list must be not null and must contain at least one entry.
		 * Set the first chromatogram selection as the master selection (chromatogramSelections.get(0)).
		 */
		if(chromatogramSelections != null && chromatogramSelections.size() >= 1) {
			this.chromatogramSelections = chromatogramSelections;
			updateSelection(chromatogramSelections.get(0), forceReload);
		}
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	public List<IChromatogramSelection> getChromatogramSelections() {

		return chromatogramSelections;
	}

	@Override
	public void mouseScrolled(MouseEvent e) {

		super.mouseScrolled(e);
		adjustChromatogramSelection();
	}

	@Override
	public void keyPressed(KeyEvent e) {

		super.keyPressed(e);
		int keyCode = e.keyCode;
		if(keyCode == SWT.ARROW_LEFT || keyCode == SWT.ARROW_DOWN || keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_RIGHT) {
			adjustChromatogramSelection();
		}
	}

	@Override
	public void redrawXAxisBottomScale() {

		double min, max;
		Range range;
		/*
		 * Set minutes scale.
		 */
		range = getXAxisTop().getRange();
		min = range.lower / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = range.upper / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		ChartUtil.setRange(getXAxisBottom(), min, max);
	}

	// ------------------------------------------Events
	@Override
	public void mouseDown(MouseEvent e) {

		super.mouseDown(e);
		xStart = e.x;
		yStart = e.y;
	}

	@Override
	public void mouseUp(MouseEvent e) {

		super.mouseUp(e);
		/*
		 * Perform an update only if the current composite is in master
		 * mode.<br/> Do the update only if the left mouse button (e.button ==
		 * 1) was pressed.<br/> <br/> If the implementing class is a peak, and
		 * not a chromatogram, the corresponding chromatogram mass spectrum will
		 * be shown.<br/> Otherwise, the peak could override this method.
		 */
		if(isMaster() && e.button == 1) {
			/*
			 * The start and stop coordinates will be different, if the user
			 * selects an area.<br/> In such a case, update the chromatogram
			 * selection.<br/> If the user performs a single or double click,
			 * the start and stop position will be the same.<br/> Don't waste
			 * processor time and throw an update in such a case.
			 */
			if(xStart != e.x || yStart != e.y) {
				/*
				 * Set the actual selection values.
				 */
				adjustChromatogramSelection();
			}
		}
	}

	@Override
	public void adjustRange() {

		if(isMaster()) {
			if(chromatogramSelection != null) {
				/*
				 * 1:1
				 */
				previousChromatogramRange.reset();
				chromatogramSelection.reset();
				chromatogramSelection.update(true);
			}
		} else {
			super.adjustRange();
		}
	}

	@Override
	public void adjustXRange() {

		if(isMaster()) {
			if(chromatogramSelection != null) {
				/*
				 * Retention Time
				 */
				@SuppressWarnings("rawtypes")
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int startRetentionTime = chromatogram.getStartRetentionTime();
				int stopRetentionTime = chromatogram.getStopRetentionTime();
				previousChromatogramRange.setStartRetentionTime(startRetentionTime);
				previousChromatogramRange.setStopRetentionTime(stopRetentionTime);
				//
				chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
				chromatogramSelection.update(true);
			}
		} else {
			super.adjustXRange();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void adjustYRange() {

		if(isMaster()) {
			if(chromatogramSelection != null) {
				/*
				 * Abundance
				 */
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				float minSignal = chromatogram.getMinSignal();
				float maxSignal = chromatogram.getMaxSignal();
				previousChromatogramRange.setStartAbundance(minSignal);
				previousChromatogramRange.setStopAbundance(maxSignal);
				//
				chromatogramSelection.setRanges(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime(), minSignal, maxSignal);
				chromatogramSelection.update(true);
			}
		} else {
			super.adjustYRange();
		}
	}

	@Override
	public void adjustPreviousRange() {

		if(isMaster()) {
			if(chromatogramSelection != null && previousChromatogramRange.isValid()) {
				/*
				 * Retention Time, Abundance
				 */
				int startRetentionTime = previousChromatogramRange.getStartRetentionTime();
				int stopRetentionTime = previousChromatogramRange.getStopRetentionTime();
				float startAbundance = previousChromatogramRange.getStartAbundance();
				float stopAbundance = previousChromatogramRange.getStopAbundance();
				//
				chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				chromatogramSelection.update(true);
			}
		} else {
			super.adjustYRange();
		}
	}

	/**
	 * Adjust the chromatogram values to the given selection by the Axes
	 * milliseconds and abundance.
	 */
	protected void adjustChromatogramSelection() {

		if(isMaster() && chromatogramSelection != null) {
			/*
			 * Keep the previous selection.
			 */
			previousChromatogramRange.setStartRetentionTime(chromatogramSelection.getStartRetentionTime());
			previousChromatogramRange.setStopRetentionTime(chromatogramSelection.getStopRetentionTime());
			previousChromatogramRange.setStartAbundance(chromatogramSelection.getStartAbundance());
			previousChromatogramRange.setStopAbundance(chromatogramSelection.getStopAbundance());
			/*
			 * Set the actual selection values.
			 */
			int startRetentionTime = (int)getXAxisTop().getRange().lower;
			int stopRetentionTime = (int)getXAxisTop().getRange().upper;
			float startAbundance = (float)getYAxisLeft().getRange().lower;
			float stopAbundance;
			if(PreferenceSupplier.autoAdjustEditorIntensityDisplay()) {
				stopAbundance = (float)getMultipleSeries().getYMax();
			} else {
				stopAbundance = (float)getYAxisLeft().getRange().upper;
			}
			/*
			 * Update the range instead of using reset, cause the selected
			 * scans, peaks, and ions shall not be overridden by
			 * default values. An update will be fired by e.g. EditorChromatogramUI if
			 * the ui is in master mode.
			 */
			chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
		}
	}

	protected int getSelectedRetentionTimeAsMilliseconds(int x) {

		int retentionTimeInMilliseconds;
		int startRetentionTime = (int)getXAxisTop().getRange().lower;
		int stopRetentionTime = (int)getXAxisTop().getRange().upper;
		int width = getPlotArea().getClientArea().width;
		/*
		 * If x <= 0, then it is the start retention time.
		 * If x > width, the it is the stop retention time.
		 */
		if(x <= 0) {
			retentionTimeInMilliseconds = startRetentionTime;
		} else if(x > width) {
			retentionTimeInMilliseconds = stopRetentionTime;
		} else {
			int delta = stopRetentionTime - startRetentionTime;
			double percentage = ((100.0d / width) * x) / 100.0d;
			retentionTimeInMilliseconds = (int)(startRetentionTime + delta * percentage);
		}
		return retentionTimeInMilliseconds;
	}

	protected double getSelectedAbundance(int y) {

		double abundanceValue;
		float startAbundance = (float)getYAxisLeft().getRange().lower;
		float stopAbundance = (float)getYAxisLeft().getRange().upper;
		int height = getPlotArea().getClientArea().height;
		/*
		 * The plot area counts from the left top corner.
		 * if y <= 0, abundance is the stop abundance
		 * if y > height, abundance is the start abundance
		 */
		if(y <= 0) {
			abundanceValue = stopAbundance;
		} else if(y > height) {
			abundanceValue = startAbundance;
		} else {
			float delta = stopAbundance - startAbundance;
			double percentage = (100.0d - ((100.0d / height) * y)) / 100.0d;
			abundanceValue = startAbundance + delta * percentage;
		}
		return abundanceValue;
	}

	// ---------------------------------------------------------------------------------------------------------------Editor
	/**
	 * Slides when the left or right arrow is pressed and if the class
	 * overrides the fireUpdateChange method.
	 * 
	 * @param keyCode
	 */
	protected void handleArrowMoveWindowSelection(int keyCode) {

		if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT) {
			/*
			 * Left, Right
			 * (Retention Time)
			 */
			if(keyCode == SWT.ARROW_RIGHT) {
				MoveDirection moveDirection = (PreferenceSupplier.useAlternateWindowMoveDirection()) ? MoveDirection.LEFT : MoveDirection.RIGHT;
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
			} else {
				MoveDirection moveDirection = (PreferenceSupplier.useAlternateWindowMoveDirection()) ? MoveDirection.RIGHT : MoveDirection.LEFT;
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
			}
			//
			chromatogramSelection.update(true);
			//
		} else if(keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN) {
			/*
			 * Up, Down
			 * (Abundance)
			 * Doesn't work if auto adjust signals is enabled.
			 */
			float stopAbundance = chromatogramSelection.getStopAbundance();
			float newStopAbundance;
			if(PreferenceSupplier.useAlternateWindowMoveDirection()) {
				newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance - stopAbundance / 20.0f : stopAbundance + stopAbundance / 20.0f;
			} else {
				newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance + stopAbundance / 20.0f : stopAbundance - stopAbundance / 20.0f;
			}
			chromatogramSelection.setRanges(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime(), chromatogramSelection.getStartAbundance(), newStopAbundance);
			chromatogramSelection.update(true);
		}
	}

	/**
	 * Slides the chromatogram selection if the selected scan is outside the visible Window and if the class
	 * overrides the fireUpdateChange method.
	 * 
	 * @param keyCode
	 */
	protected void handleControlScanSelection(int keyCode) {

		/*
		 * Select the next or previous scan.
		 */
		int scanNumber = chromatogramSelection.getSelectedScan().getScanNumber();
		if(keyCode == SWT.ARROW_RIGHT) {
			scanNumber++;
		} else {
			scanNumber--;
		}
		/*
		 * Set and fire an update.
		 */
		IScan selectedScan = chromatogramSelection.getChromatogram().getScan(scanNumber);
		//
		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, selectedScan);
		//
		if(selectedScan != null) {
			/*
			 * The selection should slide with the selected scans.
			 */
			int scanRetentionTime = selectedScan.getRetentionTime();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			/*
			 * Left or right slide on demand.
			 */
			if(scanRetentionTime <= startRetentionTime) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
			} else if(scanRetentionTime >= stopRetentionTime) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
			}
			//
			chromatogramSelection.setSelectedScan(selectedScan, false);
			chromatogramSelection.update(true);
		}
	}

	/**
	 * Handles the mouse wheel selection and if the class
	 * overrides the fireUpdateChange method.
	 * 
	 * @param e
	 */
	protected void handleMouseWheelSelection(MouseEvent e) {

		/*
		 * Switch between up and down wheel selection.
		 */
		int direction = (e.count < 0) ? SWT.UP : SWT.DOWN;
		//
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		int retentionTimeMoveWindow = (stopRetentionTime - startRetentionTime) / 20;
		/*
		 * Zoom in / zoom out
		 */
		int startRetentionTimeNew = (direction == SWT.DOWN) ? startRetentionTime + retentionTimeMoveWindow : startRetentionTime - retentionTimeMoveWindow;
		int stopRetentionTimeNew = (direction == SWT.DOWN) ? stopRetentionTime - retentionTimeMoveWindow : stopRetentionTime + retentionTimeMoveWindow;
		//
		startRetentionTimeNew = ChromatogramSelectionSupport.getValidatedStartRetentionTime(chromatogramSelection, startRetentionTimeNew);
		stopRetentionTimeNew = ChromatogramSelectionSupport.getValidatedStopRetentionTime(chromatogramSelection, stopRetentionTimeNew);
		chromatogramSelection.setRanges(startRetentionTimeNew, stopRetentionTimeNew, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		chromatogramSelection.update(true);
	}

	/**
	 * Sets the selected scan.
	 * 
	 * @param x
	 */
	protected void handleScanSelection(int x) {

		/*
		 * Get the scan number
		 */
		int retentionTime = getRetentionTime(x);
		int scan = chromatogramSelection.getChromatogram().getScanNumber(retentionTime);
		IScan selectedScan = chromatogramSelection.getChromatogram().getScan(scan);
		chromatogramSelection.setSelectedScan(selectedScan, false);
		chromatogramSelection.update(true);
		//
		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, selectedScan);
	}

	/**
	 * Returns the retention time at the given position x.
	 * 
	 * @param x
	 * @return
	 */
	protected int getRetentionTime(int x) {

		Rectangle rect = getPlotArea().getClientArea();
		double lower = getAxisSet().getXAxis(0).getRange().lower;
		double upper = getAxisSet().getXAxis(0).getRange().upper;
		double delta = upper - lower + 1;
		double part = delta / rect.width * x;
		int retentionTime = (int)(lower + part);
		//
		return retentionTime;
	}

	// ---------------------------------------------------------------------------------------------------------------Editor
	// ------------------------------------------MouseListener
	/**
	 * Sets the secondary ranges.
	 */
	private void setSecondaryRanges() {

		assert (chromatogramSelection != null) : "The chromatogramSelection instance must be not null.";
		assert (getXAxisBottom() != null) : "The minutes instance must be not null.";
		assert (getYAxisRight() != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Minutes
		 */
		min = chromatogramSelection.getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = chromatogramSelection.getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		range = new Range(min, max);
		getXAxisBottom().setRange(range);
		/*
		 * Relative Abundance Range
		 */
		min = ChartUtil.getRelativeAbundance(getMaxSignal(), chromatogramSelection.getStartAbundance());
		max = ChartUtil.getRelativeAbundance(getMaxSignal(), chromatogramSelection.getStopAbundance());
		range = new Range(min, max);
		getYAxisRight().setRange(range);
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
		if(isMaster() && forceReload) {
			/*
			 * Adjust the intensity?
			 * Note: enableCompress is important, otherwise areas
			 * with big intensities will be not displayed mistakingly
			 */
			if(PreferenceSupplier.autoAdjustEditorIntensityDisplay()) {
				/*
				 * Auto-adjust
				 */
				enableCompress(true);
				setAutoAdjustIntensity(true);
			} else {
				/*
				 * Keep height
				 */
				enableCompress(false);
				Range range = getYAxisLeft().getRange();
				range.upper = yMaxIntensityAdjusted;
				getYAxisLeft().setRange(range);
				setAutoAdjustIntensity(false);
			}
		}
		setSecondaryRanges();
		redraw();
	}
}
