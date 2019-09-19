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
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaContext {

	public static final PcaContext GLOBAL = new PcaContext();
	private Map<ISamples<? extends IVariable, ? extends ISample>, PcaPreprocessingData> preprocessings = new HashMap<>();
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaSettingsVisualization> settings = new HashMap<>();
	private Set<PcaContextListener> listeners = new LinkedHashSet<>();
	// pca selection
	private Map<ISamples<? extends IVariable, ? extends ISample>, IPcaResultsVisualization> pcaResults = new HashMap<>();
	private AtomicReference<IPcaResultsVisualization> pcaSelection = new AtomicReference<>();
	// samples
	private List<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesList = new CopyOnWriteArrayList<>();
	private AtomicReference<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesSelection = new AtomicReference<>();
	// sample
	private List<ISample> sampleList = new CopyOnWriteArrayList<>();
	private AtomicReference<ISample> sampleSelection = new AtomicReference<>();
	// variables
	private AtomicReference<IVariable> variableSelection = new AtomicReference<>();
	private List<IVariable> variablesList = new CopyOnWriteArrayList<>();

	public PcaContext() {
	}

	public <V extends IVariableVisualization, S extends ISampleVisualization> IPcaResultsVisualization evaluatePca(ISamplesVisualization<V, S> samples, IPcaSettings settings, IPcaVisualization pcaVisualization, IProgressMonitor monitor, boolean setSelected) {

		monitor.setTaskName("Evaluation");
		// remove all current result
		synchronized(pcaResults) {
			pcaResults.remove(samples);
		}
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		PcaResults results = pcaEvaluation.process(samples, settings, monitor);
		IPcaResultsVisualization pcaResultsVisualization = new PcaResultsVisualization<>(results, pcaVisualization);
		pcaResultsVisualization.getPcaResultList().forEach(r -> {
			Optional<S> sample = samples.getSampleList().stream().filter(s -> r.getSample() == s).findAny();
			r.copyVisualizationProperties(sample.get());
		});
		monitor.setTaskName("Add samples");
		addSamplesVisualization(samples);
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

	public List<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> getSamplesList() {

		return Collections.unmodifiableList(samplesList);
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

	public ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> getSamplesSelection() {

		return samplesSelection.get();
	}

	public void setSamplesSelection(ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samplesSelection) {

		if(samplesSelection == null || samplesList.contains(samplesSelection)) {
			this.samplesSelection.set(samplesSelection);
			synchronized(listeners) {
				for(PcaContextListener listener : listeners) {
					listener.samplesSelectionChanged(samplesSelection, this);
				}
			}
		}
	}

	public ISample getSampleSelection() {

		return sampleSelection.get();
	}

	public void setSampleSelection(ISample sampleSelection) {

		if(sampleSelection == null || sampleList.contains(sampleSelection)) {
			this.sampleSelection.set(sampleSelection);
			synchronized(listeners) {
				for(PcaContextListener listener : listeners) {
					listener.sampleSelectionChanged(sampleSelection, this);
				}
			}
		}
	}

	public IPcaResultsVisualization getPcaSelection() {

		return pcaSelection.get();
	}

	public void setPcaSelection(IPcaResultsVisualization pcaSelection) {

		this.pcaSelection.set(pcaSelection);
		synchronized(listeners) {
			for(PcaContextListener listener : listeners) {
				listener.pcaSelectionChanged(pcaSelection, this);
			}
		}
	}

	public void addSamplesVisualization(ISamplesVisualization<? extends IVariableVisualization, ? extends ISample> s) {

		if(!samplesList.contains(s)) {
			samplesList.add(s);
			synchronized(listeners) {
				List<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> list = getSamplesList();
				for(PcaContextListener listener : listeners) {
					listener.samplesHasBeenUpdated(list, this);
				}
			}
		}
	}

	public void addListener(PcaContextListener contextListener) {

		synchronized(listeners) {
			listeners.add(contextListener);
			// samples
			contextListener.samplesHasBeenUpdated(getSamplesList(), this);
			contextListener.samplesSelectionChanged(getSamplesSelection(), this);
			// sample
			contextListener.sampleSelectionChanged(getSampleSelection(), this);
			// variables
			contextListener.variablesHasBeenUpdated(getVariablesList(), this);
			contextListener.variableSelectionChanged(variableSelection.get(), this);
			// pca result
			contextListener.pcaSelectionChanged(getPcaSelection(), this);
		}
	}

	public void removeListener(PcaContextListener contextListener) {

		synchronized(listeners) {
			listeners.remove(contextListener);
		}
		// samples
		contextListener.samplesHasBeenUpdated(Collections.emptyList(), this);
		contextListener.samplesSelectionChanged(null, this);
		// sample
		contextListener.sampleSelectionChanged(null, this);
		// variables
		contextListener.variablesHasBeenUpdated(Collections.emptyList(), this);
		contextListener.variableSelectionChanged(null, this);
		// pca result
		contextListener.pcaSelectionChanged(null, this);
	}
}
