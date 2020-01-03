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
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class WorkspaceTargetDisplaySettings implements TargetDisplaySettings, SelectableTargetDisplaySettings, VisibilityTargetDisplaySettings {

	private static final String KEY_SYSTEM_SETTINGS = "useSystemSettings";
	private static IEclipsePreferences preferences;
	private final Preferences node;
	private final TargetDisplaySettings systemSettings;
	private TargetDisplaySettings userSettings;

	private WorkspaceTargetDisplaySettings(Preferences node, TargetDisplaySettings systemSettings) {
		this.node = node;
		this.systemSettings = systemSettings;
	}

	@Override
	public boolean isUseSystemSettings() {

		return systemSettings != null && node.getBoolean(KEY_SYSTEM_SETTINGS, true);
	}

	@Override
	public boolean isShowPeakLabels() {

		if(isUseSystemSettings()) {
			return systemSettings.isShowPeakLabels();
		}
		return node.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_PEAK_LABELS);
	}

	@Override
	public boolean isShowScanLables() {

		if(isUseSystemSettings()) {
			return systemSettings.isShowScanLables();
		}
		return node.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_SCAN_LABELS);
	}

	@Override
	public LibraryField getField() {

		if(isUseSystemSettings()) {
			return systemSettings.getField();
		}
		String string = node.get(PreferenceConstants.P_TARGET_LABEL_FIELD, null);
		if(string != null) {
			return LibraryField.valueOf(string);
		}
		return LibraryField.NAME;
	}

	@Override
	public TargetDisplaySettings getSystemSettings() {

		return systemSettings;
	}

	@Override
	public TargetDisplaySettings getUserSettings() {

		if(systemSettings == null) {
			return this;
		}
		if(userSettings == null) {
			userSettings = new WorkspaceTargetDisplaySettings(node, null);
		}
		return userSettings;
	}

	@Override
	public boolean isVisible(TargetReference reference) {

		if(isUseSystemSettings()) {
			if(systemSettings instanceof VisibilityTargetDisplaySettings) {
				return ((VisibilityTargetDisplaySettings)systemSettings).isVisible(reference);
			} else {
				return true;
			}
		}
		if(reference == null) {
			return false;
		}
		return node.getBoolean(reference.getID(), true);
	}

	@Override
	public int getRotation() {

		if(isUseSystemSettings()) {
			return systemSettings.getRotation();
		}
		return node.getInt(PreferenceConstants.P_PEAK_LABELS_ROTATION, PreferenceConstants.DEF_PEAK_LABELS_ROTATION);
	}

	@Override
	public int getCollisionDetectionDepth() {

		if(isUseSystemSettings()) {
			return systemSettings.getCollisionDetectionDepth();
		}
		return node.getInt(PreferenceConstants.P_PEAK_LABELS_COLLISION_DETECTION_DEPTH, PreferenceConstants.DEF_PEAK_LABELS_COLLISION_DETECTION_DEPTH);
	}

	@Override
	public void setCollisionDetectionDepth(int depth) {

		node.putInt(PreferenceConstants.P_PEAK_LABELS_COLLISION_DETECTION_DEPTH, depth);
	}

	@Override
	public void setShowPeakLabels(boolean showPeakLabels) {

		node.putBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, showPeakLabels);
	}

	@Override
	public void setRotation(int degree) {

		node.putInt(PreferenceConstants.P_PEAK_LABELS_ROTATION, degree);
	}

	@Override
	public void setShowScanLables(boolean showScanLables) {

		node.putBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, showScanLables);
	}

	@Override
	public void setField(LibraryField libraryField) {

		node.put(PreferenceConstants.P_TARGET_LABEL_FIELD, libraryField.name());
	}

	@Override
	public void setUseSystemSettings(boolean useSystemSettings) {

		node.putBoolean(KEY_SYSTEM_SETTINGS, useSystemSettings);
	}

	@Override
	public void setVisible(TargetReference reference, boolean visible) {

		if(visible) {
			node.remove(reference.getID());
		} else {
			node.putBoolean(reference.getID(), false);
		}
	}

	public void flush() {

		try {
			node.flush();
		} catch(BackingStoreException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, WorkspaceTargetDisplaySettings.class.getName(), "Flush WorkspaceTargetDisplaySettings failed!", e));
		}
	}

	public static WorkspaceTargetDisplaySettings getWorkspaceSettings(File file, TargetDisplaySettings systemSettings) {

		Preferences node;
		if(file == null) {
			node = getStorage().node("TargetDisplaySettingsWizard");
		} else {
			String path;
			try {
				path = file.getCanonicalPath();
			} catch(IOException e) {
				path = file.getAbsolutePath();
			}
			node = getStorage().node(path.replace('/', '_').replace('.', '_'));
			node.put("FilePath", path);
		}
		try {
			node.sync();
		} catch(BackingStoreException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, WorkspaceTargetDisplaySettings.class.getName(), "Sync WorkspaceTargetDisplaySettings failed!", e));
		}
		return new WorkspaceTargetDisplaySettings(node, systemSettings);
	}

	public static String getID(IIdentificationTarget target, LibraryField field) {

		if(target != null) {
			StringBuilder sb = new StringBuilder("IdentificationTarget.");
			sb.append(field.name());
			sb.append(".");
			sb.append(field.stringTransformer().apply(target));
			ILibraryInformation information = target.getLibraryInformation();
			if(information != null) {
				sb.append("@");
				sb.append(information.getRetentionTime());
			}
			return sb.toString().trim();
		}
		return null;
	}

	private static IEclipsePreferences getStorage() {

		if(preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(TargetDisplaySettings.class.getName());
		}
		return preferences;
	}
}
