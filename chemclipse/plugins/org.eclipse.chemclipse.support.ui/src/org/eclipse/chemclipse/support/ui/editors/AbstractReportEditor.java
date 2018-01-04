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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

public abstract class AbstractReportEditor extends MultiPageEditorPart implements IReportEditor {

	private static final Logger logger = Logger.getLogger(AbstractReportEditor.class);
	//
	private String currentUser;
	private boolean isDirty = false;
	private List<IMultiEditorPage> pages;
	private String fileExtension;

	public AbstractReportEditor(String fileExtension) {
		currentUser = UserManagement.getCurrentUser();
		pages = new ArrayList<IMultiEditorPage>();
		this.fileExtension = fileExtension;
	}

	@Override
	public String getCurrentUser() {

		return currentUser;
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	@Override
	public void setDirty(boolean isDirty) {

		this.isDirty = isDirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void dispose() {

		for(IMultiEditorPage page : pages) {
			if(page != null) {
				page.dispose();
			}
		}
		super.dispose();
	}

	@Override
	public String getFileName() {

		if(getInputFile() != null) {
			return getInputFile().getName();
		} else {
			return "NoFileAvailable";
		}
	}

	@Override
	public String getFileExtension() {

		return fileExtension;
	}

	@Override
	public void addEditorPage(IMultiEditorPage page) {

		pages.add(page);
		setPageText(page.getPageIndex(), page.getEditorTitle());
	}

	@Override
	public void setFocus() {

		/*
		 * Shows the edit history.
		 */
		int activePageIndex = getActivePage();
		//
		Iterator<IMultiEditorPage> iterator = pages.iterator();
		exitloop:
		while(iterator.hasNext()) {
			IMultiEditorPage page = iterator.next();
			if(page.getPageIndex() == activePageIndex) {
				logger.info("Page set focus: " + page.getEditorTitle());
				page.setFocus();
				break exitloop;
			}
		}
		super.setFocus();
	}

	@Override
	public void setActivePage(String editorId) {

		if(pages != null) {
			int pageIndex = -1;
			Iterator<IMultiEditorPage> iterator = pages.iterator();
			/*
			 * Find the page.
			 */
			exitloop:
			while(iterator.hasNext()) {
				IMultiEditorPage page = iterator.next();
				if(page.getEditorId().equals(editorId)) {
					pageIndex = page.getPageIndex();
					if(pageIndex >= 0) {
						/*
						 * Don't call page.setFocus() here.
						 * It will be called by default.
						 */
						logger.info("Set active page: " + page.getEditorTitle());
						setActivePage(pageIndex);
					}
					break exitloop;
				}
			}
		}
	}

	protected IFile getInputFile() {

		IEditorInput editorInput = getEditorInput();
		IFile file = ((IFileEditorInput)editorInput).getFile();
		return file;
	}

	/**
	 * Refreshed the local workspace where the file is located.
	 * 
	 * @param file
	 */
	protected void refreshWorkspace(IFile file, IProgressMonitor monitor) {

		if(file != null) {
			try {
				file.refreshLocal(1, monitor);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Updates all widgets to the current status.
	 */
	protected void updateStatusWidgets() {

		for(IMultiEditorPage page : pages) {
			page.updateWidgetStatus();
		}
	}
}
