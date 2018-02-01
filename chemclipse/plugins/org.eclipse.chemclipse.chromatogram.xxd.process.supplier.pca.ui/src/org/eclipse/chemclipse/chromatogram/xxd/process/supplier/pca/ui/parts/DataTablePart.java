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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.collections.ListChangeListener;

public class DataTablePart {

	private ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> actualSamples;
	private Runnable changeData;
	private Runnable changeSelectedSample;
	private PeakListNatTable peakListIntensityTable;
	private ListChangeListener<ISample<? extends ISampleData>> sampleChangeListener;
	private ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> samplesChangeListener;
	private ListChangeListener<IVariable> variableChangeListener;

	public DataTablePart() {
		changeData = () -> peakListIntensityTable.refreshTable();
		changeSelectedSample = () -> peakListIntensityTable.update(actualSamples);
		samplesChangeListener = new ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> c) {

				if(actualSamples != null) {
					actualSamples.getSampleList().removeListener(sampleChangeListener);
					actualSamples.getVariables().removeListener(variableChangeListener);
				}
				if(!c.getList().isEmpty()) {
					ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples = c.getList().get(0);
					actualSamples = samples;
					peakListIntensityTable.update(samples);
					actualSamples.getVariables().addListener(variableChangeListener);
					samples.getSampleList().addListener(sampleChangeListener);
				} else {
					peakListIntensityTable.clearTable();
				}
			}
		};
		sampleChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				if(!c.getList().isEmpty()) {
					Display.getDefault().timerExec(100, changeSelectedSample);
				}
			}
		};
		variableChangeListener = new ListChangeListener<IVariable>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends IVariable> c) {

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
