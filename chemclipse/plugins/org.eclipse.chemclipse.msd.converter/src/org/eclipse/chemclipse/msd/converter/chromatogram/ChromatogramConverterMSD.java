/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.io.MassLibConverter;
import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;

public final class ChromatogramConverterMSD extends AbstractChromatogramConverter<IChromatogramMSD> implements IChromatogramConverter<IChromatogramMSD> {

	private static final Logger logger = Logger.getLogger(ChromatogramConverterMSD.class);
	private static IChromatogramConverter<IChromatogramMSD> instance = null;

	public ChromatogramConverterMSD() {
		super("org.eclipse.chemclipse.msd.converter.chromatogramSupplier", IChromatogramMSD.class);
	}

	public static IChromatogramConverter<IChromatogramMSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterMSD();
		}
		//
		return instance;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo processingInfo, IProgressMonitor monitor) {

		if(processingInfo != null && processingInfo.getProcessingResult() instanceof IChromatogramMSD) {
			String referenceIdentifierMarker = PreferenceSupplier.getReferenceIdentifierMarker();
			String referenceIdentifierPrefix = PreferenceSupplier.getReferenceIdentifierPrefix();
			//
			LibraryInformationSupport libraryInformationSupport = new LibraryInformationSupport();
			IChromatogramMSD chromatogramMSD = processingInfo.getProcessingResult(IChromatogramMSD.class);
			MassLibConverter massLibConverter = new MassLibConverter();
			String chromatogramName = chromatogramMSD.getName();
			File chromatogramFile = chromatogramMSD.getFile();
			/*
			 * MassLib *.inf Data
			 */
			if(chromatogramFile != null) {
				//
				File directory = chromatogramFile;
				if(chromatogramFile.isFile()) {
					directory = chromatogramFile.getParentFile();
				}
				//
				if(directory.exists()) {
					exitloop:
					for(File filex : directory.listFiles()) {
						String xName = filex.getName();
						if(filex.isFile() && xName.endsWith(".inf")) {
							if(xName.startsWith(chromatogramName)) {
								/*
								 * RI
								 */
								if(PreferenceSupplier.isParseMassLibRetentionIndexData()) {
									try {
										IProcessingInfo processingInfoIndices = massLibConverter.parseRetentionIndices(filex);
										ISeparationColumnIndices separationColumnIndices = processingInfoIndices.getProcessingResult(ISeparationColumnIndices.class);
										chromatogramMSD.setSeparationColumnIndices(separationColumnIndices);
									} catch(TypeCastException e) {
										logger.warn(e);
									}
								}
								/*
								 * Scan Targets
								 */
								if(PreferenceSupplier.isParseMassLibTargetData()) {
									try {
										IProcessingInfo processingInfoTargets = massLibConverter.parseTargets(filex);
										@SuppressWarnings("unchecked")
										Map<Integer, String> targets = (Map<Integer, String>)processingInfoTargets.getProcessingResult(Map.class);
										for(Map.Entry<Integer, String> target : targets.entrySet()) {
											IScan scan = chromatogramMSD.getScan(target.getKey());
											if(scan != null && scan instanceof IScanMSD) {
												IScanMSD scanMSD = (IScanMSD)scan;
												ILibraryInformation libraryInformation = new LibraryInformation();
												libraryInformationSupport.extractNameAndReferenceIdentifier(target.getValue(), libraryInformation, referenceIdentifierMarker, referenceIdentifierPrefix);
												IComparisonResult comparisonResult = ComparisonResult.createBestMatchComparisonResult();
												IIdentificationTarget scanTargetMSD = new IdentificationTarget(libraryInformation, comparisonResult);
												scanMSD.getTargets().add(scanTargetMSD);
											}
										}
									} catch(TypeCastException e) {
										logger.warn(e);
									}
								}
								//
								break exitloop;
							}
						}
					}
				}
			}
		}
	}
}
