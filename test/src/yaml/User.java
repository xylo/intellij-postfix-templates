package yaml;

import lombok.Data;

import java.util.Map;

@Data
public class User {

	private String              name;
	private int                 age;
	private Map<String, String> address;
	private String[]            roles;

}
