/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public interface IExtendedPartUI {

	default Button createButtonToggleToolbar(Composite parent, AtomicReference<Composite> toolbar, String image, String tooltip) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Composite composite = toolbar.get();
				if(composite != null) {
					boolean active = PartSupport.toggleCompositeVisibility(composite);
					setButtonImage(button, image, tooltip, active);
				}
			}
		});
		//
		return button;
	}

	default void enableToolbar(AtomicReference<Composite> toolbar, Button button, String image, String tooltip, boolean active) {

		Composite composite = toolbar.get();
		if(composite != null) {
			PartSupport.setCompositeVisibility(composite, active);
			setButtonImage(button, image, tooltip, active);
		}
	}

	default void setButtonImage(Button button, String image, String tooltip, boolean active) {

		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16, active));
		button.setToolTipText(active ? "Hide " + tooltip : "Show " + tooltip);
	}
}
