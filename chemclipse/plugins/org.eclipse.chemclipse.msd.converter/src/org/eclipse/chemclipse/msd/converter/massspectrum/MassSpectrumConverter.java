/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Logging
 * Christoph Läubrich - fix generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * This class offers several methods to import and export mass spectra.<br/>
 * There are some mass spectra formats available. Everyone can write a plugin to
 * support a distinct mass spectrum format.<br/>
 * <br/>
 * <br/>
 * The goal is to implement as many mass spectrum converters as possible.<br/>
 * Be aware that some extensions could be directories and some could be files.
 * The following example gives some impressions:<br/>
 * AMDIS *.msl<br/>
 * JCAMP-DX *.jdx<br/>
 * ASCII *.txt<br/>
 *
 * @author eselmeister
 */
public class MassSpectrumConverter {

	private static final Logger logger = Logger.getLogger(MassSpectrumConverter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.msd.converter.massSpectrumSupplier"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private MassSpectrumConverter() {

	}

	/**
	 * Imports mass spectra from the given file converted by the given converter.
	 *
	 * @param file
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectra> convert(File file, String converterId, IProgressMonitor monitor) {

		IMassSpectrumImportConverter importConverter = getMassSpectrumImportConverter(converterId);
		if(importConverter != null) {
			return importConverter.convert(file, monitor);
		} else {
			return getNoImportConverterAvailableProcessingInfo(file);
		}
	}

	/**
	 * Imports mass spectra from the given file.
	 *
	 * @param file
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor) {

		return getMassSpectra(file, monitor);
	}

	/**
	 * Returns the mass spectra.
	 *
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	private static IProcessingInfo<IMassSpectra> getMassSpectra(final File file, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		MassSpectrumConverterSupport converterSupport = getMassSpectrumConverterSupport();
		try {
			List<String> availableConverterIds = converterSupport.getAvailableConverterIds(file);
			for(String converterId : availableConverterIds) {
				/*
				 * Do not use a safe runnable here, because a IMassSpectra
				 * object must be returned or null.
				 */
				IMassSpectrumImportConverter importConverter = getMassSpectrumImportConverter(converterId);
				if(importConverter != null) {
					/*
					 * Why should the method not declare the exceptions that
					 * could be thrown? Here is the explanation.<br/><br/> Think
					 * about an extension which implements an mass spectrum
					 * import filter for the file extension ".msl" and the
					 * supplier XY.<br/> If the extension has implemented the
					 * import filter in a wrong way, an IOException could be
					 * thrown for example.<br/> Now, if you want to convert a
					 * mass spectrum from the supplier XZ with the file extension
					 * ".msl" but you do not know the correct extension id, you
					 * would call this method.<br/> The first import filter
					 * extension ".ms from XY" would try to convert the file. It
					 * would test if the file format is valid ... and then an
					 * IOExceptions happens ... ufff.<br/> The exception handler
					 * would check if this class can handle the exception. IF
					 * NOT the method would return with an IOException
					 * immediately.<br/> There is no chance to reach the correct
					 * converter. That is really not what we want.<br/> If all
					 * approaches have failed null will be returned.<br/><br/> I
					 * hope it's a little bit more clear now.<br/> eselmeister
					 */
					processingInfo = importConverter.convert(file, monitor);
					if(!processingInfo.hasErrorMessages()) {
						return processingInfo;
					}
				}
			}
		} catch(NoConverterAvailableException e) {
			logger.info(e);
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * Exports the mass spectrum.
	 *
	 * @param file
	 * @param massSpectrum
	 * @param append
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static <T> IProcessingInfo<T> convert(File file, IScanMSD massSpectrum, boolean append, String converterId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		/*
		 * Do not use a safe runnable here.
		 */
		IMassSpectrumExportConverter<T> exportConverter = getMassSpectrumExportConverter(converterId);
		if(exportConverter != null) {
			processingInfo = exportConverter.convert(file, massSpectrum, append, monitor);
		} else {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * Exports the mass spectra.
	 *
	 * @param file
	 * @param massSpectra
	 * @param append
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static <T> IProcessingInfo<T> convert(File file, IMassSpectra massSpectra, boolean append, String converterId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		/*
		 * Do not use a safe runnable here.
		 */
		IMassSpectrumExportConverter<T> exportConverter = getMassSpectrumExportConverter(converterId);
		if(exportConverter != null) {
			processingInfo = exportConverter.convert(file, massSpectra, append, monitor);
		} else {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	// ------------------------------------export
	// ---------------------------------------------ConverterMethods
	/**
	 * Returns an IMassSpectrumImportConverter instance or null if none is
	 * available.
	 *
	 * @param converterId
	 * @return IMassSpectrumImportConverter
	 */
	private static IMassSpectrumImportConverter getMassSpectrumImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IMassSpectrumImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumImportConverter)element.createExecutableExtension(Converter.IMPORT_CONVERTER);
			} catch(CoreException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IMassSpectrumExportConverter instance or null if none is
	 * available.
	 *
	 * @param converterId
	 * @return IMassSpectrumExportConverter
	 */
	private static <T> IMassSpectrumExportConverter<T> getMassSpectrumExportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IMassSpectrumExportConverter<T> instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumExportConverter)element.createExecutableExtension(Converter.EXPORT_CONVERTER);
			} catch(CoreException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IChromatogramExportConverter instance or null if none is
	 * available.
	 *
	 * @param converterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String converterId) {

		if("".equals(converterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Converter.ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * This methods returns an {@link MassSpectrumConverterSupport} instance.<br/>
	 * The {@link MassSpectrumConverterSupport} instance stores descriptions
	 * about all valid and registered mass spectrum converters.<br/>
	 * It can be used to get more information about the registered converters
	 * such like filter names, file extensions.
	 *
	 * @return ChromatogramConverterSupport
	 */
	public static MassSpectrumConverterSupport getMassSpectrumConverterSupport() {

		MassSpectrumSupplier supplier;
		MassSpectrumConverterSupport massSpectrumConverterSupport = new MassSpectrumConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the MassSpectrumSupplier instance first before
			 * validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a MassSpectrumSupplier instance it will be at least
			 * "".
			 */
			supplier = new MassSpectrumSupplier();
			supplier.setFileExtension(element.getAttribute(Converter.FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(Converter.FILE_NAME));
			supplier.setDirectoryExtension(element.getAttribute(Converter.DIRECTORY_EXTENSION));
			/*
			 * Check if these values contain not allowed characters. If yes than
			 * do not add the supplier to the supported converter list.
			 */
			if(Converter.isValid(supplier.getFileExtension()) && Converter.isValid(supplier.getFileName()) && Converter.isValid(supplier.getDirectoryExtension())) {
				supplier.setId(element.getAttribute(Converter.ID));
				supplier.setDescription(element.getAttribute(Converter.DESCRIPTION));
				supplier.setFilterName(element.getAttribute(Converter.FILTER_NAME));
				supplier.setExportable(Boolean.valueOf(element.getAttribute(Converter.IS_EXPORTABLE)));
				supplier.setImportable(Boolean.valueOf(element.getAttribute(Converter.IS_IMPORTABLE)));
				supplier.setMagicNumberMatcher(getMagicNumberMatcher(element));
				massSpectrumConverterSupport.add(supplier);
			}
		}
		return massSpectrumConverterSupport;
	}

	/*
	 * This method may return null.
	 */
	private static IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(Converter.IMPORT_MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}

	// ---------------------------------------------ConverterMethods
	private static <T> IProcessingInfo<T> getNoExportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, "MassSpectrum Export Converter", "There is no suitable converter available to export the mass spectra to the file: " + file.getAbsolutePath());
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	private static <T> IProcessingInfo<T> getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, "MassSpectrum Import Converter", "There is no suitable converter available to load the mass spectra from the file: " + file.getAbsolutePath());
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
