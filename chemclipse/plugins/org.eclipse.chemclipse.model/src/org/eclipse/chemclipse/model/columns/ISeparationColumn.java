/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

public interface ISeparationColumn {

	String getName();

	void setName(String name);

	String getValue();

	void setValue(String value);

	String getLength();

	void setLength(String length);

	String getDiameter();

	void setDiameter(String diameter);

	String getPhase();

	void setPhase(String phase);
}