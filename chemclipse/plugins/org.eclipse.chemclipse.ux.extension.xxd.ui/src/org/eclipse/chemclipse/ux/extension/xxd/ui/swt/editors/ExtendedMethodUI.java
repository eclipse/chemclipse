/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods, support for init with different datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodTreeViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ProcessMethodHeader;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ProcessMethodProfiles;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ProcessMethodToolbar;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageChromatogramExport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageReportExport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;

public class ExtendedMethodUI extends Composite implements IExtendedPartUI {

	private static final String IMAGE_HEADER = IApplicationImage.IMAGE_HEADER_DATA;
	private static final String TOOLTIP_HEADER = "the header information.";
	private static final String IMAGE_PROFILE = IApplicationImage.IMAGE_INSTRUMENT;
	private static final String TOOLTIP_PROFILE = "the profile selection.";
	//
	private final IProcessSupplierContext processingSupport;
	private final DataCategory[] dataCategories;
	private final BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier;
	//
	private AtomicReference<Composite> toolbarMain = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarHeader = new AtomicReference<>();
	private AtomicReference<ProcessMethodHeader> toolbarHeader = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarProfile = new AtomicReference<>();
	private AtomicReference<ProcessMethodProfiles> toolbarProfile = new AtomicReference<>();
	private AtomicReference<MethodTreeViewer> treeViewer = new AtomicReference<>();
	private AtomicReference<ProcessMethodToolbar> toolbarButtons = new AtomicReference<>();
	//
	private ProcessMethod processMethod;
	private IModificationHandler modificationHandler;
	private Collection<ProcessEntryContainer> postActions;
	//
	private boolean readOnly = false;

	public ExtendedMethodUI(Composite parent, int style, IProcessSupplierContext processingSupport, DataCategory[] dataCategories) {

		this(parent, style, processingSupport, (entry, context) -> entry.getPreferences(context), dataCategories);
	}

	public ExtendedMethodUI(Composite parent, int style, IProcessSupplierContext processingSupport, BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier, DataCategory[] dataCategories) {

		super(parent, style);
		//
		this.readOnly = (style & SWT.READ_ONLY) != 0;
		this.processingSupport = processingSupport;
		this.preferencesSupplier = preferencesSupplier;
		this.dataCategories = dataCategories;
		//
		createControl();
	}

	public void setProcessMethod(IProcessMethod processMethod) {

		setInputs(processMethod, Collections.emptyList());
	}

	public void setInputs(IProcessMethod processMethod, Collection<ProcessEntryContainer> postActions) {

		this.postActions = postActions;
		this.processMethod = new ProcessMethod(processMethod);
		//
		toolbarHeader.get().setInput(this.processMethod);
		toolbarProfile.get().setInput(this.processMethod);
		toolbarButtons.get().setInput(this.processMethod);
		//
		updateProcessMethod();
	}

	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
		toolbarHeader.get().setModificationHandler(modificationHandler);
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createToolbarHeader(composite);
		createToolbarProfile(composite);
		createTreeViewer(composite);
		createToolbarBottom(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarHeader, buttonToolbarHeader.get(), IMAGE_HEADER, TOOLTIP_HEADER, false);
		enableToolbar(toolbarProfile, buttonToolbarProfile.get(), IMAGE_PROFILE, TOOLTIP_PROFILE, true);
		toolbarButtons.get().updateTableButtons();
	}

	public void setToolbarMainVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMain.get(), visible);
	}

	public void setToolbarProfileVisible(boolean visible) {

		enableToolbar(toolbarProfile, buttonToolbarProfile.get(), IMAGE_PROFILE, TOOLTIP_PROFILE, visible);
	}

	public void setToolbarProfileEnableEdit(boolean enabledEdit) {

		toolbarProfile.get().setEnabledEdit(enabledEdit);
	}

	public void setToolbarHeaderVisible(boolean visible) {

		enableToolbar(toolbarHeader, buttonToolbarHeader.get(), IMAGE_HEADER, TOOLTIP_HEADER, visible);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleHeader(composite);
		createButtonToggleProfile(composite);
		createSettingsButton(composite);
		//
		toolbarMain.set(composite);
	}

	private void createButtonToggleHeader(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarHeader, IMAGE_HEADER, TOOLTIP_HEADER);
		buttonToolbarHeader.set(button);
	}

	private void createButtonToggleProfile(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarProfile, IMAGE_PROFILE, TOOLTIP_PROFILE);
		buttonToolbarProfile.set(button);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageReportExport.class, PreferencePageChromatogramExport.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		setMethodDirty(true);
	}

	private void createToolbarHeader(Composite parent) {

		ProcessMethodHeader processMethodHeader = new ProcessMethodHeader(parent, SWT.NONE);
		processMethodHeader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		processMethodHeader.setProcessingSupport(processingSupport);
		//
		processMethodHeader.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateProcessMethod();
			}
		});
		//
		processMethodHeader.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				setMethodDirty(dirty);
			}
		});
		//
		toolbarHeader.set(processMethodHeader);
	}

	private void createToolbarProfile(Composite parent) {

		ProcessMethodProfiles processMethodProfiles = new ProcessMethodProfiles(parent, SWT.NONE);
		processMethodProfiles.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		processMethodProfiles.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateProcessMethod();
				setMethodDirty(true);
			}
		});
		//
		toolbarProfile.set(processMethodProfiles);
	}

	private void createTreeViewer(Composite parent) {

		MethodTreeViewer methodTreeViewer = new MethodTreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		methodTreeViewer.createControl(processingSupport, preferencesSupplier, toolbarButtons, false);
		methodTreeViewer.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateProcessMethod();
				setMethodDirty(true);
			}
		});
		//
		methodTreeViewer.getTree().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				ProcessMethodToolbar processMethodToolbar = toolbarButtons.get();
				if(processMethodToolbar != null) {
					if(e.keyCode == SWT.DEL) {
						processMethodToolbar.deleteSelectedProcessEntries(e.display.getActiveShell());
					} else {
						if(e.stateMask == SWT.MOD1) {
							if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_D) {
								processMethodToolbar.deleteAllProcessEntries(e.display.getActiveShell());
							}
						}
					}
				}
			}
		});
		//
		treeViewer.set(methodTreeViewer);
	}

	private void createToolbarBottom(Composite parent) {

		ProcessMethodToolbar processMethodToolbar = new ProcessMethodToolbar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		processMethodToolbar.setLayoutData(gridData);
		//
		processMethodToolbar.setStructuredViewer(treeViewer.get());
		processMethodToolbar.setProcessingSupport(processingSupport);
		processMethodToolbar.setPreferencesSupplier(preferencesSupplier);
		processMethodToolbar.setDataCategories(dataCategories);
		processMethodToolbar.setReadOnly(readOnly);
		processMethodToolbar.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateProcessMethod();
				setMethodDirty(true);
			}
		});
		//
		toolbarButtons.set(processMethodToolbar);
	}

	public void loadMethodFile(IProcessMethod method) {

		if(method != null) {
			List<IProcessEntry> copied = new ArrayList<>();
			method.forEach(entry -> {
				copied.add(processMethod.addProcessEntry(entry));
			});
			updateProcessMethod();
			select(copied);
		}
	}

	private void select(Iterable<? extends IProcessEntry> entries) {

		ArrayList<IProcessEntry> list = new ArrayList<>();
		entries.forEach(list::add);
		StructuredSelection structuredSelection = new StructuredSelection(list);
		treeViewer.get().setSelection(structuredSelection);
		Object firstElement = structuredSelection.getFirstElement();
		if(firstElement != null) {
			treeViewer.get().reveal(firstElement);
		}
	}

	private void updateProcessMethod() {

		StructuredViewer structuredViewer = treeViewer.get();
		/*
		 * Update the process method list.
		 */
		boolean expand = false;
		if(postActions == null || postActions.isEmpty()) {
			structuredViewer.setInput(processMethod);
		} else {
			ArrayList<Object> list = new ArrayList<>();
			list.add(processMethod);
			list.addAll(postActions);
			structuredViewer.setInput(list.toArray());
			expand = true;
		}
		//
		structuredViewer.refresh();
		if(structuredViewer instanceof TreeViewer treeViewer) {
			if(expand) {
				treeViewer.expandToLevel(1);
			}
		}
		//
		toolbarButtons.get().updateTableButtons();
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		treeViewer.get().getControl().setEnabled(enabled);
		PartSupport.setCompositeVisibility(toolbarButtons.get(), enabled);
	}

	private void setMethodDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}
}