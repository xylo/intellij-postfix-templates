package yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

public class WebTemplateFileParsingTestApp {
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			WebTemplateFile[] entries = mapper.readValue(new File("test/src/yaml/webTemplateFiles.yaml"), WebTemplateFile[].class);
			for (WebTemplateFile entry : entries) {
				System.out.println(ReflectionToStringBuilder.toString(entry, ToStringStyle.MULTI_LINE_STYLE));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
