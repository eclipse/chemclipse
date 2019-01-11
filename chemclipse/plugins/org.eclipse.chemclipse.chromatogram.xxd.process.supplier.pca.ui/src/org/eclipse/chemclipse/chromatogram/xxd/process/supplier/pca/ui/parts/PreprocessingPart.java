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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.DataPreprocessingSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.collections.ListChangeListener;

public class PreprocessingPart {

	private static Map<ISamples<? extends IVariable, ? extends ISample>, PcaPreprocessingData> preprocessings;
	private DataPreprocessingSelection dataPreprocessing;
	private ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesChangeListener;

	public PreprocessingPart() {

		synchronized(PreprocessingPart.class) {
			if(preprocessings == null) {
				preprocessings = new ConcurrentHashMap<>();
				SelectionManagerSamples.getInstance().getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

					@Override
					public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

						while(c.next()) {
							for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
								preprocessings.remove(samples);
							}
						}
					}
				});
			}
		}
		samplesChangeListener = new ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> c) {

				PcaPreprocessingData pcaPreprocessingData = new PcaPreprocessingData();
				ISamplesVisualization<? extends IVariable, ? extends ISampleVisualization> samples = null;
				if(!c.getList().isEmpty()) {
					samples = c.getList().get(0);
					pcaPreprocessingData = getPcaPreprocessingData(samples);
				}
				final PcaPreprocessingData preprocessingData = pcaPreprocessingData;
				final ISamplesVisualization<? extends IVariable, ? extends ISampleVisualization> s = samples;
				Display.getDefault().syncExec(() -> {
					dataPreprocessing.update(preprocessingData);
					dataPreprocessing.setSamples(s);
				});
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		ScrolledComposite scrollNormalizationTables = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollNormalizationTables.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollNormalizationTables.setExpandHorizontal(true);
		scrollNormalizationTables.setExpandVertical(true);
		Composite compositeNormalizationTables = new Composite(scrollNormalizationTables, SWT.NONE);
		scrollNormalizationTables.setContent(compositeNormalizationTables);
		compositeNormalizationTables.setLayout(new FillLayout());
		if(SelectionManagerSamples.getInstance().getSelection().isEmpty()) {
			dataPreprocessing = new DataPreprocessingSelection(compositeNormalizationTables, null);
		} else {
			ISamplesVisualization<? extends IVariable, ? extends ISampleVisualization> samples = SelectionManagerSamples.getInstance().getSelection().get(0);
			dataPreprocessing = new DataPreprocessingSelection(compositeNormalizationTables, null, getPcaPreprocessingData(samples));
			dataPreprocessing.setSamples(samples);
		}
		SelectionManagerSamples.getInstance().getSelection().addListener(samplesChangeListener);
	}

	private PcaPreprocessingData getPcaPreprocessingData(ISamples<? extends IVariable, ? extends ISample> samples) {

		if(samples instanceof IDataPreprocessing) {
			IDataPreprocessing dataPreprocessing = (IDataPreprocessing)samples;
			return dataPreprocessing.getPcaPreprocessingData();
		} else {
			if(preprocessings.containsKey(samples)) {
				return preprocessings.get(samples);
			} else {
				PcaPreprocessingData pcaPreprocessingData = new PcaPreprocessingData();
				preprocessings.put(samples, pcaPreprocessingData);
				return pcaPreprocessingData;
			}
		}
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getSelection().removeListener(samplesChangeListener);
	}
}
