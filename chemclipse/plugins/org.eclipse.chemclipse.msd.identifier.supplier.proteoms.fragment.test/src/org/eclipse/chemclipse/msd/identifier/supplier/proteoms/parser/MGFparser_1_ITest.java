/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.PathResolver;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.TestPathHelper;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;

import junit.framework.TestCase;

public class MGFparser_1_ITest extends TestCase {

	private MGFParser mgfParser;
	private String mgfFilePath;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		mgfFilePath = PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_PPW_ppw_L22_142651859102_CLASIC_SPECTRA);
		mgfParser = new MGFParser();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRegularFile() {

		final MGFParser p = new MGFParser();
		try {
			p.parserRegular(new File(mgfFilePath));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void test1() {

		try {
			final SpectrumMS massSpectrum = mgfParser.parse(mgfFilePath);
			assertNotNull(massSpectrum);
			final List<SpectrumMSMS> msmsSpectrumsChildren = massSpectrum.getMsmsSpectrumsChildren();
			assertNotNull(msmsSpectrumsChildren);
		} catch (final FileNotFoundException e) {
			assertTrue(false);
		} catch (final IOException e) {
			assertTrue(false);
		}
	}
}
