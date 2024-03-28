/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences.DialogBehavior;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.editor.SystemEditor;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class SettingsPreferencesPage<T> extends WizardPage {

	private static final Logger logger = Logger.getLogger(SettingsPreferencesPage.class);
	//
	private static final String DATA_URL = "DATA_URL";
	private static final int MAX_LENGTH_LITERATURE_REFERENCE = 97; // 100 = 97 + ...
	//
	private AtomicReference<Button> buttonDefault = new AtomicReference<>();
	private AtomicReference<Button> buttonUser = new AtomicReference<>();
	private AtomicReference<SettingsUI<?>> settingsUI = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerLiterature = new AtomicReference<>();
	private AtomicReference<Button> buttonLink = new AtomicReference<>();
	private AtomicReference<Button> buttonRestoreDefaults = new AtomicReference<>();
	//
	private boolean isDontAskAgain;
	private boolean isUseSystemDefaults;
	private String jsonSettings;
	//
	private final IProcessorPreferences<T> preferences;
	private final boolean showProfileToolbar;

	public SettingsPreferencesPage(IProcessorPreferences<T> preferences, boolean showProfileToolbar) {

		super(SettingsPreferencesPage.class.getName());
		this.preferences = preferences;
		this.showProfileToolbar = showProfileToolbar;
	}

	@Override
	public void createControl(Composite parent) {

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayout(new GridLayout(1, true));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Composite control = createOptionSection(scrolledComposite);
		//
		scrolledComposite.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setContent(control);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setAlwaysShowScrollBars(false);
		//
		setControl(scrolledComposite);
	}

	public boolean getIsDontAskAgainEdited() {

		return isDontAskAgain;
	}

	public String getSettingsEdited() {

		return jsonSettings;
	}

	public boolean getIsUseSystemDefaultsEdited() {

		return isUseSystemDefaults;
	}

	private Composite createOptionSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createLiteratureSection(composite);
		createSystemOptions(composite);
		createLabelSeparator(composite);
		createUserOptions(composite);
		createProcessSection(composite);
		//
		return composite;
	}

	private void createLiteratureSection(Composite parent) {

		List<LiteratureReference> literatureReferences = preferences.getSupplier().getLiteratureReferences();
		boolean useLiterature = !literatureReferences.isEmpty();
		/*
		 * Create the literature references only on demand.
		 */
		if(useLiterature) {
			createLabelText(parent, ExtensionMessages.literatureReferences);
			createReferenceSection(parent, literatureReferences);
			createLabelText(parent, ExtensionMessages.settings);
		}
	}

	private void createReferenceSection(Composite parent, List<LiteratureReference> literatureReferences) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		createComboViewerLiterature(composite);
		createButtonDOI(composite);
		//
		comboViewerLiterature.get().setInput(literatureReferences);
		comboViewerLiterature.get().setSelection(new StructuredSelection(literatureReferences.get(0)));
		updateLiteratureSelection();
	}

	private void createComboViewerLiterature(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ListContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof LiteratureReference literatureReference) {
					String title = literatureReference.getTitle();
					if(title.isEmpty()) {
						return ExtensionMessages.literatureReference;
					} else {
						if(title.length() > MAX_LENGTH_LITERATURE_REFERENCE) {
							title = title.substring(0, MAX_LENGTH_LITERATURE_REFERENCE) + "...";
						}
						return title;
					}
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText(ExtensionMessages.literatureReferences);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateLiteratureSelection();
			}
		});
		//
		comboViewerLiterature.set(comboViewer);
	}

	private void updateLiteratureSelection() {

		Object object = comboViewerLiterature.get().getStructuredSelection().getFirstElement();
		if(object instanceof LiteratureReference literatureReference) {
			updateLiterature(literatureReference);
		}
	}

	private void updateLiterature(LiteratureReference literatureReference) {

		String url = "";
		if(literatureReference != null) {
			url = literatureReference.getUrl();
			UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_LITERATURE_UPDATE, literatureReference.getContent());
		}
		//
		buttonLink.get().setData(DATA_URL, url);
		buttonLink.get().setEnabled(!url.isEmpty());
		buttonLink.get().setToolTipText(!url.isEmpty() ? url : ExtensionMessages.noLinkIsSupplierYet);
	}

	private void createButtonDOI(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ExtensionMessages.openInExternalBrowser);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_BROWSER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object data = button.getData(DATA_URL);
				if(data instanceof String value) {
					if(!value.isEmpty()) {
						try {
							URL url = new URL(value);
							SystemEditor.browse(url);
						} catch(MalformedURLException e1) {
							logger.warn(e1);
						}
					}
				}
			}
		});
		//
		buttonLink.set(button);
	}

	private void createSystemOptions(Composite parent) {

		buttonDefault.set(createButtonDefault(parent));
	}

	private Button createButtonDefault(Composite parent) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText(ExtensionMessages.useSystemOptions);
		if(preferences.requiresUserSettings()) {
			button.setEnabled(false);
			button.setToolTipText(ExtensionMessages.noSystemOptionsAvailable);
		}
		//
		return button;
	}

	private void createLabelSeparator(Composite parent) {

		Label label = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelText(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createUserOptions(Composite parent) {

		createButtonUser(parent);
		createSettingsUI(parent);
	}

	private void createButtonUser(Composite parent) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText(ExtensionMessages.useSpecificOptions);
		//
		buttonUser.set(button);
	}

	private void createSettingsUI(Composite parent) {

		SettingsUI<?> control = null;
		//
		try {
			control = new SettingsUI<>(parent, preferences, showProfileToolbar);
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch(IOException e) {
			throw new RuntimeException("Reading the settings failed.", e);
		}
		//
		settingsUI.set(control);
	}

	private void createProcessSection(Composite parent) {

		Listener validationListener = createValidationListener();
		SelectionListener selectionListener = createSelectionListener(validationListener);
		//
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		addButtonRememberSettings(composite, selectionListener);
		addButtonRestoreDefaults(composite, selectionListener);
		//
		settingsUI.get().getControl().addChangeListener(validationListener);
	}

	private Listener createValidationListener() {

		return new Listener() {

			@Override
			public void handleEvent(Event event) {

				jsonSettings = null;
				if(buttonUser.get().getSelection()) {
					IStatus validate = settingsUI.get().getControl().validate();
					if(validate.isOK()) {
						setErrorMessage(null);
						setPageComplete(true);
					} else {
						setErrorMessage(validate.getMessage());
						setPageComplete(false);
					}
				} else {
					setErrorMessage(null);
					setPageComplete(true);
				}
				/*
				 * User Specific Settings
				 */
				try {
					jsonSettings = settingsUI.get().getControl().getSettings();
				} catch(Exception e) {
					logger.warn("Error while fetching the settings.");
					logger.warn(e);
					setErrorMessage(e.toString());
					setPageComplete(false);
				}
			}
		};
	}

	private SelectionListener createSelectionListener(Listener validationListener) {

		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				settingsUI.get().setEnabled(buttonUser.get().getSelection());
				validationListener.handleEvent(null);
				isUseSystemDefaults = buttonDefault.get().getSelection();
			}
		};
	}

	private void addButtonRememberSettings(Composite parent, SelectionListener selectionListener) {

		buttonDefault.get().addSelectionListener(selectionListener);
		buttonUser.get().addSelectionListener(selectionListener);
		//
		if(preferences.getDialogBehaviour() == DialogBehavior.NONE) {
			isDontAskAgain = false;
			addLabelNoOption(parent);
		} else {
			addButtonDontAskAgain(parent);
		}
		/*
		 * Defaults
		 */
		if(preferences.isUseSystemDefaults() && !preferences.requiresUserSettings()) {
			buttonDefault.get().setSelection(true);
		} else {
			buttonUser.get().setSelection(true);
		}
		//
		selectionListener.widgetSelected(null);
	}

	private void addLabelNoOption(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		label.setToolTipText("The option remember the settings is not available here.");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void addButtonDontAskAgain(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText(ExtensionMessages.rememberDecision);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isDontAskAgain = button.getSelection();
			}
		});
		//
		isDontAskAgain = preferences.getDialogBehaviour() != DialogBehavior.SHOW;
		button.setSelection(isDontAskAgain);
	}

	private void addButtonRestoreDefaults(Composite parent, SelectionListener selectionListener) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ExtensionMessages.resetDefaults);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				settingsUI.get().getControl().restoreDefaults();
			}
		});
		button.addSelectionListener(selectionListener);
		//
		buttonRestoreDefaults.set(button);
	}
}