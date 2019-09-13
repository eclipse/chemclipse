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
package org.eclipse.chemclipse.model.methods;

import java.util.List;

import org.eclipse.chemclipse.model.types.DataType;

public interface IProcessEntry {

	String EMPTY_JSON_SETTINGS = "{}";

	String getProcessorId();

	void setProcessorId(String processorId);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getJsonSettings();

	void setJsonSettings(String jsonSettings);

	List<DataType> getSupportedDataTypes();
}