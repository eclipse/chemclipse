/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class MethodConverter {

	/*
	 * Keep in sync with:
	 * org.eclipse.chemclipse.xxd.converter.supplier.chemclipse
	 */
	public static final String DEFAULT_METHOD_CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.processMethodSupplier";
	public static final String DEFAULT_METHOD_FILE_NAME_EXTENSION = "ocm";
	public static final String DEFAULT_METHOD_FILE_NAME = "ProcessMethod." + DEFAULT_METHOD_FILE_NAME_EXTENSION;
	public static final String[] DEFAULT_METHOD_FILE_EXTENSIONS = new String[]{"*." + DEFAULT_METHOD_FILE_NAME_EXTENSION};
	public static final String[] DEFAULT_METHOD_FILE_NAMES = new String[]{"Process Method (*." + DEFAULT_METHOD_FILE_NAME_EXTENSION + ")"};
	//
	private static final Logger logger = Logger.getLogger(MethodConverter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.converter.processMethodSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	private static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	private static final String FILE_NAME = "fileName"; //$NON-NLS-1$
	private static final String IMPORT_CONVERTER = "importConverter"; //$NON-NLS-1$
	private static final String EXPORT_CONVERTER = "exportConverter"; //$NON-NLS-1$
	private static final String IS_EXPORTABLE = "isExportable"; //$NON-NLS-1$
	private static final String IS_IMPORTABLE = "isImportable"; //$NON-NLS-1$
	private static final String IMPORT_MAGIC_NUMBER_MATCHER = "importMagicNumberMatcher"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private MethodConverter() {
	}

	public static IProcessingInfo convert(final File file, IProgressMonitor monitor) {

		MethodConverterSupport converterSupport = getMethodConverterSupport();
		for(ISupplier supplier : converterSupport.getSupplier()) {
			//
			IProcessingInfo<IProcessMethod> processinInfo = convert(file, supplier.getId(), monitor);
			IProcessMethod processMethod = processinInfo.getProcessingResult();
			if(processMethod != null) {
				return processinInfo;
			}
		}
		//
		return getNoImportConverterAvailableProcessingInfo(file);
	}

	public static IProcessingInfo convert(final File file, final String converterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IMethodImportConverter importConverter = getMethodImportConverter(converterId);
		if(importConverter != null) {
			processingInfo = importConverter.convert(file, monitor);
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IProcessingInfo convert(File file, IProcessMethod processMethod, String converterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = null;
		MethodConverterSupport converterSupport = getMethodConverterSupport();
		exitloop:
		for(ISupplier supplier : converterSupport.getSupplier()) {
			if(supplier.isExportable() && supplier.getId().equals(converterId)) {
				IMethodExportConverter exportConverter = getMethodExportConverter(converterId);
				processingInfo = exportConverter.convert(file, processMethod, monitor);
				break exitloop;
			}
		}
		//
		if(processingInfo == null) {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		//
		return processingInfo;
	}

	private static IMethodImportConverter getMethodImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IMethodImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IMethodImportConverter)element.createExecutableExtension(IMPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	private static IMethodExportConverter getMethodExportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IMethodExportConverter instance = null;
		if(element != null) {
			try {
				instance = (IMethodExportConverter)element.createExecutableExtension(EXPORT_CONVERTER);
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
			if(element.getAttribute(ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	public static MethodConverterSupport getMethodConverterSupport() {

		MethodSupplier supplier;
		MethodConverterSupport converterSupport = new MethodConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			//
			supplier = new MethodSupplier();
			supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(FILE_NAME));
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			supplier.setExportable(Boolean.valueOf(element.getAttribute(IS_EXPORTABLE)));
			supplier.setImportable(Boolean.valueOf(element.getAttribute(IS_IMPORTABLE)));
			supplier.setMagicNumberMatcher(getMagicNumberMatcher(element));
			converterSupport.add(supplier);
		}
		return converterSupport;
	}

	private static IProcessingInfo getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Method Import Converter", "There is no suitable converter available to load the method from the file: " + file.getAbsolutePath());
		return processingInfo;
	}

	private static IProcessingInfo getNoExportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Method Export Converter", "There is no suitable converter available to write the method to the file: " + file.getAbsolutePath());
		return processingInfo;
	}

	/*
	 * This method may return null.
	 */
	private static IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(IMPORT_MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}

	public static Collection<IProcessMethod> getUserMethods() {

		List<IProcessMethod> methodFiles = new ArrayList<>();
		File directory = getUserMethodDirectory();
		if(directory.exists() && directory.isDirectory()) {
			try {
				String[] extensions = MethodConverter.getMethodConverterSupport().getFilterExtensions();
				for(File file : directory.listFiles()) {
					for(String extension : extensions) {
						if(file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
							IProcessMethod adapt = Adapters.adapt(file, IProcessMethod.class);
							if(adapt != null) {
								methodFiles.add(adapt);
							}
						}
					}
				}
			} catch(NoConverterAvailableException e) {
			}
		}
		return methodFiles;
	}

	public static File getUserMethodDirectory() {

		String settings = PreferenceSupplier.getSettings(PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, PreferenceSupplier.DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		return new File(settings);
	}

	public static void setUserMethodDirectory(File file) {

		PreferenceSupplier.setSettings(PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, file.getAbsolutePath());
	}
}
