/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.parts;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.Activator;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.RCPUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.project.ProjectManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ProjectExplorerPart {

	private TreeViewer treeViewer;
	private Composite composite;

	@PostConstruct
	public void createControls(Composite composite) {

		this.composite = composite;
		Composite compositeMain = new Composite(composite, SWT.NONE);
		compositeMain.setLayout(new FillLayout(SWT.HORIZONTAL));
		treeViewer = new TreeViewer(compositeMain, SWT.BORDER);
		initTree();
	}

	private void initTree() {

		treeViewer.setContentProvider(new MSTreeNavigator.TreeContentProvider());
		treeViewer.setLabelProvider(new MSTreeNavigator.TreeLabelProvider());
		ProjectManager pm = Activator.getDefault().getProjectManager();
		try {
			treeViewer.setInput(pm.findProjects());
		} catch(IOException e) {
			RCPUtil.showWarningMessageDialog("Error occur", composite.getShell());
		}
	}
}
