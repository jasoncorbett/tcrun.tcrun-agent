
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.io.FilenameUtils;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.webapp.WebAppContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * TODO fix or dead!
 * @author seiji
 */
public class Start {

    public static void main(String[] args) {
        try {
            final Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(4200);
            server.addConnector(connector);

            URL url = Start.class.getClassLoader().getResource("Start.class");
            File warFile = new File(((JarURLConnection) url.openConnection()).getJarFile().getName());
	    String outputPath = FilenameUtils.concat(FilenameUtils.getFullPath(warFile.getAbsolutePath()), "output");

	    Context output = new Context();
	    output.setContextPath("/output");

	    output.setResourceBase(outputPath);
	    output.addServlet(DefaultServlet.class, "/");

	    HashMap<String, String> outputParams = new HashMap<String,String>();
	    outputParams.put("org.mortbay.jetty.servlet.Default.dirAllowed", "true");
	    output.setInitParams(outputParams);

            WebAppContext context = new WebAppContext(warFile.getAbsolutePath(), "/svc");

            context.setConfigurationClasses(new String[]{
                    "org.mortbay.jetty.webapp.WebInfConfiguration",
                    "org.mortbay.jetty.plus.webapp.EnvConfiguration",
                    "org.mortbay.jetty.plus.webapp.Configuration",
                    "org.mortbay.jetty.webapp.JettyWebXmlConfiguration",
                    "org.mortbay.jetty.webapp.TagLibConfiguration"
                });
	    HashMap<String, String> params = new HashMap<String,String>();
	    params.put("base-jar", warFile.getAbsolutePath());
	    params.put("output-path", outputPath);
	    context.setInitParams(params);

	    server.addHandler(output);
            server.addHandler(context);


            server.start();
	    final SystemTray tray = SystemTray.getSystemTray();
	    Dimension size = tray.getTrayIconSize();
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Image icon = kit.createImage(Start.class.getResource("/gas_can_square.png"));

	    // Popup menu
	    PopupMenu menu = new PopupMenu();
	    MenuItem exitmenuitem = new MenuItem("Quit");
	    menu.add(exitmenuitem);
	    final TrayIcon trayapplet = new TrayIcon(icon, "Gasoline is the tcrun-agent");
	    trayapplet.setPopupMenu(menu);
	    tray.add(trayapplet);
	    exitmenuitem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					tray.remove(trayapplet);
					try
					{
						server.stop();
					} catch(Exception ex)
					{
						System.err.println("Error stopping jetty: " + ex.getMessage());
						ex.printStackTrace(System.err);
					}
					System.exit(0);
				}
			});
            server.join();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
