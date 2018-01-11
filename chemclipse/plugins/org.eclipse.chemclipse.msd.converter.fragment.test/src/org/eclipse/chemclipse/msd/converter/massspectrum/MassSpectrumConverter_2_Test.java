/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverterSupport;

import junit.framework.TestCase;

/**
 * Testing the method getMassSpectrumConverterSupport() in
 * MassSpectrumConverter.
 * 
 * @author eselmeister
 */
public class MassSpectrumConverter_2_Test extends TestCase {

	private DatabaseConverterSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = DatabaseConverter.getDatabaseConverterSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testFilterNames_1() {

		try {
			String[] filterNames = support.getFilterNames();
			String result = "";
			for(String name : filterNames) {
				result += name + ";";
			}
			assertEquals("FilterName", true, result.contains("AMDIS Mass Spectra (*.msl)"));
			assertEquals("FilterName", true, result.contains("NIST Text (*.msp)"));
			// and others
		} catch(NoConverterAvailableException e) {
			assertTrue("NoConverterAvailableException", false);
		}
	}
}
