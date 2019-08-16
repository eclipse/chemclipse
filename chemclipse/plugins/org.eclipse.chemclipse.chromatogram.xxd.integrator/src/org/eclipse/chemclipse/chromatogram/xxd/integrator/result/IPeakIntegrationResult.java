/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import java.util.Set;

public interface IPeakIntegrationResult {

	double getIntegratedArea();

	void setIntegratedArea(double integratedArea);

	float getTailing();

	void setTailing(float tailing);

	int getWidth();

	void setWidth(int width);

	String getIntegratorType();

	void setIntegratorType(String integratorType);

	String getPeakType();

	void setPeakType(String peakType);

	String getModelDescription();

	void setModelDescription(String modelDescription);

	float getSN();

	void setSN(float sn);

	int getStartRetentionTime();

	void setStartRetentionTime(int startRetentionTime);

	int getStopRetentionTime();

	void setStopRetentionTime(int stopRetentionTime);

	float getPurity();

	void setPurity(float purity);

	/**
	 * Returns the ion(s) which has been integrated as a set.<br/>
	 * If it was TIC (total ion chromatogram), the IIon.TIC_Ion will be
	 * returned.
	 * 
	 * @return int
	 */
	Set<Integer> getIntegratedIons();

	/**
	 * Sets the ion which has been integrated.
	 * 
	 * @param ion
	 */
	void addIntegratedIon(int ion);

	/**
	 * Removes the ion from the actual integrated ion list.
	 * 
	 * @param ion
	 */
	void removeIntegratedIon(int ion);

	/**
	 * Sets the ions which has been integrated.
	 * 
	 * @param List
	 *            <Integer>
	 */
	void addIntegratedIons(Set<Integer> ions);
}
