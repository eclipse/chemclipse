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
 * Christoph Läubrich - changes for PartSupport
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class ChromatogramEditor3x extends EditorPart implements IChromatogramEditor {

	/*
	 * This is the editor id from the plugin.xml "org.eclipse.ui.editors" extension point.
	 * Currently, only the *.ocb format is supported.
	 */
	private static final String EDITOR_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.editors.chromatogramEditor3x";
	private static final String SUPPLIER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	//
	private ChromatogramEditor chromatogramEditor;
	private MPart part;
	private IPartListener2 partListener = null;
	//
	private final MDirtyable dirtyable = new MDirtyable() {

		private boolean value = false;

		@Override
		public void setDirty(boolean value) {

			this.value = value;
		}

		@Override
		public boolean isDirty() {

			return value;
		}
	};

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		DataType dataType = getDataType();
		if(dataType != null) {
			chromatogramEditor = new ChromatogramEditor(dataType, parent, part, dirtyable, DisplayUtils.getShell(), new ProcessTypeSupport(), Activator.getDefault().getEclipseContext());
		} else {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Sorry, the chromatogram couldn't be displayed.");
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		setSite(site);
		setInput(input);
		//
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		File file = null;
		//
		if(input instanceof IFileEditorInput fileEditorInput) {
			file = fileEditorInput.getFile().getLocation().toFile();
		} else if(input instanceof IURIEditorInput uriEditorInput) {
			file = new File(uriEditorInput.getURI());
		} else {
			throw new PartInitException("The file couldn't be loaded.");
		}
		/*
		 * Using the PartSupport works, but leads to strange behavior when loading multiple files.
		 * PartSupport.get3xEditorPart(EDITOR_ID, partService, modelService, application);
		 */
		part = MBasicFactory.INSTANCE.createPart();
		part.setElementId(EDITOR_ID);
		part.setLabel(ExtensionMessages.chromatogram);
		Map<String, Object> map = new HashMap<>();
		map.put(EditorSupport.MAP_FILE, file.getAbsolutePath());
		map.put(EditorSupport.MAP_BATCH, false);
		part.setObject(map);
		part.setTooltip(ExtensionMessages.chromatogramFromProjectExplorer);
		//
		partListener = createPartListener();
		getSite().getPage().addPartListener(partListener);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		if(chromatogramEditor != null) {
			chromatogramEditor.save();
		}
	}

	@Override
	public void doSaveAs() {

		if(chromatogramEditor != null) {
			chromatogramEditor.saveAs();
		}
	}

	@Override
	public boolean isDirty() {

		return dirtyable.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	@Override
	public void setFocus() {

		if(chromatogramEditor != null) {
			chromatogramEditor.setFocus();
		}
	}

	@Override
	public boolean saveAs() {

		return false;
	}

	@Override
	public IChromatogramSelection<?, ?> getChromatogramSelection() {

		if(chromatogramEditor != null) {
			return chromatogramEditor.getChromatogramSelection();
		} else {
			return null;
		}
	}

	private IPartListener2 createPartListener() {

		return new IPartListener2() {

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {

				if(isActivatePart(partRef)) {
					IChromatogramSelection<?, ?> chromatogramSelection = getChromatogramSelection();
					if(chromatogramSelection != null) {
						UpdateNotifierUI.update(getSite().getShell().getDisplay(), chromatogramSelection);
					}
				}
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {

				if(isActivatePart(partRef)) {
					/*
					 * Remove
					 */
					if(partListener != null) {
						getSite().getPage().removePartListener(partListener);
						partListener = null;
					}
					UpdateNotifierUI.update(getSite().getShell().getDisplay(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE, "ChromatogramEditor3x Close");
				}
			}
		};
	}

	private boolean isActivatePart(IWorkbenchPartReference partRef) {

		IWorkbenchPartReference partThis = getSite().getPage().getReference(getSite().getPart());
		return partThis == partRef;
	}

	private DataType getDataType() {

		DataType dataType = null;
		//
		Object object = part.getObject();
		if(object instanceof Map<?, ?> map) {
			String path = (String)map.get(EditorSupport.MAP_FILE);
			File file = new File(path);
			/*
			 * Check the data format.
			 */
			if(isMatch(file, getSupplierFileIdentifier(DataType.MSD))) {
				dataType = DataType.MSD;
			} else if(isMatch(file, getSupplierFileIdentifier(DataType.CSD))) {
				dataType = DataType.CSD;
			} else if(isMatch(file, getSupplierFileIdentifier(DataType.WSD))) {
				dataType = DataType.WSD;
			}
		}
		//
		return dataType;
	}

	private SupplierFileIdentifier getSupplierFileIdentifier(DataType dataType) {

		SupplierFileIdentifier supplierFileIdentifier = new SupplierFileIdentifier(dataType);
		ISupplier supplierSupported = null;
		/*
		 * Fetch and set the *.ocb specific supplier.
		 */
		for(ISupplier supplier : supplierFileIdentifier.getSupplier()) {
			if(SUPPLIER_ID.equals(supplier.getId())) {
				supplierSupported = supplier;
			}
		}
		//
		if(supplierSupported != null) {
			supplierFileIdentifier.getSupplier().clear();
			supplierFileIdentifier.getSupplier().add(supplierSupported);
		}
		//
		return supplierFileIdentifier;
	}

	private boolean isMatch(File file, ISupplierFileIdentifier supplierFileIdentifier) {

		boolean isMatch = false;
		if(supplierFileIdentifier != null && file != null && file.exists()) {
			if(supplierFileIdentifier.isSupplierFile(file) && supplierFileIdentifier.isMatchMagicNumber(file)) {
				if(supplierFileIdentifier.isMatchContent(file)) {
					isMatch = true;
				}
			}
		}
		//
		return isMatch;
	}
}