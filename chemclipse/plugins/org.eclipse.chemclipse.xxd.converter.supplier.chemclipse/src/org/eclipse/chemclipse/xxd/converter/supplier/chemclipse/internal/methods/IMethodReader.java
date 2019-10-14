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
 * Christoph Läubrich - stream support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMethodReader {

	/**
	 * try to convert the given file
	 * 
	 * @param file
	 * @param consumer
	 * @param monitor
	 * @return <code>null</code> if this reader is not valid for the given file
	 * @throws IOException
	 */
	default IProcessMethod convert(File file, MessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
			IProcessMethod method = convert(stream, file.getName(), consumer, monitor);
			if(method instanceof ProcessMethod) {
				((ProcessMethod)method).setSourceFile(file);
			}
			return method;
		}
	}

	IProcessMethod convert(InputStream stream, String nameHint, MessageConsumer consumer, IProgressMonitor monitor) throws IOException;
}
