/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.services;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The implementation shall return a molecule image, given by the information stored in the library information.
 * 
 */
public interface IMoleculeImageService {

	String getName();

	String getDescription();

	String getVersion();

	boolean isOnline();

	Image create(Display display, ILibraryInformation libraryInformation, int width, int height);

	Class<? extends IWorkbenchPreferencePage> getPreferencePage();
}
