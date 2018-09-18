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
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

public class InputValue {

	private Class<?> rawType = null;
	private String name = "";
	private String description = "";
	private String defaultValue = "";

	public Class<?> getRawType() {

		return rawType;
	}

	public void setRawType(Class<?> rawType) {

		this.rawType = rawType;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
	}
}
