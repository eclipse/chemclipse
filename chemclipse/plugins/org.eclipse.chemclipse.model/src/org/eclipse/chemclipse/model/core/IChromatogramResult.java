/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public interface IChromatogramResult {

	/**
	 * Returns the result identifier, used to store in the chromatogram hash map.
	 * Prefer to use the IDN of the plug-in that supports the result.
	 * 
	 * @return String
	 */
	String getIdentifier();

	/**
	 * Returns a short description of the result.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * Each plug-in may store its own result object, hence it returns a generic object.
	 * Plug-ins are responsible to cast it.
	 * 
	 * @return Object
	 */
	Object getResult();
}
