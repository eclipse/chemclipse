/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramProcessorSupport {

	/**
	 * Fires an update.
	 * 
	 * @param chromatogramSelection
	 */
	@SuppressWarnings("rawtypes")
	void fireUpdate(IChromatogramSelection chromatogramSelection);
}
