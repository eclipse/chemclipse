/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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

	ISlopes getSlopes();

	void setSlopes(ISlopes slopes);

	IPeaks getPeaks();

	void setPeaks(IPeaks peaks);

	double[] getSampleData();

	void setSampleData(double[] sampleData);

	double[] getEigenSpace();

	void setEigenSpace(double[] eigenSpace);

	double getErrorMemberShip();

	void setErrorMemberShip(double errorMemberShip);
}