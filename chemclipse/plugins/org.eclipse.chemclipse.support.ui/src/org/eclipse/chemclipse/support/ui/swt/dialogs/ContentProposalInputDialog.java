/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.dialogs;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.autoComplete;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ContentProposalInputDialog extends InputDialog {

	private final IContentProposalProvider contentProposalProvider;

	public ContentProposalInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator, IContentProposalProvider contentProposalProvider) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.contentProposalProvider = contentProposalProvider;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Control area = super.createDialogArea(parent);
		Text input = getText();
		autoComplete(input, contentProposalProvider);
		return area;
	}
}
