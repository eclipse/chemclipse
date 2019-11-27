/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class WorkspaceTargetDisplaySettings implements TargetDisplaySettings {

	private static IEclipsePreferences preferences;
	private final Preferences node;
	private final TargetDisplaySettings systemSettings;

	private WorkspaceTargetDisplaySettings(Preferences node, TargetDisplaySettings systemSettings) {
		this.node = node;
		this.systemSettings = systemSettings;
	}

	public boolean useSystemSettings() {

		return systemSettings != null && node.getBoolean("useSystemSettings", true);
	}

	public TargetDisplaySettings getSystemSettings() {

		return systemSettings;
	}

	@Override
	public boolean isShowPeakLabels() {

		if(useSystemSettings()) {
			return systemSettings.isShowPeakLabels();
		}
		return node.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_PEAK_LABELS);
	}

	@Override
	public boolean isShowScanLables() {

		if(useSystemSettings()) {
			return systemSettings.isShowScanLables();
		}
		return node.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_SCAN_LABELS);
	}

	@Override
	public LibraryField getField() {

		if(useSystemSettings()) {
			return systemSettings.getField();
		}
		String string = node.get(PreferenceConstants.P_TARGET_LABEL_FIELD, null);
		if(string != null) {
			return LibraryField.valueOf(string);
		}
		return LibraryField.NAME;
	}

	@Override
	public boolean isVisible(IIdentificationTarget target) {

		if(useSystemSettings()) {
			return systemSettings.isVisible(target);
		}
		String id = getID(target, getField());
		if(id != null) {
			return node.getBoolean(id, true);
		}
		return true;
	}

	private String getID(IIdentificationTarget target, LibraryField field) {

		if(target != null) {
			StringBuilder sb = new StringBuilder("IdentificationTarget.");
			sb.append(field.name());
			sb.append(".");
			sb.append(field.stringTransformer().apply(target));
			return sb.toString();
		}
		return null;
	}

	@Override
	public void setShowPeakLabels(boolean showPeakLabels) {

		node.putBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, showPeakLabels);
	}

	@Override
	public void setShowScanLables(boolean showScanLables) {

		node.putBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, showScanLables);
	}

	@Override
	public void setField(LibraryField libraryField) {

		node.put(PreferenceConstants.P_TARGET_LABEL_FIELD, libraryField.name());
	}

	public static WorkspaceTargetDisplaySettings getWorkspaceSettings(File file, TargetDisplaySettings systemSettings) {

		Preferences node;
		if(file == null) {
			node = getStorage().node("TargetDisplaySettingsWizard");
		} else {
			try {
				node = getStorage().node(file.getCanonicalPath());
			} catch(IOException e) {
				node = getStorage().node(file.getAbsolutePath());
			}
		}
		try {
			node.sync();
		} catch(BackingStoreException e) {
			// can't sync then...
		}
		return new WorkspaceTargetDisplaySettings(node, systemSettings);
	}

	private static IEclipsePreferences getStorage() {

		if(preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(TargetDisplaySettings.class.getName());
		}
		return preferences;
	}
}
