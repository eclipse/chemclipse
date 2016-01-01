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

import junit.framework.TestCase;

public class BackfoldingFilterSettings_1_Test extends TestCase {

	private ISupplierFilterSettings settings;
	private IBackfoldingSettings backfoldingSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new SupplierFilterSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		settings = null;
		backfoldingSettings = null;
		super.tearDown();
	}

	public void testGetBackfoldingSettings_1() {

		backfoldingSettings = settings.getBackfoldingSettings();
		assertNotNull(backfoldingSettings);
	}
}
