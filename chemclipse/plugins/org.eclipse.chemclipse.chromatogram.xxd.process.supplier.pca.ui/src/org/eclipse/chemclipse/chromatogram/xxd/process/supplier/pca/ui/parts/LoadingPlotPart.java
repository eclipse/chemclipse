/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.LoadingPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class LoadingPlotPart {

	private LoadingPlot loadingPlot;
	private ChangeListener<IPcaResults> pcaResultChangeLisnter;

	public LoadingPlotPart() {
		pcaResultChangeLisnter = new ChangeListener<IPcaResults>() {

			@Override
			public void changed(ObservableValue<? extends IPcaResults> observable, IPcaResults oldValue, IPcaResults newValue) {

				Display.getCurrent().syncExec(() -> {
					if(newValue != null) {
						loadingPlot.update(newValue);
					} else {
						loadingPlot.deleteSeries();
					}
				});
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		loadingPlot = new LoadingPlot(composite);
		ReadOnlyObjectProperty<IPcaResults> pcaResults = SelectionManagerSamples.getInstance().getActualSelectedPcaResults();
		pcaResults.addListener(pcaResultChangeLisnter);
		if(pcaResults.isNotNull().get()) {
			loadingPlot.update(pcaResults.get());
		}
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
	}
}
