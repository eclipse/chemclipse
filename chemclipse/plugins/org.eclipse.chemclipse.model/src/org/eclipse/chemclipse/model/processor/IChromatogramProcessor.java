/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.processor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public interface IChromatogramProcessor {

	/**
	 * Returns a description of the executed action.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * Returns the chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelection}
	 */
	
	IChromatogramSelection<?, ?>getChromatogramSelection();

	/**
	 * Executes an implemented chromatogram modifier action.<br/>
	 * If not using a graphical user interface, chose a {@link NullProgressMonitor}.
	 */
	void execute(IProgressMonitor monitor);
}
