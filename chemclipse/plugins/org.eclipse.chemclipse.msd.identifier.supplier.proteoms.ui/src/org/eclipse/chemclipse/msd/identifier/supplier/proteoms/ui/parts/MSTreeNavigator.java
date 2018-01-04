/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.parts;

import java.util.List;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ProteomsUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.AbstractSpectrum;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.model.ProteomsProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class MSTreeNavigator {

	public static class TreeLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {

			if(element instanceof AbstractSpectrum) {
				AbstractSpectrum spec = (AbstractSpectrum)element;
				return spec.getName();
			}
			if(element instanceof ProteomsProject) {
				ProteomsProject p = (ProteomsProject)element;
				return p.getName();
			}
			return "???";
		}
	}

	public static class TreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public Object[] getElements(Object inputElement) {

			if(inputElement instanceof List) {
				List<?> list = (List<?>)inputElement;
				return list.toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {

			if(parentElement instanceof SpectrumMS) {
				SpectrumMS ms = (SpectrumMS)parentElement;
				return ms.getMsmsSpectrumsChildren().toArray();
			}
			if(parentElement instanceof ProteomsProject) {
				ProteomsProject p = (ProteomsProject)parentElement;
				List<SpectrumMS> msList = p.getMsList();
				if(msList != null) {
					return msList.toArray();
				}
				return ProteomsUtil.EMPTY_ARRAY;
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {

			if(element instanceof SpectrumMSMS) {
				SpectrumMSMS msms = (SpectrumMSMS)element;
				return msms.getParentMS();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {

			if(element instanceof SpectrumMS) {
				SpectrumMS ms = (SpectrumMS)element;
				if(!ms.getMsmsSpectrumsChildren().isEmpty()) {
					return true;
				}
			}
			if(element instanceof ProteomsProject) {
				ProteomsProject p = (ProteomsProject)element;
				if(p.containMSdata()) {
					return true;
				}
				return false;
			}
			return false;
		}
	}
}
