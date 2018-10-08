/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IMarkedSignal;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapData;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapSupport;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.datadefinition.IPrimaryArrayWrapper;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class HeatmapUI {

	private XYGraph m_plot;
	private IntensityGraphFigure m_heatmap;
	private Figure m_container;
	private LightweightSystem m_lws;
	private Trace trace;
	private ScaledSliderFigure slider;
	private List<IMarkedSignal> signals;
	private IChromatogramSelection chromatogramSelection;
	private ChromatogramHeatmapData chromatogramHeatmap;
	private Label selectedWavelengthLabel;
	//
	private ChromatogramHeatmapSupport chromatogramHeatmapSupport = new ChromatogramHeatmapSupport();
	//
	private Composite parent;

	public HeatmapUI(Composite parent) {

		signals = new ArrayList<>();
		this.parent = parent;
	}

	public void setChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		Optional<ChromatogramHeatmapData> heatmap = chromatogramHeatmapSupport.getHeatmap(chromatogramSelection.getChromatogram());
		this.chromatogramSelection = chromatogramSelection;
		signals.clear();
		if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			initWSD();
			if(heatmap.isPresent()) {
				chromatogramHeatmap = heatmap.get();
				setHeatMap(chromatogramHeatmap);
			}
			IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
			double selectedSliderValue = setSlider(chromatogramSelectionWSD);
			setSelectedWavelength(selectedSliderValue);
		} else if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			initMSD();
			if(heatmap.isPresent()) {
				chromatogramHeatmap = heatmap.get();
				setHeatMap(chromatogramHeatmap);
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			initCSD();
			if(heatmap.isPresent()) {
				chromatogramHeatmap = heatmap.get();
				setHeatMap(chromatogramHeatmap);
			}
		}
	}

	private void initWSD() {

		disposeChildren(parent);
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		Canvas cavensSlider = new Canvas(composite, SWT.NONE);
		cavensSlider.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		slider(cavensSlider);
		Canvas cavensHeatMap = new Canvas(composite, SWT.NONE);
		cavensHeatMap.setLayoutData(new GridData(GridData.FILL_BOTH));
		createChart(cavensHeatMap);
		selectedWavelengthLabel = new Label(composite, SWT.None);
		GridData gridData = new GridData(GridData.END);
		gridData.horizontalSpan = 2;
		selectedWavelengthLabel.setLayoutData(gridData);
		parent.layout(true, true);
		parent.redraw();
	}

	private void initMSD() {

		disposeChildren(parent);
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		Canvas cavensHeatMap = new Canvas(composite, SWT.NONE);
		createChart(cavensHeatMap);
		parent.layout(true, true);
		parent.redraw();
	}

	private void initCSD() {

		disposeChildren(parent);
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		Canvas cavensHeatMap = new Canvas(composite, SWT.NONE);
		createChart(cavensHeatMap);
		parent.layout(true, true);
		parent.redraw();
	}

	private void disposeChildren(Composite composite) {

		Arrays.stream(composite.getChildren()).forEach(control -> control.dispose());
	}

	private void setHeatMap(ChromatogramHeatmapData chromatogramHeatmap) {

		Range axisRangeWidth = chromatogramHeatmap.getAxisRangeWidth();
		Range axisRangeHight = chromatogramHeatmap.getAxisRangeHight();
		double maxIntensity = chromatogramHeatmap.getMaximum();
		double minIntensity = chromatogramHeatmap.getMinimum();
		int dataWidth = chromatogramHeatmap.getDataWidth();
		int dataHeight = chromatogramHeatmap.getDataHeight();
		IPrimaryArrayWrapper data = chromatogramHeatmap.getArrayWrapper();
		m_plot.getPrimaryXAxis().setRange(axisRangeWidth);
		m_plot.getPrimaryYAxis().setRange(axisRangeHight);
		m_heatmap.getXAxis().setRange(axisRangeWidth);
		m_heatmap.getYAxis().setRange(axisRangeHight);
		m_heatmap.setMax(maxIntensity);
		m_heatmap.setMin(minIntensity);
		m_heatmap.setDataHeight(dataHeight);
		m_heatmap.setDataWidth(dataWidth);
		m_heatmap.setDataArray(data);
		m_heatmap.repaint();
	}

	private int setSlider(IChromatogramSelectionWSD chromatogramSelectionWSD) {

		chromatogramSelectionWSD.getChromatogramWSD().getWavelengths().stream().sorted((w1, w2) -> Double.compare(w1, w2)).forEach(wavelength -> signals.add(new MarkedWavelength(wavelength)));
		IMarkedWavelengths markedWavelenghts = chromatogramSelectionWSD.getSelectedWavelengths();
		if(markedWavelenghts.isEmpty()) {
			markedWavelenghts.add((IMarkedWavelength)signals.get(0));
		}
		IMarkedWavelength selectedWawelength = markedWavelenghts.stream().findFirst().get();
		int selectedSliderValue = 0;
		double selectedWavelength = selectedWawelength.getWavelength();
		for(int i = 0; i < signals.size(); i++) {
			if(((IMarkedWavelength)signals.get(i)).getWavelength() == selectedWavelength) {
				selectedSliderValue = i + 1;
			}
		}
		if(signals.size() > 1) {
			slider.setEnabled(true);
			slider.setRange(1, signals.size());
			slider.setStepIncrement(1);
			slider.repaint();
			slider.setValue(selectedSliderValue);
			m_container.setEnabled(true);
		} else {
			slider.setEnabled(false);
			m_container.setEnabled(false);
		}
		return selectedSliderValue;
	}

	private void slider(Canvas cavens) {

		final LightweightSystem lws = new LightweightSystem(cavens);
		// Create Scaled Slider
		slider = new ScaledSliderFigure();
		// Init Scaled Slider
		slider.setFillBackgroundColor(ColorConstants.lightGray);
		slider.setFillColor(ColorConstants.lightGray);
		slider.setShowHi(false);
		slider.setShowHihi(false);
		slider.setShowLo(false);
		slider.setShowLolo(false);
		slider.setShowMarkers(false);
		slider.setShowScale(false);
		slider.setThumbColor(ColorConstants.lightGray);
		slider.addManualValueChangeListener(new IManualValueChangeListener() {

			@Override
			public void manualValueChanged(double selectedSliderValue) {

				if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
					setSelectedWavelength(selectedSliderValue);
				}
			}
		});
		lws.setContents(slider);
	}

	private void createChart(Canvas cavens) {
		// Scale scale = new Scale(cavens,SWT.VERTICAL);

		///
		m_plot = new XYGraph();
		// m_plot.setTitle(DEFAULT_TITLE);
		m_plot.setShowLegend(false);
		m_plot.setTransparent(true);
		m_plot.getPrimaryXAxis().setTitle(null);
		m_plot.getPrimaryXAxis().setShowMinLabel(false);
		m_plot.getPrimaryXAxis().setShowMaxLabel(false);
		m_plot.getPrimaryYAxis().setTitle(null);
		m_plot.getPrimaryXAxis().setMinorTicksVisible(false);
		m_plot.getPrimaryYAxis().setMinorTicksVisible(false);
		m_plot.getPrimaryYAxis().setScaleLineVisible(false);
		m_plot.setShowLegend(false);
		m_plot.setTransparent(true);
		m_plot.getPrimaryXAxis().setMinorTicksVisible(false);
		m_plot.getPrimaryXAxis().setShowMajorGrid(false);
		m_plot.getPrimaryYAxis().setMinorTicksVisible(false);
		m_plot.getPrimaryYAxis().setShowMajorGrid(false);
		// set heatmap
		m_heatmap = new IntensityGraphFigure(true);
		m_heatmap.getXAxis().setTitle(null);
		m_heatmap.getYAxis().setTitle(null);
		m_heatmap.getXAxis().setMinorTicksVisible(false);
		m_heatmap.getYAxis().setMinorTicksVisible(false);
		m_heatmap.setShowRamp(false);
		m_heatmap.getXAxis().setMinorTicksVisible(false);
		m_heatmap.getXAxis().setShowMajorGrid(false);
		m_heatmap.getXAxis().setShowMinLabel(false);
		m_heatmap.getXAxis().setShowMaxLabel(false);
		m_heatmap.getYAxis().setMinorTicksVisible(false);
		m_heatmap.getYAxis().setShowMajorGrid(false);
		m_heatmap.getYAxis().setScaleLineVisible(false);
		m_heatmap.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
		// add trace
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		trace = new Trace("Trace1-XY Plot", m_plot.getPrimaryXAxis(), m_plot.getPrimaryYAxis(), traceDataProvider);
		trace.setLineWidth(5);
		trace.setTraceColor(ColorConstants.orange);
		// set trace property
		trace.setPointStyle(PointStyle.NONE);
		m_plot.addTrace(trace);
		// overlay the graphs
		org.eclipse.swt.graphics.Rectangle client = cavens.getClientArea();
		Rectangle constraint = new Rectangle(0, 0, client.width, client.height);
		m_container = new Figure();
		m_container.setLayoutManager(new XYLayout());
		// put graph in canvas
		m_lws = new LightweightSystem(cavens);
		m_lws.setContents(m_container);
		m_lws.getRootFigure().setBackgroundColor(ColorConstants.white);
		m_lws.setContents(m_container);
		// make canvens resizable
		cavens.setLayoutData(new GridData(GridData.FILL_BOTH));
		cavens.setLayout(new FillLayout());
		cavens.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event e) {

				org.eclipse.swt.graphics.Rectangle rect = cavens.getClientArea();
				Rectangle constraint = new Rectangle(0, 0, rect.width, rect.height);
				m_container.add(m_heatmap, constraint);
				m_container.add(m_plot, constraint);
			}
		});
	}

	private void setSelectedWavelength(double selectedSliderValue) {

		IMarkedWavelength selectedWavelength = (IMarkedWavelength)signals.get((int)selectedSliderValue - 1);
		createTrace(selectedWavelength.getWavelength());
		selectedWavelengthLabel.setText("Selected wavelength " + selectedWavelength.getWavelength() + " nm");
		IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
		IMarkedWavelengths selectedWavelengths = chromatogramSelectionWSD.getSelectedWavelengths();
		selectedWavelengths.clear();
		selectedWavelengths.add(selectedWavelength);
		chromatogramSelectionWSD.update(true);
		parent.layout(true, true);
		parent.redraw();
	}

	private void createTrace(double value) {

		if(chromatogramHeatmap != null) {
			CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
			Range axisRangeWidth = chromatogramHeatmap.getAxisRangeWidth();
			traceDataProvider.setBufferSize(100);
			traceDataProvider.setCurrentYDataArray(new double[]{value, value});
			traceDataProvider.setCurrentXDataArray(new double[]{axisRangeWidth.getLower(), axisRangeWidth.getUpper()});
			trace.setDataProvider(traceDataProvider);
			// add the trace to xyGraph
			m_plot.repaint();
		}
		// create the trace
	}
}
