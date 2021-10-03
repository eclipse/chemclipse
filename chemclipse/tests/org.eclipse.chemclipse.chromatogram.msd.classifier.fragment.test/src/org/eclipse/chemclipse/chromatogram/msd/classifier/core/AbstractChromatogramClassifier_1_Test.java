/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class AbstractChromatogramClassifier_1_Test extends TestCase {

	private IChromatogramClassifier classifier;
	private IChromatogramSelectionMSD chromatogramSelection;
	private IChromatogramMSD chromatogram;
	private IChromatogramClassifierSettings chromatogramClassifierSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		chromatogramSelection = null;
		chromatogramClassifierSettings = null;
		classifier = new TestChromatogramClassifier();
		IProcessingInfo<IChromatogramClassifierResult> processingInfo = classifier.applyClassifier(chromatogramSelection, chromatogramClassifierSettings, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testConstructor_2() {

		chromatogram = new ChromatogramMSD();
		try {
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		} catch(ChromatogramIsNullException e) {
			assertTrue("ChromatogramIsNullException", false);
		}
		chromatogramClassifierSettings = null;
		classifier = new TestChromatogramClassifier();
		IProcessingInfo<IChromatogramClassifierResult> processingInfo = classifier.applyClassifier(chromatogramSelection, chromatogramClassifierSettings, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}
}
