/*
/*
 * Jirecon, the JItsi REcording COntainer.
 *
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.jirecon;

import org.jitsi.jirecon.api.http.HttpApi;
import org.jitsi.jirecon.task.TaskManager;
import org.jitsi.jirecon.utils.ConfigurationKey;
import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.libjitsi.LibJitsi;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;


/**
 * A launch application which is used to run <tt>TaskManager</tt>.
 * <p>
 * Usually there will be a associated Shell script to start this application.
 * 
 * @author lishunyang
 * 
 */
public class Main
{

	/**
     * Prefix of configuration parameter.
     */
    private static final String CONF_ARG_NAME = "--conf=";

    /**
     * Configuration file path.
     */
    private static String conf;

    /**
     * Application entry.
     * 
     * @param args <tt>JireconLauncher</tt> only cares about two arguments:
     *            <p>
     *            1. --conf=CONFIGURATION_FILE_PATH. Indicate the path of
     *            configuration file.
     *            <p>
     *            2. --time=RECORDING_SECONDS. Indicate how many seconds will
     *            the recording task last. If you didn't specify this parameter,
     *            Jirecon will continue recording forever unless all
     *            participants has left the meeting.
     */
    public static void main(String[] args)
    {
        conf = null;

        for (String arg : args)
        {
            if (arg.startsWith(CONF_ARG_NAME))
                conf = arg.substring(CONF_ARG_NAME.length());
        }

        if (conf == null)
            conf = "jirecon.properties";

        TaskManager jirecon = TaskManager.gI();

        LibJitsi.start();

        System.setProperty(ConfigurationService.PNAME_CONFIGURATION_FILE_NAME, conf);
        System.setProperty(ConfigurationService.PNAME_CONFIGURATION_FILE_IS_READ_ONLY, "true");

        try
        {
            jirecon.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        ConfigurationService cfg = LibJitsi.getConfigurationService();

    	int httpApiPort = cfg.getInt(ConfigurationKey.HTTP_PORT, 2323);
    	System.out.println("Using port "+httpApiPort+" for the HTTP API");

    	try {
			launchHttpServer(httpApiPort, new HttpApi());
		} catch (Exception e) {
		}
    }

    private static void launchHttpServer(int port, Object component)
    		throws Exception
    {
    	ResourceConfig jerseyConfig = new ResourceConfig();
    	jerseyConfig.register(JacksonFeature.class);
    	jerseyConfig.registerInstances(component);

    	ServletHolder servlet = new ServletHolder(new ServletContainer(jerseyConfig));
    	Server server = new Server(port);
    	ServletContextHandler context = new ServletContextHandler(server, "/*");
    	context.addServlet(servlet, "/*");
    	server.start();
    }

}
