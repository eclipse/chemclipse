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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TimeRangeDialog extends Dialog {

	private TimeRangeLabels timeRangeLabels;
	private IInputValidator inputValidator = null;
	private AtomicReference<Combo> comboControl = new AtomicReference<>();
	private AtomicReference<Label> feedbackControl = new AtomicReference<>();
	//
	private String identifier = "";

	public TimeRangeDialog(Shell shell, TimeRangeLabels timeRangeLabels, IInputValidator inputValidator) {

		super(shell);
		//
		this.timeRangeLabels = timeRangeLabels;
		this.inputValidator = inputValidator;
	}

	public String getIdentifier() {

		return identifier;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		if(timeRangeLabels != null) {
			shell.setText(timeRangeLabels.getTitle());
		}
	}

	@Override
	protected Point getInitialSize() {

		return new Point(450, 150);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		//
		createComboViewer(composite);
		createFeedbackLabel(composite);
		assignIdentifier();
		//
		return composite;
	}

	private void createComboViewer(Composite parent) {

		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		Combo combo = new Combo(parent, SWT.BORDER);
		if(OperatingSystemUtils.isLinux()) {
			combo.setBackground(combo.getBackground());
		}
		//
		combo.setToolTipText("Select or type in a new name.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				assignIdentifier();
			}
		});
		//
		combo.setItems(timeRangeLabels.getProposals());
		combo.setText(timeRangeLabels.getInitialValue());
		enableProposals(combo);
		//
		comboControl.set(combo);
	}

	private void createFeedbackLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		label.setToolTipText("Validation");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		feedbackControl.set(label);
	}

	private void enableProposals(Combo combo) {

		IContentProposalProvider contentProposalProvider = new IContentProposalProvider() {

			@Override
			public IContentProposal[] getProposals(String contents, int position) {

				List<ContentProposal> contentProposals = new ArrayList<>();
				if(contents != null) {
					for(String proposal : timeRangeLabels.getProposals()) {
						if(proposal.toLowerCase().contains(contents.toLowerCase())) {
							contentProposals.add(new ContentProposal(proposal));
						}
					}
				}
				return contentProposals.toArray(new IContentProposal[0]);
			}
		};
		//
		enableProposals(combo, contentProposalProvider);
	}

	private ContentProposalAdapter enableProposals(Combo combo, IContentProposalProvider contentProposalProvider) {

		ContentProposalAdapter contentProposalAdapter = new ContentProposalAdapter(combo, new ComboContentAdapter(), contentProposalProvider, null, null);
		contentProposalAdapter.setPropagateKeys(true);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		//
		return contentProposalAdapter;
	}

	private void assignIdentifier() {

		this.identifier = comboControl.get().getText();
		validateInput();
	}

	private void validateInput() {

		String message = null;
		if(inputValidator != null) {
			message = inputValidator.isValid(identifier);
		}
		//
		Control button = getButton(IDialogConstants.OK_ID);
		if(button != null) {
			button.setEnabled(message == null);
		}
		//
		Label label = feedbackControl.get();
		if(message == null) {
			label.setBackground(Colors.getColor(Colors.LIGHT_GREEN));
			label.setText("The name is valid.");
		} else {
			label.setBackground(Colors.getColor(Colors.LIGHT_YELLOW));
			label.setText(message);
		}
	}
}