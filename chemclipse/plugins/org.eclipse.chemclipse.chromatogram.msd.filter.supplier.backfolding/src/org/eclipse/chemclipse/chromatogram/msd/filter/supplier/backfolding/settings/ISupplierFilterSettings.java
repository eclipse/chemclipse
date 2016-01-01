/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

/**
 * @author eselmeister
 */
public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	/**
	 * Returns the backfolding settings.
	 * 
	 * @return {@link IBackfoldingSettings}
	 */
	IBackfoldingSettings getBackfoldingSettings();
}
