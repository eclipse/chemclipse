/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class GenericStreamMethodFormat implements IMethodWriter, IMethodReader {

	private static final String MAGIC = "MTH";
	private final byte[] identifier;

	protected GenericStreamMethodFormat(String version) {
		identifier = (MAGIC + "." + version).getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public IProcessMethod convert(InputStream stream, String nameHint, MessageConsumer consumer, IProgressMonitor monitor) throws IOException {

		stream.mark(identifier.length);
		for(int i = 0; i < identifier.length; i++) {
			int read = stream.read();
			if(read < 0 || ((byte)(read & 0xFF) != identifier[i])) {
				stream.reset();
				// invalid version/file
				return null;
			}
		}
		// magic header matches, we can now read the payload
		return deserialize(new GZIPInputStream(stream), consumer, monitor);
	}

	protected abstract IProcessMethod deserialize(InputStream stream, MessageConsumer consumer, IProgressMonitor monitor) throws IOException;

	protected abstract void serialize(OutputStream stream, IProcessMethod processMethod, MessageConsumer consumer, IProgressMonitor monitor) throws IOException;

	@Override
	public void convert(OutputStream stream, String nameHint, IProcessMethod processMethod, MessageConsumer messages, IProgressMonitor monitor) throws IOException {

		// write the identifier
		stream.write(identifier);
		// and now the payload
		GZIPOutputStream outputStream = new GZIPOutputStream(stream);
		serialize(outputStream, processMethod, messages, monitor);
		outputStream.finish();
		outputStream.flush();
		stream.flush();
		// we don't close here so it is possible to write multiple items to a stream (if desired)
	}
}
