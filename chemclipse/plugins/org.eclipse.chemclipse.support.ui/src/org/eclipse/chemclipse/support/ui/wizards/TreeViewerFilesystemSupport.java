/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support new lazy table model
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

public class TreeViewerFilesystemSupport {

	/**
	 * Load the local file system.
	 * 
	 * @param treeViewer
	 */
	public static void retrieveAndSetLocalFileSystem(final TreeViewer treeViewer) {

		retrieveAndSetLocalFileSystem(treeViewer, "");
	}

	/**
	 * The tree will be expanded to the given directory path after it has been loaded.
	 * 
	 * @param treeViewer
	 * @param elementOrTreePath
	 */
	public static void retrieveAndSetLocalFileSystem(final TreeViewer treeViewer, final String expandToDirectoryPath) {

		retrieveAndSetLocalFileSystem(treeViewer, File.listRoots(), expandToDirectoryPath);
	}

	public static void retrieveAndSetLocalFileSystem(final TreeViewer treeViewer, File input, final String expandToDirectoryPath) {

		retrieveAndSetLocalFileSystem(treeViewer, new File[]{input}, expandToDirectoryPath);
	}

	public static void retrieveAndSetLocalFileSystem(final TreeViewer treeViewer, File[] input, final String expandToDirectoryPath) {

		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(input);
				if(expandToDirectoryPath != null && !expandToDirectoryPath.equals("")) {
					File elementOrTreePath = new File(expandToDirectoryPath);
					if(elementOrTreePath.exists()) {
						treeViewer.expandToLevel(elementOrTreePath, 1);
					}
				}
			}
		});
	}
}
