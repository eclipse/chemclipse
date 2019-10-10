/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;

public class MethodReader_1000 extends AbstractMethodReader {

	public MethodReader_1000() {
		super(IFormat.METHOD_VERSION_0001);
	}

	@Override
	protected IProcessMethod deserialize(DataInputStream dataInputStream, File file) throws IOException {

		ProcessMethod processMethod = new ProcessMethod();
		processMethod.setSourceFile(file);
		processMethod.setOperator(readString(dataInputStream));
		processMethod.setDescription(readString(dataInputStream));
		int processEntries = dataInputStream.readInt();
		for(int i = 0; i < processEntries; i++) {
			IProcessEntry processEntry = new ProcessEntry();
			processEntry.setProcessorId(readString(dataInputStream));
			processEntry.setName(readString(dataInputStream));
			processEntry.setDescription(readString(dataInputStream));
			processEntry.setJsonSettings(readString(dataInputStream));
			//
			int dataTypes = dataInputStream.readInt();
			for(int j = 0; j < dataTypes; j++) {
				// read them for backward-compatibility
				readString(dataInputStream);
			}
			// read but ignore for backward compat
			readString(dataInputStream);
			readString(dataInputStream);
			processMethod.addProcessEntry(processEntry);
		}
		return processMethod;
	}
}
