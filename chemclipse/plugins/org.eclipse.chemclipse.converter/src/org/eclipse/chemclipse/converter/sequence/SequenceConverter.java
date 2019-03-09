/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.converter.sequence;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class SequenceConverter {

	private static final Logger logger = Logger.getLogger(SequenceConverter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.converter.sequenceImportSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	private static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	private static final String FILE_NAME = "fileName"; //$NON-NLS-1$
	private static final String IMPORT_CONVERTER = "importConverter"; //$NON-NLS-1$
	private static final String IMPORT_MAGIC_NUMBER_MATCHER = "importMagicNumberMatcher"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private SequenceConverter() {

	}

	public static IProcessingInfo<ISequence<?>> convert(final File file, IProgressMonitor monitor) {

		SequenceConverterSupport sequenceConverterSupport = getSequenceConverterSupport();
		for(ISupplier supplier : sequenceConverterSupport.getSupplier()) {
			//
			IProcessingInfo<ISequence<?>> processinInfo = convert(file, supplier.getId(), monitor);
			ISequence<?> sequence = processinInfo.getProcessingResult();
			if(sequence != null) {
				return processinInfo;
			}
		}
		//
		return getNoImportConverterAvailableProcessingInfo(file);
	}

	public static IProcessingInfo convert(final File file, final String converterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		ISequenceImportConverter importConverter = getSequenceImportConverter(converterId);
		if(importConverter != null) {
			processingInfo = importConverter.convert(file, monitor);
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	private static ISequenceImportConverter getSequenceImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		ISequenceImportConverter instance = null;
		if(element != null) {
			try {
				instance = (ISequenceImportConverter)element.createExecutableExtension(IMPORT_CONVERTER);
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

	public static SequenceConverterSupport getSequenceConverterSupport() {

		SequenceSupplier supplier;
		SequenceConverterSupport sequenceConverterSupport = new SequenceConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			//
			supplier = new SequenceSupplier();
			supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(FILE_NAME));
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			supplier.setMagicNumberMatcher(getMagicNumberMatcher(element));
			sequenceConverterSupport.add(supplier);
		}
		return sequenceConverterSupport;
	}

	private static IProcessingInfo getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Sequence Import Converter", "There is no suitable converter available to load the sequence from the file: " + file.getAbsolutePath());
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
}
