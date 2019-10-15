/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - refactoring for new method API, optimize E4 access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ProcessMethodEditor implements IModificationHandler {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.processMethodEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/method.gif";
	public static final String TOOLTIP = "Process Method Editor";
	//
	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;
	//
	private File processMethodFile;
	private ExtendedMethodUI extendedMethodUI;
	@Inject
	private ProcessMethodNotifications notifications;
	@Inject
	private PartSupport partsupport;
	private IProcessMethod currentProcessMethod;

	@Focus
	public void setFocus() {

		extendedMethodUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		partsupport.closePart(part);
	}

	@Persist
	public void save() {

		if(processMethodFile != null) {
			IProcessMethod oldMethod = currentProcessMethod;
			IProcessMethod editedMethod = extendedMethodUI.getProcessMethod();
			ProcessMethod newMethod = new ProcessMethod(editedMethod);
			newMethod.setSourceFile(processMethodFile);
			// copy the UUID from the old method to keep the file consistent
			newMethod.setUUID(oldMethod.getUUID());
			// copy the readOnlyFlag
			if(editedMethod.isFinal()) {
				newMethod.setReadOnly(editedMethod.isFinal());
			}
			IProcessingInfo<?> info = MethodConverter.convert(processMethodFile, newMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			if(info.hasErrorMessages()) {
				ProcessingInfoViewSupport.updateProcessingInfo(info);
			} else {
				dirtyable.setDirty(false);
				currentProcessMethod = newMethod;
				notifications.updated(newMethod, oldMethod);
			}
		}
	}

	@PostConstruct
	public void initialize(Composite parent) {

		currentProcessMethod = null;
		Object object = part.getObject();
		if(object instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
			processMethodFile = new File((String)map.get(EditorSupport.MAP_FILE));
			currentProcessMethod = Adapters.adapt(processMethodFile, IProcessMethod.class);
			if(currentProcessMethod == null) {
				throw new RuntimeException("can't read file " + processMethodFile);
			}
		} else {
			currentProcessMethod = null;
			processMethodFile = null;
		}
		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, new ProcessTypeSupport(), new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD});
		extendedMethodUI.setModificationHandler(this);
		extendedMethodUI.setProcessMethod(currentProcessMethod);
	}

	@Override
	public void setDirty(boolean dirty) {

		dirtyable.setDirty(!extendedMethodUI.getProcessMethod().contentEquals(currentProcessMethod, true));
	}
}
