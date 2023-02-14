/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.net.URL;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.target.ITargetIdentifierSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TargetIdentifierUI extends Composite {

	private ILibraryInformation libraryInformation;
	//
	private Button button;
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.identifier.targetIdentifier";
	private static final Logger logger = Logger.getLogger(TargetIdentifierUI.class);

	public TargetIdentifierUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	private URL getURL() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : config) {
			try {
				Object object = element.createExecutableExtension("targetURL");
				if(object instanceof ITargetIdentifierSupplier identifier) {
					return identifier.getURL(libraryInformation);
				}
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return null;
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setInput(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		setEnabled(libraryInformation != null);
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		button = createButton(composite);
		//
		initialize();
	}

	private void initialize() {

		setEnabled(false);
	}

	private Button createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : config) {
			button.setToolTipText(element.getAttribute(Identifier.IDENTIFIER_NAME));
		}
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_BROWSER, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				launchBrowser();
			}
		});
		//
		return button;
	}

	private void launchBrowser() {

		URL url = getURL();
		if(url != null) {
			Program.launch(url.toString());
		}
	}
}
