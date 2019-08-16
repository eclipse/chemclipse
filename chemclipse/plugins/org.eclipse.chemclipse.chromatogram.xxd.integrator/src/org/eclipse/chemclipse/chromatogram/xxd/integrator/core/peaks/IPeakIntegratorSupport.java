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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;

/**
 * @author eselmeister
 */
public interface IPeakIntegratorSupport {

	/**
	 * Returns the id of the selected integrator name.<br/>
	 * The id of the selected integrator is used to determine which integrator
	 * should be used to calculate the integration results of the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoIntegratorAvailableException
	 */
	String getIntegratorId(int index) throws NoIntegratorAvailableException;

	/**
	 * Returns an IPeakIntegratorSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param integratorId
	 * @return {@link IPeakIntegratorSupplier}
	 * @throws NoIntegratorAvailableException
	 */
	IPeakIntegratorSupplier getIntegratorSupplier(String integratorId) throws NoIntegratorAvailableException;

	/**
	 * Returns an ArrayList with all available peak integrator ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoIntegratorAvailableException
	 */
	List<String> getAvailableIntegratorIds() throws NoIntegratorAvailableException;

	/**
	 * Returns the list of available peak integrator names.
	 * 
	 * @return String[]
	 * @throws NoIntegratorAvailableException
	 */
	String[] getIntegratorNames() throws NoIntegratorAvailableException;
}
