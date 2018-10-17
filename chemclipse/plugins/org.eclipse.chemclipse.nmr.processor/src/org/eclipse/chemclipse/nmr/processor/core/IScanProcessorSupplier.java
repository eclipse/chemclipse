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
package org.eclipse.chemclipse.nmr.processor.core;

import org.eclipse.chemclipse.nmr.processor.settings.IProcessorSettings;

public interface IScanProcessorSupplier {

	String getId();

	void setId(String id);

	String getDescription();

	void setDescription(String description);

	String getProcessorName();

	void setProcessorName(String processorName);

	void setSettingsClass(Class<? extends IProcessorSettings> settingsClass);

	Class<? extends IProcessorSettings> getSettingsClass();
}