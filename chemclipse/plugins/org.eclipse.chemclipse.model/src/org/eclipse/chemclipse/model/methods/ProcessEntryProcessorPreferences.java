/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public final class ProcessEntryProcessorPreferences<T> implements ProcessorPreferences<T> {

	private IProcessEntry processEntry;
	private IProcessSupplier<T> supplier;

	public ProcessEntryProcessorPreferences(IProcessSupplier<T> supplier, IProcessEntry processEntry) {
		this.supplier = supplier;
		this.processEntry = processEntry;
	}

	@Override
	public DialogBehavior getDialogBehaviour() {

		return DialogBehavior.NONE;
	}

	@Override
	public void setAskForSettings(boolean askForSettings) {

		// no-op
	}

	@Override
	public void setUserSettings(String settings) {

		processEntry.setJsonSettings(settings);
	}

	@Override
	public boolean isUseSystemDefaults() {

		if(supplier.getSettingsClass() == null) {
			return true;
		}
		String jsonSettings = processEntry.getJsonSettings();
		return jsonSettings == null || jsonSettings.isEmpty() || ProcessEntry.EMPTY_JSON_SETTINGS.equals(jsonSettings);
	}

	@Override
	public void setUseSystemDefaults(boolean useSystemDefaults) {

		if(useSystemDefaults) {
			processEntry.setJsonSettings(ProcessEntry.EMPTY_JSON_SETTINGS);
		}
	}

	@Override
	public void reset() {

		throw new UnsupportedOperationException();
	}

	@Override
	public IProcessSupplier<T> getSupplier() {

		return supplier;
	}

	@Override
	public String getUserSettingsAsString() {

		return processEntry.getJsonSettings();
	}
}