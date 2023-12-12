/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.IChromatogramIdentificationResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramIdentifier {

	/**
	 * Identifies a chromatogram.<br/>
	 * Several plugins can use the extension point mechanism to support a
	 * identifier (e.g. F-Search, DB-Tools, ...).
	 * 
	 * @param chromatogramSelection
	 * @param identifierSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramIdentificationResult> identify(IChromatogramSelectionMSD chromatogramSelection, IChromatogramIdentifierSettings identifierSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramIdentificationResult> identify(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor);
}
