package de.endrullis.idea.postfixtemplates.settings;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptVirtualFile {

	@Getter
	private URL    url;
	@Getter
	private File   file;

	public CptVirtualFile(@NotNull URL url, @NotNull File file) {
		this.url = url;
		this.file = file;
	}

	public String getName() {
		return file.getName();
	}

	public boolean isLocal() {
		return url.getProtocol().equals("file");
	}

	public boolean isEditable() {
		return false;
	}

}
