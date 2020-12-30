/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - improve log output, adjust to new api
 *******************************************************************************/
package org.eclipse.chemclipse.converter.chromatogram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public abstract class AbstractChromatogramConverter<P extends IPeak, T extends IChromatogram<P>> implements IChromatogramConverter<P, T> {

	private static final Logger logger = Logger.getLogger(AbstractChromatogramConverter.class);
	//
	private static final String DESCRIPTION_IMPORT = "Chromatogram Import Converter";
	private static final String DESCRIPTION_EXPORT = "Chromatogram Export Converter";
	//
	private String extensionPoint = "";
	private final Class<T> type;
	private final DataCategory dataCategory;

	public AbstractChromatogramConverter(String extensionPoint, Class<T> type, DataCategory dataCategory) {

		this.extensionPoint = extensionPoint;
		this.type = type;
		this.dataCategory = dataCategory;
	}

	/**
	 * This methods returns an {@link IChromatogramConverterSupport} instance.<br/>
	 * The {@link ChromatogramConverterSupport} instance stores descriptions
	 * about all valid and registered chromatogram converters.<br/>
	 * It can be used to get more information about the registered converters
	 * such like filter names, file extensions.
	 *
	 * @return ChromatogramConverterSupport
	 */
	@Override
	public IChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport(dataCategory);
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(extensionPoint);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the ChromatogramSupplier instance first before
			 * validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a ChromatogramSupplier instance it will be at least
			 * "".
			 */
			ChromatogramSupplier supplier = new ChromatogramSupplier();
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
				supplier.setImportable(isImportable(element));
				supplier.setExportable(isExportable(element));
				supplier.setMagicNumberMatcher(getMagicNumberMatcher(element));
				chromatogramConverterSupport.add(supplier);
			}
		}
		return chromatogramConverterSupport;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		return getChromatogram(file, true, monitor);
	}

	/**
	 * Returns an IChromatogramOverview instance if the given converter is
	 * available.<br/>
	 * If no converter was available, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 *
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, String converterId, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo;
		Object converter = getChromatogramConverter(converterId, Converter.IMPORT_CONVERTER);
		if(converter instanceof IChromatogramImportConverter) {
			processingInfo = ((IChromatogramImportConverter<?>)converter).convertOverview(file, monitor);
		} else {
			processingInfo = getNoOverviewImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<T> convert(File file, IProgressMonitor monitor) {

		return getChromatogram(file, false, monitor);
	}

	/**
	 * Returns an IChromatogram instance.<br/>
	 * The raw files will be converted depending on the selected converter id.<br/>
	 * If a failure in the file could be detected an exception will be thrown.<br/>
	 * For example that the file is not readable or the file is not existent.<br/>
	 * If no converter was able to read the file, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 *
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	@Override
	public IProcessingInfo<T> convert(File file, String converterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		Object converter = getChromatogramConverter(converterId, Converter.IMPORT_CONVERTER);
		if(converter instanceof IChromatogramImportConverter) {
			processingInfo = ((IChromatogramImportConverter<?>)converter).convert(file, monitor);
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * If no suitable parser was found, null will be returned.
	 *
	 * @param file
	 * @param overview
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	@Override
	public IProcessingInfo getChromatogram(File file, boolean overview, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		List<IProcessingMessage> processingMessagesError = new ArrayList<>();
		IChromatogramConverterSupport converterSupport = getChromatogramConverterSupport();
		//
		try {
			List<String> availableConverterIds = converterSupport.getAvailableConverterIds(file);
			exitloop:
			for(String converterId : availableConverterIds) {
				/*
				 * Do not use a safe runnable here, because a IChromatogram
				 * object must be returned or null.
				 */
				Object converter = getChromatogramConverter(converterId, Converter.IMPORT_CONVERTER);
				if(converter instanceof IChromatogramImportConverter) {
					/*
					 * Why should the method not declare the exceptions that
					 * could be thrown? Here is the explanation.<br/><br/> Think
					 * about an extension which implements an chromatogram
					 * import filter for the file extension ".ms" and the
					 * supplier XY.<br/> If the extension has implemented the
					 * import filter in a wrong way, an IOException could be
					 * thrown for example.<br/> Now, if you want to convert a
					 * chromatogram from the supplier XZ with the file extension
					 * ".ms" but you do not know the correct extension id, you
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
					IChromatogramImportConverter<?> importConverter = (IChromatogramImportConverter<?>)converter;
					if(overview) {
						/*
						 * OVERVIEW
						 */
						IProcessingInfo<IChromatogramOverview> processingInfox = importConverter.convertOverview(file, monitor);
						if(!processingInfox.hasErrorMessages()) {
							/*
							 * File OK, but converter failed if
							 * object is not type of IChromatogramOverview.
							 */
							Object object = processingInfox.getProcessingResult();
							if(object instanceof IChromatogramOverview) {
								processingInfo.addMessages(processingInfox);
								processingInfo.setProcessingResult(object);
								break exitloop;
							}
						} else {
							/*
							 * Something wrong with the file.
							 * Cache the messages until all converters have been tested.
							 */
							processingMessagesError.addAll(processingInfox.getMessages());
						}
					} else {
						/*
						 * NORMAL
						 */
						IProcessingInfo<?> processingInfox = importConverter.convert(file, monitor);
						if(!processingInfox.hasErrorMessages()) {
							/*
							 * File OK, but converter failed if
							 * object is not type of IChromatogramMSD.
							 */
							Object object = processingInfox.getProcessingResult();
							if(type.isInstance(object)) {
								processingInfo.addMessages(processingInfox);
								processingInfo.setProcessingResult(object);
								break exitloop;
							}
						} else {
							/*
							 * Something wrong with the file.
							 * Cache the messages until all converters have been tested.
							 */
							processingMessagesError.addAll(processingInfox.getMessages());
						}
					}
				}
			}
		} catch(NoConverterAvailableException e) {
			logger.info(e);
			processingInfo.addErrorMessage(DESCRIPTION_IMPORT, "There is no suitable converter available to load the chromatogram file: " + getFileName(file));
		}
		/*
		 * Post process or collect the errors.
		 */
		if(type.isInstance(processingInfo.getProcessingResult())) {
			postProcessChromatogram(processingInfo, monitor);
		} else {
			for(IProcessingMessage processingMessage : processingMessagesError) {
				processingInfo.addMessage(processingMessage);
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<File> convert(File file, T chromatogram, String converterId, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo;
		Object converter = getChromatogramConverter(converterId, Converter.EXPORT_CONVERTER);
		if(converter instanceof IChromatogramExportConverter) {
			processingInfo = ((IChromatogramExportConverter)converter).convert(file, chromatogram, monitor);
		} else {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	private boolean isImportable(IConfigurationElement element) {

		boolean result = false;
		if(element != null) {
			result = Boolean.valueOf(element.getAttribute(Converter.IS_IMPORTABLE)) && getChromatogramConverter(element, Converter.IMPORT_CONVERTER) instanceof IChromatogramImportConverter;
		}
		return result;
	}

	private boolean isExportable(IConfigurationElement element) {

		boolean result = false;
		if(element != null) {
			result = Boolean.valueOf(element.getAttribute(Converter.IS_EXPORTABLE)) && getChromatogramConverter(element, Converter.EXPORT_CONVERTER) instanceof IChromatogramExportConverter;
		}
		return result;
	}

	private Object getChromatogramConverter(String converterId, String attribute) {

		IConfigurationElement element = getConfigurationElement(converterId);
		return getChromatogramConverter(element, attribute);
	}

	private Object getChromatogramConverter(IConfigurationElement element, String attribute) {

		Object instance = null;
		if(element != null) {
			try {
				instance = element.createExecutableExtension(attribute);
			} catch(CoreException e) {
				logger.warn("can't load ChromatogramConverter with id = " + element.getAttribute(Converter.ID) + ", attribute = " + attribute + ": " + e);
			}
		}
		return instance;
	}

	private IProcessingInfo<File> getNoExportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(DESCRIPTION_EXPORT, "There is no suitable converter available to save the chromatogram to the file: " + getFileName(file));
		return processingInfo;
	}

	private IProcessingInfo<IChromatogram<?>> getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<IChromatogram<?>> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(DESCRIPTION_IMPORT, "There is no suitable converter available to load the chromatogram from the file: " + getFileName(file));
		return processingInfo;
	}

	private IProcessingInfo<IChromatogramOverview> getNoOverviewImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo<IChromatogramOverview> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(DESCRIPTION_IMPORT, "There is no suitable converter available to load the chromatogram overview from the file: " + getFileName(file));
		return processingInfo;
	}

	private String getFileName(File file) {

		return file != null ? file.getAbsolutePath() : "no file";
	}

	/**
	 * Returns an IChromatogramExportConverter instance or null if none is
	 * available.
	 *
	 * @param converterId
	 * @return IConfigurationElement
	 */
	private IConfigurationElement getConfigurationElement(String converterId) {

		if("".equals(converterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(extensionPoint);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Converter.ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * This method may return null.
	 *
	 * @param element
	 * @return {@link IMagicNumberMatcher}
	 */
	private IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(Converter.IMPORT_MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}
}
