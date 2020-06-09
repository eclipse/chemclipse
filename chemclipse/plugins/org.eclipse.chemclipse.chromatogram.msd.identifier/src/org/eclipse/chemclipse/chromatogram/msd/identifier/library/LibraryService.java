/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add Null-Check
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.library;

import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
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

public class LibraryService {

	private static final Logger logger = Logger.getLogger(LibraryService.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.identifier.libraryService";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable library service available";

	/**
	 * This class should have only static methods.
	 */
	private LibraryService() {

	}

	public static IProcessingInfo<IMassSpectra> identify(IIdentificationTarget identificationTarget, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo;
		ILibraryService libraryService = getLibraryService(identifierId);
		if(libraryService != null) {
			processingInfo = libraryService.identify(identificationTarget, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static IProcessingInfo<IMassSpectra> identify(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		ILibraryServiceSupport libraryServiceSupport = getLibraryServiceSupport();
		try {
			/*
			 * Get a match.
			 */
			List<String> availableIdentifierIds = libraryServiceSupport.getAvailableIdentifierIds();
			IMassSpectra massSpectra = null;
			exitloop:
			for(String identifierId : availableIdentifierIds) {
				/*
				 * Test all available library services.
				 */
				ILibraryService libraryService = getLibraryService(identifierId);
				if(libraryService != null) {
					/*
					 * It's a match if at least one mass spectrum is returned.
					 */
					processingInfo = libraryService.identify(identificationTarget, monitor);
					massSpectra = processingInfo.getProcessingResult();
					if(massSpectra != null && massSpectra.size() > 0) {
						break exitloop;
					}
				}
			}
			/*
			 * Post check.
			 */
			if(massSpectra == null || massSpectra.size() == 0) {
				processingInfo = getNoIdentifierAvailableProcessingInfo();
			}
		} catch(NoIdentifierAvailableException e) {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		//
		return processingInfo;
	}

	/**
	 * Returns an {@link ILibraryServiceSupport} instance.
	 *
	 * @return {@link ILibraryServiceSupport}
	 */
	public static ILibraryServiceSupport getLibraryServiceSupport() {

		LibraryServiceSupplier supplier;
		LibraryServiceSupport identifierSupport = new LibraryServiceSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new LibraryServiceSupplier();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			identifierSupport.add(supplier);
		}
		return identifierSupport;
	}

	/**
	 * Returns a {@link ILibraryService} instance given by the
	 * identifierId or null, if none is available.
	 */
	public static ILibraryService getLibraryService(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		ILibraryService instance = null;
		if(element != null) {
			try {
				instance = (ILibraryService)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IConfigurationElement} instance or null if none is
	 * available.
	 *
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if("".equals(filterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Identifier.ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}

	private static <T> IProcessingInfo<T> getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Library Service", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
