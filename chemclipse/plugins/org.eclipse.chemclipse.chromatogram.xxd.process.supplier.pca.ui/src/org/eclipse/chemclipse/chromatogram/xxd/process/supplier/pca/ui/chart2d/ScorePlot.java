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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.SeriesConverter;
import org.eclipse.swt.widgets.Composite;

public class ScorePlot extends AbtractPlotPCA {

	private final Map<String, IResultPCA> extractedResults = new HashMap<>();

	public ScorePlot(Composite parent, int style) {
		super(parent, style, "Score Plot");
	}

	@SuppressWarnings("rawtypes")
	public void setInput(EvaluationPCA evaluationPCA) {

		if(evaluationPCA != null) {
			IResultsPCA resultsPCA = evaluationPCA.getResults();
			update(resultsPCA);
		}
	}

	@SuppressWarnings("rawtypes")
	public void update(IResultsPCA pcaResults) {

		deleteSeries();
		// TODO from Score Plot chart menu toolbar
		int pcX = 1;
		int pcY = 2;
		addSeriesData(SeriesConverter.sampleToSeries(pcaResults, pcX, pcY, extractedResults));
		update(pcX, pcY);
		redraw();
	}
}
