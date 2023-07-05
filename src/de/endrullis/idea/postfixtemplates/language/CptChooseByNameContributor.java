package de.endrullis.idea.postfixtemplates.language;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CptChooseByNameContributor implements ChooseByNameContributor {
	@NotNull
	@Override
	public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
		val mappings = CptUtil.findMappings(project);
		val names    = new ArrayList<String>(mappings.size());
		for (CptMapping mapping : mappings) {
			if (mapping.getMatchingClassName() != null && !mapping.getMatchingClassName().isEmpty()) {
				names.add(mapping.getMatchingClassName());
			}
		}
		return names.toArray(new String[0]);
	}

	@NotNull
	@Override
	public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
		// todo include non project items
		val properties = CptUtil.findMappings(project, name);
		//noinspection SuspiciousToArrayCall
		return properties.toArray(new NavigationItem[0]);
	}
}
