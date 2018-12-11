/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.notifier.IChromatogramSelectionUpdateNotifier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.base.InteractiveChartExtended;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISelectedRangeInfo;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;

/**
 * This class offers a solution to draw chromatographic data like chromatograms,
 * baselines and peaks.<br/>
 * It has four axes: milliseconds and minutes on x, abundance and relative
 * abundance on y.<br/>
 * Childs must only override the method given by {@link ISeriesSetter}.
 * 
 * @author eselmeister
 */
public abstract class AbstractLineSeriesUI extends InteractiveChartExtended implements ISeriesSetter, ISelectedRangeInfo {

	private boolean master = false;
	private IMultipleSeries multipleLineSeries;
	/*
	 * To draw the relative abundance correctly, the maximum total signal of the
	 * chromatogram is needed. It's not sufficient to get only the max signal of
	 * the current series, cause the currents series could be only a small
	 * selection of a chromatogram. If an update is requested,
	 * chromatogramSelection.getChromatogram().getMaxSignal() will be called to
	 * get the value.
	 */
	private double maxSignal = 0.0d;
	/*
	 * The Axes are partly needed by subclasses to determine the selected area
	 * whilst using zoom and adjust menu items.<br/> That's why the millisecond
	 * and abundance axis are marked protected.<br/> See e.g.
	 * EditorChromatogramUI -> handleEvent(Event event).
	 */
	private IAxis xAxisTop; // e.g.: retention time (milliseconds), scans, dalton
	private IAxis xAxisBottom; // e.g.: retention time (minutes), m/z
	private IAxis yAxisLeft; // e.g.: abundance
	private IAxis yAxisRight; // e.g.: relative abundance
	//
	private IAxisTitles axisTitles;
	/*
	 * Sets the minimum y value to zero.
	 */
	private boolean yMinimumToZero = false;
	private double yMaxIntensityAdjusted = 0;
	private boolean autoAdjustIntensity = true; // True by default
	//
	private boolean suppressHairlineDivider = false;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param style
	 */
	public AbstractLineSeriesUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style);
		suspendUpdate(true);
		this.axisTitles = axisTitles;
		initialize();
		suspendUpdate(false);
	}

	/**
	 * USE THIS METHOD ONLY IF NECESSARY!
	 * This method is used to set the retention time range (info page) of the master chromatogram manually.
	 * 
	 * @param chromatogramSelection
	 */
	public void updateSelectionManually(IChromatogramSelection chromatogramSelection) {

		ChartUtil.checkAndSetRange(xAxisTop, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
		redraw();
	}

	@Override
	public Composite getChartPlotArea() {

		return getPlotArea();
	}

	@Override
	public int getSelectedStartRetentionTime() {

		int startRetentionTime = (int)getXAxisTop().getRange().lower;
		return startRetentionTime;
	}

	@Override
	public int getSelectedStopRetentionTime() {

		int stopRetentionTime = (int)getXAxisTop().getRange().upper;
		return stopRetentionTime;
	}

	/**
	 * Returns whether this ui is a master ui or not.<br/>
	 * See setMaster(boolean master)
	 * 
	 * @return boolean
	 */
	public boolean isMaster() {

		return master;
	}

	/**
	 * If true, than this ui will be set to master state.<br/>
	 * In the master state, the whole series will be kept loaded until the
	 * update method from {@link IChromatogramSelectionUpdateNotifier} calls forceReload ==
	 * true.<br/>
	 * When should the master method be used? In example if the chromatogram
	 * should be presented in an editor.<br/>
	 * All changes from the editor will be propagated to the views, which are
	 * not in master mode. So, the views change their chromatographic data,
	 * depending on the selection they get.<br/>
	 * The master still keeps the selection.
	 * 
	 * @param master
	 */
	public void setMaster(boolean master) {

		this.master = master;
	}

	/**
	 * Implement the setSeries method e.g. to add
	 * different series instances, e.g. for the
	 * TIC of a chromatogram and peaks.
	 * 
	 * @param series
	 */
	public void addSeries(ISeries series) {

		if(multipleLineSeries != null) {
			multipleLineSeries.add(series);
		}
	}

	/**
	 * Only use this method to access values.
	 * Use addSeries to store series.
	 * 
	 * @return {@link IMultipleSeries}
	 */
	public IMultipleSeries getMultipleSeries() {

		return multipleLineSeries;
	}

	public IAxis getXAxisTop() {

		return xAxisTop;
	}

	public IAxis getXAxisBottom() {

		return xAxisBottom;
	}

	public IAxis getYAxisLeft() {

		return yAxisLeft;
	}

	public IAxis getYAxisRight() {

		return yAxisRight;
	}

	public void setMaxSignal(double maxSignal) {

		this.maxSignal = maxSignal;
	}

	public double getMaxSignal() {

		return maxSignal;
	}

	public void setYMinimumToZero(boolean yMinimumToZero) {

		this.yMinimumToZero = yMinimumToZero;
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public synchronized void redraw() {

		assert (yAxisLeft != null) : "The abundance instance must be not null.";
		assert (xAxisTop != null) : "The milliseconds instance must be not null.";
		/*
		 * x values
		 */
		double xMin = multipleLineSeries.getXMin();
		double xMax = multipleLineSeries.getXMax();
		/*
		 * y values
		 */
		double yMin;
		if(yMinimumToZero) {
			yMin = 0.0d;
		} else {
			yMin = multipleLineSeries.getYMin();
		}
		//
		double yMax;
		if(autoAdjustIntensity) {
			yMax = multipleLineSeries.getYMax();
		} else {
			yMax = yMaxIntensityAdjusted;
		}
		/*
		 * Check and corrects the ranges and assures that xMin and xMax are not
		 * both 0.
		 */
		if(xMin < xMax && yMin < yMax) {
			ChartUtil.checkAndSetRange(xAxisTop, xMin, xMax);
			ChartUtil.checkAndSetRange(yAxisLeft, yMin, yMax);
			redrawXAxisBottomScale();
			redrawYAxisRightScale();
		}
		//
		setTopAndLeftAxisVisibility();
		super.redraw();
	}

	/**
	 * Redraws the x axis bottom scale.
	 */
	public void redrawXAxisBottomScale() {

		assert (xAxisTop != null) : "The xAxisTop instance must be not null.";
		assert (xAxisBottom != null) : "The xAxisBottom instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set xAxisBottom scale.
		 */
		range = xAxisTop.getRange();
		min = range.lower;
		max = range.upper;
		ChartUtil.setRange(xAxisBottom, min, max);
	}

	/**
	 * Redraws the y axis right scale.
	 */
	public void redrawYAxisRightScale() {

		assert (yAxisLeft != null) : "The yAxisLeft instance must be not null.";
		assert (yAxisRight != null) : "The yAxisRight instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set the yAxisRight scale.
		 */
		range = yAxisLeft.getRange();
		min = ChartUtil.getRelativeAbundance(maxSignal, range.lower);
		max = ChartUtil.getRelativeAbundance(maxSignal, range.upper);
		ChartUtil.setRange(yAxisRight, min, max);
	}

	public void setSuppressHairlineDivider(boolean suppressHairlineDivider) {

		this.suppressHairlineDivider = suppressHairlineDivider;
	}

	/**
	 * Initializes the chart with axes, series, colors.
	 */
	protected void initialize() {

		IPlotArea plotArea = (IPlotArea)getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				if(!suppressHairlineDivider) {
					if(PreferenceSupplier.showChromatogramHairlineDivider()) {
						Color color = e.gc.getForeground();
						int lineStyle = e.gc.getLineStyle();
						int lineWidth = e.gc.getLineWidth();
						/*
						 * Draw a vertical line at half width.
						 */
						e.gc.setForeground(Colors.DARK_RED);
						e.gc.setLineStyle(SWT.LINE_DASHDOT);
						e.gc.setLineWidth(2);
						int x = e.width / 2;
						int y = e.height;
						e.gc.drawLine(x, 0, x, y);
						/*
						 * Set the old gc values.
						 */
						e.gc.setForeground(color);
						e.gc.setLineStyle(lineStyle);
						e.gc.setLineWidth(lineWidth);
					}
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return true;
			}
		});
		/*
		 * Add key and mouse listeners.
		 */
		multipleLineSeries = new MultipleSeries();
		/*
		 * Hide legend and title.
		 */
		getLegend().setVisible(false);
		getTitle().setVisible(false);
		//
		createPrimaryAxes();
		createSecondaryAxes();
		/*
		 * red, white and black
		 */
		setAxesAndBackgroundColors();
		getAxisSet().adjustRange();
	}

	/**
	 * Deletes all series.
	 */
	protected void deleteAllCurrentSeries() {

		multipleLineSeries.removeAll();
		org.eclipse.swtchart.ISeries[] series = getSeriesSet().getSeries();
		List<String> ids = new ArrayList<String>();
		/*
		 * Get the ids.
		 */
		for(org.eclipse.swtchart.ISeries serie : series) {
			ids.add(serie.getId());
		}
		/*
		 * Remove all ids.
		 */
		for(String id : ids) {
			getSeriesSet().deleteSeries(id);
		}
	}

	protected void setYMaxIntensityAdjusted(double yMaxIntensityAdjusted) {

		this.yMaxIntensityAdjusted = yMaxIntensityAdjusted;
	}

	protected void setAutoAdjustIntensity(boolean autoAdjustIntensity) {

		this.autoAdjustIntensity = autoAdjustIntensity;
	}

	/**
	 * Creates the primary axes abundance and milliseconds.
	 */
	private void createPrimaryAxes() {

		/*
		 * Main Axes.
		 */
		IAxisSet axisSet = getAxisSet();
		/*
		 * e.g. Intensity, Abundance, µV
		 */
		yAxisLeft = axisSet.getYAxis(0);
		yAxisLeft.getTitle().setText(axisTitles.getYAxisLeftTitle());
		yAxisLeft.setPosition(Position.Primary);
		yAxisLeft.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0#E0"));
		/*
		 * e.g. Milliseconds
		 */
		xAxisTop = axisSet.getXAxis(0);
		xAxisTop.getTitle().setText(axisTitles.getXAxisTopTitle());
		xAxisTop.setPosition(Position.Secondary);
	}

	/**
	 * Creates the secondary axes minutes and relative abundance.<br/>
	 * The primary axis must be created first and setSeries has to be called at
	 * least once.
	 */
	private void createSecondaryAxes() {

		IAxisSet axisSet = getAxisSet();
		/*
		 * e.g. Minute axis
		 */
		int axisIdMinutes = axisSet.createXAxis();
		xAxisBottom = axisSet.getXAxis(axisIdMinutes);
		xAxisBottom.getTitle().setText(axisTitles.getXAxisBottomTitle());
		xAxisBottom.setPosition(Position.Primary);
		xAxisBottom.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
		/*
		 * e.g. Relative abundance axis
		 */
		int axisIdAbundanceRelative = axisSet.createYAxis();
		yAxisRight = axisSet.getYAxis(axisIdAbundanceRelative);
		yAxisRight.getTitle().setText(axisTitles.getYAxisRightTitle());
		yAxisRight.setPosition(Position.Secondary);
		yAxisRight.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
	}

	/**
	 * Sets the axes and background colors.
	 */
	private void setAxesAndBackgroundColors() {

		assert (yAxisLeft != null) : "The abundance instance must be not null.";
		assert (xAxisTop != null) : "The milliseconds instance must be not null.";
		assert (xAxisBottom != null) : "The minutes instance must be not null.";
		assert (yAxisRight != null) : "The relativeAbundance instance must be not null.";
		/*
		 * Color background, axes and line series
		 */
		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		ChartUtil.setAxisColor(yAxisLeft, Colors.BLACK);
		ChartUtil.setAxisColor(xAxisTop, Colors.BLACK);
		ChartUtil.setAxisColor(xAxisBottom, Colors.BLACK);
		ChartUtil.setAxisColor(yAxisRight, Colors.BLACK);
	}

	private void setTopAndLeftAxisVisibility() {

		assert (xAxisTop != null) : "The milliseconds instance must be not null.";
		assert (yAxisRight != null) : "The relativeAbundance instance must be not null.";
		/*
		 * The title will be used white space
		 * if the top axis is not visible.
		 */
		boolean showMilliseconds = PreferenceSupplier.isShowMilliseconds();
		boolean showRelativeIntensity = PreferenceSupplier.isShowRelativeIntensity();
		//
		getTitle().setForeground(getBackground());
		if(showMilliseconds) {
			getTitle().setText("");
			getTitle().setVisible(false);
		} else {
			getTitle().setText("Series"); // White space is not recognized.
			getTitle().setVisible(true);
		}
		//
		xAxisTop.getTitle().setVisible(showMilliseconds);
		xAxisTop.getTick().setVisible(showMilliseconds);
		yAxisRight.getTitle().setVisible(showRelativeIntensity);
		yAxisRight.getTick().setVisible(showRelativeIntensity);
	}
}
