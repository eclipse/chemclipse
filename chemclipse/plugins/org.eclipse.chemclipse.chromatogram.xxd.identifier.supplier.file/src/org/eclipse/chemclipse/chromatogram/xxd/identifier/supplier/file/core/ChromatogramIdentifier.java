/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram.AbstractChromatogramIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.internal.identifier.FileIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIdentifier extends AbstractChromatogramIdentifier {

	private static final Logger logger = Logger.getLogger(ChromatogramIdentifier.class);
	private static final String DESCRIPTION = "Library File (MS)";

	@Override
	public IProcessingInfo<?> identify(IChromatogramSelectionMSD chromatogramSelection, IChromatogramIdentifierSettings chromatogramIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, chromatogramIdentifierSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramIdentifierSettings instanceof IdentifierSettings) {
				try {
					/*
					 * Settings
					 */
					IdentifierSettings settings = (IdentifierSettings)chromatogramIdentifierSettings;
					boolean useNormalize = settings.isUseNormalizedScan();
					CalculationType calculationType = settings.getCalculationType();
					boolean usePeaksInsteadOfScans = settings.isUsePeaksInsteadOfScans();
					/*
					 * Combined Scan
					 */
					IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
					float limitMatchFactor = settings.getLimitMatchFactor();
					if(LimitSupport.doIdentify(chromatogram.getTargets(), limitMatchFactor)) {
						IScanMSD combinedMassSpectrum = FilterSupport.getCombinedMassSpectrum(chromatogramSelection, null, useNormalize, calculationType, usePeaksInsteadOfScans);
						FileIdentifier fileIdentifier = new FileIdentifier();
						List<IScanMSD> scansMSD = new ArrayList<>();
						scansMSD.add(combinedMassSpectrum);
						fileIdentifier.runIdentification(scansMSD, settings, monitor);
						chromatogram.getTargets().addAll(combinedMassSpectrum.getTargets());
						processingInfo.addInfoMessage(DESCRIPTION, "The chromatogram has been identified.");
					}
				} catch(FileNotFoundException e) {
					logger.warn(e);
					processingInfo.addWarnMessage(DESCRIPTION, "The database file couldn't be found.");
				}
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> identify(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IdentifierSettings identifierSettings = PreferenceSupplier.getIdentifierSettings();
		return identify(chromatogramSelection, identifierSettings, monitor);
	}
}