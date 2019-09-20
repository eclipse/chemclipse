/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - refactor / remove FX dependencies
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.SeriesConverter;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.IChartSettings;
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

	public ScorePlot(Composite parent) {
		super(parent, "Score Plot");
		IChartSettings chartSettings = getChartSettings();
		chartSettings.clearHandledEventProcessors();
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

	public void update(IPcaResultsVisualization pcaResults, ISample selectedSample) {

		deleteSeries();
		addSeriesData(SeriesConverter.sampleToSeries(pcaResults, pcaResults.getPcaVisualization().getPcX(), pcaResults.getPcaVisualization().getPcY(), extractedResults));
		selectSample(selectedSample);
		update(pcaResults.getPcaVisualization().getPcX(), pcaResults.getPcaVisualization().getPcY());
		redraw();
	}

	public void selectSample(ISample sample) {

		getBaseChart().resetSeriesSettings();
		extractedResults.entrySet().forEach(e -> {
			if(e.getValue().getSample() == sample) {
				getBaseChart().selectSeries(e.getKey());
			}
		});
	}
}
