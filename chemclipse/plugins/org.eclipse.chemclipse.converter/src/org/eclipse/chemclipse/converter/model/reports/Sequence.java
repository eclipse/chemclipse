/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

import java.util.Collection;
import java.util.function.UnaryOperator;

public class Sequence<T extends ISequenceRecord> extends AbstractFileAttributes<T> implements ISequence<T> {

	private static final long serialVersionUID = 5584821269053736876L;
	//
	private String sequenceId = "";
	private String info = "";
	private String method = "";
	private String dataPath = "";

	@Override
	public String getSequenceId() {

		return sequenceId;
	}

	@Override
	public void setSequenceId(String sequenceId) {

		this.sequenceId = sequenceId;
	}

	@Override
	public String getInfo() {

		return info;
	}

	@Override
	public void setInfo(String info) {

		this.info = info;
	}

	@Override
	public String getMethod() {

		return method;
	}

	@Override
	public void setMethod(String method) {

		this.method = method;
	}

	@Override
	public String getDataPath() {

		return dataPath;
	}

	@Override
	public void setDataPath(String dataPath) {

		this.dataPath = dataPath;
		updateDataPath();
	}

	@Override
	public void add(int arg0, T arg1) {

		super.add(arg0, arg1);
		updateDataPath();
	}

	@Override
	public boolean add(T arg0) {

		boolean result = super.add(arg0);
		updateDataPath();
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {

		boolean result = super.addAll(arg0);
		updateDataPath();
		return result;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {

		boolean result = super.addAll(arg0, arg1);
		updateDataPath();
		return result;
	}

	@Override
	public void replaceAll(UnaryOperator<T> arg0) {

		super.replaceAll(arg0);
		updateDataPath();
	}

	private void updateDataPath() {

		for(int i = 0; i < size(); i++) {
			T sequenceRecord = get(i);
			sequenceRecord.setDataPath(dataPath);
		}
	}
}
