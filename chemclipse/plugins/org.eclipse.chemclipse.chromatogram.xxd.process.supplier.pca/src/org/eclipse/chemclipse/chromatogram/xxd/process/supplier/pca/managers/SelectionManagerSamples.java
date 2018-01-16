/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.ux.fx.ui.SelectionManagerProto;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

public class SelectionManagerSamples extends SelectionManagerProto<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> {

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

	private ObjectProperty<IPcaResults> actualSelectedPcaResults;
	private Map<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>, IPcaResults> pcaResults;

	private SelectionManagerSamples() {
		super();
		pcaResults = new HashMap<>();
		actualSelectedPcaResults = new SimpleObjectProperty<>();
	}

	public <V extends IVariable, S extends ISample<? extends ISampleData>> IPcaResults evaluatePca(ISamples<V, S> samples, IPcaSettings settings, IProgressMonitor monitor) {

		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		return pcaEvaluation.process(samples, settings, monitor);
	}

	public <V extends IVariable, S extends ISample<? extends ISampleData>> IPcaResults evaluatePca(ISamples<V, S> samples, IPcaSettings settings, IProgressMonitor monitor, boolean setSelected) {

		IPcaResults results = evaluatePca(samples, settings, monitor);
		if(!getElements().contains(samples)) {
			getElements().add(samples);
		}
		synchronized(pcaResults) {
			pcaResults.put(samples, results);
		}
		if(setSelected) {
			actualSelectedPcaResults.setValue(results);
			if(!getSelection().contains(samples)) {
				getSelection().setAll(samples);
			}
		}
		return results;
	}

	public ReadOnlyObjectProperty<IPcaResults> getActualSelectedPcaResults() {

		return actualSelectedPcaResults;
	}

	public Optional<IPcaResults> getPcaResults(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples) {

		synchronized(pcaResults) {
			return Optional.ofNullable(pcaResults.get(samples));
		}
	}
}
