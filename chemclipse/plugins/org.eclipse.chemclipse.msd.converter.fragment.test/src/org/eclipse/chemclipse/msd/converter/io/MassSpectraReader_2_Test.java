/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.io;

import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

import junit.framework.TestCase;

public class MassSpectraReader_2_Test extends TestCase {

	private IMassSpectraReader massSpectraReader;
	private IRegularLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectraReader = new MassSpectraReader_Test_Impl();
		massSpectrum = new RegularLibraryMassSpectrum_Test_Impl();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		String value = null;
		String delimiter = null;
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertFalse(massSpectrum.hasAdditionalRetentionIndices());
	}

	public void test2() {

		String value = "";
		String delimiter = null;
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertFalse(massSpectrum.hasAdditionalRetentionIndices());
	}

	public void test3() {

		String value = null;
		String delimiter = "";
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertFalse(massSpectrum.hasAdditionalRetentionIndices());
	}

	public void test4() {

		String value = "";
		String delimiter = "";
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertFalse(massSpectrum.hasAdditionalRetentionIndices());
	}

	public void test5() {

		String value = "1160, 1632";
		String delimiter = " ";
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(1160.0f, massSpectrum.getRetentionIndex());
		assertTrue(massSpectrum.hasAdditionalRetentionIndices());
		assertEquals(1160.0f, massSpectrum.getRetentionIndex(RetentionIndexType.APOLAR));
	}

	public void test6() {

		String value = "1160, 1632";
		String delimiter = ", ";
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(1160.0f, massSpectrum.getRetentionIndex());
		assertTrue(massSpectrum.hasAdditionalRetentionIndices());
		assertEquals(1160.0f, massSpectrum.getRetentionIndex(RetentionIndexType.APOLAR));
		assertEquals(1632.0f, massSpectrum.getRetentionIndex(RetentionIndexType.POLAR));
	}

	public void test7() {

		String value = "1160, ";
		String delimiter = ", ";
		//
		massSpectraReader.extractRetentionIndices(massSpectrum, value, delimiter);
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertFalse(massSpectrum.hasAdditionalRetentionIndices());
	}
}
