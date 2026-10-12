/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals method, add copy constructor, extend for new methods
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.support.model.SeparationColumnType;

public class ProcessMethod extends ListProcessEntryContainer implements IProcessMethod {

	public static final Set<DataCategory> CHROMATOGRAPHY = Collections.unmodifiableSet(Set.of(DataCategory.chromatographyCategories()));
	public static final Set<DataCategory> NMR = Collections.unmodifiableSet(Set.of(DataCategory.spectroscopyCategories()));
	public static final Set<DataCategory> MS = Collections.unmodifiableSet(Set.of(DataCategory.spectrometryCategories()));

	private static final Logger logger = Logger.getLogger(ProcessMethod.class);

	private final Map<String, String> metadata = new LinkedHashMap<>();
	private final Set<DataCategory> catgories;

	private String uuid = "";
	private String operator = "";
	private String category = "";
	private Map<String, SeparationColumnType> profileColumnsMap = new HashMap<>();
	/*
	 * Transient
	 */
	private File sourceFile = null;
	private ResourceBundle resourceBundle;

	public ProcessMethod(Set<DataCategory> categories) {

		uuid = java.util.UUID.randomUUID().toString();
		this.catgories = Collections.unmodifiableSet(categories);
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.chemclipse.processing.l10n.messages");
		} catch(MissingResourceException e) {
			logger.warn(e);
		}
	}

	/**
	 * Copies all data from other into a new process method. This does <b>NOT</b> copies the UUID but generates a new one!
	 * 
	 * @param other
	 */
	public ProcessMethod(IProcessMethod other) {

		super(other);
		if(other != null) {
			/*
			 * Settings
			 */
			setUUID(other.getUUID());
			this.operator = other.getOperator();
			setDescription(other.getDescription());
			this.category = other.getCategory();
			setName(other.getName());
			setSupportResume(other.isSupportResume());
			setResumeIndex(other.getResumeIndex());
			if(other instanceof ProcessMethod processMethod) {
				this.sourceFile = processMethod.getSourceFile();
			}
			metadata.putAll(other.getMetaData());
			catgories = Collections.unmodifiableSet(new HashSet<>(other.getDataCategories()));
			profileColumnsMap.putAll(other.getProfileColumnsMap());
			/*
			 * read-only must be set at the end!
			 */
			setReadOnly(other.isFinal());
		} else {
			this.catgories = Collections.emptySet();
		}
	}

	@Override
	public String getOperator() {

		if(operator == null) {
			return "";
		}

		return operator;
	}

	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public String getName() {

		String name = super.getName();
		if(name == null || name.isEmpty()) {
			if(sourceFile != null) {
				return sourceFile.getName();
			}
			return "";
		}

		return name;
	}

	@Override
	public String getCategory() {

		if(category == null || category.isEmpty()) {
			return "";
		}

		if(resourceBundle == null) {
			return category;
		}

		try {
			return resourceBundle.getString(category);
		} catch(MissingResourceException e) {
			// logger.warn(e);
			return category;
		}
	}

	public void setCategory(String category) throws IllegalStateException {

		this.category = category;
	}

	@Override
	public Map<String, SeparationColumnType> getProfileColumnsMap() {

		return profileColumnsMap;
	}

	public void setSourceFile(File sourceFile) {

		this.sourceFile = sourceFile;
	}

	@Override
	public File getSourceFile() {

		return sourceFile;
	}

	@Override
	public String getUUID() {

		return uuid;
	}

	public void setUUID(String uuid) {

		this.uuid = uuid;
	}

	@Override
	public boolean isFinal() {

		return isReadOnly();
	}

	@Override
	public Set<DataCategory> getDataCategories() {

		return catgories;
	}

	@Override
	public Map<String, String> getMetaData() {

		if(isFinal()) {
			return Collections.unmodifiableMap(metadata);
		}

		return metadata;
	}
}