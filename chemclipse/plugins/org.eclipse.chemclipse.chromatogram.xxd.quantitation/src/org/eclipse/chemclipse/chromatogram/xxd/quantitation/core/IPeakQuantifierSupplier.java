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

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.settings.IPeakQuantifierSettings;

public interface IPeakQuantifierSupplier {

	/**
	 * The id of the extension point: e.g.
	 * (org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse)
	 * 
	 * @return String
	 */
	String getId();

	/**
	 * A short description of the functionality of the extension point.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * The name that will be displayed in a list box dialogue.
	 * 
	 * @return String
	 */
	String getPeakQuantifierName();

	/**
	 * TODO: either returns a bean-like class or with annotations ..., with a public default constructor, ... or returns <code>null</code> if no filter settings are associated
	 * 
	 * @return setting class or null, default method return null
	 */
	default Class<? extends IPeakQuantifierSettings> getSettingsClass() {

		return null;
	}
}
