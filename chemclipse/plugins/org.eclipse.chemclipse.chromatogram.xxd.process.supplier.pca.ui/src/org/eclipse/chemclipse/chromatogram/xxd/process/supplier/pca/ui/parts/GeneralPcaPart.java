/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.function.Consumer;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;

import javafx.collections.ListChangeListener;

public abstract class GeneralPcaPart {

	private ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> actualSamples;
	private IAnalysisSettings analysisSettings = new AnalysisSettings();
	private ListChangeListener<ISample> sampleChangeListener = e -> samplesHasBeenUpdated();;
	private ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesChangeListener = e -> settingsHasBeenChanged();;
	private ListChangeListener<IVariableVisualization> variableChangeListener = e -> variablesHasBeenUpdated();;
	private Consumer<IAnalysisSettings> updateSettings = e -> settingsHasBeenChanged();
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;

	protected void initializeHandler() {

		samplesChangeListener = new ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> c) {

				if(actualSamples != null) {
					actualSamples.getSampleList().removeListener(sampleChangeListener);
					actualSamples.getVariables().removeListener(variableChangeListener);
					// analysisSettings.removeListener(updateSettings);
				}
				if(!c.getList().isEmpty()) {
					ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = c.getList().get(0);
					actualSamples = samples;
					analysisSettings = getSelectionManagerSamples().getPcaSettings(samples);
					// analysisSettings.addListener(updateSettings);
					actualSamples.getVariables().addListener(variableChangeListener);
					samples.getSampleList().addListener(sampleChangeListener);
				} else {
					analysisSettings = null;
					actualSamples = null;
				}
				samplesHasBeenSet();
			}
		};
		sampleChangeListener = new ListChangeListener<ISample>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISample> c) {

				if(!c.getList().isEmpty()) {
					samplesHasBeenUpdated();
				}
			}
		};
		variableChangeListener = new ListChangeListener<IVariableVisualization>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends IVariableVisualization> c) {

				variablesHasBeenUpdated();
			}
		};
		getSelectionManagerSamples().getSelection().addListener(samplesChangeListener);
		if(!getSelectionManagerSamples().getSelection().isEmpty()) {
			actualSamples = getSelectionManagerSamples().getSelection().get(0);
			actualSamples.getSampleList().addListener(sampleChangeListener);
			actualSamples.getVariables().addListener(variableChangeListener);
			analysisSettings = getSelectionManagerSamples().getPcaSettings(actualSamples);
			// analysisSettings.addListener(updateSettings);
			samplesHasBeenSet();
		}
	}

	protected SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}

	public ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> getSamples() {

		return actualSamples;
	}

	public IAnalysisSettings getPcaSettings() {

		return analysisSettings;
	}

	@PreDestroy
	private void preDestroy() {

		getSelectionManagerSamples().getSelection().removeListener(samplesChangeListener);
		if(actualSamples != null) {
			actualSamples.getSampleList().removeListener(sampleChangeListener);
			actualSamples.getVariables().removeListener(variableChangeListener);
			// analysisSettings.removeListener(updateSettings);
			actualSamples = null;
			analysisSettings = null;
		}
	}

	protected abstract void variablesHasBeenUpdated();

	protected abstract void samplesHasBeenUpdated();

	protected abstract void samplesHasBeenSet();

	protected abstract void settingsHasBeenChanged();
}
