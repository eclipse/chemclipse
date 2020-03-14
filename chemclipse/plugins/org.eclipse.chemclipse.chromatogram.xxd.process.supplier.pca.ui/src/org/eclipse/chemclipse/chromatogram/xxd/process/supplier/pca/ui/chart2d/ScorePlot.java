/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - getting rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.SeriesConverter;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.MouseDownEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveCursorEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveSelectionEvent;
import org.eclipse.swtchart.extensions.events.MouseMoveShiftEvent;
import org.eclipse.swtchart.extensions.events.MouseUpEvent;
import org.eclipse.swtchart.extensions.events.ResetSeriesEvent;
import org.eclipse.swtchart.extensions.events.UndoRedoEvent;
import org.eclipse.swtchart.extensions.events.ZoomEvent;

public class ScorePlot extends PCA2DPlot {

	private final Map<String, IPcaResult> extractedResults = new HashMap<>();

	private class SelectActualSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getButton() {

			return IMouseSupport.MOUSE_BUTTON_LEFT;
		}

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			String selectedSeriesId = baseChart.getSelectedseriesId(event);
			if(!selectedSeriesId.equals("")) {
				ISample sample = extractedResults.get(selectedSeriesId).getSample();
				System.out.println(sample);
			}
		}
	}

	private class SelectSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getButton() {

			return IMouseSupport.MOUSE_BUTTON_LEFT;
		}

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			String selectedSeriesId = baseChart.getSelectedseriesId(event);
			if(!selectedSeriesId.equals("")) {
				ISample sample = extractedResults.get(selectedSeriesId).getSample();
				if(sample.isSelected()) {
					sample.setSelected(false);
				} else {
					sample.setSelected(true);
				}
			}
		}
	}

	public ScorePlot(Composite parent) {
		super(parent, "Score Plot");
		//
		IChartSettings chartSettings = getChartSettings();
		chartSettings.clearHandledEventProcessors();
		chartSettings.addHandledEventProcessor(new SelectSeriesEvent());
		chartSettings.addHandledEventProcessor(new SelectActualSeriesEvent());
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

	@SuppressWarnings("rawtypes")
	public void setInput(IPcaResults pcaResults) {

		if(pcaResults != null) {
			update(pcaResults);
		}
	}

	@SuppressWarnings("rawtypes")
	public void update(IPcaResults pcaResults) {

		deleteSeries();
		// TODO from Score Plot chart menu toolbar
		int pcX = 1;
		int pcY = 2;
		addSeriesData(SeriesConverter.sampleToSeries(pcaResults, pcX, pcY, extractedResults));
		update(pcX, pcY);
		redraw();
	}
}
