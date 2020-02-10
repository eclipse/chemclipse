/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add generic reader/writer methods
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface IFileHelper {

	/**
	 * Returns the file directory.
	 * 
	 * @param file
	 * @return String
	 */
	String getBaseFileDirectory(File file);

	/**
	 * Returns the base file name:
	 * G160113E.DAT_001;1 -> G160113E
	 * 
	 * @param file
	 * @return String
	 */
	String getBaseFileName(File file);

	static void writeStringCollection(DataOutputStream dataOutputStream, Collection<String> collection) throws IOException {

		if(collection == null) {
			dataOutputStream.writeInt(-1);
		} else {
			dataOutputStream.writeInt(collection.size());
			for(String string : collection) {
				writeString(dataOutputStream, string);
			}
		}
	}

	static void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		if(value == null) {
			dataOutputStream.writeInt(-1);
		} else {
			dataOutputStream.writeInt(value.length());
			dataOutputStream.writeChars(value);
		}
	}

	static String readString(DataInputStream dataInputStream) throws IOException {

		int size = dataInputStream.readInt();
		if(size > -1) {
			char[] buffer = new char[size];
			for(int i = 0; i < buffer.length; i++) {
				buffer[i] = dataInputStream.readChar();
			}
			return new String(buffer);
		}
		return null;
	}

	static List<String> readStringCollection(DataInputStream dataInputStream) throws IOException {

		ArrayList<String> list = new ArrayList<>();
		int size = dataInputStream.readInt();
		if(size > -1) {
			list.ensureCapacity(size);
			for(int i = 0; i < size; i++) {
				list.add(readString(dataInputStream));
			}
		}
		return list;
	}
}
