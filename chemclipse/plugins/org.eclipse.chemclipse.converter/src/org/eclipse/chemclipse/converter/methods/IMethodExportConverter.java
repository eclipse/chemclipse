/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - Stream support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.chemclipse.converter.core.IExportConverter;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMethodExportConverter extends IExportConverter {

	default void convert(File file, IProcessMethod processMethod, MessageConsumer messages, IProgressMonitor monitor) throws IOException {

		try (FileOutputStream stream = new FileOutputStream(file)) {
			convert(stream, file.getName(), processMethod, messages, monitor);
		}
	}

	/**
	 * writes the given method to the {@link OutputStream}
	 *
	 * @param stream
	 * @param nameHint
	 * @param monitor
	 * @throws IOException
	 *             in case of an IOError while reading streams
	 */
	void convert(OutputStream stream, String nameHint, IProcessMethod processMethod, MessageConsumer messages, IProgressMonitor monitor) throws IOException;
}
