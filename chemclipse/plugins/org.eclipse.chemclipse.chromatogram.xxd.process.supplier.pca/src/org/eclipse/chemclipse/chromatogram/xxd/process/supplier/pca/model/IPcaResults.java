/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

public interface IPcaResults {

	List<double[]> getBasisVectors();

	List<IRetentionTime> getExtractedRetentionTimes();

	int getNumberOfPrincipleComponents();

	List<IPcaResult> getPcaResultGroupsList();

	List<IPcaResult> getPcaResultList();

	void setBasisVectors(List<double[]> basisVectors);

	void setExtractedRetentionTimes(List<IRetentionTime> extractedRetentionTimes);

	void setNumberOfPrincipleComponents(int numberOfPrincipleComponents);

	void setPcaResultGroupsList(List<IPcaResult> pcaResultGroupsList);

	void setPcaResultList(List<IPcaResult> pcaResultList);
}