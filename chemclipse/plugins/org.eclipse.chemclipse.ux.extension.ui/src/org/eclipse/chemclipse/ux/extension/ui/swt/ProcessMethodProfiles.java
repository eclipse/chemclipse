/*******************************************************************************
 * Copyright (c) 2021, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.model.SeparationColumnType;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
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

	private AtomicReference<ComboViewer> comboViewerProfiles = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerColumns = new AtomicReference<>();
	private AtomicReference<Button> buttonAdd = new AtomicReference<>();
	private AtomicReference<Button> buttonDelete = new AtomicReference<>();

	private ProcessMethod processMethod;
	private boolean enabledEdit = true;
	private IUpdateListener updateListener = null;

	public ProcessMethodProfiles(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ProcessMethod processMethod) {

		this.processMethod = processMethod;
		updateProfilesColumn();
	}

	public void setEnabledEdit(boolean enabledEdit) {

		this.enabledEdit = enabledEdit;
		updateProfilesColumn();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setColumnsVisible(boolean visible) {

		GridLayout gridLayout = (GridLayout)getLayout();
		gridLayout.numColumns = visible ? 4 : 3;
		PartSupport.setCompositeVisibility(comboViewerColumns.get().getCombo(), visible);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);

		createComboViewerProfiles(this);
		createComboViewerColumns(this);
		createButtonAdd(this);
		createButtonDelete(this);

		updateProfilesColumn();
	}

	private void createComboViewerProfiles(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
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
					updateProfilesColumn();
					fireUpdate();
				}
			}
		});

		comboViewerProfiles.set(comboViewer);
	}

	private void createComboViewerColumns(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof SeparationColumnType separationColumnType) {
					return separationColumnType.label();
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select a column.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					/*
					 * Set the active profile.
					 */
					String activeProfile = getActiveProfile();
					SeparationColumnType separationColumnType = getSeparationColumnType();
					processMethod.getProfileColumnsMap().put(activeProfile, separationColumnType);
					fireUpdate();
				}
			}
		});

		comboViewerColumns.set(comboViewer);
	}

	private void createButtonAdd(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setToolTipText("Add a new profile.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));

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
								} else if(IProcessEntryContainer.DEFAULT_PROFILE.equals(input)) {
									return "The default profile can't be used.";
								} else if(processMethod.getProfiles().contains(input)) {
									return "The profile exists already.";
								}
							}

							return null;
						}
					});

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
						updateProfilesColumn();
						fireUpdate();
					}
				}
			}
		});

		buttonAdd.set(button);
	}

	private void createButtonDelete(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setToolTipText("Delete the profile.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					String activeProfile = getActiveProfile();
					if(!IProcessEntryContainer.DEFAULT_PROFILE.equals(activeProfile)) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete Profile");
						messageBox.setMessage("Do you really want to delete the selected profile?");
						int decision = messageBox.open();
						if(SWT.YES == decision) {
							processMethod.getProfileColumnsMap().remove(activeProfile);
							processMethod.deleteProfile(activeProfile);
							updateProfilesColumn();
							fireUpdate();
						}
					}
				}
			}
		});

		buttonDelete.set(button);
	}

	private String getActiveProfile() {

		Object object = comboViewerProfiles.get().getStructuredSelection().getFirstElement();
		if(object instanceof String text) {
			return text;
		}

		return IProcessEntryContainer.DEFAULT_PROFILE;
	}

	private SeparationColumnType getSeparationColumnType() {

		Object object = comboViewerColumns.get().getStructuredSelection().getFirstElement();
		if(object instanceof SeparationColumnType separationColumnType) {
			return separationColumnType;
		}

		return SeparationColumnType.DEFAULT;
	}

	private SeparationColumnType getSeparationColumnTypeMethod() {

		SeparationColumnType separationColumnType = SeparationColumnType.DEFAULT;
		if(processMethod != null) {
			String activeProfile = processMethod.getActiveProfile();
			separationColumnType = processMethod.getProfileColumnsMap().getOrDefault(activeProfile, SeparationColumnType.DEFAULT);
		}

		return separationColumnType;
	}

	private void updateProfilesColumn() {

		updateProfiles();
		updateColumns();
	}

	private void updateProfiles() {

		if(processMethod != null) {
			/*
			 * Ensure that the default profile is available.
			 */
			String defaultProfile = IProcessEntryContainer.DEFAULT_PROFILE;
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
			comboViewerProfiles.get().setInput(profiles.toArray());
			/*
			 * Get the selected profile.
			 */
			String activeProfile = processMethod.getActiveProfile();

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
			comboViewerProfiles.get().getCombo().select(index);
		} else {
			comboViewerProfiles.get().setInput(new String[]{IProcessEntryContainer.DEFAULT_PROFILE});
			comboViewerProfiles.get().getCombo().select(0);
		}
		/*
		 * Enable/Disable the buttons.
		 */
		boolean isEditable = enabledEdit && isMethodEditable(processMethod);
		buttonAdd.get().setEnabled(isEditable);
		buttonDelete.get().setEnabled(isEditable && !IProcessEntryContainer.DEFAULT_PROFILE.equals(getActiveProfile()));
	}

	private void updateColumns() {

		comboViewerColumns.get().setInput(SeparationColumnType.values());
		SeparationColumnType separationColumnType = getSeparationColumnTypeMethod();
		comboViewerColumns.get().setSelection(new StructuredSelection(separationColumnType));
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