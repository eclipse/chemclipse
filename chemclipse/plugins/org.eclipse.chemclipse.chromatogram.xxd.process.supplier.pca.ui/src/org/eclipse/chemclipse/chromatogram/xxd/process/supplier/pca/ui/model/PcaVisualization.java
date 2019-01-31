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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

public class PcaVisualization implements IPcaVisualization {

	private Map<Consumer<IPcaVisualization>, Boolean> listeners = new ConcurrentHashMap<>();
	private IntegerProperty pcX;
	private IntegerProperty pcY;
	private IntegerProperty pcZ;

	public PcaVisualization() {

		this.pcX = new SimpleIntegerProperty(1);
		this.pcY = new SimpleIntegerProperty(2);
		this.pcZ = new SimpleIntegerProperty(3);
		this.pcX.addListener((ChangeListener<Number>)(observable, oldValue, newValue) -> updateLister());
		this.pcY.addListener((ChangeListener<Number>)(observable, oldValue, newValue) -> updateLister());
		this.pcZ.addListener((ChangeListener<Number>)(observable, oldValue, newValue) -> updateLister());
	}

	public PcaVisualization(int pcX, int pcY, int pcZ) {

		this();
		setPcX(pcX);
		setPcY(pcY);
		setPcZ(pcZ);
	}

	@Override
	public void addChangeListener(Consumer<IPcaVisualization> listener) {

		listeners.put(listener, true);
	}

	@Override
	public int getPcX() {

		return this.pcX.get();
	}

	@Override
	public int getPcY() {

		return this.pcY.get();
	}

	@Override
	public int getPcZ() {

		return this.pcZ.get();
	}

	@Override
	public IntegerProperty pcXProperty() {

		return pcX;
	}

	@Override
	public IntegerProperty pcYProperty() {

		return pcY;
	}

	@Override
	public IntegerProperty pcZProperty() {

		return pcZ;
	}

	@Override
	public void removeChangeListener(Consumer<IPcaVisualization> listener) {

		listeners.remove(listener);
	}

	@Override
	public void setPcX(int pcX) {

		this.pcX.set(pcX);
	}

	@Override
	public void setPcY(int pcY) {

		this.pcY.set(pcY);
	}

	@Override
	public void setPcZ(int pcZ) {

		this.pcZ.set(pcZ);
	}

	protected void updateLister() {

		synchronized(listeners) {
			listeners.forEach((e, b) -> {
				e.accept(this);
			});
		}
	}
}
