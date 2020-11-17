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
 * Christoph Läubrich - changes for PartSupport
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class ChromatogramEditor3x extends EditorPart implements IChromatogramEditor {

	/*
	 * This is the editor id from the plugin.xml "org.eclipse.ui.editors" extension point.
	 */
	private static final String EDITOR_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.editors.chromatogramEditor3x";
	//
	private ChromatogramEditor chromatogramEditor;
	private final EPartService partService = Activator.getDefault().getPartService();
	private final EModelService modelService = Activator.getDefault().getModelService();
	private final MApplication application = Activator.getDefault().getApplication();
	private final MPart part = PartSupport.get3xEditorPart(EDITOR_ID, partService, modelService, application);
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
			chromatogramEditor = new ChromatogramEditor(dataType, parent, part, dirtyable, DisplayUtils.getShell(), new ProcessTypeSupport());
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
		//
		if(input instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput)input;
			File file = fileEditorInput.getFile().getLocation().toFile();
			//
			part.setLabel("Chromatogram");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(EditorSupport.MAP_FILE, file.getAbsolutePath());
			map.put(EditorSupport.MAP_BATCH, false);
			part.setObject(map);
			part.setTooltip("Chromatogram from Project Explorer");
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
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

	@SuppressWarnings("rawtypes")
	private DataType getDataType() {

		DataType dataType = null;
		//
		Object object = part.getObject();
		if(object instanceof Map) {
			Map map = (Map)object;
			String path = (String)map.get(EditorSupport.MAP_FILE);
			File file = new File(path);
			//
			if(isMatch(file, new SupplierFileIdentifier(DataType.MSD))) {
				dataType = DataType.MSD;
			} else if(isMatch(file, new SupplierFileIdentifier(DataType.CSD))) {
				dataType = DataType.CSD;
			} else if(isMatch(file, new SupplierFileIdentifier(DataType.WSD))) {
				dataType = DataType.WSD;
			}
		}
		//
		return dataType;
	}

	private boolean isMatch(File file, ISupplierFileIdentifier supplierFileIdentifier) {

		boolean isMatch = false;
		if(supplierFileIdentifier != null && file != null && file.exists()) {
			if(supplierFileIdentifier.isSupplierFile(file) && supplierFileIdentifier.isMatchMagicNumber(file)) {
				isMatch = true;
			}
		}
		return isMatch;
	}

	@Override
	public boolean saveAs() {

		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IChromatogramSelection getChromatogramSelection() {

		if(chromatogramEditor != null) {
			return chromatogramEditor.getChromatogramSelection();
		} else {
			return null;
		}
	}
}
