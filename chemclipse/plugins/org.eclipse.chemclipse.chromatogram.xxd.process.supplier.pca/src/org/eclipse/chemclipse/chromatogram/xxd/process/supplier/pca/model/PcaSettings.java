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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PcaSettings implements IPcaSettings {

	public IntegerProperty numberOfPrincipleComponents;
	private IntegerProperty pcX;
	private IntegerProperty pcY;
	private IntegerProperty pcZ;

	public PcaSettings() {
		this.numberOfPrincipleComponents = new SimpleIntegerProperty(3);
		this.pcX = new SimpleIntegerProperty(1);
		this.pcY = new SimpleIntegerProperty(2);
		this.pcZ = new SimpleIntegerProperty(3);
	}

	public PcaSettings(int numberOfPrincipleComponents) {
		super();
		this.numberOfPrincipleComponents.set(numberOfPrincipleComponents);
	}

	@Override
	public int getNumberOfPrincipalComponents() {

		return numberOfPrincipleComponents.get();
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
	public IntegerProperty numberPrincipalCoponentsPropety() {

		return numberOfPrincipleComponents;
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
	public void setNumberOfPrincipalComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents.set(numberOfPrincipleComponents);
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
}
