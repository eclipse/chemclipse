/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.statistics.ISample;

public interface IResultPCA {

	double[] getScoreVector();

	double getErrorMemberShip();

	String getGroupName();

	String getName();

	ISample getSample();

	double[] getSampleData();

	boolean isDisplayed();

	void setDisplayed(boolean displayed);

	void setScoreVector(double[] eigenSpace);

	void setErrorMemberShip(double errorMemberShip);

	void setGroupName(String groupName);

	void setName(String name);

	void setSampleData(double[] sampleData);

	/**
	 * For example red: 255,0,0
	 * The color will be mapped to an SWT color in the UI.
	 * This is the model bundle, hence no UI related code is allowed.
	 * 
	 * @return String
	 */
	String getRGB();

	/**
	 * For example red: 255,0,0
	 * 
	 * @param rgb
	 */
	void setRGB(String rgb);
}