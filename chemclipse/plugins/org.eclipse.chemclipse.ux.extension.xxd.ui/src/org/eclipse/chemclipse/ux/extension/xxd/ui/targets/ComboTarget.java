/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.targets;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.autoComplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.targets.TargetListUtil;
import org.eclipse.chemclipse.model.targets.TargetValidator;
import org.eclipse.chemclipse.model.updates.ITargetUpdateListener;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ComboTarget extends Composite {

	private final TargetListUtil targetListUtil = new TargetListUtil();
	private final TargetValidator targetValidator = new TargetValidator();
	//
	private Combo combo;
	private ControlDecoration controlDecoration;
	//
	private ITargetUpdateListener targetUpdateListener = null;

	public ComboTarget(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setTargetUpdateListener(ITargetUpdateListener targetUpdateListener) {

		this.targetUpdateListener = targetUpdateListener;
	}

	public void setText(String text) {

		combo.setText(text);
	}

	/**
	 * This methods tries to create a target using the stored text.
	 * It may return null if the target validation fails.
	 * 
	 * @return {@link IIdentificationTarget}
	 */
	public IIdentificationTarget createTarget() {

		if(validate()) {
			return targetValidator.getIdentificationTarget();
		}
		return null;
	}

	public void updateContentProposals() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean useTargetList = preferenceStore.getBoolean(PreferenceSupplier.P_USE_TARGET_LIST);
		//
		String[] items;
		if(useTargetList) {
			items = targetListUtil.parseString(preferenceStore.getString(PreferenceSupplier.P_TARGET_LIST));
			Arrays.sort(items);
		} else {
			items = new String[]{};
		}
		//
		combo.setItems(items);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		combo = createCombo(this);
	}

	private Combo createCombo(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.NONE);
		combo.setText("");
		combo.setToolTipText("Select a target or type in a new substance name.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		controlDecoration = new ControlDecoration(combo, SWT.LEFT | SWT.TOP);
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					IIdentificationTarget identificationTarget = createTarget();
					if(targetUpdateListener != null) {
						targetUpdateListener.update(identificationTarget);
					}
				}
			}
		});
		enableAutoComplete(combo);
		return combo;
	}

	private void enableAutoComplete(Combo combo) {

		IContentProposalProvider proposalProvider = new IContentProposalProvider() {

			@Override
			public IContentProposal[] getProposals(String contents, int position) {

				List<ContentProposal> list = new ArrayList<>();
				if(contents != null) {
					String[] items = combo.getItems();
					for(String item : items) {
						if(item.toLowerCase().contains(contents.toLowerCase())) {
							list.add(new ContentProposal(item));
						}
					}
				}
				return list.toArray(new IContentProposal[0]);
			}
		};
		autoComplete(combo, proposalProvider);
	}

	private boolean validate() {

		IStatus status = targetValidator.validate(combo.getText());
		if(status.isOK()) {
			controlDecoration.hide();
			clearLabelInputErrors(combo);
			return true;
		} else {
			setLabelInputError(combo, status.getMessage());
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			return false;
		}
	}

	private void clearLabelInputErrors(Combo combo) {

		combo.setToolTipText(TargetListUtil.EXAMPLE);
	}

	private void setLabelInputError(Combo combo, String message) {

		combo.setToolTipText(message);
	}
}
