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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.ui.updates.IUpdateListenerUI;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.swt.ui.services.ImageServiceInput;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMolecule;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.MoleculeImageServiceSupport;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ExtendedMoleculeUI extends Composite implements IExtendedPartUI {

	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarEdit = new AtomicReference<>();
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerServices = new AtomicReference<>();
	private AtomicReference<Text> textInput = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerInput = new AtomicReference<>();
	private AtomicReference<MoleculeUI> moleculeControl = new AtomicReference<>();
	private AtomicReference<Text> textMolecule = new AtomicReference<>();
	//
	private ILibraryInformation libraryInformation;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ExtendedMoleculeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public boolean setFocus() {

		updateOnFocus();
		return true;
	}

	public void clear() {

		setInput(null);
	}

	public void setInput(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		updateContent(Display.getDefault());
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarEdit(this);
		createTabFolderSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);
		/*
		 * Input
		 */
		updateComboViewerInputTypes();
		updateContent(Display.getDefault());
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleInfo(composite);
		createButtonToggleEdit(composite);
		createComboViewerServices(composite);
		createButtonReset(composite);
		createButtonExport(composite);
		createSettingsButton(composite);
	}

	private void createButtonToggleInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonToggleEdit(Composite parent) {

		buttonToolbarEdit.set(createButtonToggleToolbar(parent, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT));
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createTextInput(composite);
		createComboViewerInput(composite);
		createButtonCalculate(composite);
		//
		toolbarEdit.set(composite);
	}

	private void createTextInput(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setToolTipText("Input");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(isEnterPressed(e)) {
					libraryInformation = createLibraryInformationByInput();
					updateContent(e.display);
				}
			}
		});
		//
		textInput.set(text);
	}

	private void createComboViewerInput(Composite composite) {

		ComboViewer comboViewer = new EnhancedComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ImageServiceInput imageServiceInput) {
					return imageServiceInput.label();
				}
				//
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select an image service input type.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setTextInput();
			}
		});
		//
		comboViewerInput.set(comboViewer);
	}

	private Button createButtonCalculate(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Calculate the molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CALCULATE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				libraryInformation = createLibraryInformationByInput();
				updateContent(e.display);
			}
		});
		//
		return button;
	}

	private void createTabFolderSection(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createMoleculeImage(tabFolder);
		createMoleculeContent(tabFolder);
	}

	private void createMoleculeImage(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Molecule");
		//
		Composite composite = new Composite(tabFolder, SWT.BORDER);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createMoleculeUI(composite);
		tabItem.setControl(composite);
	}

	private void createMoleculeUI(Composite parent) {

		MoleculeUI moleculeUI = new MoleculeUI(parent, SWT.NONE);
		moleculeUI.setUpdateListenerUI(new IUpdateListenerUI() {

			@Override
			public void update(Display display) {

				/*
				 * Only update the combo box if the service has been
				 * changed via the MoleculeServiceDialog.
				 */
				IMoleculeImageService moleculeImageService = MoleculeImageServiceSupport.getMoleculeImageServiceSelection();
				if(moleculeImageService != null) {
					comboViewerServices.get().setSelection(new StructuredSelection(moleculeImageService));
				}
			}
		});
		//
		moleculeControl.set(moleculeUI);
	}

	private void createMoleculeContent(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Content");
		//
		Text text = createTextMolecule(tabFolder);
		text.setEditable(false);
		tabItem.setControl(text);
		//
		textMolecule.set(text);
	}

	private Text createTextMolecule(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setText("");
		text.setToolTipText("Molecule Structure");
		//
		return text;
	}

	private void createComboViewerServices(Composite parent) {

		ComboViewer comboViewer = MoleculeImageServiceSupport.createComboViewerServices(parent);
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboViewer.getStructuredSelection().getFirstElement() instanceof IMoleculeImageService moleculeImageService) {
					PreferenceSupplier.setMoleculeImageServiceId(moleculeImageService.getClass().getName());
					updateMoleculeService(Display.getDefault());
				}
			}
		});
		//
		comboViewerServices.set(comboViewer);
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateMoleculeService(e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export the molecule image");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ImageData imageData = moleculeControl.get().getImageData();
				if(imageData != null) {
					FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText("Save Molecule");
					fileDialog.setFilterExtensions(new String[]{"*.png"});
					fileDialog.setFilterNames(new String[]{"Portable Network Graphics  (*.png)"});
					fileDialog.setFileName(getExportName());
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceSupplier.P_MOLECULE_PATH_EXPORT));
					//
					String pathname = fileDialog.open();
					if(pathname != null) {
						preferenceStore.setValue(PreferenceSupplier.P_MOLECULE_PATH_EXPORT, fileDialog.getFilterPath());
						ImageLoader loader = new ImageLoader();
						loader.data = new ImageData[]{imageData};
						loader.save(pathname, SWT.IMAGE_PNG);
					}
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		/*
		 * Default page
		 */
		List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
		preferencePages.add(PreferencePageMolecule.class);
		/*
		 * Additional pages.
		 */
		Object[] moleculeImageServices = Activator.getDefault().getMoleculeImageServices();
		if(moleculeImageServices != null) {
			for(Object object : moleculeImageServices) {
				if(object instanceof IMoleculeImageService moleculeImageService) {
					Class<? extends IWorkbenchPreferencePage> preferencePage = moleculeImageService.getPreferencePage();
					if(preferencePage != null) {
						preferencePages.add(preferencePage);
					}
				}
			}
		}
		//
		createSettingsButton(parent, preferencePages, new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				updateMoleculeService(display);
			}
		});
	}

	private void updateMoleculeService(Display display) {

		/*
		 * Update the content
		 */
		moleculeControl.get().setMoleculeImageService(MoleculeImageServiceSupport.getMoleculeImageServiceSelection());
		moleculeControl.get().clear(display);
		updateContent(display);
	}

	private boolean isEnterPressed(KeyEvent e) {

		return (e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR);
	}

	private String getMoleculeInformation(ILibraryInformation libraryInformation) {

		StringBuilder builder = new StringBuilder();
		//
		if(libraryInformation != null) {
			builder.append(getInfo(libraryInformation.getName()));
			builder.append(" | ");
			builder.append(getInfo(libraryInformation.getCasNumber()));
			builder.append(" | ");
			builder.append(getInfo(libraryInformation.getSmiles()));
		}
		//
		return builder.toString();
	}

	private String getInfo(String value) {

		return value.isEmpty() ? "--" : value;
	}

	private ImageServiceInput getImageInput() {

		Object object = comboViewerInput.get().getStructuredSelection().getFirstElement();
		if(object instanceof ImageServiceInput imageServiceInput) {
			return imageServiceInput;
		}
		/*
		 * Default
		 */
		return ImageServiceInput.NAME;
	}

	private String getExportName() {

		String input = textInput.get().getText().trim();
		String name = input.replace(":", "");
		name = name.isEmpty() ? "Unkown" : name;
		int length = preferenceStore.getInt(PreferenceSupplier.P_LENGTH_MOLECULE_NAME_EXPORT);
		if(length >= PreferenceSupplier.MIN_LENGTH_NAME_EXPORT && name.length() > length) {
			return name.substring(0, 20);
		} else {
			return name;
		}
	}

	private void updateContent(Display display) {

		updateWidgets();
		moleculeControl.get().setInput(display, libraryInformation);
	}

	private void updateWidgets() {

		toolbarInfo.get().setText(getMoleculeInformation(libraryInformation));
		setTextMolecule();
		setTextInput();
	}

	private void setTextMolecule() {

		if(libraryInformation != null) {
			textMolecule.get().setText(libraryInformation.getMoleculeStructure());
		} else {
			textMolecule.get().setText("");
		}
	}

	private void setTextInput() {

		if(libraryInformation != null) {
			ImageServiceInput imageInput = getImageInput();
			switch(imageInput) {
				case SMILES:
					String smiles = libraryInformation.getSmiles();
					if(!smiles.isEmpty()) {
						textInput.get().setText(smiles);
					}
					break;
				default:
					String name = libraryInformation.getName();
					if(!name.isEmpty()) {
						textInput.get().setText(name);
					}
					break;
			}
		} else {
			textInput.get().setText("");
		}
	}

	private ILibraryInformation createLibraryInformationByInput() {

		ILibraryInformation libraryInformationByInput = new LibraryInformation();
		//
		ImageServiceInput imageInput = getImageInput();
		String text = textInput.get().getText().trim();
		switch(imageInput) {
			case SMILES:
				libraryInformationByInput.setSmiles(text);
				break;
			default:
				libraryInformationByInput.setName(text);
				break;
		}
		//
		return libraryInformationByInput;
	}

	private void updateOnFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(getLastTopic(dataUpdateSupport.getTopics()));
		//
		if(!objects.isEmpty()) {
			Object object = objects.get(0);
			if(object instanceof ILibraryMassSpectrum libraryMassSpectrum) {
				setInput(libraryMassSpectrum.getLibraryInformation());
			} else if(object instanceof IIdentificationTarget identificationTarget) {
				setInput(identificationTarget.getLibraryInformation());
			}
		}
	}

	private String getLastTopic(List<String> topics) {

		Collections.reverse(topics);
		for(String topic : topics) {
			if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE)) {
				return topic;
			}
		}
		//
		return "";
	}

	private void updateComboViewerInputTypes() {

		comboViewerInput.get().setInput(ImageServiceInput.values());
		comboViewerInput.get().getCombo().select(0);
	}
}