package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.model;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;

public class ProteomsProject {

	private String name;
	private List<SpectrumMS> msList;
	private Path dir;

	public static enum Type {
		CAF_CAF, NORMAL
	}

	public ProteomsProject() {
	}

	public ProteomsProject(Path dir) {
		this.dir = dir;
		this.name = dir.getName(dir.getNameCount() - 1).toString();
	}

	public Path getDir() {

		return dir;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public List<SpectrumMS> getMsList() {

		return msList;
	}

	public void setMsList(List<SpectrumMS> msList) {

		this.msList = msList;
	}

	public boolean containMSdata() {

		if(msList == null) {
			return false;
		}
		return !msList.isEmpty();
	}
}
