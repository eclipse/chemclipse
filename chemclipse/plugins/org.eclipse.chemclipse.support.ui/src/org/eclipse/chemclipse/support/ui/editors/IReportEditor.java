/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.editors;

public interface IReportEditor {

	String getCurrentUser();

	void setDirty(boolean isDirty);

	/**
	 * Returns the file name.
	 * 
	 * @return String
	 */
	String getFileName();

	/**
	 * Returns the file extension.
	 * 
	 * @return String
	 */
	String getFileExtension();

	/**
	 * Adds the page to the internal list and sets the
	 * page index and text.
	 * 
	 * @param page
	 */
	void addEditorPage(IMultiEditorPage page);

	/**
	 * Sets the active page.
	 * USE IT ONLY IF NECESSARY.
	 * 
	 * @param editorId
	 */
	void setActivePage(String editorId);
}
