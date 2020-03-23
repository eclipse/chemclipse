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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IPreprocessingSettings;

public interface IAnalysisSettings {

	void setNumberOfPrincipalComponents(int numberOfPrincipalComponents);

	int getNumberOfPrincipalComponents();

	Algorithm getAlgorithm();

	void setAlgorithm(Algorithm algorithm);

	boolean isRemoveUselessVariables();

	void setRemoveUselessVariables(boolean removeUselessVariables);

	void setFilterSettings(IFilterSettings filterSettings);

	IFilterSettings getFilterSettings();

	void setPreprocessingSettings(IPreprocessingSettings preprocessingSettings);

	IPreprocessingSettings getPreprocessingSettings();
}
