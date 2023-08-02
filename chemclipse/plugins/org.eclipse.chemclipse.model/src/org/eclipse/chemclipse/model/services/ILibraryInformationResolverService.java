/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.services;

import java.util.List;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * https://en.wikipedia.org/wiki/Chemical_nomenclature
 */
public interface ILibraryInformationResolverService {

	String getName();

	String getDescription();

	String getVersion();

	/**
	 * Returns a list of IUPAC names if matched, based on the
	 * options and values. Could return an empty list.
	 * 
	 * @param settings
	 * @param searchTerm
	 * @param monitor
	 * @return {@link List}
	 */
	List<ILibraryInformation> retrieve(LibraryInformationResolverSettings settings, String searchTerm, IProgressMonitor monitor);
}