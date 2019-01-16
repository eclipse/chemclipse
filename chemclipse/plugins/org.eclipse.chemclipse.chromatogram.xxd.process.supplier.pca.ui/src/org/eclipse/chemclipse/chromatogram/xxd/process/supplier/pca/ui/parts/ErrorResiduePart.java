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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.barchart.ErrorResidueBarChart;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class ErrorResiduePart {

	private ListChangeListener<ISample> actualSelectionChangeListener;
	private ErrorResidueBarChart errorResidueChart;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ListChangeListener<IPcaResult> selectionChangeListener;
	private boolean partHasBeenDestroy;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSample managerSample;

	public ErrorResiduePart() {
		selectionChangeListener = new ListChangeListener<IPcaResult>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IPcaResult> c) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					errorResidueChart.updateSelection();
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
						errorResidueChart.update(newValue);
					} else {
						errorResidueChart.removeData();
					}
				});
			}
		};
		actualSelectionChangeListener = new ListChangeListener<ISample>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample> c) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					errorResidueChart.updateSelection();
				});
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		errorResidueChart = new ErrorResidueBarChart(composite, null, getSelectionManagerSample());
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaresults = getSelectionManagerSamples().getActualSelectedPcaResults();
		pcaresults.addListener(pcaResultChangeLisnter);
		pcaResults = pcaresults.get();
		if(pcaResults != null) {
			errorResidueChart.update(pcaresults.getValue());
			pcaResults.getPcaResultList().addListener(selectionChangeListener);
		}
		getSelectionManagerSample().getSelection().addListener(actualSelectionChangeListener);
	}

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		getSelectionManagerSample().getSelection().removeListener(actualSelectionChangeListener);
		getSelectionManagerSamples().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			pcaResults.getPcaResultList().removeListener(selectionChangeListener);
		}
	}

	private SelectionManagerSample getSelectionManagerSample() {

		if(managerSample != null) {
			return managerSample;
		}
		return SelectionManagerSample.getInstance();
	}

	private SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}
}
