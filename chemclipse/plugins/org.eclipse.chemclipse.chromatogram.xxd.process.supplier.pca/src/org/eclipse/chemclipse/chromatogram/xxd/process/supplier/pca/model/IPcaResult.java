/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeaks;

public interface IPcaResult {

	double[] getEigenSpace();

	double getErrorMemberShip();

	IPeaks getPeaks();

	double[] getSampleData();

	ISlopes getSlopes();

	void setEigenSpace(double[] eigenSpace);

	void setErrorMemberShip(double errorMemberShip);

	void setPeaks(IPeaks peaks);

	void setSampleData(double[] sampleData);

	void setSlopes(ISlopes slopes);
}