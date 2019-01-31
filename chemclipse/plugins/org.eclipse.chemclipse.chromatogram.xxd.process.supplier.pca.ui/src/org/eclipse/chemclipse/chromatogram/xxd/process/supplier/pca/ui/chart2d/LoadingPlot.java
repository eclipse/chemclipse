/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.SeriesConverter;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ICustomSelectionHandler;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.MouseDownEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveCursorEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveSelectionEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveShiftEvent;
import org.eclipse.swtchart.extensions.events.MouseUpEvent;
import org.eclipse.swtchart.extensions.events.ResetSeriesEvent;
import org.eclipse.swtchart.extensions.events.SelectDataPointEvent;
import org.eclipse.swtchart.extensions.events.UndoRedoEvent;
import org.eclipse.swtchart.extensions.events.ZoomEvent;
import org.eclipse.swtchart.extensions.scattercharts.IScatterSeriesData;

import javafx.collections.ObservableList;

public class LoadingPlot extends PCA2DPlot {

	private class SelectActualSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			String selectedSeriesId = baseChart.getSelectedseriesId(event);
			if(!selectedSeriesId.equals("")) {
				IVariable variable = extractedValues.get(selectedSeriesId);
				ObservableList<IVariable> selection = selectionManagerVariable.getSelection();
				if(!selection.contains(variable)) {
					selection.setAll(variable);
				} else {
					selection.remove(variable);
				}
			}
		}
	}

	private class SelectSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			String selectedSeriesId = baseChart.getSelectedseriesId(event);
			if(!selectedSeriesId.equals("")) {
				IVariable variable = extractedValues.get(selectedSeriesId);
				variable.setSelected(!variable.isSelected());
			}
		}
	}

	final public static int LABELS_DESCRIPTION = 2;
	final public static int LABELS_RETENTION_TIME_MINUTES = 1;
	final private Set<String> actualSelection = new HashSet<>();
	final private Map<String, IVariable> extractedValues = new HashMap<>();
	private int labelsType = LABELS_RETENTION_TIME_MINUTES;
	private SelectionManagerVariable selectionManagerVariable;

	public LoadingPlot(Composite parent, SelectionManagerVariable selectionManagerVariable) {

		super(parent, "Loading Plot");
		this.selectionManagerVariable = selectionManagerVariable;
		IChartSettings chartSettings = getChartSettings();
		chartSettings.clearHandledEventProcessors();
		chartSettings.addHandledEventProcessor(new SelectSeriesEvent());
		chartSettings.addHandledEventProcessor(new SelectActualSeriesEvent());
		chartSettings.addHandledEventProcessor(new ResetSeriesEvent());
		chartSettings.addHandledEventProcessor(new SelectDataPointEvent());
		chartSettings.addHandledEventProcessor(new ZoomEvent());
		chartSettings.addHandledEventProcessor(new MouseDownEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveSelectionEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveShiftEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveCursorEvent());
		chartSettings.addHandledEventProcessor(new MouseUpEvent());
		chartSettings.addHandledEventProcessor(new UndoRedoEvent());
		applySettings(chartSettings);
		getBaseChart().addCustomRangeSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				updateSelection();
			}
		});
	}

	public Set<String> getActualSelection() {

		return actualSelection;
	}

	public Map<String, IVariable> getExtractedValues() {

		return extractedValues;
	}

	public int getLabelsType() {

		return labelsType;
	}

	public void setLabelsType(int labelsType) {

		if(labelsType == LABELS_DESCRIPTION || labelsType == LABELS_RETENTION_TIME_MINUTES) {
			this.labelsType = labelsType;
		}
	}

	public void update(IPcaResultsVisualization pcaResults) {

		int pcX = pcaResults.getPcaVisualization().getPcX();
		int pcY = pcaResults.getPcaVisualization().getPcY();
		List<IScatterSeriesData> series;
		if(labelsType == LABELS_RETENTION_TIME_MINUTES) {
			series = SeriesConverter.basisVectorsToSeries(pcaResults, pcX, pcY, extractedValues);
		} else {
			series = SeriesConverter.basisVectorsToSeriesDescription(pcaResults, pcX, pcY, extractedValues);
		}
		deleteSeries();
		addSeriesData(series);
		update(pcX, pcY);
		extractedValues.entrySet().forEach(e -> {
			if(selectionManagerVariable.getSelection().contains(e.getValue())) {
				getBaseChart().selectSeries(e.getKey());
			}
		});
		updateSelection();
	}

	private void updateSelection() {

		BaseChart baseChart = getBaseChart();
		Rectangle plotAreaBounds = baseChart.getPlotArea().getBounds();
		ISeries[] series = baseChart.getSeriesSet().getSeries();
		//
		actualSelection.clear();
		for(ISeries scatterSeries : series) {
			if(scatterSeries != null) {
				int size = scatterSeries.getXSeries().length;
				String id = scatterSeries.getId();
				for(int i = 0; i < size; i++) {
					Point point = scatterSeries.getPixelCoordinates(i);
					if(isPointVisible(point, plotAreaBounds)) {
						actualSelection.add(id);
					}
				}
			}
		}
	}
}
