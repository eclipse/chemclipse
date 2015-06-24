/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
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

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	private IBackfoldingSettings backfoldingSettings;

	public SupplierFilterSettings() {

		backfoldingSettings = new BackfoldingSettings();
	}

	// ------------------------------------------------------IBackfoldingSettings
	@Override
	public IBackfoldingSettings getBackfoldingSettings() {

		return backfoldingSettings;
	}
	// ------------------------------------------------------IBackfoldingSettings
}
