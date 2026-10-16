/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - remove default methods
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.AbstractPeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.l10n.Messages;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifier extends AbstractPeakIdentifierMSD {

	private static final Logger logger = Logger.getLogger(PeakIdentifier.class);

	@Override
	public IProcessingInfo<IPeakIdentificationResults> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IPeakIdentificationResults> processingInfo = new ProcessingInfo<>();

		if(identifierSettings instanceof PeakIdentifierSettings peakIdentifierSettings) {
			try {
				/*
				 * Pre-filter
				 */
				float limitMatchFactor = peakIdentifierSettings.getLimitMatchFactor();
				List<IPeakMSD> peaksToIdentify = new ArrayList<>();
				for(IPeakMSD peakMSD : peaks) {
					if(LimitSupport.doIdentify(peakMSD.getTargets(), limitMatchFactor)) {
						peaksToIdentify.add(peakMSD);
					}
				}

				Identifier identifier = new Identifier();
				IPeakIdentificationResults peakIdentificationResults = identifier.runPeakIdentification(peaksToIdentify, peakIdentifierSettings, processingInfo, monitor);
				int numberOfIdentifiedPeaks = peakIdentificationResults.getIdentificationResults().size();
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, Messages.nistDbIdentifier, numberOfIdentifiedPeaks + " peaks were identified successfully."));
				processingInfo.setProcessingResult(peakIdentificationResults);
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(Messages.nistDbIdentifier, "File not found.", "Setup folders in preferences.");
				logger.error(e);
			}
		} else {
			processingInfo.addErrorMessage(Messages.nistDbIdentifier, "The settings are not of type: " + PeakIdentifierSettings.class);
		}

		return processingInfo;
	}
}
