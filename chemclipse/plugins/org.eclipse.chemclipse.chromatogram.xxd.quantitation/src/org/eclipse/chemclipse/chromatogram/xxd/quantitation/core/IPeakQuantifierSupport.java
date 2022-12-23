/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.exceptions.NoPeakQuantifierAvailableException;

public interface IPeakQuantifierSupport {

	/**
	 * Returns the id of the selected quantifier name.<br/>
	 * The id of the selected quantifier is used to determine which quantifier should
	 * be used<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoPeakQuantifierAvailableException
	 */
	String getPeakQuantifierId(int index) throws NoPeakQuantifierAvailableException;

	/**
	 * Returns an IPeakQuantifierSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param quantifierId
	 * @return {@link IPeakQuantifierSupplier}
	 * @throws NoPeakQuantifierAvailableException
	 */
	IPeakQuantifierSupplier getPeakQuantifierSupplier(String quantifierId) throws NoPeakQuantifierAvailableException;

	/**
	 * Returns an ArrayList with all available quantifier ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoPeakQuantifierAvailableException
	 */
	List<String> getAvailablePeakQuantifierIds() throws NoPeakQuantifierAvailableException;

	/**
	 * Returns the list of available quantifier names.
	 * 
	 * @return String[]
	 * @throws NoPeakQuantifierAvailableException
	 */
	String[] getPeakQuantifierNames() throws NoPeakQuantifierAvailableException;
}
