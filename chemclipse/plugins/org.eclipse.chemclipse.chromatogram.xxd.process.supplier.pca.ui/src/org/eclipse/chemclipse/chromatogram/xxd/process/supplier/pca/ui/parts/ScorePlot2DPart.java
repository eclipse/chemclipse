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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.PcaContext;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.PcaContextListener;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ScorePlot2DPart implements PcaContextListener {

	private ScorePlot scorePlot;
	@Inject
	private PcaContext context;

	@Override
	public void sampleSelectionChanged(ISample sampleSelection, PcaContext pcaContext) {

		scorePlot.selectSample(sampleSelection);
	}

	@Override
	public void pcaSelectionChanged(IPcaResultsVisualization pcaSelection, PcaContext pcaContext) {

		if(pcaSelection == null) {
			scorePlot.deleteSeries();
		} else {
			scorePlot.update(pcaSelection, context.getSampleSelection());
		}
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Composite scorePlotComposite = new Composite(composite, SWT.None);
		scorePlotComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlotComposite.setLayout(new FillLayout());
		scorePlot = new ScorePlot(scorePlotComposite);
		context.addListener(this);
	}

	public void refresh() {

		scorePlot.update(context.getPcaResultSelection(), context.getSampleSelection());
	}

	@PreDestroy
	public void preDestroy() {

		context.removeListener(this);
	}
}
