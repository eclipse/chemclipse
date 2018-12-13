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
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
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

/**
 * This class offers only static methods for file and chromatogram conversion.<br/>
 * A specific conversion converter can be used to get an IChromatogram or an
 * File object.<br/>
 * You can also use conversion converter where the type of chromatogram supplier
 * is not known. The method will test all possible converters. <br/>
 * <br/>
 * The goal is to implement as many chromatogram converters as possible.<br/>
 * Be aware that some extensions are directories and some are files. The
 * following example gives some impressions:<br/>
 * Agilent ChemStation *.D<br/>
 * Agilent MS Engine *.MS<br/>
 * Bruker *.MSF<br/>
 * Finnigan GCQ *.MS<br/>
 * Finnigan INCOS *.MI<br/>
 * Finnigan ITDS *.DAT<br/>
 * INFICON GCMS *.acq<br/>
 * JEOL/Schrader *.lrp<br/>
 * Kratos Mach 3 *.run<br/>
 * MassLynx NT<br/>
 * NetCDF *.CDF<br/>
 * Perkin Elmer TurboMass *.raw<br/>
 * Shimadzu *.R##<br/>
 * Schrader/GCMate *.lrp<br/>
 * Varian *.MS<br/>
 * Varian SMS *.sms<br/>
 * Varian XMS *.xms<br/>
 * XCalibur Raw *.raw <br/>
 * <br/>
 * There are many more supplier. Here a list, just to give you an impression.<br/>
 * Anelva AGS-7000 Anelva DOS Balzers QuadStar 420 Balzers QuadStar 421 EPA
 * Extrel Merlin Finnigan Incos Finnigan ITS80 Finnigan ITS40 Finnigan GCQ<br/>
 * Finnigan Magnum Finnigan MassLab Finnigan SSX Fisons/VG 11-250 Fisons/VG
 * JCAMP Fisons/VG Lab Base/Trio Fisons/VG MassLab Fisons/VG Thermolab Hitachi
 * NEC9801 Hitachi 3DQ<br/>
 * HP FirstDerivative (Agilent) HP RTE JEOL Automass JEOL Complement JEOL DA5000
 * JEOL DA6000 JEOL GCMate JEOL JCAMP JEOL K9 JEOL MARIO<br/>
 * Kratos DS90 Kratos MACH3 Mass Evolution MassLib JCAMP MSS Nermag Automass
 * Nermag SIDAR netCDF Netzsch Palisade<br/>
 * PerkinElmer Clarus PerkinElmer Qmass 910 PerkinElmer TurboMass Shimadzu
 * PAC200 Shimadzu QP5000 Shimadzu GC Solutions Shrader for Windows Teknivent
 * Vector/1 Teknivent Vector/2 Text<br/>
 * Varian Saturn Varian Saturn 5 Varian Saturn Extended
 * 
 * @author eselmeister
 */
public final class ChromatogramConverterMSD {

	private static final Logger logger = Logger.getLogger(ChromatogramConverterMSD.class);
	private static ChromatogramConverter<IChromatogramMSD> chromatogramConverter = new ChromatogramConverter<>("org.eclipse.chemclipse.msd.converter.chromatogramSupplier", IChromatogramMSD.class);

	/**
	 * This class has only static methods.
	 */
	private ChromatogramConverterMSD() {
	}

	public static IProcessingInfo convert(final File file, final String converterId, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo = chromatogramConverter.convert(file, converterId, monitor);
		postProcessChromatogram(processingInfo);
		return processingInfo;
	}

	public static IProcessingInfo convert(File file, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = chromatogramConverter.getChromatogram(file, false, monitor);
		postProcessChromatogram(processingInfo);
		return processingInfo;
	}

	public static IProcessingInfo convertOverview(File file, String converterId, IProgressMonitor monitor) {

		return chromatogramConverter.convertOverview(file, converterId, monitor);
	}

	public static IProcessingInfo convertOverview(File file, IProgressMonitor monitor) {

		return chromatogramConverter.getChromatogram(file, true, monitor);
	}

	public static IProcessingInfo convert(File file, IChromatogramMSD chromatogram, String converterId, IProgressMonitor monitor) {

		return chromatogramConverter.convert(file, chromatogram, converterId, monitor);
	}

	public static IChromatogramConverterSupport getChromatogramConverterSupport() {

		return chromatogramConverter.getChromatogramConverterSupport();
	}

	@SuppressWarnings("unchecked")
	private static void postProcessChromatogram(IProcessingInfo processingInfo) {

		/*
		 * TODO Link Process Method?
		 */
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
