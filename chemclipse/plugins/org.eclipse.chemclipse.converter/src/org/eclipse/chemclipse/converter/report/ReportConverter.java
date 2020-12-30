/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.converter.report;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ReportConverter {

	private static final Logger logger = Logger.getLogger(ReportConverter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.converter.reportImportSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	private static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	private static final String FILE_NAME = "fileName"; //$NON-NLS-1$
	private static final String IMPORT_CONVERTER = "importConverter"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private ReportConverter() {

	}

	public static <T> IProcessingInfo<T> convert(final File file, final String converterId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IReportImportConverter<T> importConverter = getReportImportConverter(converterId);
		if(importConverter != null) {
			processingInfo = importConverter.convert(file, monitor);
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	private static IReportImportConverter getReportImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IReportImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IReportImportConverter)element.createExecutableExtension(IMPORT_CONVERTER);
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

	public static ReportConverterSupport getReportConverterSupport() {

		ReportSupplier supplier;
		ReportConverterSupport reportConverterSupport = new ReportConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			//
			supplier = new ReportSupplier();
			supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(FILE_NAME));
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			reportConverterSupport.add(supplier);
		}
		return reportConverterSupport;
	}

	private static IProcessingInfo getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Report Import Converter", "There is no suitable converter available to load the report from the file: " + file.getAbsolutePath());
		return processingInfo;
	}
}
