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

import junit.framework.TestCase;

/**
 * This TestCase analyses if the class MassSpectrumConverterSupport methods work
 * in a correct way. The behaviour after initialization is especially analysed
 * in this TestCase.
 * 
 * @author eselmeister
 */
public class MassSpectrumConverterSupport_1_Test extends TestCase {

	private MassSpectrumConverterSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new MassSpectrumConverterSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testGetConverterId_1() {

		try {
			@SuppressWarnings("unused")
			String id = support.getConverterId(1);
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}

	public void testGetFilterExtensions_1() {

		try {
			@SuppressWarnings("unused")
			String[] ids = support.getFilterExtensions();
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}

	public void testGetFilterNames_1() {

		try {
			@SuppressWarnings("unused")
			String[] names = support.getFilterNames();
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}
}
