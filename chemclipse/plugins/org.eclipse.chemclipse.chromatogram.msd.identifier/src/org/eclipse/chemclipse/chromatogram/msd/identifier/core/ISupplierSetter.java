/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.core;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettings;

public interface ISupplierSetter extends ISupplier {

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.fsearch".
	 * 
	 * @param id
	 */
	void setId(final String id);

	/**
	 * Sets the description of the identifier supplier.
	 * 
	 * @param description
	 */
	void setDescription(final String description);

	/**
	 * Set the identifier name to be shown a DropDownDialog.
	 * 
	 * @param identifierName
	 */
	void setIdentifierName(final String identifierName);

	/**
	 * TODO: either returns a bean-like class or with annotations ..., with a public default constructor, ... or returns <code>null</code> if no filter settings are associated
	 * 
	 * @return
	 */
	Class<? extends IIdentifierSettings> getIdentifierSettingsClass();
}
