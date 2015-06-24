/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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
	 * Returns the chromatogram integrator description.
	 * 
	 * @return String
	 */
	String getChromatogramIntegratorDescription();

	/**
	 * Sets the chromatogram integrator description.
	 * 
	 * @param integratorDescription
	 */
	void setChromatogramIntegratorDescription(String chromatogramIntegratorDescription);

	/**
	 * Returns the integrated area of the chromatogram.
	 * 
	 * @return double
	 */
	double getChromatogramIntegratedArea();

	/**
	 * Returns the background integrator description.
	 * 
	 * @return String
	 */
	String getBackgroundIntegratorDescription();

	/**
	 * Sets the background integrator description.
	 * 
	 * @param integratorDescription
	 */
	void setBackgroundIntegratorDescription(String backgroundIntegratorDescription);

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
	 * Sets the integration results for the chromatogram area.
	 * 
	 * @param integratorDescription
	 * @param integrationEntries
	 */
	void setChromatogramIntegratedArea(List<IIntegrationEntry> chromatogramIntegrationEntries, String chromatogramIntegratorDescription);

	/**
	 * Returns the list of integration entries for the chromatographic area.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	List<IIntegrationEntry> getChromatogramIntegrationEntries();

	/**
	 * Sets the integration results for the background area.
	 * 
	 * @param integratorDescription
	 * @param integrationEntries
	 */
	void setBackgroundIntegratedArea(List<IIntegrationEntry> backgroundIntegrationEntries, String backgroundIntegratorDescription);

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
