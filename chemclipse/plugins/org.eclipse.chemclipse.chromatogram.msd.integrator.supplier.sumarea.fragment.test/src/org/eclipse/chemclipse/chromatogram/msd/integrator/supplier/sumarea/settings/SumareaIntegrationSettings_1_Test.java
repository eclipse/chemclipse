/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings;

import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;

import junit.framework.TestCase;

public class SumareaIntegrationSettings_1_Test extends TestCase {

	private ISumareaIntegrationSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new SumareaIntegrationSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		settings = null;
		super.tearDown();
	}

	public void testSettings_1() {

		assertEquals(0, settings.getSelectedIons().getIonsNominal().size());
	}

	public void testSettings_2() {

		settings.getSelectedIons().add(new MarkedIon(45.2f));
		settings.getSelectedIons().add(new MarkedIon(28.1f));
		assertEquals(2, settings.getSelectedIons().getIonsNominal().size());
	}

	public void testSettings_3() {

		settings.getSelectedIons().add(new MarkedIon(45.2f));
		settings.getSelectedIons().add(new MarkedIon(28.1f));
		settings.getSelectedIons().remove(new MarkedIon(45.2f));
		assertEquals(1, settings.getSelectedIons().getIonsNominal().size());
	}
}
