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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class PcaSettingsVisualization implements IPcaSettingsVisualization {

	private StringProperty pcaAlgorithm = new SimpleStringProperty();
	private IntegerProperty numberOfPrincipalComponents = new SimpleIntegerProperty();
	private BooleanProperty removeUselessVariables = new SimpleBooleanProperty();
	private Map<Consumer<IPcaSettingsVisualization>, Boolean> listeners = new ConcurrentHashMap<>();

	public PcaSettingsVisualization() {

		this.pcaAlgorithm.addListener((ChangeListener<String>)(observable, oldValue, newValue) -> updateLister());
		this.numberOfPrincipalComponents.addListener((ChangeListener<Number>)(observable, oldValue, newValue) -> updateLister());
		this.removeUselessVariables.addListener((ChangeListener<Boolean>)(observable, oldValue, newValue) -> updateLister());
	}

	public PcaSettingsVisualization(IPcaSettings pcaSettings) {

		this();
		this.pcaAlgorithm.set(pcaSettings.getPcaAlgorithm());
		this.numberOfPrincipalComponents.set(pcaSettings.getNumberOfPrincipalComponents());
		this.removeUselessVariables.set(pcaSettings.isRemoveUselessVariables());
	}

	@Override
	public void setNumberOfPrincipalComponents(int numberOfPrincipalComponents) {

		this.numberOfPrincipalComponents.set(numberOfPrincipalComponents);
	}

	@Override
	public int getNumberOfPrincipalComponents() {

		return numberOfPrincipalComponents.get();
	}

	@Override
	public String getPcaAlgorithm() {

		return pcaAlgorithm.get();
	}

	@Override
	public void setPcaAlgorithm(String pcaAlgo) {

		pcaAlgorithm.set(pcaAlgo);
	}

	@Override
	public boolean isRemoveUselessVariables() {

		return removeUselessVariables.get();
	}

	@Override
	public void setRemoveUselessVariables(boolean b) {

		removeUselessVariables.set(b);
	}

	@Override
	public IPcaSettingsVisualization makeDeepCopy() {

		PcaSettingsVisualization pcaSettingsVisualization = new PcaSettingsVisualization();
		pcaSettingsVisualization.setNumberOfPrincipalComponents(numberOfPrincipalComponents.get());
		pcaSettingsVisualization.setPcaAlgorithm(pcaAlgorithm.get());
		pcaSettingsVisualization.setRemoveUselessVariables(removeUselessVariables.get());
		return pcaSettingsVisualization;
	}

	@Override
	public void removeListener(Consumer<IPcaSettingsVisualization> changeSettingsListener) {

		listeners.remove(changeSettingsListener);
	}

	@Override
	public void addListener(Consumer<IPcaSettingsVisualization> changeSettingsListener) {

		listeners.put(changeSettingsListener, true);
	}

	@Override
	public IntegerProperty numberOfPrincipalComponentsProperty() {

		return numberOfPrincipalComponents;
	}

	@Override
	public StringProperty pcaAlgorithmProperty() {

		return pcaAlgorithm;
	}

	@Override
	public BooleanProperty removeUselessVariablesProperty() {

		return removeUselessVariables;
	}

	protected void updateLister() {

		synchronized(listeners) {
			for(Consumer<IPcaSettingsVisualization> listener : listeners.keySet()) {
				listener.accept(this);
			}
		}
	}
}
