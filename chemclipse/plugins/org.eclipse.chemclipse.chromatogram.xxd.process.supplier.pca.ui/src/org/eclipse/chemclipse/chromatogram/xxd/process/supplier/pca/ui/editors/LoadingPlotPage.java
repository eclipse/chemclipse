/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.thirdpartylibraries.swtchart.ext.InteractiveChartExtended;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

public class LoadingPlotPage {

	private List<double[]> basisVectors = new ArrayList<>();
	private Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private Color COLOR_RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private boolean drawLables = true;
	private List<String> extractedRetentionTimes = new ArrayList<>();
	private InteractiveChartExtended loadingPlotChart;
	private NumberFormat nf = NumberFormat.getInstance(Locale.US);
	//
	private PcaEditor pcaEditor;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;
	//
	private int SYMBOL_SIZE = 8;
	private Table table;

	public LoadingPlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	public void clien() {

		basisVectors.clear();
		extractedRetentionTimes.clear();
		table.clearAll();
		table.removeAll();
		ISeriesSet seriesSet = loadingPlotChart.getSeriesSet();
		ISeries[] series = seriesSet.getSeries();
		for(ISeries serie : series) {
			seriesSet.deleteSeries(serie.getId());
		}
		loadingPlotChart.redraw();
		loadingPlotChart.update();
		table.redraw();
	}

	private void createLoadingPlot(Composite parent) {

		loadingPlotChart = new InteractiveChartExtended(parent, SWT.NONE);
		loadingPlotChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadingPlotChart.getTitle().setText("PCA Loading Plot");
		loadingPlotChart.getTitle().setForeground(COLOR_BLACK);
		//
		loadingPlotChart.setBackground(COLOR_WHITE);
		loadingPlotChart.getLegend().setVisible(false);
		//
		loadingPlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + spinnerPCx.getSelection());
		loadingPlotChart.getAxisSet().getXAxis(0).getTitle().setForeground(COLOR_BLACK);
		loadingPlotChart.getAxisSet().getXAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		loadingPlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + spinnerPCy.getSelection());
		loadingPlotChart.getAxisSet().getYAxis(0).getTitle().setForeground(COLOR_BLACK);
		loadingPlotChart.getAxisSet().getYAxis(0).getTick().setForeground(COLOR_BLACK);
		//
		IPlotArea plotArea = (IPlotArea)loadingPlotChart.getPlotArea();
		/*
		 * Plot a marker at zero.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public boolean drawBehindSeries() {

				return false;
			}

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = loadingPlotChart.getAxisSet().getXAxes()[0].getRange();
				Range yRange = loadingPlotChart.getAxisSet().getYAxes()[0].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = loadingPlotChart.getPlotArea().getClientArea();
					int width = rectangle.width;
					int height = rectangle.height;
					int xWidth;
					int yHeight;
					/*
					 * Dependent where the zero values are.
					 * xDelta and yDelta can't be zero -> protect from division by zero.
					 */
					double xDelta = xRange.upper - xRange.lower;
					double yDelta = yRange.upper - yRange.lower;
					double xDiff = xRange.lower * -1; // lower is negative
					double yDiff = yRange.upper;
					double xPart = ((100 / xDelta) * xDiff) / 100; // percent -> 0.0 - 1.0
					double yPart = ((100 / yDelta) * yDiff) / 100; // percent -> 0.0 - 1.0
					xWidth = (int)(width * xPart);
					yHeight = (int)(height * yPart);
					/*
					 * Draw the line.
					 */
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawLine(xWidth, 0, xWidth, height); // Vertical line through zero
					e.gc.drawLine(0, yHeight, width, yHeight); // Horizontal line through zero
				}
			}
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public boolean drawBehindSeries() {

				return false;
			}

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = loadingPlotChart.getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					if(serie.isVisible() && drawLables) {
						String label = serie.getId();
						Point point = serie.getPixelCoordinates(0);
						/*
						 * Draw the label
						 */
						Point labelSize = e.gc.textExtent(label);
						e.gc.setForeground(COLOR_BLACK);
						e.gc.drawText(label, (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
					}
				}
			}
		});
	}

	private void createTable(Composite parent) {

		table = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column = new TableColumn(table, SWT.None);
		column.setText("Ret.Time(min)");
		column.setWidth(150);
		table.addListener(SWT.Selection, e -> {
			TableItem[] selection = table.getSelection();
			Set<String> selectedItems = new HashSet<>();
			for(int i = 0; i < selection.length; i++) {
				selectedItems.add((String)selection[i].getData());
			}
			updateSelection(selectedItems);
		});
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Loading Plot");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(7, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label;
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText("PC X-Axis: ");
		spinnerPCx = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCx.setMinimum(1);
		spinnerPCx.setMaximum(1);
		spinnerPCx.setIncrement(1);
		spinnerPCx.setLayoutData(gridData);
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText(" PC Y-Axis: ");
		spinnerPCy = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCy.setMinimum(1);
		spinnerPCy.setMaximum(1);
		spinnerPCy.setIncrement(1);
		spinnerPCy.setLayoutData(gridData);
		//
		Button button;
		button = new Button(spinnerComposite, SWT.CHECK);
		button.setText("Display Labels");
		button.setSelection(drawLables);
		button.addListener(SWT.Selection, e -> {
			drawLables = ((Button)e.widget).getSelection();
			loadingPlotChart.redraw();
		});
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Loading Plot");
		button.addListener(SWT.Selection, e -> reloadLoadingPlotChart());
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Load data");
		button.addListener(SWT.Selection, e -> update());
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		/*
		 * Plot the PCA chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.BORDER);
		chartComposite.setLayout(new GridLayout(2, false));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createLoadingPlot(chartComposite);
		createTable(chartComposite);
		//
		tabItem.setControl(composite);
	}

	private void reloadLoadingPlotChart() {

		if(loadingPlotChart != null) {
			/*
			 * Data
			 */
			int pcx = spinnerPCx.getSelection();
			int pcy = spinnerPCy.getSelection();
			double[] vectorX = basisVectors.get(pcx - 1);
			double[] vectorY = basisVectors.get(pcy - 1);
			for(int i = 0; i < extractedRetentionTimes.size(); i++) {
				ILineSeries scatterSeries = (ILineSeries)loadingPlotChart.getSeriesSet().getSeries(extractedRetentionTimes.get(i));
				scatterSeries.setXSeries(new double[]{vectorX[i]});
				scatterSeries.setYSeries(new double[]{vectorY[i]});
				scatterSeries.setSymbolColor(COLOR_RED);
				scatterSeries.setSymbolType(PlotSymbolType.CIRCLE);
			}
			loadingPlotChart.getAxisSet().getXAxis(0).getTitle().setText("PC" + pcx);
			loadingPlotChart.getAxisSet().getYAxis(0).getTitle().setText("PC" + pcy);
			loadingPlotChart.getAxisSet().adjustRange();
			loadingPlotChart.redraw();
			loadingPlotChart.update();
		}
	}

	public void update() {

		Optional<IPcaResults> results = pcaEditor.getPcaResults();
		if(results.isPresent()) {
			basisVectors.addAll(results.get().getBasisVectors());
			updateSpinnerPCMaxima(results.get().getNumberOfPrincipleComponents());
			for(int i = 0; i < results.get().getExtractedRetentionTimes().size(); i++) {
				if(results.get().isSelectedRetentionTimes().get(i)) {
					extractedRetentionTimes.add(nf.format(results.get().getExtractedRetentionTimes().get(i) / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				}
			}
			ISeriesSet seriesSet = loadingPlotChart.getSeriesSet();
			ISeries[] series = seriesSet.getSeries();
			for(ISeries serie : series) {
				seriesSet.deleteSeries(serie.getId());
			}
			for(int i = 0; i < extractedRetentionTimes.size(); i++) {
				loadingPlotChart.getSeriesSet().createSeries(SeriesType.LINE, extractedRetentionTimes.get(i));
			}
			reloadLoadingPlotChart();
			updateTable();
		}
	}

	private void updateSelection(Set<String> selectedItems) {

		for(String name : extractedRetentionTimes) {
			ILineSeries scatterSeries = (ILineSeries)loadingPlotChart.getSeriesSet().getSeries(name);
			scatterSeries.setVisible(false);
		}
		for(String name : selectedItems) {
			ILineSeries scatterSeries = (ILineSeries)loadingPlotChart.getSeriesSet().getSeries(name);
			scatterSeries.setVisible(true);
			scatterSeries.setVisibleInLegend(true);
		}
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
	}

	private void updateTable() {

		table.clearAll();
		table.removeAll();
		for(int i = 0; i < extractedRetentionTimes.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.None);
			tableItem.setText(extractedRetentionTimes.get(i));
			tableItem.setData(extractedRetentionTimes.get(i));
		}
		table.selectAll();
	}
}
