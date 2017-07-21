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
public class CptApplicationSettings implements PersistentStateComponent<CptApplicationSettings.State>, CptPluginSettings.Holder {

  private State state = new State();

  public CptApplicationSettings() {
  }

  @NotNull
  public static CptApplicationSettings getInstance() {
    return ServiceManager.getService(CptApplicationSettings.class);
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
  public void setPluginSettings(@NotNull CptPluginSettings settings) {
    state.pluginSettings = settings;

    ApplicationManager.getApplication().getMessageBus().syncPublisher(SettingsChangedListener.TOPIC).onSettingsChange(this);
  }

  @NotNull
  @Override
  public CptPluginSettings getPluginSettings() {
    return state.pluginSettings;
  }



  public static class State {
    @Property(surroundWithTag = false)
    @NotNull
    private CptPluginSettings pluginSettings = CptPluginSettings.DEFAULT;
  }

  public interface SettingsChangedListener {
    Topic<SettingsChangedListener> TOPIC = Topic.create("CustomPostfixTemplatesApplicationSettingsChanged", SettingsChangedListener.class);

    void onSettingsChange(@NotNull CptApplicationSettings settings);
  }
}
