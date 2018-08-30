/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.processor.IChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramProcessorSupport {

	/**
	 * Perform a do operation.
	 * 
	 * @param chromatogramProcessor
	 */
	void doOperation(IChromatogramProcessor chromatogramProcessor, IProgressMonitor monitor);

	/**
	 * Performs an operation the same way doOperation(IChromatogramModifier chromatogramModifier, IProgressMonitor monitor) does.
	 * But it must be explicitly determined whether to be able to undo the operation.
	 * The method doOperation(IChromatogramModifier chromatogramModifier, IProgressMonitor monitor) checks it automatically.
	 * If isDataModifying is true, all peaks and targets will be removed.
	 * 
	 * @param chromatogramProcessor
	 * @param isUndoable
	 * @param isDataModifying
	 * @param monitor
	 */
	void doOperation(IChromatogramProcessor chromatogramProcessor, boolean isUndoable, boolean isDataModifying, IProgressMonitor monitor);

	/**
	 * Performs an undo operation.<br/>
	 * The {@link IChromatogramSelectionFID} instance is needed to fire updates to
	 * all listeners.
	 * 
	 * @param chromatogramSelection
	 */
	@SuppressWarnings("rawtypes")
	void undoOperation(IChromatogramSelection chromatogramSelection);

	/**
	 * Performs a redo operation.<br/>
	 * The {@link IChromatogramSelectionFID} instance is needed to fire updates to
	 * all listeners.
	 * 
	 * @param chromatogramSelection
	 */
	@SuppressWarnings("rawtypes")
	void redoOperation(IChromatogramSelection chromatogramSelection);

	/**
	 * Fires an update.
	 * 
	 * @param chromatogramSelection
	 */
	@SuppressWarnings("rawtypes")
	void fireUpdate(IChromatogramSelection chromatogramSelection);
}
