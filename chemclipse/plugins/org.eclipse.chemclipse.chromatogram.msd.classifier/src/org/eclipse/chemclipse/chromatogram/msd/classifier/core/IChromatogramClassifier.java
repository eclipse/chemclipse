/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.IChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

/**
 * @author eselmeister
 */
public interface IChromatogramClassifier {

	IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor);

	IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor);
}
