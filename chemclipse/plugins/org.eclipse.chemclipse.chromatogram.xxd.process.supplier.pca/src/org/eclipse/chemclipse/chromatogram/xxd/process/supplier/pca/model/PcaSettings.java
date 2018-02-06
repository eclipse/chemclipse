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

	public int numberOfPrincipalComponents;

	public PcaSettings() {
		this.numberOfPrincipalComponents = 3;
	}

	public PcaSettings(int numberOfPrincipalComponents) {
		this();
		this.numberOfPrincipalComponents = numberOfPrincipalComponents;
	}

	@Override
	public int getNumberOfPrincipalComponents() {

		return numberOfPrincipalComponents;
	}
}
