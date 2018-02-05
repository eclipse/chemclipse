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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public class PcaSettings implements IPcaSettings {

	public int numberOfPrincipleComponents;

	public PcaSettings() {
		this.numberOfPrincipleComponents = 3;
	}

	public PcaSettings(int numberOfPrincipleComponents) {
		this();
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public int getNumberOfPrincipalComponents() {

		return numberOfPrincipleComponents;
	}
}
