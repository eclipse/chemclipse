/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.List;

public interface IChromatogramIntegrationSupport {

	/**
	 * Returns the integrator description.
	 * 
	 * @return String
	 */
	String getIntegratorDescription();

	/**
	 * Sets the integrator description.
	 * 
	 * @param integratorDescription
	 */
	void setIntegratorDescription(String integratorDescription);

	/**
	 * Returns the integrated area of the chromatogram.
	 * 
	 * @return double
	 */
	double getChromatogramIntegratedArea();

	/**
	 * Returns the integrated area of the background.
	 * 
	 * @return double
	 */
	double getBackgroundIntegratedArea();

	/**
	 * Returns the integrated area of the peaks.
	 * 
	 * @return double
	 */
	double getPeakIntegratedArea();

	/**
	 * Sets the integration results.
	 * 
	 * @param integratorDescription
	 * @param integrationEntries
	 */
	void setIntegratedArea(List<IIntegrationEntry> chromatogramIntegrationEntries, List<IIntegrationEntry> backgroundIntegrationEntries, String integratorDescription);

	/**
	 * Returns the list of integration entries for the chromatographic area.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	List<IIntegrationEntry> getChromatogramIntegrationEntries();

	/**
	 * Returns the list of integration entries for the background area.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	List<IIntegrationEntry> getBackgroundIntegrationEntries();

	/**
	 * Removes all background integration entries.
	 */
	void removeAllBackgroundIntegrationEntries();

	/**
	 * Removes all chromatogram integration entries.
	 */
	void removeAllChromatogramIntegrationEntries();
}
