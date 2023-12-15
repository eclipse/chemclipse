/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add stream support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMethodImportConverter extends IImportConverter {

	default IProcessingInfo<IProcessMethod> convert(File file, IProgressMonitor monitor) throws IOException {

		try (FileInputStream stream = new FileInputStream(file)) {
			IProcessingInfo<IProcessMethod> info = readFrom(stream, file.getName(), monitor);
			if(info != null) {
				IProcessMethod result = info.getProcessingResult();
				if(result instanceof ProcessMethod processMethod) {
					processMethod.setSourceFile(file);
				}
			}
			return info;
		}
	}

	IProcessingInfo<IProcessMethod> readFrom(InputStream stream, String nameHint, IProgressMonitor monitor) throws IOException;
}
