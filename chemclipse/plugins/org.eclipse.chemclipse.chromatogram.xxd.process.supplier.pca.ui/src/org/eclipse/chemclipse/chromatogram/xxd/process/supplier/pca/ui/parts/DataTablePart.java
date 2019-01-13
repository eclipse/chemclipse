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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.collections.ListChangeListener;

public class DataTablePart {

	private ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> actualSamples;
	private Runnable changeData;
	private Runnable changeSelectedSample;
	private PeakListNatTable peakListIntensityTable;
	private ListChangeListener<ISample> sampleChangeListener;
	private ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesChangeListener;
	private ListChangeListener<IVariableVisualization> variableChangeListener;

	public DataTablePart() {

		changeData = () -> peakListIntensityTable.refreshTable();
		changeSelectedSample = () -> peakListIntensityTable.update(actualSamples);
		samplesChangeListener = new ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> c) {

				if(actualSamples != null) {
					actualSamples.getSampleList().removeListener(sampleChangeListener);
					actualSamples.getVariables().removeListener(variableChangeListener);
				}
				if(!c.getList().isEmpty()) {
					ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = c.getList().get(0);
					actualSamples = samples;
					peakListIntensityTable.update(samples);
					actualSamples.getVariables().addListener(variableChangeListener);
					samples.getSampleList().addListener(sampleChangeListener);
				} else {
					peakListIntensityTable.clearTable();
				}
			}
		};
		sampleChangeListener = new ListChangeListener<ISample>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISample> c) {

				if(!c.getList().isEmpty()) {
					Display.getDefault().timerExec(100, changeSelectedSample);
				}
			}
		};
		variableChangeListener = new ListChangeListener<IVariableVisualization>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends IVariableVisualization> c) {

				Display.getDefault().timerExec(100, changeData);
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		peakListIntensityTable = new PeakListNatTable(composite, null);
		SelectionManagerSamples.getInstance().getSelection().addListener(samplesChangeListener);
		if(!SelectionManagerSamples.getInstance().getSelection().isEmpty()) {
			actualSamples = SelectionManagerSamples.getInstance().getSelection().get(0);
			actualSamples.getSampleList().addListener(sampleChangeListener);
			actualSamples.getVariables().addListener(variableChangeListener);
			peakListIntensityTable.update(actualSamples);
		}
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getSelection().removeListener(samplesChangeListener);
		if(actualSamples != null) {
			actualSamples.getSampleList().removeListener(sampleChangeListener);
			actualSamples.getVariables().removeListener(variableChangeListener);
		}
		Display.getDefault().timerExec(-1, changeSelectedSample);
		Display.getDefault().timerExec(-1, changeData);
	}
}
