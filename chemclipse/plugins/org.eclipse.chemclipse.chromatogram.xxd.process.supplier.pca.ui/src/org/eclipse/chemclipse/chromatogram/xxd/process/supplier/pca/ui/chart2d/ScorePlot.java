/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.SeriesConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.events.AbstractHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.IHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.MouseDownEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveCursorEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveSelectionEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveShiftEvent;
import org.eclipse.eavp.service.swtchart.events.MouseUpEvent;
import org.eclipse.eavp.service.swtchart.events.ResetSeriesEvent;
import org.eclipse.eavp.service.swtchart.events.UndoRedoEvent;
import org.eclipse.eavp.service.swtchart.events.ZoomEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import javafx.collections.ObservableList;

public class ScorePlot extends PCA2DPlot {

	private class SelectSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int hideMask = SWT.CTRL;

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
				if(((event.stateMask & hideMask) == hideMask)) {
					ISample<? extends ISampleData> sample = extractedResults.get(selectedSeriesId).getSample();
					if(sample.isSelected()) {
						sample.setSelected(false);
					} else {
						sample.setSelected(true);
					}
				} else {
					ISample<? extends ISampleData> sample = extractedResults.get(selectedSeriesId).getSample();
					ObservableList<ISample<? extends ISampleData>> selection = SelectionManagerSample.getInstance().getSelection();
					if(!selection.contains(sample)) {
						selection.setAll(sample);
					} else {
						selection.remove(sample);
					}
				}
			}
		}
	}

	private final Map<String, IPcaResult> extractedResults = new HashMap<>();

	public ScorePlot(Composite parent) {
		super(parent, "Score Plot");
		IChartSettings chartSettings = getChartSettings();
		chartSettings.clearHandledEventProcessors();
		chartSettings.addHandledEventProcessor(new SelectSeriesEvent());
		chartSettings.addHandledEventProcessor(new ResetSeriesEvent());
		chartSettings.addHandledEventProcessor(new ZoomEvent());
		chartSettings.addHandledEventProcessor(new MouseDownEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveSelectionEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveShiftEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveCursorEvent());
		chartSettings.addHandledEventProcessor(new MouseUpEvent());
		chartSettings.addHandledEventProcessor(new UndoRedoEvent());
		applySettings(chartSettings);
	}

	public Map<String, IPcaResult> getExtractedResults() {

		return extractedResults;
	}

	public void update(IPcaResults pcaResults) {

		deleteSeries();
		addSeriesData(SeriesConverter.sampleToSeries(pcaResults, pcaResults.getPcaSettings().getPcX(), pcaResults.getPcaSettings().getPcY(), extractedResults));
		extractedResults.entrySet().forEach(e -> {
			if(SelectionManagerSample.getInstance().getSelection().contains(e.getValue().getSample())) {
				getBaseChart().selectSeries(e.getKey());
			}
		});
		update(pcaResults.getPcaSettings().getPcX(), pcaResults.getPcaSettings().getPcY());
	}
}
