package ar.com.anura.plugins.backgroundmode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BackgroundModeSettingsTest {

    @Test
    public void buildAppliesDefaults() {
        BackgroundModeSettings settings = new BackgroundModeSettings.Builder().build();

        assertEquals(BackgroundModeSettings.DEFAULT_TITLE, settings.getTitle());
        assertEquals(BackgroundModeSettings.DEFAULT_VISIBILITY, settings.getVisibility());
        assertFalse(settings.getSilent());
        assertTrue(settings.isDisableWebViewOptimization());
    }

    @Test
    public void mergeOnlyOverridesProvidedValues() {
        BackgroundModeSettings current = new BackgroundModeSettings.Builder().title("Call in progress").silent(false).build();
        BackgroundModeSettings override = new BackgroundModeSettings.Builder().silent(true).buildRaw();

        BackgroundModeSettings merged = current.merge(override);

        assertEquals("Call in progress", merged.getTitle());
        assertTrue(merged.getSilent());
        assertEquals(BackgroundModeSettings.DEFAULT_TEXT, merged.getText());
    }

    @Test
    public void visibilityLookupHandlesKnownAndMissingValues() {
        assertEquals(Visibility.PUBLIC, Visibility.valueOfLabel("public"));
        assertEquals(Visibility.SECRET, Visibility.valueOfLabel("secret"));
        assertNull(Visibility.valueOfLabel(null));
    }
}
