/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TaskQuickAccessPart {

	@Inject
	public TaskQuickAccessPart(Composite parent) {
		initialize(parent);
	}

	private void initialize(Composite parent) {

		/*
		 * Add buttons here to focus specialized views.
		 */
		parent.setLayout(new RowLayout());
		createButtonOverlayTask(parent);
		createButtonOverviewTask(parent);
		//
		initializeParts();
	}

	private void initializeParts() {

		/*
		 * It's important to set the initial visibility of the parts.
		 * Otherwise, PartSupport.togglePartVisibility won't work as expected.
		 */
		PartSupport.setPartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY, PartSupport.PARTSTACK_BOTTOM_LEFT, false);
		PartSupport.setPartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEADER, PartSupport.PARTSTACK_OVERVIEW, false);
		PartSupport.setPartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, PartSupport.PARTSTACK_OVERVIEW, false);
	}

	private void createButtonOverlayTask(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overlay modus");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Chromatogram Overview
				 */
				MPart part = PartSupport.getPart(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY, PartSupport.PARTSTACK_BOTTOM_LEFT);
				PartSupport.togglePartVisibility(part, PartSupport.PARTSTACK_BOTTOM_LEFT);
			}
		});
	}

	private void createButtonOverviewTask(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overview modus");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Show the part stack on demand.
				 */
				PartSupport.setPartStackVisibility(PartSupport.PARTSTACK_OVERVIEW, true);
				/*
				 * Chromatogram Header
				 */
				MPart chromatogramHeaderPart = PartSupport.getPart(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEADER, PartSupport.PARTSTACK_OVERVIEW);
				PartSupport.togglePartVisibility(chromatogramHeaderPart, PartSupport.PARTSTACK_OVERVIEW);
				/*
				 * Chromatogram Overview
				 */
				MPart chromatogramOverviewPart = PartSupport.getPart(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, PartSupport.PARTSTACK_OVERVIEW);
				PartSupport.togglePartVisibility(chromatogramOverviewPart, PartSupport.PARTSTACK_OVERVIEW);
			}
		});
	}
}
