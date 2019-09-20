/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - config class support
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;

public interface IMassSpectrumFilterSupplier {

	/**
	 * The id of the extension point: e.g.
	 * (org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backgroundRemover)
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
	 * The filter name that can be shown in a list box dialogue.
	 * 
	 * @return String
	 */
	String getFilterName();

	/**
	 * 
	 * @return the config class for this supplier
	 */
	Class<? extends IMassSpectrumFilterSettings> getConfigClass();
}
