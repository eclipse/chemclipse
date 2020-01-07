/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings;

import junit.framework.TestCase;

public class WncClassifierSettings_1_Test extends TestCase {

	private ClassifierSettings classifierSettings;

	@Override
	protected void setUp() throws Exception {

		classifierSettings = new ClassifierSettings();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		classifierSettings = null;
		super.tearDown();
	}

	public void testGetWncIons_1() {

		assertNotNull(classifierSettings.getWNCIons());
	}
}
