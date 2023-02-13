/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.fragment.test;

import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.query.QueryStructuralFormula;

import junit.framework.TestCase;

public class Aspirin_Test extends TestCase {

	public void testName() {

		String url = QueryStructuralFormula.fromName("acetylsalicylic acid");
		assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Acetylsalicyls%C3%A4ure2.svg", url);
	}

	public void testCAS() {

		String url = QueryStructuralFormula.fromCAS("50-78-2");
		assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Acetylsalicyls%C3%A4ure2.svg", url);
	}

	public void testSMILES() {

		String url = QueryStructuralFormula.fromSMILES("CC(=O)OC1=CC=CC=C1C(=O)O");
		assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Acetylsalicyls%C3%A4ure2.svg", url);
	}

	public void testInChI() {

		String url = QueryStructuralFormula.fromInChI("InChI=1S/C9H8O4/c1-6(10)13-8-5-3-2-4-7(8)9(11)12/h2-5H,1H3,(H,11,12)");
		assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Acetylsalicyls%C3%A4ure2.svg", url);
	}

	public void testInChIKey() {

		String url = QueryStructuralFormula.fromInChIKey("BSYNRYMUTXBXSQ-UHFFFAOYSA-N");
		assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Acetylsalicyls%C3%A4ure2.svg", url);
	}
}
