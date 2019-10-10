/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public interface IProcessEntry {

	String getProcessorId();

	void setProcessorId(String processorId);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getJsonSettings();

	void setJsonSettings(String jsonSettings);

	public static <T> ProcessorPreferences<T> getProcessEntryPreferences(IProcessEntry entry, ProcessSupplierContext context) {

		IProcessSupplier<T> supplier = context.getSupplier(entry.getProcessorId());
		if(supplier == null) {
			return null;
		}
		return new ProcessEntryProcessorPreferences<>(supplier, entry);
	}
}