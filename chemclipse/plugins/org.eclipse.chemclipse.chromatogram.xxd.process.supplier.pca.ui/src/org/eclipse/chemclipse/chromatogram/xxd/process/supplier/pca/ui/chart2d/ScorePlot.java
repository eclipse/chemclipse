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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.SeriesConverter;
import org.eclipse.swt.widgets.Composite;

public class ScorePlot extends PCA2DPlot {

	private IPcaResults pcaResults;

	public ScorePlot(Composite parent) {
		super(parent, "Score Plot");
	}

	@Override
	public void update(int pcX, int pcY) {

		if(pcaResults != null) {
			deleteSeries();
			addSeriesData(SeriesConverter.sampleToSeries(pcaResults, pcX, pcY));
			super.update(pcX, pcY);
		}
	}

	public void update(IPcaResults pcaResults, int pcX, int pcY) {

		if(pcaResults != null) {
			this.pcaResults = pcaResults;
			update(pcX, pcY);
		}
	}
}
