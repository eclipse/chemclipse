/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * This class offers several methods to import and export peaks.<br/>
 * There are some peak formats available. Everyone can write a plugin to
 * support a distinct peaks format.<br/>
 * <br/>
 * <br/>
 * The goal is to implement as many peak converters as possible.<br/>
 * Be aware that some extensions could be directories and some could be files.
 * The following example gives some impressions:<br/>
 * Matlab Parafac Peaklist *.mpl<br/>
 * 
 * @author eselmeister
 */
public class PeakConverterMSD {

	private static final Logger logger = Logger.getLogger(PeakConverterMSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.msd.converter.peakSupplier"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private PeakConverterMSD() {
	}

	public static IProcessingInfo<IPeaks<?>> convert(File file, String converterId, IProgressMonitor monitor) {

		/*
		 * Do not use a safe runnable here.
		 */
		IPeakImportConverter importConverter = getPeakImportConverter(converterId);
		if(importConverter != null) {
			return importConverter.convert(file, monitor);
		} else {
			return getNoImportConverterAvailableProcessingInfo(file);
		}
	}

	public static IProcessingInfo convert(File file, IProgressMonitor monitor) {

		return getPeaks(file, monitor);
	}

	public static IProcessingInfo convert(File file, IPeaks peaks, boolean append, String converterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		/*
		 * Do not use a safe runnable here.
		 */
		IPeakExportConverter exportConverter = getPeakExportConverter(converterId);
		if(exportConverter != null) {
			processingInfo = exportConverter.convert(file, peaks, append, monitor);
		} else {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	private static IProcessingInfo getPeaks(final File file, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		PeakConverterSupport converterSupport = getPeakConverterSupport();
		/*
		 * Try to convert.
		 */
		List<String> availableConverterIds;
		try {
			availableConverterIds = converterSupport.getAvailableConverterIds(file);
			for(String converterId : availableConverterIds) {
				/*
				 * Do not use a safe runnable here, because a IPeaks
				 * object must be returned or null.
				 */
				IPeakImportConverter importConverter = getPeakImportConverter(converterId);
				if(importConverter != null) {
					/*
					 * Why should the method not declare the exceptions that
					 * could be thrown? Here is the explanation.<br/><br/> Think
					 * about an extension which implements an peaks
					 * import filter for the file extension ".ms" and the
					 * supplier XY.<br/> If the extension has implemented the
					 * import filter in a wrong way, an IOException could be
					 * thrown for example.<br/> Now, if you want to convert a
					 * peak from the supplier XZ with the file extension
					 * ".mpl" but you do not know the correct extension id, you
					 * would call this method.<br/> The first import filter
					 * extension ".mpl from XY" would try to convert the file. It
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
						try {
							processingInfo.getProcessingResult(IPeaks.class);
							return processingInfo;
						} catch(TypeCastException e) {
							logger.warn(e);
						}
					}
				}
			}
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
		/*
		 * If this point is reached, there was by sure no suitable converter.
		 */
		return getNoImportConverterAvailableProcessingInfo(file);
	}

	private static IPeakImportConverter getPeakImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IPeakImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IPeakImportConverter)element.createExecutableExtension(Converter.IMPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	public static IPeakExportConverter getPeakExportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IPeakExportConverter instance = null;
		if(element != null) {
			try {
				instance = (IPeakExportConverter)element.createExecutableExtension(Converter.EXPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

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

	public static PeakConverterSupport getPeakConverterSupport() {

		PeakSupplier supplier;
		PeakConverterSupport peakConverterSupport = new PeakConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the PeakSupplier instance first before
			 * validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a PeakSupplier instance it will be at least
			 * "".
			 */
			supplier = new PeakSupplier();
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
				peakConverterSupport.add(supplier);
			}
		}
		return peakConverterSupport;
	}

	private static IProcessingInfo getNoExportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, "Peak Export Converter", "There is no suitable converter available to export the peaks to the file: " + file.getAbsolutePath());
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	private static <T> IProcessingInfo<T> getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, "Peak Import Converter", "There is no suitable converter available to load the peaks from the file: " + file.getAbsolutePath());
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	private static IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(Converter.IMPORT_MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}
}
