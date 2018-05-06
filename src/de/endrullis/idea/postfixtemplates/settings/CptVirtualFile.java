package de.endrullis.idea.postfixtemplates.settings;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptVirtualFile {

	/** Local (file) or remote (web) address of the templates file to copy. */
	@Getter
	@Setter
	@Nullable
	private URL  url;
	/** Local file (local copy) in the plugin directory . */
	@Getter
	@Setter
	private File file;
	/** Old URL used when changing the the file in the settings dialog. */
	@Getter
	@Setter
	@Nullable
	private URL  oldUrl;
	/** Old file used when changing the the file in the settings dialog. */
	@Getter
	@Setter
	@Nullable
	private File oldFile;
	/** New state used when creating a file in the settings dialog. */
	@Getter
	@Setter
	private boolean isNew;

	public CptVirtualFile(@Nullable URL url, @NotNull File file) {
		this.url = url;
		this.file = file;
	}

	public CptVirtualFile(@Nullable URL url, @NotNull File file, boolean isNew) {
		this.url = url;
		this.file = file;
		this.isNew = isNew;
	}

	public String getName() {
		return file.getName();
	}

	public boolean isLocal() {
		return url != null && url.getProtocol().equals("file");
	}

	public boolean isSelfMade() {
		return url == null;
	}

	public boolean isEditable() {
		return isSelfMade();
	}

	public boolean hasChanged() {
		return isNew || urlHashChanged() || fileHashChanged();
	}

	public boolean urlHashChanged() {
		return oldUrl != null;
	}

	public boolean fileHashChanged() {
		return oldFile != null;
	}

}
