package de.endrullis.idea.postfixtemplates.language;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CptChooseByNameContributor implements ChooseByNameContributor {
	@NotNull
	@Override
	public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
		List<CptMapping> mappings = CptUtil.findMappings(project);
		List<String> names = new ArrayList<>(mappings.size());
		for (CptMapping mapping : mappings) {
			if (mapping.getMatchingClassName() != null && mapping.getMatchingClassName().length() > 0) {
				names.add(mapping.getMatchingClassName());
			}
		}
		return names.toArray(new String[0]);
	}

	@NotNull
	@Override
	public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
		// todo include non project items
		List<CptMapping> properties = CptUtil.findMappings(project, name);
		return properties.toArray(new NavigationItem[0]);
	}
}