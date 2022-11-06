/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class LibraryInformationComposite extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	//
	private ILibraryInformation libraryInformation = null;

	public LibraryInformationComposite(Composite parent, int style) {

		super(parent, style);
	}

	public void setInput(ILibraryInformation libraryInformation) {

		if(this.libraryInformation != libraryInformation) {
			this.libraryInformation = libraryInformation;
			updateToolbarInfo();
			updateInput();
		}
	}

	public ILibraryInformation getLibraryInformation() {

		return libraryInformation;
	}

	protected abstract void updateInput();

	protected void createButtonToolbarInfo(Composite parent) {

		buttonToolbarInfo = createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
	}

	protected void initializeToolbarInfo() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
	}

	protected void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void updateToolbarInfo() {

		if(libraryInformation != null) {
			toolbarInfo.get().setText(libraryInformation.getName());
		} else {
			toolbarInfo.get().setText("--");
		}
	}
}