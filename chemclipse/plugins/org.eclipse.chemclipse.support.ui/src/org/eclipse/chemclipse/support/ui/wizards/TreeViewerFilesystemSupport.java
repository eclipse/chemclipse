/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

public class TreeViewerFilesystemSupport {

	public static void retrieveAndSetLocalFileSystem(final TreeViewer treeViewer) {

		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(EFS.getLocalFileSystem());
			}
		});
	}
}
