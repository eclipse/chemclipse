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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;

@Deprecated
public class MethodWriter_1000 extends AbstractMethodWriter {

	public MethodWriter_1000() {
		super(IFormat.METHOD_VERSION_0001);
	}

	@Override
	protected void serializeData(IProcessMethod processMethod, DataOutputStream dataOutputStream) throws IOException {

		//
		writeString(dataOutputStream, processMethod.getOperator());
		writeString(dataOutputStream, processMethod.getDescription());
		dataOutputStream.writeInt(processMethod.getNumberOfEntries());
		for(IProcessEntry processEntry : processMethod) {
			writeString(dataOutputStream, processEntry.getProcessorId());
			writeString(dataOutputStream, processEntry.getName());
			writeString(dataOutputStream, processEntry.getDescription());
			writeString(dataOutputStream, processEntry.getSettings());
			Set<DataCategory> supportedDataTypes = processEntry.getDataCategories();
			dataOutputStream.writeInt(supportedDataTypes.size());
			for(DataCategory category : supportedDataTypes) {
				writeString(dataOutputStream, DataType.fromDataCategory(category).toString());
			}
			// obsolete, just write for backward compat
			writeString(dataOutputStream, "");
			writeString(dataOutputStream, "");
		}
	}
}
