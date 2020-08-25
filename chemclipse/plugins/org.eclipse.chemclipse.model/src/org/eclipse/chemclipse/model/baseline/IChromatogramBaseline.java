/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.baseline;

import java.util.Set;

public interface IChromatogramBaseline {

	/*
	 * Do not translate this ID.
	 */
	String DEFAULT_BASELINE_ID = "Default"; // $NON-NLS-1$

	/**
	 * Returns the active baseline model.<br/>
	 * You can modify the baseline through this baseline model.
	 * 
	 * @return {IBaselineModel}
	 */
	IBaselineModel getBaselineModel();

	/**
	 * Return the set of available baseline ids.
	 * 
	 * @return {Set<String>}
	 */
	Set<String> getBaselineIds();

	/**
	 * Returns the active baseline id.
	 * 
	 * @return {String}
	 */
	String getActiveBaseline();

	/**
	 * Activates the default baseline.
	 */
	void setActiveBaselineDefault();

	/**
	 * Empty or null is not allowed.
	 * 
	 * @param id
	 */
	void setActiveBaseline(String id);

	/**
	 * Removes the given baseline id.
	 * The default id can't be removed.
	 * 
	 * @param id
	 */
	void removeBaseline(String id);
}
