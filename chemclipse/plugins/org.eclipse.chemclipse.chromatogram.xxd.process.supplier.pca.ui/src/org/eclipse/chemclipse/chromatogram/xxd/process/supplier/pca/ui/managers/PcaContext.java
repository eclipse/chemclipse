/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDefaultPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaResultsVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class PcaContext {

	private Map<ISamples<? extends IVariable, ? extends ISample>, PreprocessingSettings> preprocessings = new HashMap<>();
	private Map<ISamples<? extends IVariable, ? extends ISample>, IAnalysisSettings> settings = new HashMap<>();
	private Set<PcaContextListener> listeners = new LinkedHashSet<>();
	// pca selection
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaResultsVisualization> pcaResults = new HashMap<>();
	private AtomicReference<IPcaResultsVisualization> pcaSelection = new AtomicReference<>();
	// sample
	private AtomicReference<ISample> sampleSelection = new AtomicReference<>();
	// variables
	private AtomicReference<IVariable> variableSelection = new AtomicReference<>();
	private List<IVariable> variablesList = new CopyOnWriteArrayList<>();
	// samples
	private ISamplesVisualization<?, ?> visualization;
	private SelectionManagerSamples managerSamples;

	public PcaContext(ISamplesVisualization<?, ?> samplesVisualization, SelectionManagerSamples managerSamples) {
		this.visualization = samplesVisualization;
		this.managerSamples = managerSamples;
		managerSamples.getActualSelectedPcaResults().addListener(new ChangeListener<IPcaResultsVisualization>() {

			@Override
			public void changed(ObservableValue<? extends IPcaResultsVisualization> observable, IPcaResultsVisualization oldValue, IPcaResultsVisualization newValue) {

				setPcaSelection(newValue);
			}
		});
		managerSamples.getSelectionManagerSample().getSelection().addListener(new ListChangeListener<ISample>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample> c) {

				ObservableList<? extends ISample> list = c.getList();
				if(list.isEmpty()) {
					setSampleSelection(null);
				} else {
					setSampleSelection(list.get(0));
				}
			}
		});
	}

	public ISamplesVisualization<?, ?> getSamples() {

		return visualization;
	}

	public SelectionManagerSamples getManagerSamples() {

		return managerSamples;
	}

	public <V extends IVariableVisualization, S extends ISampleVisualization> IPcaResultsVisualization evaluatePca(ISamplesVisualization<V, S> samples, IAnalysisSettings settings, IPcaVisualization pcaVisualization, IProgressMonitor monitor, boolean setSelected) {

		monitor.setTaskName("Evaluation");
		// remove all current result
		synchronized(pcaResults) {
			pcaResults.remove(samples);
		}
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		PcaResults results = null;
		// PcaResults results = pcaEvaluation.process(samples, settings, monitor);
		IPcaResultsVisualization pcaResultsVisualization = new PcaResultsVisualization<>(results, pcaVisualization);
		pcaResultsVisualization.getPcaResultList().forEach(r -> {
			Optional<S> sample = samples.getSampleList().stream().filter(s -> r.getSample() == s).findAny();
			r.copyVisualizationProperties(sample.get());
		});
		monitor.setTaskName("Add samples");
		// addSamplesVisualization(samples);
		synchronized(pcaResults) {
			pcaResults.put(samples, pcaResultsVisualization);
		}
		if(setSelected) {
			// TODO sampleSelection.set(pcaResultsVisualization);
			monitor.setTaskName("Set actual result");
			pcaSelection.set(pcaResultsVisualization);
			setPcaSelection(pcaResultsVisualization);
			// TODO???
			// if(!getSelection().contains(samples)) {
			// monitor.setTaskName("Set actual selection");
			// getSelection().setAll(samples);
			// }
		}
		return pcaResultsVisualization;
	}

	public PreprocessingSettings getPreprocessingSettings(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(preprocessings) {
			if(samples instanceof IDataPreprocessing) {
				IDataPreprocessing dataPreprocessing = (IDataPreprocessing)samples;
				return dataPreprocessing.getPreprocessingSettings();
			} else {
				if(preprocessings.containsKey(samples)) {
					return preprocessings.get(samples);
				} else {
					PreprocessingSettings preprocessingSettings = new PreprocessingSettings();
					preprocessings.put(samples, preprocessingSettings);
					return preprocessingSettings;
				}
			}
		}
	}

	public IAnalysisSettings getPcaSettings(ISamples<? extends IVariable, ? extends ISample> samples) {

		synchronized(settings) {
			if(settings.containsKey(samples)) {
				return settings.get(samples);
			} else {
				IAnalysisSettings analysisSettings;
				if(samples instanceof IDefaultPcaSettings) {
					analysisSettings = ((IDefaultPcaSettings)samples).getDefaultPcaSettings();
				} else {
					analysisSettings = PreferenceSupplier.getPcaSettings();
				}
				IAnalysisSettings s = new AnalysisSettings(analysisSettings);
				settings.put(samples, s);
				return s;
			}
		}
	}

	public List<IVariable> getVariablesList() {

		return Collections.unmodifiableList(variablesList);
	}

	public IVariable getVariableSelection() {

		return variableSelection.get();
	}

	public void setVariableSelection(IVariable variableSelection) {

		if(variableSelection == null || variablesList.contains(variableSelection)) {
			this.variableSelection.set(variableSelection);
			synchronized(listeners) {
				for(PcaContextListener listener : listeners) {
					listener.variableSelectionChanged(variableSelection, this);
				}
			}
		}
	}

	public ISample getSampleSelection() {

		ISample sample = sampleSelection.get();
		if(sample == null) {
			ObservableList<ISample> list = managerSamples.getSelectionManagerSample().getSelection();
			if(list.isEmpty()) {
				return null;
			}
			return list.get(0);
		}
		return sample;
	}

	public void setSampleSelection(ISample sampleSelection) {

		if(sampleSelection == null || visualization.getSampleList().contains(sampleSelection)) {
			this.sampleSelection.set(sampleSelection);
			synchronized(listeners) {
				for(PcaContextListener listener : listeners) {
					listener.sampleSelectionChanged(sampleSelection, this);
				}
			}
		}
	}

	public IPcaResultsVisualization getPcaResultSelection() {

		IPcaResultsVisualization resultsVisualization = pcaSelection.get();
		if(resultsVisualization == null) {
			return managerSamples.getActualSelectedPcaResults().get();
		}
		return resultsVisualization;
	}

	public void setPcaSelection(IPcaResultsVisualization pcaSelection) {

		this.pcaSelection.set(pcaSelection);
		synchronized(listeners) {
			for(PcaContextListener listener : listeners) {
				listener.pcaSelectionChanged(pcaSelection, this);
			}
		}
	}

	public void addListener(PcaContextListener contextListener) {

		synchronized(listeners) {
			listeners.add(contextListener);
			// sample
			contextListener.sampleSelectionChanged(getSampleSelection(), this);
			// variables
			contextListener.variablesHasBeenUpdated(getVariablesList(), this);
			contextListener.variableSelectionChanged(variableSelection.get(), this);
			// pca result
			contextListener.pcaSelectionChanged(getPcaResultSelection(), this);
		}
	}

	public void removeListener(PcaContextListener contextListener) {

		synchronized(listeners) {
			listeners.remove(contextListener);
		}
		// sample
		contextListener.sampleSelectionChanged(null, this);
		// variables
		contextListener.variablesHasBeenUpdated(Collections.emptyList(), this);
		contextListener.variableSelectionChanged(null, this);
		// pca result
		contextListener.pcaSelectionChanged(null, this);
	}
}
