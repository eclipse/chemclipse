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
package org.eclipse.chemclipse.model.identifier.core;

import java.util.List;

import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;

public interface ISupport {

	/**
	 * Returns the filter names which are actually registered at the
	 * chromatogram converter extension point.<br/>
	 * The filter names are the specific chromatogram file names to be displayed
	 * for example in the SWT FileDialog. Agilent has for example an filter name
	 * "Agilent Chromatogram (.D)".
	 * 
	 * @return String[]
	 * @throws NoIdentifierAvailableException
	 */
	String[] getIdentifierNames() throws NoIdentifierAvailableException;

	/**
	 * Returns the id of the selected identifier index.<br/>
	 * The id of the selected filter is used to determine which identifier
	 * should be used.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoIdentifierAvailableException
	 */
	String getIdentifierId(int index) throws NoIdentifierAvailableException;

	/**
	 * Returns an ArrayList with all available identifier ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoIdentifierAvailableException
	 */
	List<String> getAvailableIdentifierIds() throws NoIdentifierAvailableException;

	/**
	 * Returns an ISupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param filterId
	 * @return {@link IChromatogramFilterSupplier}
	 * @throws NoIdentifierAvailableException
	 */
	ISupplier getIdentifierSupplier(String identifierId) throws NoIdentifierAvailableException;
}
