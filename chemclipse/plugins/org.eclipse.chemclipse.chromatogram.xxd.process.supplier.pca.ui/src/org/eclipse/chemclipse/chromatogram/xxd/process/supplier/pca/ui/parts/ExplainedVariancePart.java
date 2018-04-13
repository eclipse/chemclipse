/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.errorresidue.ExplainedVarianceBarChart;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class ExplainedVariancePart {

	private ExplainedVarianceBarChart explainedVarianceChart;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ListChangeListener<IPcaResult> selectionChangeListener;
	private boolean partHasBeenDestroy;

	public ExplainedVariancePart() {
		selectionChangeListener = new ListChangeListener<IPcaResult>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IPcaResult> c) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					explainedVarianceChart.updateSelection();
				});
			}
		};
		pcaResultChangeLisnter = new ChangeListener<IPcaResultsVisualization>() {

			@Override
			public void changed(ObservableValue<? extends IPcaResultsVisualization> observable, IPcaResultsVisualization oldValue, IPcaResultsVisualization newValue) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					pcaResults = newValue;
					if(oldValue != null) {
						oldValue.getPcaResultList().removeListener(selectionChangeListener);
					}
					if(newValue != null) {
						newValue.getPcaResultList().addListener(selectionChangeListener);
						explainedVarianceChart.update(newValue);
					} else {
						explainedVarianceChart.removeData();
					}
				});
			}
		};
		/*
		 * actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {
		 * @Override
		 * public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {
		 * Display.getDefault().asyncExec(() -> {
		 * if(partHasBeenDestroy)
		 * return;
		 * explainedVarianceChart.updateSelection();
		 * });
		 * }
		 * };
		 */
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		explainedVarianceChart = new ExplainedVarianceBarChart(composite, null);
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaresults = SelectionManagerSamples.getInstance().getActualSelectedPcaResults();
		pcaresults.addListener(pcaResultChangeLisnter);
		pcaResults = pcaresults.get();
		if(pcaResults != null) {
			explainedVarianceChart.update(pcaresults.getValue());
			pcaResults.getPcaResultList().addListener(selectionChangeListener);
		}
		// SelectionManagerSample.getInstance().getSelection().addListener(actualSelectionChangeListener);
	}

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		// SelectionManagerSample.getInstance().getSelection().removeListener(actualSelectionChangeListener);
		SelectionManagerSamples.getInstance().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			pcaResults.getPcaResultList().removeListener(selectionChangeListener);
		}
	}
}
