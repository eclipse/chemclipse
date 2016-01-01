/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.targets.ITarget;

public interface IIdentificationTarget extends ITarget {

	/**
	 * Returns the library information instance.
	 * 
	 * @return {@link ILibraryInformation}
	 */
	ILibraryInformation getLibraryInformation();

	/**
	 * Returns the comparison result.
	 * 
	 * @return {@link IComparisonResult}
	 */
	IComparisonResult getComparisonResult();

	/**
	 * Get the identifier (e.g. "INCOS Mass Spectrum Identifier").
	 * 
	 * @return String
	 */
	String getIdentifier();

	/**
	 * Set the identifier.
	 * 
	 * @param identifier
	 */
	void setIdentifier(String identifier);
}
