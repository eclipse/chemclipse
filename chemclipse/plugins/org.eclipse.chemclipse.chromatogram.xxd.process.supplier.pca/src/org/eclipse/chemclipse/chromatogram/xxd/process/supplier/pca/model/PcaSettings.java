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

public class PcaSettings implements IPcaSettings {

	public int numberOfPrincipleComponents;
	private int pcX;
	private int pcY;
	private int pcZ;

	public PcaSettings() {
		this.numberOfPrincipleComponents = 3;
		this.pcX = 1;
		this.pcY = 2;
		this.pcZ = 3;
	}

	public PcaSettings(int numberOfPrincipleComponents) {
		super();
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public int getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	@Override
	public int getPcX() {

		return this.pcX;
	}

	@Override
	public int getPcY() {

		return this.pcY;
	}

	@Override
	public int getPcZ() {

		return this.pcZ;
	}

	@Override
	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public void setPcX(int pcX) {

		this.pcX = pcX;
	}

	@Override
	public void setPcY(int pcY) {

		this.pcY = pcY;
	}

	@Override
	public void setPcZ(int pcZ) {

		this.pcZ = pcZ;
	}
}
