package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = "CustomPostfixTemplatesApplicationSettings",
  storages = @Storage("customPostfixTemplates.xml")
)
public class PluginApplicationSettings implements PersistentStateComponent<PluginApplicationSettings.State>, PluginSettings.Holder {

  private State state = new State();

  public PluginApplicationSettings() {
  }

  @NotNull
  public static PluginApplicationSettings getInstance() {
    return ServiceManager.getService(PluginApplicationSettings.class);
  }

  @Nullable
  @Override
  public State getState() {
    return state;
  }

  @Override
  public void loadState(State state) {
    XmlSerializerUtil.copyBean(state, this.state);
  }

  @Override
  public void setPluginSettings(@NotNull PluginSettings settings) {
    state.pluginSettings = settings;

    ApplicationManager.getApplication().getMessageBus().syncPublisher(SettingsChangedListener.TOPIC).onSettingsChange(this);
  }

  @NotNull
  @Override
  public PluginSettings getPluginSettings() {
    return state.pluginSettings;
  }



  public static class State {
    @Property(surroundWithTag = false)
    @NotNull
    private PluginSettings pluginSettings = PluginSettings.DEFAULT;
  }

  public interface SettingsChangedListener {
    Topic<SettingsChangedListener> TOPIC = Topic.create("CustomPostfixTemplatesApplicationSettingsChanged", SettingsChangedListener.class);

    void onSettingsChange(@NotNull PluginApplicationSettings settings);
  }
}
