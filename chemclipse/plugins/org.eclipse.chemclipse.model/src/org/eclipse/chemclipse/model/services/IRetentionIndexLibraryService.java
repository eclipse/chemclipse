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

public interface IRetentionIndexLibraryService {

	String getName();

	String getDescription();

	String getVersion();

	List<ILibraryInformation> getLibraryInformation(float retentionIndex, RetentionIndexLibrarySettings settings, IProgressMonitor monitor);
}