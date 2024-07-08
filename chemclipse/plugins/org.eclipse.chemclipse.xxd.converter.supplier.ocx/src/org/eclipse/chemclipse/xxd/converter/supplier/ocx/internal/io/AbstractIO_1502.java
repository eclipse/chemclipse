/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.Format;

public abstract class AbstractIO_1502 {

	public static final String VERSION = Format.CHROMATOGRAM_VERSION_1502;

	protected String readString(DataInputStream dataInputStream) throws IOException {

		int length = dataInputStream.readInt();
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= length; i++) {
			builder.append(String.valueOf(dataInputStream.readChar()));
		}
		return builder.toString();
	}

	protected void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		if(value == null) {
			dataOutputStream.writeInt(-1);
		} else {
			dataOutputStream.writeInt(value.length());
			dataOutputStream.writeChars(value);
		}
	}
}
