/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.csd.identifier.impl;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.AbstractPeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierRemoveUnidentified<T> extends AbstractPeakIdentifierCSD<T> {

	@Override
	public IProcessingInfo<T> identify(List<? extends IPeakCSD> peaks, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		/*
		 * Remove all unidentified peaks.
		 */
		try {
			for(IPeakCSD peak : peaks) {
				if(peak instanceof IChromatogramPeakCSD chromatogramPeakCSD) {
					IChromatogramCSD chromatogramCSD = chromatogramPeakCSD.getChromatogram();
					if(chromatogramCSD != null) {
						if(chromatogramPeakCSD.getTargets().isEmpty()) {
							chromatogramCSD.removePeak(chromatogramPeakCSD);
						}
					}
				}
			}
			processingInfo.addInfoMessage(Messages.identifier, Messages.removedUnidentifiedPeaks);
		} catch(Exception e) {
			processingInfo.addErrorMessage(Messages.identifier, Messages.identifierError, e);
		}
		return processingInfo;
	}
}
