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
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.NoRetentionIndicesConverterAvailableException;
import org.eclipse.chemclipse.chromatogram.alignment.model.core.IRetentionIndices;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class offers only static methods for file and retention index
 * conversion.<br/>
 * A specific conversion converter can be used to get an IRetentionIndices or an
 * File object.<br/>
 * You can also use conversion converter where the type of retention indices
 * supplier is not known. The method will test all possible converters. <br/>
 * <br/>
 * The goal is to implement as many retention index converters as possible.<br/>
 * Be aware that some extensions are directories and some are files.<br/>
 * AMDIS Calibration *.cal<br/>
 */
public class RetentionIndicesConverter {

	private static final Logger logger = Logger.getLogger(RetentionIndicesConverter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.alignment.converter.retentionIndexSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String IS_EXPORTABLE = "isExportable";
	private static final String IS_IMPORTABLE = "isImportable";
	private static final String EXPORT_CONVERTER = "exportConverter";
	private static final String IMPORT_CONVERTER = "importConverter";

	private RetentionIndicesConverter() {

	}

	/**
	 * Returns an IRetentionIndices instance.<br/>
	 * The raw files will be converted depending on the selected converter id.<br/>
	 * If a failure in the file could be detected an exception will be thrown.<br/>
	 * For example that the file is not readable or the file is not existent.<br/>
	 * If the converter was not able to convert the file, null will be returned.
	 * 
	 * @param retentionIndices
	 * @param converterId
	 * @return {@link IChromatogram}
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	public static IRetentionIndices convert(final File retentionIndices, final String converterId) throws FileIsNotReadableException, FileIsEmptyException, IOException {

		IRetentionIndices instance = null;
		/*
		 * Do not use a safe runnable here, because a IChromatogram object must
		 * be returned or null.
		 */
		IRetentionIndicesImportConverter importConverter = getRetentionIndicesImportConverter(converterId);
		if(importConverter != null) {
			instance = importConverter.convert(retentionIndices);
		}
		return instance;
	}

	/**
	 * Returns an IRetentionIndices instance. Use this method if you do not know
	 * which retention index converter to use.<br/>
	 * This method will test all suitable converters.<br/>
	 * If no converter was able to read the file, null will be returned.
	 * 
	 * @param retentionIndices
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @return {@link IChromatogram}
	 */
	public static IRetentionIndices convert(final File file) throws FileIsNotReadableException, FileIsEmptyException {

		Object chromatogramInstance = getRetentionIndices(file);
		if(chromatogramInstance instanceof IRetentionIndices retentionIndices) {
			return retentionIndices;
		} else {
			return null;
		}
	}

	private static Object getRetentionIndices(final File chromatogram) throws FileIsNotReadableException, FileIsEmptyException {

		RetentionIndicesConverterSupport converterSupport = getRetentionIndicesConverterSupport();
		try {
			List<String> availableConverterIds = converterSupport.getAvailableConverterIds(chromatogram);
			for(String converterId : availableConverterIds) {
				/*
				 * Do not use a safe runnable here, because a IRetentionIndices
				 * object must be returned or null.
				 */
				IRetentionIndicesImportConverter importConverter = getRetentionIndicesImportConverter(converterId);
				if(importConverter != null) {
					Object chromatogramInstance;
					/*
					 * Why should the method not declare the exceptions that
					 * could be thrown? Here is the explanation.<br/><br/> Think
					 * about an extension which implements an retention index
					 * import filter for the file extension ".cal" and the
					 * supplier XY.<br/> If the extension has implemented the
					 * import filter in a wrong way, an IOException could be
					 * thrown for example.<br/> Now, if you want to convert a
					 * retention indices file from the supplier XZ with the file
					 * extension ".cal" but you do not know the correct
					 * extension id, you would call this method.<br/> The first
					 * import filter extension ".cal from XY" would try to
					 * convert the file. It would test if the file format is
					 * valid ... and then an IOExceptions happens ... ufff.<br/>
					 * The exception handler would check if this class can
					 * handle the exception. IF NOT the method would return with
					 * an IOException immediately.<br/> There is no chance to
					 * reach the correct converter. That is really not what we
					 * want.<br/> If all approaches have failed null will be
					 * returned.<br/><br/> I hope it's a little bit more clear
					 * now.<br/> eselmeister
					 */
					try {
						chromatogramInstance = importConverter.convert(chromatogram);
						if(chromatogramInstance != null) {
							return chromatogramInstance;
						}
						/*
						 * Why shouldn't FileNotFoundException,
						 * FileIsNotReadableException and FileIsEmptyException
						 * be catched here?<br/> If the file is not found or not
						 * readable or empty, than no converter can read the
						 * file.<br/> If a IOException will be thrown it is not
						 * clear, it is not clear if the file was wrong or the
						 * converter.<br/> So, catch the IOException and try the
						 * next converter and throw all other exceptions.
						 */
					} catch(IOException e) {
						logger.info(e);
					}
				}
			}
		} catch(NoRetentionIndicesConverterAvailableException e) {
			logger.info(e);
			return null;
		}
		return null;
	}

	/**
	 * Returns the written file or null if something has gone wrong.
	 * 
	 * @param file
	 * @param retentionIndices
	 * @param converterId
	 * @return File
	 * @throws FileNotFoundException
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	public static File convert(final File file, final IRetentionIndices retentionIndices, final String converterId) throws FileIsNotWriteableException, IOException {

		File chromFile = null;
		/*
		 * Do not use a safe runnable here, because a IChromatogram object must
		 * be returned or null.
		 */
		IRetentionIndicesExportConverter exportConverter = getRetentionIndicesExportConverter(converterId);
		if(exportConverter != null) {
			chromFile = exportConverter.convert(file, retentionIndices);
		}
		return chromFile;
	}

	/**
	 * Returns an IRetentionIndicesImportConverter instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IChromatogramImportConverter
	 */
	private static IRetentionIndicesImportConverter getRetentionIndicesImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IRetentionIndicesImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IRetentionIndicesImportConverter)element.createExecutableExtension(IMPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IRetentionIndicesExportConverter instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IRetentionIndicesExportConverter
	 */
	private static IRetentionIndicesExportConverter getRetentionIndicesExportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IRetentionIndicesExportConverter instance = null;
		if(element != null) {
			try {
				instance = (IRetentionIndicesExportConverter)element.createExecutableExtension(EXPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IRetentionIndicesImportConverter or
	 * IRetentionIndicesExportConverter instance or null if none is available.
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
			if(element.getAttribute(ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * This methods returns an {@link RetentionIndicesConverterSupport} instance.<br/>
	 * The {@link RetentionIndicesConverterSupport} instance stores descriptions
	 * about all valid and registered chromatogram converters.<br/>
	 * It can be used to get more information about the registered converters
	 * such like filter names, file extensions.
	 * 
	 * @return RetentionIndicesConverterSupport
	 */
	public static RetentionIndicesConverterSupport getRetentionIndicesConverterSupport() {

		RetentionIndicesSupplier supplier;
		RetentionIndicesConverterSupport chromatogramConverterSupport = new RetentionIndicesConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the RetentionIndicesSupplier instance first
			 * before validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a RetentionIndicesSupplier instance it will be at
			 * least "".
			 */
			supplier = new RetentionIndicesSupplier();
			supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
			/*
			 * Check if these values contain not allowed characters. If yes than
			 * do not add the supplier to the supported converter list.
			 */
			if(isValid(supplier.getFileExtension())) {
				supplier.setId(element.getAttribute(ID));
				supplier.setDescription(element.getAttribute(DESCRIPTION));
				supplier.setFilterName(element.getAttribute(FILTER_NAME));
				supplier.setExportable(Boolean.valueOf(element.getAttribute(IS_EXPORTABLE)));
				supplier.setImportable(Boolean.valueOf(element.getAttribute(IS_IMPORTABLE)));
				chromatogramConverterSupport.add(supplier);
			}
		}
		return chromatogramConverterSupport;
	}

	/**
	 * This method return true if the input string contains a not allowed
	 * character like \/:*?"<>| It returns true if the input string is a valid
	 * string and false if not.<br/>
	 * If the input string is null it returns false.
	 * 
	 * @return boolean
	 */
	protected static boolean isValid(final String input) {

		/*
		 *
		 */
		if(input == null) {
			return false;
		}
		/*
		 * Use four times backslash to search after a normal backslash. See
		 * "Mastering Regular Expressions" from Jeffrey Friedl, ISBN:
		 * 0596528124.
		 */
		String regex = "[\\\\/:*?\"<>|]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return !matcher.find();
	}
}
