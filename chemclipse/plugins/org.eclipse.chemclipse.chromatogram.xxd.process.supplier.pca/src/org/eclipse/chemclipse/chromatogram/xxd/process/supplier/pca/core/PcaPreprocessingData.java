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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MeanValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class PcaPreprocessingData implements IDataModification {

	private ObjectProperty<ICentering> centeringScaling;
	private ObjectProperty<INormalization> normalization;
	private boolean onlySelected;
	private ObjectProperty<ITransformation> transformation;
	private ObjectProperty<IPreprocessing> replaceEmptyValues;
	private boolean removeUselessVariables;
	private Map<Consumer<IPreprocessing>, Boolean> listeners = new ConcurrentHashMap<>();

	public PcaPreprocessingData() {

		onlySelected = true;
		removeUselessVariables = true;
		centeringScaling = new SimpleObjectProperty<>();
		centeringScaling.addListener(new ChangeListener<ICentering>() {

			@Override
			public void changed(ObservableValue<? extends ICentering> observable, ICentering oldValue, ICentering newValue) {

				for(Consumer<IPreprocessing> listener : listeners.keySet()) {
					listener.accept(newValue);
				}
			}
		});
		normalization = new SimpleObjectProperty<>();
		normalization.addListener(new ChangeListener<INormalization>() {

			@Override
			public void changed(ObservableValue<? extends INormalization> observable, INormalization oldValue, INormalization newValue) {

				for(Consumer<IPreprocessing> listener : listeners.keySet()) {
					listener.accept(newValue);
				}
			}
		});
		transformation = new SimpleObjectProperty<>();
		transformation.addListener(new ChangeListener<ITransformation>() {

			@Override
			public void changed(ObservableValue<? extends ITransformation> observable, ITransformation oldValue, ITransformation newValue) {

				for(Consumer<IPreprocessing> listener : listeners.keySet()) {
					listener.accept(newValue);
				}
			}
		});
		replaceEmptyValues = new SimpleObjectProperty<>(new MeanValuesReplacer());
		replaceEmptyValues.addListener(new ChangeListener<IPreprocessing>() {

			@Override
			public void changed(ObservableValue<? extends IPreprocessing> observable, IPreprocessing oldValue, IPreprocessing newValue) {

				for(Consumer<IPreprocessing> listener : listeners.keySet()) {
					listener.accept(newValue);
				}
			}
		});
	}

	@Override
	public boolean availableModification() {

		return normalization != null || transformation != null || centeringScaling != null;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples, IProgressMonitor monitor) {

		for(ISample sample : samples.getSampleList()) {
			sample.getSampleData().stream().forEach(d -> {
				double data = d.getData();
				d.setModifiedData(data);
			});
		}
		if(normalization.getValue() != null) {
			normalization.getValue().setOnlySelected(onlySelected);
			normalization.getValue().process(samples);
		}
		replaceEmptyValues.getValue().setOnlySelected(onlySelected);
		replaceEmptyValues.getValue().process(samples);
		if(transformation.getValue() != null) {
			transformation.getValue().setOnlySelected(onlySelected);
			transformation.getValue().process(samples);
		}
		if(centeringScaling.getValue() != null) {
			centeringScaling.getValue().setOnlySelected(onlySelected);
			centeringScaling.getValue().process(samples);
		}
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	public void setRemoveUselessVariables(boolean removeUselessVariables) {

		this.removeUselessVariables = removeUselessVariables;
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

	public ObjectProperty<IPreprocessing> replaceEmptyValuesProperty() {

		return this.replaceEmptyValues;
	}

	public IPreprocessing getReplaceEmptyValues() {

		return this.replaceEmptyValuesProperty().get();
	}

	public void setReplaceEmptyValues(final IPreprocessing replaceEmptyValues) {

		this.replaceEmptyValuesProperty().set(replaceEmptyValues);
	}

	public void addListener(Consumer<IPreprocessing> listener) {

		listeners.put(listener, true);
	}

	public void removeListener(Consumer<IPreprocessing> listener) {

		listeners.remove(listener);
	}
}
