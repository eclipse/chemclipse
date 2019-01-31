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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IDataModificator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MeanValuesReplacer;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PcaPreprocessingData implements IDataModification {

	private ObjectProperty<ICentering> centeringScaling;
	private ObjectProperty<INormalization> normalization;
	private ObjectProperty<ITransformation> transformation;
	private ObjectProperty<IDataModificator> replaceEmptyValues;
	private BooleanProperty modifyOnlySelectedVariable;
	private BooleanProperty onlySelected;
	private BooleanProperty removeUselessVariables;
	private Map<Consumer<PcaPreprocessingData>, Boolean> listeners = new ConcurrentHashMap<>();

	public PcaPreprocessingData() {

		centeringScaling = new SimpleObjectProperty<>();
		centeringScaling.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		normalization = new SimpleObjectProperty<>();
		normalization.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		transformation = new SimpleObjectProperty<>();
		transformation.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		replaceEmptyValues = new SimpleObjectProperty<>(new MeanValuesReplacer());
		replaceEmptyValues.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		onlySelected = new SimpleBooleanProperty(true);
		onlySelected.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		modifyOnlySelectedVariable = new SimpleBooleanProperty(false);
		modifyOnlySelectedVariable.addListener((observable, oldValue, newValue) -> objectUpdate());
		//
		removeUselessVariables = new SimpleBooleanProperty(true);
		removeUselessVariables.addListener((observable, oldValue, newValue) -> objectUpdate());
	}

	private void objectUpdate() {

		synchronized(listeners) {
			for(Consumer<PcaPreprocessingData> listener : listeners.keySet()) {
				listener.accept(PcaPreprocessingData.this);
			}
		}
	}

	@Override
	public boolean availableModification() {

		return normalization != null || transformation != null || centeringScaling != null;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected.get();
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples, IProgressMonitor monitor) {

		for(ISample sample : samples.getSampleList()) {
			sample.getSampleData().stream().forEach(d -> {
				double data = d.getData();
				d.setModifiedData(data);
			});
		}
		if(normalization.get() != null) {
			normalization.get().setOnlySelected(onlySelected.get());
			normalization.get().setRemoveUselessVariables(removeUselessVariables.get());
			normalization.get().setModifyOnlySelectedVariable(modifyOnlySelectedVariable.get());
			normalization.get().process(samples);
		}
		replaceEmptyValues.get().setOnlySelected(onlySelected.get());
		replaceEmptyValues.get().setRemoveUselessVariables(removeUselessVariables.get());
		replaceEmptyValues.get().setModifyOnlySelectedVariable(modifyOnlySelectedVariable.get());
		replaceEmptyValues.get().process(samples);
		if(transformation.get() != null) {
			transformation.get().setOnlySelected(onlySelected.get());
			transformation.get().setRemoveUselessVariables(removeUselessVariables.get());
			transformation.get().setModifyOnlySelectedVariable(modifyOnlySelectedVariable.get());
			transformation.get().process(samples);
		}
		if(centeringScaling.get() != null) {
			centeringScaling.get().setOnlySelected(onlySelected.get());
			centeringScaling.get().setRemoveUselessVariables(removeUselessVariables.get());
			centeringScaling.get().setModifyOnlySelectedVariable(modifyOnlySelectedVariable.get());
			centeringScaling.get().process(samples);
		}
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected.set(onlySelected);
	}

	public BooleanProperty onlySelectedPropery() {

		return onlySelected;
	}

	public ObjectProperty<ICentering> centeringScalingProperty() {

		return this.centeringScaling;
	}

	public ICentering getCenteringScaling() {

		return this.centeringScalingProperty().get();
	}

	public void setCenteringScaling(final ICentering centeringScaling) {

		this.centeringScalingProperty().set(centeringScaling);
	}

	public ObjectProperty<INormalization> normalizationProperty() {

		return this.normalization;
	}

	public INormalization getNormalization() {

		return this.normalizationProperty().get();
	}

	public void setNormalization(final INormalization normalization) {

		this.normalizationProperty().set(normalization);
	}

	public ObjectProperty<ITransformation> transformationProperty() {

		return this.transformation;
	}

	public ITransformation getTransformation() {

		return this.transformationProperty().get();
	}

	public void setTransformation(final ITransformation transformation) {

		this.transformationProperty().set(transformation);
	}

	public ObjectProperty<IDataModificator> replaceEmptyValuesProperty() {

		return this.replaceEmptyValues;
	}

	public IDataModificator getReplaceEmptyValues() {

		return this.replaceEmptyValuesProperty().get();
	}

	public void setReplaceEmptyValues(final IDataModificator replaceEmptyValues) {

		this.replaceEmptyValuesProperty().set(replaceEmptyValues);
	}

	public void addListener(Consumer<PcaPreprocessingData> listener) {

		listeners.put(listener, true);
	}

	public void removeListener(Consumer<PcaPreprocessingData> listener) {

		listeners.remove(listener);
	}

	public boolean isModifyOnlySelectedVariable() {

		return modifyOnlySelectedVariable.get();
	}

	public void setModifyOnlySelectedVariable(boolean modifyOnlySelectedVariable) {

		this.modifyOnlySelectedVariable.set(modifyOnlySelectedVariable);
	}

	public BooleanProperty modifyOnlySelectedVariable() {

		return modifyOnlySelectedVariable;
	}

	public void setRemoveUselessVariables(boolean removeUselessVariables) {

		this.removeUselessVariables.set(removeUselessVariables);
	}

	public BooleanProperty removeUselessVariablesProperty() {

		return removeUselessVariables;
	}
}
