/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.UserPathPreferencePage;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifierCache;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;

public class InputWizardSettings {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	//
	private String title = "Title";
	private String description = "Description";
	private IPreferenceStore preferenceStore;
	private String userLocationPreferenceKey;
	private SupplierFileIdentifierCache identifierCache;

	public InputWizardSettings(IPreferenceStore preferenceStore, String userLocationPreferenceKey, Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		this.preferenceStore = preferenceStore;
		this.userLocationPreferenceKey = userLocationPreferenceKey;
		identifierCache = new SupplierFileIdentifierCache(500);
		identifierCache.setIdentifier(supplierFileIdentifierList);
	}

	public IPreferencePage getPreferencePage() {

		return new UserPathPreferencePage(getPreferenceStore(), getUserLocationPreferenceKey());
	}

	public IPreferenceStore getPreferenceStore() {

		return preferenceStore;
	}

	public String getUserLocationPreferenceKey() {

		return userLocationPreferenceKey;
	}

	public SupplierFileIdentifierCache getSupplierCache() {

		return identifierCache;
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

	public static InputWizardSettings create(IPreferenceStore preferenceStore, DataType... dataTypes) {

		return create(preferenceStore, null, dataTypes);
	}

	public static InputWizardSettings create(IPreferenceStore preferenceStore, String userLocationPreferenceKey, DataType... dataTypes) {

		Collection<ISupplierFileIdentifier> list = new ArrayList<>();
		for(DataType dataType : dataTypes) {
			list.add(new SupplierEditorSupport(dataType));
		}
		return new InputWizardSettings(preferenceStore, userLocationPreferenceKey, list);
	}
}
