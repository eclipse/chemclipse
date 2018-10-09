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

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;

public interface IProcessMethod {

	String getProcessorId();

	void setProcessorId(String processorId);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getJsonSettings();

	void setJsonSettings(String jsonSettings);

	List<DataType> getSupportedDataTypes();

	Class<? extends IProcessSettings> getProcessSettingsClass();

	/**
	 * String symbolicName = FrameworkUtil.getBundle(processSettingsClass).getSymbolicName();
	 * String className = processSettingsClass.getName();
	 */
	void setProcessSettingsClass(String symbolicName, String className);

	void setProcessSettingsClass(Class<? extends IProcessSettings> processSettingsClass);
}