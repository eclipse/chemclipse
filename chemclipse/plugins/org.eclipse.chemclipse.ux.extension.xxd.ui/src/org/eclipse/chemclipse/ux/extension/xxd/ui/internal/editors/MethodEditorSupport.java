/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;

public class MethodEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierEditorSupport {

	public MethodEditorSupport() {
		super(getSupplier());
	}

	private static List<ISupplier> getSupplier() {

		List<ISupplier> supplier = new ArrayList<ISupplier>();
		supplier.add(new MethodFileSupplier());
		return supplier;
	}

	@Override
	public String getType() {

		return TYPE_MTH;
	}

	@Override
	public boolean openEditor(File file) {

		return openEditor(file, false);
	}

	@Override
	public boolean openEditor(final File file, boolean batch) {

		String elementId = ProcessMethodEditor.ID;
		String contributionURI = ProcessMethodEditor.CONTRIBUTION_URI;
		String iconURI = ProcessMethodEditor.ICON_URI;
		String tooltip = ProcessMethodEditor.TOOLTIP;
		openEditor(file, null, elementId, contributionURI, iconURI, tooltip, batch);
		return true;
	}

	@Override
	public void openEditor(IMeasurement measurement) {

	}

	@Override
	public void openOverview(final File file) {

	}

	@Override
	public void openOverview(IMeasurementInfo measurementInfo) {

	}
}
