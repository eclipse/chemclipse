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
package org.eclipse.chemclipse.xir.converter.service;

import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.xir.converter.chromatogram.IExportConverterISD;
import org.eclipse.chemclipse.xir.converter.chromatogram.IImportConverterISD;

public interface IConverterServiceISD {

	String getId();

	String getDescription();

	String getFilterName();

	String getFileExtension();

	String getFileName();

	String getDirectoryExtension();

	IImportConverterISD getImportConverter();

	IExportConverterISD getExportConverter();

	IMagicNumberMatcher getMagicNumberMatcher();

	IProcessSettings getProcessSettings();

	default boolean isImportable() {

		return getImportConverter() != null;
	}

	default boolean isExportable() {

		return getExportConverter() != null;
	}

	default boolean hasProcessSettings() {

		return getProcessSettings() != null;
	}
}