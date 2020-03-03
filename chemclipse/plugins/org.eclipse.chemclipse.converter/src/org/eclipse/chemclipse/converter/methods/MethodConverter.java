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
 * Christoph Läubrich - Stream support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;

public class MethodConverter {

	private static final String NAME_IMPORT = "Method Import Converter";
	private static final String NAME_EXPORT = "Method Export Converter";
	// 5MB should be enough for all cases and don't hurt much...
	private static final int STREAM_BUFFER_SIZE = 1024 * 1024 * 5;
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

	public static IProcessingInfo<IProcessMethod> convert(final File file, IProgressMonitor monitor) {

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

	public static IProcessingInfo<IProcessMethod> convert(final File file, final String converterId, IProgressMonitor monitor) {

		IProcessingInfo<IProcessMethod> processingInfo;
		IMethodImportConverter importConverter = getMethodImportConverter(converterId);
		if(importConverter != null) {
			try {
				processingInfo = importConverter.convert(file, monitor);
			} catch(IOException e) {
				ProcessingInfo<IProcessMethod> info = new ProcessingInfo<>();
				info.addErrorMessage(NAME_IMPORT, "can't read file " + file, e);
				return info;
			}
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IProcessingInfo<IProcessMethod> load(InputStream stream, String nameHint, IProgressMonitor monitor) throws IOException {

		if(!stream.markSupported()) {
			stream = new BufferedInputStream(stream, STREAM_BUFFER_SIZE);
		}
		MethodConverterSupport converterSupport = getMethodConverterSupport();
		List<ISupplier> list = converterSupport.getSupplier();
		SubMonitor subMonitor = SubMonitor.convert(monitor, list.size() * 100);
		IProcessingInfo<IProcessMethod> errors = getNoImportConverterAvailableProcessingInfo(nameHint);
		for(ISupplier supplier : list) {
			IMethodImportConverter converter = getMethodImportConverter(supplier.getId());
			if(converter == null) {
				continue;
			}
			IProcessingInfo<IProcessMethod> info = converter.readFrom(stream, nameHint, subMonitor.split(100));
			if(info == null) {
				continue;
			}
			if(info.hasErrorMessages() || info.getProcessingResult() == null) {
				errors.addMessages(info);
				continue;
			}
			return info;
		}
		//
		return errors;
	}

	public static void store(OutputStream stream, String nameHint, IProcessMethod processMethod, MessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		store(stream, nameHint, processMethod, DEFAULT_METHOD_CONVERTER_ID, consumer, monitor);
	}

	public static void store(OutputStream stream, String nameHint, IProcessMethod processMethod, String converterId, MessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		MethodConverterSupport converterSupport = getMethodConverterSupport();
		for(ISupplier supplier : converterSupport.getSupplier()) {
			if(supplier.isExportable() && supplier.getId().equals(converterId)) {
				IMethodExportConverter exportConverter = getMethodExportConverter(converterId);
				exportConverter.convert(stream, nameHint, processMethod, consumer, monitor);
				return;
			}
		}
		consumer.addMessages(getNoExportConverterAvailableProcessingInfo(nameHint));
	}

	public static IProcessingInfo<Void> convert(File file, IProcessMethod processMethod, String converterId, IProgressMonitor monitor) {

		MethodConverterSupport converterSupport = getMethodConverterSupport();
		for(ISupplier supplier : converterSupport.getSupplier()) {
			if(supplier.isExportable() && supplier.getId().equals(converterId)) {
				IProcessingInfo<Void> processingInfo = new ProcessingInfo<>();
				IMethodExportConverter exportConverter = getMethodExportConverter(converterId);
				try {
					exportConverter.convert(file, processMethod, processingInfo, monitor);
				} catch(IOException e) {
					ProcessingInfo<Void> info = new ProcessingInfo<>();
					info.addErrorMessage(NAME_EXPORT, "can't write file " + file, e);
					return info;
				}
				return processingInfo;
			}
		}
		return getNoExportConverterAvailableProcessingInfo(file);
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

	private static <T> IProcessingInfo<T> getNoImportConverterAvailableProcessingInfo(File file) {

		return getNoImportConverterAvailableProcessingInfo("the file: " + file.getAbsolutePath());
	}

	private static <T> IProcessingInfo<T> getNoImportConverterAvailableProcessingInfo(String hint) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(NAME_IMPORT, "There is no suitable converter available to load the method from " + hint);
		return processingInfo;
	}

	private static <T> IProcessingInfo<T> getNoExportConverterAvailableProcessingInfo(File file) {

		return getNoExportConverterAvailableProcessingInfo("the file: " + file.getAbsolutePath());
	}

	private static <T> IProcessingInfo<T> getNoExportConverterAvailableProcessingInfo(String nameHint) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(NAME_EXPORT, "There is no suitable converter available to write the method to " + nameHint);
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

		return new File(PreferenceSupplier.getSettings(PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, PreferenceSupplier.DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER));
	}

	public static void setUserMethodDirectory(File file) {

		if(file != null) {
			String directory;
			if(file.isDirectory()) {
				directory = file.getAbsolutePath();
			} else {
				directory = file.getParent();
			}
			PreferenceSupplier.setSettings(PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, directory);
		}
	}
}
