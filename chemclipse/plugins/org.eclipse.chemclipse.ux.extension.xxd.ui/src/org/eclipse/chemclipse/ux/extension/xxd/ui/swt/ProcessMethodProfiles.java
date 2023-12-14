/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

public class ProcessMethodProfiles extends Composite {

	private ComboViewer comboViewerProfiles;
	private Button buttonAdd;
	private Button buttonDelete;
	//
	private ProcessMethod processMethod;
	private boolean enabledEdit = true;
	private IUpdateListener updateListener = null;

	public ProcessMethodProfiles(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ProcessMethod processMethod) {

		this.processMethod = processMethod;
		updateProfiles();
	}

	public void setEnabledEdit(boolean enabledEdit) {

		this.enabledEdit = enabledEdit;
		updateProfiles();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		comboViewerProfiles = createComboViewerProfiles(this);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
		//
		updateProfiles();
	}

	private ComboViewer createComboViewerProfiles(Composite composite) {

		ComboViewer comboViewer = new EnhancedComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String text) {
					return text;
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select a profile.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					/*
					 * Set the active profile.
					 */
					String activeProfile = getActiveProfile();
					processMethod.setActiveProfile(activeProfile);
					updateProfiles();
					fireUpdate();
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonAdd(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a new profile.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Profile", "Create a new profile.", "", new IInputValidator() {

						@Override
						public String isValid(String input) {

							if(input == null) {
								return "Please set a profile name.";
							} else {
								input = input.trim();
								if(input.isEmpty()) {
									return "The profile name must be not empty.";
								} else if(ProcessEntryContainer.DEFAULT_PROFILE.equals(input)) {
									return "The default profile can't be used.";
								} else if(processMethod.getProfiles().contains(input)) {
									return "The profile exists already.";
								}
							}
							//
							return null;
						}
					});
					//
					if(IDialogConstants.OK_ID == dialog.open()) {
						/*
						 * Get the active profile and copy the settings.
						 */
						String previousProfile = processMethod.getActiveProfile();
						String newProfile = dialog.getValue().trim();
						processMethod.setActiveProfile(newProfile);
						for(IProcessEntry processEntry : processMethod.getEntries()) {
							processEntry.copySettings(previousProfile);
						}
						updateProfiles();
						fireUpdate();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonDelete(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the profile.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					String activeProfile = getActiveProfile();
					if(!ProcessEntryContainer.DEFAULT_PROFILE.equals(activeProfile)) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete Profile");
						messageBox.setMessage("Do you really want to delete the selected profile?");
						int decision = messageBox.open();
						if(SWT.YES == decision) {
							processMethod.deleteProfile(activeProfile);
							updateProfiles();
							fireUpdate();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private String getActiveProfile() {

		Object object = comboViewerProfiles.getStructuredSelection().getFirstElement();
		if(object instanceof String text) {
			return text;
		}
		//
		return ProcessEntryContainer.DEFAULT_PROFILE;
	}

	private void updateProfiles() {

		if(processMethod != null) {
			/*
			 * Ensure that the default profile is available.
			 */
			String defaultProfile = ProcessEntryContainer.DEFAULT_PROFILE;
			if(!processMethod.getProfiles().contains(defaultProfile)) {
				processMethod.addProfile(defaultProfile);
			}
			/*
			 * Sort the items and set the default profile at first position.
			 */
			Set<String> profileSet = new HashSet<>(processMethod.getProfiles());
			profileSet.remove(defaultProfile);
			List<String> profiles = new ArrayList<>(profileSet);
			Collections.sort(profiles);
			profiles.add(0, defaultProfile);
			comboViewerProfiles.setInput(profiles.toArray());
			/*
			 * Get the selected profile.
			 */
			String activeProfile = processMethod.getActiveProfile();
			//
			int index = 0;
			exitloop:
			for(int i = 0; i < profiles.size(); i++) {
				if(profiles.get(i).equals(activeProfile)) {
					index = i;
					break exitloop;
				}
			}
			/*
			 * Set the selected profile.
			 */
			comboViewerProfiles.getCombo().select(index);
		} else {
			comboViewerProfiles.setInput(new String[]{ProcessEntryContainer.DEFAULT_PROFILE});
			comboViewerProfiles.getCombo().select(0);
		}
		/*
		 * Enable/Disable the buttons.
		 */
		boolean isEditable = enabledEdit && isMethodEditable(processMethod);
		buttonAdd.setEnabled(isEditable);
		buttonDelete.setEnabled(isEditable && !ProcessEntryContainer.DEFAULT_PROFILE.equals(getActiveProfile()));
	}

	private boolean isMethodEditable(ProcessMethod processMethod) {

		return processMethod != null && !processMethod.isFinal() && !processMethod.isReadOnly();
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}
