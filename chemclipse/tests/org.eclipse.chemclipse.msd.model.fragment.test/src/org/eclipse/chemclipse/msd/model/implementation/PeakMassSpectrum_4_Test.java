/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

public class PeakMassSpectrum_4_Test extends TestCase {

	public void testGetNumberOfIons_1() {

		try {
			new PeakMassSpectrum(null);
		} catch(IllegalArgumentException e) {
			assertTrue("IllegalArgumentException", true);
		}
	}
}
