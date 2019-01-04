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
package org.eclipse.chemclipse.model.quantitation;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface IQuantitationDatabase extends Set<IQuantitationCompound> {

	File getFile();

	void setFile(File file);

	String getConverterId();

	void setConverterId(String converterId);

	String getOperator();

	void setOperator(String operator);

	String getDescription();

	void setDescription(String description);

	List<String> getCompoundNames();

	/**
	 * This method could return null.
	 * 
	 * @param name
	 * @return IQuantitationCompound
	 */
	IQuantitationCompound getQuantitationCompound(String name);

	boolean containsQuantitationCompund(String name);
}