/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.comparator;

import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.BenzenepropanoicAcid;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ITestMassSpectrum;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.NoMatchA1;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.NoMatchA2;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.PhenolBenzimidazolyl;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemA1;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemA2;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemB1;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemB2;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemC1;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.ProblemC2;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.SinapylAlcohol;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.SinapylAlcoholCis;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.spectra.Syringylacetone;
import org.junit.Ignore;

import junit.framework.TestCase;

/**
 * Comparison NIST-DB 12 (MF, RMF):
 * sinapylAclohol vs sinapylAcloholCis: 79.4, 92.6
 * sinapylAclohol vs benzenepropanoicAcid: 61.9, 68.0
 * sinapylAclohol vs syringylAcetone: 59.5, 76.3
 * sinapylAclohol vs phenolBenzimidazolyl: 51.5, 57.6
 * 
 */
@Ignore
public class MassSpectrumSetTestCase extends TestCase {

	protected ITestMassSpectrum sinapylAclohol;
	protected ITestMassSpectrum sinapylAcloholCis;
	protected ITestMassSpectrum benzenepropanoicAcid;
	protected ITestMassSpectrum syringylAcetone;
	protected ITestMassSpectrum phenolBenzimidazolyl;
	//
	protected ITestMassSpectrum noMatchA1;
	protected ITestMassSpectrum noMatchA2;
	protected ITestMassSpectrum problemA1;
	protected ITestMassSpectrum problemA2;
	protected ITestMassSpectrum problemB1;
	protected ITestMassSpectrum problemB2;
	protected ITestMassSpectrum problemC1;
	protected ITestMassSpectrum problemC2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		sinapylAclohol = new SinapylAlcohol();
		sinapylAcloholCis = new SinapylAlcoholCis();
		benzenepropanoicAcid = new BenzenepropanoicAcid();
		syringylAcetone = new Syringylacetone();
		phenolBenzimidazolyl = new PhenolBenzimidazolyl();
		//
		noMatchA1 = new NoMatchA1();
		noMatchA2 = new NoMatchA2();
		problemA1 = new ProblemA1();
		problemA2 = new ProblemA2();
		problemB1 = new ProblemB1();
		problemB2 = new ProblemB2();
		problemC1 = new ProblemC1();
		problemC2 = new ProblemC2();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}
}
