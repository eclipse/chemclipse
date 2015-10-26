/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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

	public abstract IPeaks getPeaks();

	public abstract void setPeaks(IPeaks peaks);

	public abstract double[] getSampleData();

	public abstract void setSampleData(double[] sampleData);

	public abstract double[] getEigenSpace();

	public abstract void setEigenSpace(double[] eigenSpace);

	public abstract double getErrorMemberShip();

	public abstract void setErrorMemberShip(double errorMemberShip);
}