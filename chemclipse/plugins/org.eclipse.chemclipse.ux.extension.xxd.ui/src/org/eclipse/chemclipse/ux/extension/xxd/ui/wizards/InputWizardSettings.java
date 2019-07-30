/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - support new lazy table model, support NMR_SCANs as InputDataType
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.util.Collection;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.jface.preference.IPreferenceStore;

public class InputWizardSettings {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	private String title = "Title";
	private String description = "Description";
	private Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList;
	private IPreferenceStore preferenceStore;

	public InputWizardSettings(IPreferenceStore preferenceStore, Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		this.preferenceStore = preferenceStore;
		this.supplierFileIdentifierList = supplierFileIdentifierList;
		// for(DataType dataType : dataTypes) {
		// supplierFileIdentifierList.add(new EditorSupportFactory(dataType).getInstanceIdentifier());
		// supplierFileIdentifierList.add(new SupplierEditorSupport(DataType.CSD));
		// }
	}

	public IPreferenceStore getPreferenceStore() {

		return preferenceStore;
	}

	public Collection<? extends ISupplierFileIdentifier> getSupplierFileIdentifierList() {

		return supplierFileIdentifierList;
	}

	public String getTitle() {

		return (title == null) ? "" : title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getDescription() {

		return (description == null) ? "" : description;
	}

	public void setDescription(String description) {

		this.description = description;
	}
}
