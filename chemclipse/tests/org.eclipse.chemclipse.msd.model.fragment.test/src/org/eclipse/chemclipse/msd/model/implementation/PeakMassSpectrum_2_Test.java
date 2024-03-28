/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import junit.framework.TestCase;

public class PeakMassSpectrum_2_Test extends TestCase {

	@Test
	public void testNullPointer() {

		assertThrows(IllegalArgumentException.class, () -> new PeakMassSpectrum(null));
	}
}
