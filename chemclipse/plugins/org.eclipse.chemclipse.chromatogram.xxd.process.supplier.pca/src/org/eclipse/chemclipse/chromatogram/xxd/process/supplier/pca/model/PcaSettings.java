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
	public String pcaAlgorithm;
	private boolean removeUselessVariables;

	public PcaSettings() {

	}

	@Override
	public int getNumberOfPrincipalComponents() {

		return numberOfPrincipalComponents;
	}

	@Override
	public void setNumberOfPrincipalComponents(int numberOfPrincipalComponents) {

		this.numberOfPrincipalComponents = numberOfPrincipalComponents;
	}

	@Override
	public String getPcaAlgorithm() {

		return this.pcaAlgorithm;
	}

	@Override
	public void setPcaAlgorithm(String pcaAlgo) {

		this.pcaAlgorithm = pcaAlgo;
	}

	@Override
	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	@Override
	public void setRemoveUselessVariables(boolean b) {

		removeUselessVariables = b;
	}

	@Override
	public PcaSettings makeDeepCopy() {

		PcaSettings pcaSettings = new PcaSettings();
		pcaSettings.setNumberOfPrincipalComponents(this.numberOfPrincipalComponents);
		pcaSettings.setPcaAlgorithm(this.pcaAlgorithm);
		pcaSettings.setRemoveUselessVariables(this.removeUselessVariables);
		return pcaSettings;
	}
}
