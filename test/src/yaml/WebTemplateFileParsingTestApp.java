package yaml;

import de.endrullis.idea.postfixtemplates.settings.WebTemplateFile;
import de.endrullis.idea.postfixtemplates.settings.WebTemplateFileLoader;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

public class WebTemplateFileParsingTestApp {
	public static void main(String[] args) {
		try {
			WebTemplateFile[] entries = WebTemplateFileLoader.load(new File("test/src/yaml/webTemplateFiles.yaml"));
			for (WebTemplateFile entry : entries) {
				System.out.println(ReflectionToStringBuilder.toString(entry, ToStringStyle.MULTI_LINE_STYLE));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
