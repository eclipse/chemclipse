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

public interface IPcaSettings {

	String PCA_ALGO_SVD = "SVD";
	String PCA_ALGO_NIPALS = "Nipals";
	String OPLS_ALGO_NIPALS = "OPLS";

	void setNumberOfPrincipalComponents(int numberOfPrincipalComponents);

	int getNumberOfPrincipalComponents();

	String getPcaAlgorithm();

	void setPcaAlgorithm(String pcaAlgo);

	boolean isRemoveUselessVariables();

	void setRemoveUselessVariables(boolean b);

	IPcaSettings makeDeepCopy();
}
