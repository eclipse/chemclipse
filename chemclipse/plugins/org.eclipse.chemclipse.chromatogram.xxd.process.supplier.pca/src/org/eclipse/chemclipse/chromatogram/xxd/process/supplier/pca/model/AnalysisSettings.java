/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - added a title
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;

public class AnalysisSettings implements IAnalysisSettings {

	private String title = "";
	private int numberOfPrincipalComponents = 3;
	private Algorithm algorithm = Algorithm.NIPALS;
	private boolean removeUselessVariables = true;
	//
	private IPreprocessingSettings preprocessingSettings = new PreprocessingSettings();
	private IFilterSettings filterSettings = new FilterSettings();

	public AnalysisSettings() {

	}

	public AnalysisSettings(IAnalysisSettings analysisSettings) {

		this.numberOfPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
		this.algorithm = analysisSettings.getAlgorithm();
		this.removeUselessVariables = analysisSettings.isRemoveUselessVariables();
		//
		this.preprocessingSettings = new PreprocessingSettings(analysisSettings.getPreprocessingSettings());
		this.filterSettings = new FilterSettings(analysisSettings.getFilterSettings());
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public void setTitle(String title) {

		this.title = title;
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

	public IPreprocessingSettings getPreprocessingSettings() {

		return preprocessingSettings;
	}

	public void setPreprocessingSettings(IPreprocessingSettings preprocessingSettings) {

		this.preprocessingSettings = preprocessingSettings;
	}

	public IFilterSettings getFilterSettings() {

		return filterSettings;
	}

	public void setFilterSettings(IFilterSettings filterSettings) {

		this.filterSettings = filterSettings;
	}
}