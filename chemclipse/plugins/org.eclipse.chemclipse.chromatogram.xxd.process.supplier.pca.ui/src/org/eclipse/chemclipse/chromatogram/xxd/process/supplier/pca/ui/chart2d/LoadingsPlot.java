/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.SeriesConverter;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.scattercharts.IScatterSeriesData;

public class LoadingsPlot extends AbtractPlotPCA {

	public static final int LABEL_RETENTION_TIME_MINUTES = 1;
	public static final int LABEL_DESCRIPTION = 2;
	//
	private int labelType = LABEL_RETENTION_TIME_MINUTES;

	public LoadingsPlot(Composite parent, int style) {

		super(parent, style, "Loadings Plot");
	}

	public int getLabelsType() {

		return labelType;
	}

	public void setLabelsType(int labelsType) {

		if(labelsType == LABEL_DESCRIPTION || labelsType == LABEL_RETENTION_TIME_MINUTES) {
			this.labelType = labelsType;
		}
	}

	public void setInput(EvaluationPCA evaluationPCA, int pcX, int pcY) {

		deleteSeries();
		if(evaluationPCA != null) {
			IResultsPCA<? extends IResultPCA, ? extends IVariable> resultsPCA = evaluationPCA.getResults();
			List<IScatterSeriesData> series;
			if(labelType == LABEL_RETENTION_TIME_MINUTES) {
				series = SeriesConverter.basisVectorsToSeries(resultsPCA, pcX, pcY);
			} else {
				series = SeriesConverter.basisVectorsToSeriesDescription(resultsPCA, pcX, pcY);
			}
			//
			addSeriesData(series);
			update(pcX, pcY, resultsPCA.getExplainedVariances());
		}
		redraw();
	}
}
