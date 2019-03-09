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
 * Alexander Kerner - API
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.FrameworkUtil;

public class MethodWriter_1000 implements IMethodWriter {

	@Override
	public void convert(File file, IProcessMethod processMethod, IProgressMonitor monitor) throws IOException {

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
		writeString(dataOutputStream, IFormat.METHOD_VERSION_0001);
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
		//
		writeString(dataOutputStream, processMethod.getOperator());
		writeString(dataOutputStream, processMethod.getDescription());
		dataOutputStream.writeInt(processMethod.size());
		for(IProcessEntry processEntry : processMethod) {
			writeString(dataOutputStream, processEntry.getProcessorId());
			writeString(dataOutputStream, processEntry.getName());
			writeString(dataOutputStream, processEntry.getDescription());
			writeString(dataOutputStream, processEntry.getJsonSettings());
			//
			List<DataType> supportedDataTypes = processEntry.getSupportedDataTypes();
			dataOutputStream.writeInt(supportedDataTypes.size());
			for(DataType dataType : supportedDataTypes) {
				writeString(dataOutputStream, dataType.toString());
			}
			//
			Class<? extends IProcessSettings> clazz = processEntry.getProcessSettingsClass();
			if(clazz != null) {
				String symbolicName = FrameworkUtil.getBundle(clazz).getSymbolicName();
				String className = clazz.getName();
				writeString(dataOutputStream, symbolicName);
				writeString(dataOutputStream, className);
			} else {
				writeString(dataOutputStream, "");
				writeString(dataOutputStream, "");
			}
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}
}
