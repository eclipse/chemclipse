/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ViewFocusHelper {

	@Inject
	public ViewFocusHelper(Composite parent) {
		initialize(parent);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new RowLayout());
		//
		Button buttonPeaks = new Button(parent, SWT.PUSH);
		buttonPeaks.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAKS, IApplicationImage.SIZE_16x16));
		buttonPeaks.setToolTipText("Focus Peaks");
		buttonPeaks.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out.println("Show Peaks Views");
			}
		});
		//
		Button buttonPCA = new Button(parent, SWT.PUSH);
		buttonPCA.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PCA, IApplicationImage.SIZE_16x16));
		buttonPCA.setToolTipText("Focus PCA");
		buttonPCA.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out.println("Show PCA Views");
			}
		});
	}
}
