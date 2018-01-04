/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.versioning;

import java.io.File;

public interface IChromatogramVersioning {

	/**
	 * Returns the version management.
	 * 
	 * @return {@link IVersionManagement}
	 */
	IVersionManagement getVersionManagement();

	/**
	 * Returns if the chromatogram supports undo / redo operations.<br/>
	 * 
	 * @return boolean
	 */
	boolean isUndoable();

	// TODO JUnit
	/**
	 * If the chromatogram is undoable and at least one operation has been
	 * performed, the state canUndo is true.<br/>
	 * If the user has gone back to the initial state of undo, the value will be
	 * false, but redo could be still true.<br/>
	 * Don't mix it up with isUndoable(). The method isUndoable() shows only if
	 * it is possible at all to perform do, undo and redo operations on the
	 * chromatogram.
	 * 
	 * @return boolean
	 */
	boolean canUndo();

	// TODO Junit
	/**
	 * If the chromatogram is undoable and at least one operation has been
	 * performed, the state canRedo is true.<br/>
	 * Don't mix it up with isUndoable(). The method isUndoable() shows only if
	 * it is possible at all to perform do, undo and redo operations on the
	 * chromatogram.
	 * 
	 * @return boolean
	 */
	boolean canRedo();

	/**
	 * Each chromatogram could store a large amount of scan data.<br/>
	 * If the chromatogram is currently not used, store the scans to
	 * disk.<br/>
	 * This will save memory.<br/>
	 * When the chromatogram is used again, wake the chromatogram up: use
	 * wakeUp().
	 */
	void hibernate();

	/**
	 * Wake the chromatogram up if it has been hibernated.
	 */
	void wakeUp();

	/**
	 * Writes the given chromatogram serialized to the file.
	 * PLEASE USE THIS METHOD ONLY IF YOU KNOW EXACTLY WHAT
	 * YOU'RE DOING.
	 * 
	 * @param file
	 */
	void writeSerializedChromatogram(File file);

	/**
	 * Reads the given serialized chromatogram file.
	 * PLEASE USE THIS METHOD ONLY IF YOU KNOW EXACTLY WHAT
	 * YOU'RE DOING.
	 * 
	 * @param file
	 *            ;
	 */
	void readSerializedChromatogram(File file);
}
