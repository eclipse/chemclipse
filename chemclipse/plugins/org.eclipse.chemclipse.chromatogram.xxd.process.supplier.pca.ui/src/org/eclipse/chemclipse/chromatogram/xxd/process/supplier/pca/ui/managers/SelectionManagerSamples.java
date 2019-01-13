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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaResultsVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.ux.fx.ui.SelectionManagerProto;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

public class SelectionManagerSamples extends SelectionManagerProto<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> {

	private static SelectionManagerSamples instance;

	public static SelectionManagerSamples getInstance() {

		synchronized(SelectionManagerSamples.class) {
			if(instance == null) {
				instance = new SelectionManagerSamples();
			}
			instance.getSelection().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

					SelectionManagerSample.getInstance().getSelection().clear();
					if(!c.getList().isEmpty()) {
						instance.actualSelectedPcaResults.setValue(instance.pcaResults.get(c.getList().get(0)));
					} else {
						instance.actualSelectedPcaResults.setValue(null);
					}
				}
			});
			instance.getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

					while(c.next()) {
						for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
							instance.pcaResults.remove(samples);
						}
					}
				}
			});
		}
		return instance;
	}

	private ObjectProperty<IPcaResultsVisualization> actualSelectedPcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaResultsVisualization> pcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample>, PcaPreprocessingData> preprocessings;

	private SelectionManagerSamples() {

		super();
		pcaResults = new HashMap<>();
		actualSelectedPcaResults = new SimpleObjectProperty<>();
		preprocessings = new HashMap<>();
		getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

				while(c.next()) {
					synchronized(preprocessings) {
						for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
							preprocessings.remove(samples);
						}
					}
				}
			}
		});
	}

	public <V extends IVariableVisualization, S extends ISampleVisualization> IPcaResultsVisualization evaluatePca(ISamplesVisualization<V, S> samples, IPcaSettings settings, IPcaSettingsVisualization pcaSettingsVisualization, IProgressMonitor monitor, boolean setSelected) {

		monitor.setTaskName("Evaluation");
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		PcaResults results = pcaEvaluation.process(samples, settings, monitor);
		IPcaResultsVisualization pcaResultsVisualization = new PcaResultsVisualization<>(results, pcaSettingsVisualization);
		pcaResultsVisualization.getPcaResultList().forEach(r -> {
			Optional<S> sample = samples.getSampleList().stream().filter(s -> r.getSample() == s).findAny();
			r.copyVisualizationProperties(sample.get());
		});
		monitor.setTaskName("Add samples");
		if(!getElements().contains(samples)) {
			getElements().add(samples);
		}
		synchronized(pcaResults) {
			pcaResults.put(samples, pcaResultsVisualization);
		}
		if(setSelected) {
			monitor.setTaskName("Set actual result");
			actualSelectedPcaResults.setValue(pcaResultsVisualization);
			if(!getSelection().contains(samples)) {
				monitor.setTaskName("Set actual selection");
				getSelection().setAll(samples);
			}
		}
		return pcaResultsVisualization;
	}

	public ReadOnlyObjectProperty<IPcaResultsVisualization> getActualSelectedPcaResults() {

		return actualSelectedPcaResults;
	}

	public PcaPreprocessingData getPreprocessoringData(ISamples<? extends IVariable, ? extends ISample> samples) {

		if(samples instanceof IDataPreprocessing) {
			IDataPreprocessing dataPreprocessing = (IDataPreprocessing)samples;
			return dataPreprocessing.getPcaPreprocessingData();
		} else {
			synchronized(preprocessings) {
				if(preprocessings.containsKey(samples)) {
					return preprocessings.get(samples);
				} else {
					PcaPreprocessingData pcaPreprocessingData = new PcaPreprocessingData();
					preprocessings.put(samples, pcaPreprocessingData);
					return pcaPreprocessingData;
				}
			}
		}
	}

	public Optional<IPcaResultsVisualization> getPcaResults(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(pcaResults) {
			return Optional.ofNullable(pcaResults.get(samples));
		}
	}
}
