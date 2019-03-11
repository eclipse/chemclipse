/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support.ChromatogramTestCase;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

public class WncClassifier_1_ITest extends ChromatogramTestCase {

	private IChromatogramClassifierResult result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		Classifier wncClassifier = new Classifier();
		IChromatogramSelectionMSD chromatogramSelection = getChromatogramSelection();
		IProcessingInfo<IChromatogramClassifierResult> processingInfo = wncClassifier.applyClassifier(chromatogramSelection, new NullProgressMonitor());
		result = processingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetClassifierResult_1() {

		assertNotNull(result);
	}
}
