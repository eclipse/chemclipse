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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.collections.ListChangeListener;

public class DataTablePart extends GeneralPcaPart {

	private Runnable changeData;
	private Runnable changeSelectedSample;
	private PeakListNatTable peakListIntensityTable;
	private ListChangeListener<IVariable> actualVariableChangeListener = e -> {
		if(!e.getList().isEmpty()) {
			peakListIntensityTable.selectRow(e.getList().get(0));
		}
	};
	private Composite composite;

	@Inject
	public DataTablePart() {
		super();
	}

	@PostConstruct
	public void postContruct(Composite composite) {

		this.composite = composite;
		changeData = () -> peakListIntensityTable.refreshTable();
		changeSelectedSample = () -> {
			ISamples<? extends IVariableVisualization, ? extends ISampleVisualization> samples = getSamples();
			if(samples != null) {
				peakListIntensityTable.update(getSamples(), getPcaSettings());
			} else {
				peakListIntensityTable.clearTable();
			}
		};
		getSelectionManagerSamples().getSelectionManagerVariable().getSelection().addListener(actualVariableChangeListener);
		createComposite(composite);
		initializeHandler();
	}

	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		peakListIntensityTable = new PeakListNatTable(composite, null, getSelectionManagerSamples());
	}

	@PreDestroy
	private void preDestroy() {

		getSelectionManagerSamples().getSelectionManagerVariable().getSelection().removeListener(actualVariableChangeListener);
		Display.getDefault().timerExec(-1, changeSelectedSample);
		Display.getDefault().timerExec(-1, changeData);
	}

	@Override
	protected void variablesHasBeenUpdated() {

		composite.getDisplay().timerExec(100, changeData);
	}

	@Override
	protected void samplesHasBeenUpdated() {

		composite.getDisplay().timerExec(100, changeSelectedSample);
	}

	@Override
	protected void samplesHasBeenSet() {

		composite.getDisplay().timerExec(100, changeSelectedSample);
	}

	@Override
	protected void settingsHasBeenChanged() {

		composite.getDisplay().timerExec(100, changeData);
	}
}
