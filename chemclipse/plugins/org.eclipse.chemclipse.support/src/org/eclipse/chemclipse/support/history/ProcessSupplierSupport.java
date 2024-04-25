/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.history;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.support.settings.UserManagement;

public class ProcessSupplierSupport {

	public static final String IDENTIFIER_PROCESS_SUPPLIER = "ProcessSupplierEntry_";
	//
	private static final String DELIMITER = "_";
	private static final String PLACEHOLER_EMPTY = "EMPTY";

	public static IEditInformation createEditInformation(ProcessSupplierEntry processSupplierEntry) {

		String payload = toPayload(processSupplierEntry);
		return new EditInformation(IDENTIFIER_PROCESS_SUPPLIER + payload, UserManagement.getCurrentUser());
	}

	public static ProcessSupplierEntry extractProcessSupplierEntry(IEditInformation editInformation) {

		ProcessSupplierEntry processSupplierEntry = null;
		if(isProcessSupplierEntry(editInformation)) {
			processSupplierEntry = fromPayload(editInformation.getDescription().replace(IDENTIFIER_PROCESS_SUPPLIER, ""));
		}
		//
		return processSupplierEntry;
	}

	public static boolean isProcessSupplierEntry(IEditInformation editInformation) {

		return editInformation.getDescription().startsWith(IDENTIFIER_PROCESS_SUPPLIER);
	}

	public static String toPayload(ProcessSupplierEntry processSupplierEntry) {

		String payload = "";
		if(processSupplierEntry != null) {
			/*
			 * Content
			 */
			List<String> values = new ArrayList<>();
			values.add(processSupplierEntry.getId());
			values.add(processSupplierEntry.getName());
			values.add(processSupplierEntry.getDescription());
			values.add(processSupplierEntry.getUserSettings());
			//
			Iterator<String> iterator = values.iterator();
			StringBuilder builder = new StringBuilder();
			while(iterator.hasNext()) {
				builder.append(encode(iterator.next()));
				if(iterator.hasNext()) {
					builder.append(DELIMITER);
				}
			}
			/*
			 * Keep the payload condense.
			 */
			payload = encode(builder.toString());
		}
		//
		return payload;
	}

	public static ProcessSupplierEntry fromPayload(String payload) {

		ProcessSupplierEntry processSupplierEntry = null;
		if(payload != null && !payload.isEmpty()) {
			String content = decode(payload);
			String[] parts = content.split(DELIMITER);
			if(parts.length == 4) {
				processSupplierEntry = new ProcessSupplierEntry();
				processSupplierEntry.setId(decode(parts[0]));
				processSupplierEntry.setName(decode(parts[1]));
				processSupplierEntry.setDescription(decode(parts[2]));
				processSupplierEntry.setUserSettings(decode(parts[3]));
			}
		}
		//
		return processSupplierEntry;
	}

	private static String encode(String value) {

		value = value.isEmpty() ? PLACEHOLER_EMPTY : value;
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

	private static String decode(String value) {

		String data = new String(Base64.getDecoder().decode(value.trim()));
		return PLACEHOLER_EMPTY.equals(data) ? "" : data;
	}
}