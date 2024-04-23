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

import org.eclipse.chemclipse.support.settings.UserManagement;

import com.google.gson.Gson;

public class ProcessSupplierSupport {

	public static final String IDENTIFIER_PROCESS_SUPPLIER = "ProcessSupplierEntry_";

	public static IEditInformation createEditInformation(ProcessSupplierEntry processSupplierEntry) {

		Gson gson = new Gson();
		String payload = gson.toJson(processSupplierEntry);
		//
		return new EditInformation(IDENTIFIER_PROCESS_SUPPLIER + payload, UserManagement.getCurrentUser());
	}

	public static ProcessSupplierEntry extractProcessSupplierEntry(IEditInformation editInformation) {

		ProcessSupplierEntry processSupplierEntry = null;
		//
		if(isProcessSupplierEntry(editInformation)) {
			Gson gson = new Gson();
			processSupplierEntry = gson.fromJson(editInformation.getDescription().replace(IDENTIFIER_PROCESS_SUPPLIER, ""), ProcessSupplierEntry.class);
		}
		//
		return processSupplierEntry;
	}

	public static boolean isProcessSupplierEntry(IEditInformation editInformation) {

		return editInformation.getDescription().startsWith(IDENTIFIER_PROCESS_SUPPLIER);
	}
}