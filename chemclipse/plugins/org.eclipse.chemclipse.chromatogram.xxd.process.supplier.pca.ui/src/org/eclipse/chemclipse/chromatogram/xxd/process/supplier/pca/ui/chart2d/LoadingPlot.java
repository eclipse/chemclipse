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

public class LoadingPlot extends PCA2DPlot {

	private IPcaResults pcaResults;

	public LoadingPlot(Composite parent) {
		super(parent, "Loading Plot");
	}

	@Override
	public void update(int pcX, int pcY) {

		if(pcaResults != null) {
			deleteSeries();
			addSeriesData(SeriesConverter.basisVectorsToSeries(pcaResults, pcX, pcY));
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
