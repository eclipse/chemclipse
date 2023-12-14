/*******************************************************************************
 * Copyright (c) 2010, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to simplified API, add generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.INistSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.ScanIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanIdentifier extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectrumList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		//
		if(identifierSettings == null) {
			identifierSettings = PreferenceSupplier.getScanIdentifierSettings();
		}
		//
		if(identifierSettings instanceof ScanIdentifierSettings scanIdentifierSettings) {
			try {
				/*
				 * Pre-filter
				 */
				float limitMatchFactor = scanIdentifierSettings.getLimitMatchFactor();
				List<IScanMSD> scansToIdentify = new ArrayList<>();
				for(IScanMSD scanMSD : massSpectrumList) {
					if(LimitSupport.doIdentify(scanMSD.getTargets(), limitMatchFactor)) {
						scansToIdentify.add(scanMSD);
					}
				}
				//
				Identifier identifier = new Identifier();
				IMassSpectra massSpectra = identifier.runMassSpectrumIdentification(scansToIdentify, scanIdentifierSettings, monitor);
				processingInfo.setProcessingResult(massSpectra);
			} catch(FileNotFoundException e) {
				processingInfo.addErrorMessage(INistSupport.NIST_DESCRIPTION, "An I/O error ocurred.");
			}
		} else {
			processingInfo.addErrorMessage(INistSupport.NIST_DESCRIPTION, "The settings are not of type: " + ScanIdentifierSettings.class);
		}
		//
		return processingInfo;
	}
}
