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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDefaultPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaSettingsVisualization;
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
		}
		return instance;
	}

	private ObjectProperty<IPcaResultsVisualization> actualSelectedPcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaResultsVisualization> pcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaSettingsVisualization> settings;
	private static Map<ISamples<? extends IVariable, ? extends ISample>, PcaPreprocessingData> preprocessings = new HashMap<>();
	private SelectionManagerSample selectionManagerSample;
	private SelectionManagerVariable selectionManagerVariable;

	public SelectionManagerSamples() {
		this.selectionManagerSample = new SelectionManagerSample();
		this.selectionManagerVariable = new SelectionManagerVariable();
		pcaResults = new HashMap<>();
		actualSelectedPcaResults = new SimpleObjectProperty<>();
		settings = new HashMap<>();
		getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

				while(c.next()) {
					synchronized(settings) {
						for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
							settings.remove(samples);
						}
					}
				}
			}
		});
		getSelection().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

				selectionManagerSample.getSelection().clear();
				selectionManagerVariable.getSelection().clear();
				if(!c.getList().isEmpty()) {
					actualSelectedPcaResults.setValue(pcaResults.get(c.getList().get(0)));
				} else {
					actualSelectedPcaResults.setValue(null);
				}
			}
		});
		getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

				while(c.next()) {
					for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
						pcaResults.remove(samples);
					}
				}
			}
		});
	}

	public SelectionManagerSample getSelectionManagerSample() {

		return selectionManagerSample;
	}

	public SelectionManagerVariable getSelectionManagerVariable() {

		return selectionManagerVariable;
	}

	public <V extends IVariableVisualization, S extends ISampleVisualization> IPcaResultsVisualization evaluatePca(ISamplesVisualization<V, S> samples, IPcaSettings settings, IPcaVisualization pcaVisualization, IProgressMonitor monitor, boolean setSelected) {

		monitor.setTaskName("Evaluation");
		// remove all current result
		synchronized(pcaResults) {
			pcaResults.remove(samples);
		}
		if(setSelected) {
			actualSelectedPcaResults.setValue(null);
		}
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		PcaResults results = pcaEvaluation.process(samples, settings, monitor);
		IPcaResultsVisualization pcaResultsVisualization = new PcaResultsVisualization<>(results, pcaVisualization);
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

	//
	public PcaPreprocessingData getPreprocessingData(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(preprocessings) {
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
	}

	public IPcaSettingsVisualization getPcaSettings(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(settings) {
			if(settings.containsKey(samples)) {
				return settings.get(samples);
			} else {
				IPcaSettings pcaSettings;
				if(samples instanceof IDefaultPcaSettings) {
					pcaSettings = ((IDefaultPcaSettings)samples).getDefaultPcaSettings();
				} else {
					pcaSettings = PreferenceSupplier.getPcaSettings();
				}
				IPcaSettingsVisualization s = new PcaSettingsVisualization(pcaSettings);
				settings.put(samples, s);
				return s;
			}
		}
	}

	public Optional<IPcaResultsVisualization> getPcaResults(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(pcaResults) {
			return Optional.ofNullable(pcaResults.get(samples));
		}
	}
}
