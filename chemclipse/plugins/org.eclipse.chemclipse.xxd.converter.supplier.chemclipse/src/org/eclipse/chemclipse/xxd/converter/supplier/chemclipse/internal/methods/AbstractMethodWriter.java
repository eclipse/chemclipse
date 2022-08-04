/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - API
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractMethodWriter implements IMethodWriter {

	private final String version;

	public AbstractMethodWriter(String version) {
		this.version = version;
	}

	@Override
	public void convert(File file, IProcessMethod processMethod, IMessageConsumer messages, IProgressMonitor monitor) throws IOException {

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(PreferenceSupplier.getMethodCompressionLevel());
		zipOutputStream.setMethod(IFormat.METHOD_COMPRESSION_TYPE);
		/*
		 * Write the data
		 */
		writeProcessMethod(zipOutputStream, processMethod, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	public void writeProcessMethod(ZipOutputStream zipOutputStream, IProcessMethod processMethod, IProgressMonitor monitor) throws IOException {

		writeVersion(zipOutputStream, monitor);
		writeData(zipOutputStream, processMethod, monitor);
	}

	private void writeVersion(ZipOutputStream zipOutputStream, IProgressMonitor monitor) throws IOException {

		/*
		 * Version
		 */
		ZipEntry zipEntry = new ZipEntry(IFormat.FILE_VERSION);
		zipOutputStream.putNextEntry(zipEntry);
		DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);
		writeString(dataOutputStream, version);
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeData(ZipOutputStream zipOutputStream, IProcessMethod processMethod, IProgressMonitor monitor) throws IOException {

		/*
		 * Data
		 */
		ZipEntry zipEntry = new ZipEntry(IFormat.FILE_PROCESS_METHOD);
		zipOutputStream.putNextEntry(zipEntry);
		DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);
		serializeData(processMethod, dataOutputStream);
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	protected abstract void serializeData(IProcessMethod processMethod, DataOutputStream dataOutputStream) throws IOException;

	protected static void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}

	@Override
	public void convert(OutputStream stream, String nameHint, IProcessMethod processMethod, IMessageConsumer messages, IProgressMonitor monitor) throws IOException {

		messages.addErrorMessage(getClass().getName(), "Writing to stream is not supported by this converter");
	}
}
