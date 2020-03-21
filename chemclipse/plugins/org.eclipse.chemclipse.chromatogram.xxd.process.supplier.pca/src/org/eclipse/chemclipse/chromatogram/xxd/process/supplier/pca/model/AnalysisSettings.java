/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

public class AnalysisSettings implements IAnalysisSettings {

	private int numberOfPrincipalComponents = 3;
	private Algorithm algorithm = Algorithm.NIPALS;
	private boolean removeUselessVariables = true;

	public AnalysisSettings() {
	}

	public AnalysisSettings(IAnalysisSettings analysisSettings) {
		this.numberOfPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
		this.algorithm = analysisSettings.getAlgorithm();
		this.removeUselessVariables = analysisSettings.isRemoveUselessVariables();
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
	public Algorithm getAlgorithm() {

		return this.algorithm;
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {

		this.algorithm = algorithm;
	}

	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	public void setRemoveUselessVariables(boolean removeUselessVariables) {

		this.removeUselessVariables = removeUselessVariables;
	}

	@Override
	public AnalysisSettings makeDeepCopy() {

		AnalysisSettings analysisSettings = new AnalysisSettings();
		analysisSettings.setNumberOfPrincipalComponents(this.numberOfPrincipalComponents);
		analysisSettings.setAlgorithm(this.algorithm);
		analysisSettings.setRemoveUselessVariables(this.removeUselessVariables);
		return analysisSettings;
	}
}
