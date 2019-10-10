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

import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;

public class MethodWriter_1001 extends AbstractMethodWriter {

	public MethodWriter_1001() {
		super(IFormat.METHOD_VERSION_0002);
	}

	@Override
	protected void serializeData(IProcessMethod processMethod, DataOutputStream dataOutputStream) throws IOException {

		writeString(dataOutputStream, processMethod.getUUID());
		writeString(dataOutputStream, processMethod.getName());
		writeString(dataOutputStream, processMethod.getOperator());
		writeString(dataOutputStream, processMethod.getDescription());
		writeString(dataOutputStream, processMethod.getCategory());
		dataOutputStream.writeInt(processMethod.getNumberOfEntries());
		for(IProcessEntry processEntry : processMethod) {
			writeString(dataOutputStream, processEntry.getProcessorId());
			writeString(dataOutputStream, processEntry.getName());
			writeString(dataOutputStream, processEntry.getDescription());
			writeString(dataOutputStream, processEntry.getJsonSettings());
			// only for backward compat
			dataOutputStream.writeInt(0);
		}
	}
}
