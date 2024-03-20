/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - fix generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.database;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDatabaseImportConverter extends IImportConverter {

	IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor);
}