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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.PcaResultsVisualization;
import org.eclipse.chemclipse.ux.fx.ui.SelectionManagerProto;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

public class SelectionManagerSamples extends SelectionManagerProto<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> {

	private static SelectionManagerSamples instance;

	public static SelectionManagerSamples getInstance() {

		synchronized(SelectionManagerSamples.class) {
			if(instance == null) {
				instance = new SelectionManagerSamples();
			}
			instance.getSelection().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> c) {

					SelectionManagerSample.getInstance().getSelection().clear();
					if(!c.getList().isEmpty()) {
						instance.actualSelectedPcaResults.setValue(instance.pcaResults.get(c.getList().get(0)));
					} else {
						instance.actualSelectedPcaResults.setValue(null);
					}
				}
			});
			instance.getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> c) {

					while(c.next()) {
						for(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples : c.getRemoved()) {
							instance.pcaResults.remove(samples);
						}
					}
				}
			});
		}
		return instance;
	}

	private ObjectProperty<IPcaResultsVisualization> actualSelectedPcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>, IPcaResultsVisualization> pcaResults;

	private SelectionManagerSamples() {
		super();
		pcaResults = new HashMap<>();
		actualSelectedPcaResults = new SimpleObjectProperty<>();
	}

	public <V extends IVariableVisualization, S extends ISampleVisualization<? extends ISampleData>> IPcaResultsVisualization evaluatePca(ISamplesVisualization<V, S> samples, IPcaSettings settings, IPcaSettingsVisualization pcaSettingsVisualization, IProgressMonitor monitor, boolean setSelected) {

		monitor.setTaskName("Evaluation");
		PcaResults results = evaluatePca(samples, settings, monitor);
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

	private <V extends IVariableVisualization, S extends ISampleVisualization<? extends ISampleData>> PcaResults evaluatePca(ISamplesVisualization<V, S> samples, IPcaSettings settings, IProgressMonitor monitor) {

		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		return pcaEvaluation.process(samples, settings, monitor);
	}

	public ReadOnlyObjectProperty<IPcaResultsVisualization> getActualSelectedPcaResults() {

		return actualSelectedPcaResults;
	}

	public Optional<IPcaResultsVisualization> getPcaResults(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples) {

		synchronized(pcaResults) {
			return Optional.ofNullable(pcaResults.get(samples));
		}
	}
}
