/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - simplyfy API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.core;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A chromatogram classifier is used to extract key values of a chromatogram. This could
 * be for example to check the water, nitrogen and oxygene concentration in the chromatogram.
 * The classification data is stored in the chromatogram, but the data is transient and only
 * intended to be used for live inspection of the chromatogram.
 */
public interface IChromatogramClassifier {

	IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor);

	DataType[] getDataTypes();
}