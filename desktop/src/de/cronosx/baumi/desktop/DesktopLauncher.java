package de.cronosx.baumi.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.cronosx.baumi.Application;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "baumi";
        config.height = 1000;
        config.width = config.height * 9 / 16;
		new LwjglApplication(new Application(), config);
	}
}

