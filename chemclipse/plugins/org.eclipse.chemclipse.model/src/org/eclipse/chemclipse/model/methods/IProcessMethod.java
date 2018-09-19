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

public interface IProcessMethod {

	String getId();

	void setId(String id);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getSettings();

	void setSettings(String settings);

	String getType();

	void setType(String type);
}