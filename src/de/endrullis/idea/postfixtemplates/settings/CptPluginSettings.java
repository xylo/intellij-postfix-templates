package de.endrullis.idea.postfixtemplates.settings;

import org.jetbrains.annotations.NotNull;

public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Override
	public boolean equals(Object o) {
		return o instanceof CptPluginSettings;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}
}
