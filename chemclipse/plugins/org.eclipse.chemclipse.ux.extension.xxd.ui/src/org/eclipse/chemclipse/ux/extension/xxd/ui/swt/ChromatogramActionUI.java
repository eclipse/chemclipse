/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - remove reference to IapplicationImageProvider
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramEditorActionExtension;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IChromatogramEditorAction;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ChromatogramActionUI extends Composite {

	private static final Logger logger = Logger.getLogger(ChromatogramActionUI.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.ux.extension.xxd.ui.chromatogramEditorActionSupplier";
	private static final String EXTENSION_LABEL = "label"; //$NON-NLS-1$
	private static final String EXTENSION_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String EXTENSION_ACTION_EXECUTABLE = "actionExecutable"; //$NON-NLS-1$
	private static final String EXTENSION_MSD = "MSD"; //$NON-NLS-1$
	private static final String EXTENSION_CSD = "CSD"; //$NON-NLS-1$
	private static final String EXTENSION_WSD = "WSD"; //$NON-NLS-1$
	//
	private ComboViewer comboChromatogramAction;
	private Button buttonChromatogramAction;
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;
	private String selectedActionId = "";
	private final HashMap<String, ChromatogramEditorActionExtension> actionHashMap = new HashMap<>();
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramActionUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@SuppressWarnings("rawtypes")
	public void setChromatogramActionMenu(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		boolean enabled = enableChromatogramActionMenu(chromatogramSelection);
		comboChromatogramAction.getCombo().setEnabled(enabled);
		//
		if(enabled) {
			boolean success = setChromatogramActionItems(comboChromatogramAction.getCombo(), chromatogramSelection);
			buttonChromatogramAction.setEnabled(success);
		} else {
			buttonChromatogramAction.setEnabled(false);
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		comboChromatogramAction = createChromatogramActionCombo(composite);
		buttonChromatogramAction = createChromatogramActionButton(composite);
	}

	private ComboViewer createChromatogramActionCombo(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ChromatogramEditorActionExtension) {
					return ((ChromatogramEditorActionExtension)element).getLabel();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a chromatogram action.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ChromatogramEditorActionExtension) {
					ChromatogramEditorActionExtension extension = (ChromatogramEditorActionExtension)object;
					combo.setToolTipText(extension.getDescription());
					buttonChromatogramAction.setEnabled(true);
					String id = extension.getUniqueId();
					preferenceStore.putValue(PreferenceConstants.P_CHROMATOGRAM_SELECTED_ACTION_ID, id);
					selectedActionId = id;
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createChromatogramActionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Execute the selected chromatogram action.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_EXTENSION, IApplicationImage.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelection != null) {
					ChromatogramEditorActionExtension extension = actionHashMap.get(selectedActionId);
					if(extension != null) {
						IChromatogramEditorAction action = extension.getChromatogramEditorAction();
						if(action != null) {
							IProcessingInfo processingInfo = action.applyAction(chromatogramSelection);
							ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
							e.display.asyncExec(new Runnable() {

								@Override
								public void run() {

									chromatogramSelection.update(false);
								}
							});
						}
					} else {
						MessageDialog.openInformation(DisplayUtils.getShell(), "Action", "Please select a execute method first via the button popup menu (right mouse click).");
					}
				}
			}
		});
		//
		return button;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private boolean setChromatogramActionItems(Control parent, IChromatogramSelection chromatogramSelection) {

		/*
		 * Fill the hash map.
		 */
		actionHashMap.clear();
		List<ChromatogramEditorActionExtension> activeExtensions = new ArrayList<>();
		for(ChromatogramEditorActionExtension extension : getChromatogramEditorExtensions()) {
			if(isChromatogramSuitableForExtension(extension, chromatogramSelection)) {
				String id = extension.getUniqueId();
				actionHashMap.put(id, extension);
				activeExtensions.add(extension);
			}
		}
		comboChromatogramAction.setInput(activeExtensions);
		/*
		 * Get the stored action id and set the selected entry and the description.
		 */
		boolean success = false;
		selectedActionId = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_ACTION_ID);
		ChromatogramEditorActionExtension extension = actionHashMap.get(selectedActionId);
		if(extension != null) {
			Combo combo = comboChromatogramAction.getCombo();
			combo.setToolTipText(extension.getDescription());
			int index = getChromatogramActionSelectionIndex(activeExtensions, selectedActionId);
			if(index > -1) {
				combo.select(index);
				success = true;
			}
		}
		//
		return success;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private boolean enableChromatogramActionMenu(IChromatogramSelection chromatogramSelection) {

		boolean enabled = false;
		IConfigurationElement[] elements = getConfigurationElements();
		exitloop:
		for(IConfigurationElement element : elements) {
			ChromatogramEditorActionExtension extension = new ChromatogramEditorActionExtension();
			extension.setMSD(Boolean.valueOf(element.getAttribute(EXTENSION_MSD)));
			extension.setCSD(Boolean.valueOf(element.getAttribute(EXTENSION_CSD)));
			extension.setWSD(Boolean.valueOf(element.getAttribute(EXTENSION_WSD)));
			if(isChromatogramSuitableForExtension(extension, chromatogramSelection)) {
				enabled = true;
				break exitloop;
			}
		}
		return enabled;
	}

	private List<ChromatogramEditorActionExtension> getChromatogramEditorExtensions() {

		List<ChromatogramEditorActionExtension> extensions = new ArrayList<>();
		IConfigurationElement[] elements = getConfigurationElements();
		for(IConfigurationElement element : elements) {
			try {
				ChromatogramEditorActionExtension extension = new ChromatogramEditorActionExtension();
				extension.setLabel(element.getAttribute(EXTENSION_LABEL));
				extension.setDescription(element.getAttribute(EXTENSION_DESCRIPTION));
				extension.setChromatogramEditorAction((IChromatogramEditorAction)element.createExecutableExtension(EXTENSION_ACTION_EXECUTABLE));
				extension.setMSD(Boolean.valueOf(element.getAttribute(EXTENSION_MSD)));
				extension.setCSD(Boolean.valueOf(element.getAttribute(EXTENSION_CSD)));
				extension.setWSD(Boolean.valueOf(element.getAttribute(EXTENSION_WSD)));
				extensions.add(extension);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return extensions;
	}

	private boolean isChromatogramSuitableForExtension(ChromatogramEditorActionExtension extension, IChromatogramSelection<? extends IPeak, ?> chromatogramSelection) {

		if(extension != null && chromatogramSelection != null) {
			if(chromatogramSelection instanceof IChromatogramSelectionMSD && extension.isMSD()) {
				return true;
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD && extension.isCSD()) {
				return true;
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD && extension.isWSD()) {
				return true;
			}
		}
		return false;
	}

	private IConfigurationElement[] getConfigurationElements() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		return elements;
	}

	private int getChromatogramActionSelectionIndex(List<ChromatogramEditorActionExtension> extensions, String actionId) {

		int index = -1;
		exitloop:
		for(int i = 0; i < extensions.size(); i++) {
			ChromatogramEditorActionExtension extension = extensions.get(i);
			String id = extension.getUniqueId();
			if(id.equals(actionId)) {
				index = i;
				break exitloop;
			}
		}
		return index;
	}
}
