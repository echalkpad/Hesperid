package ch.astina.hesperid.microclient;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 124 $, $Date: 2011-09-23 13:05:15 +0200 (Fr, 23 Sep 2011) $
 */
public class ClientOutputLogger extends Thread
{
    private InputStream inputStream;

    public ClientOutputLogger(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public void run()
    {
        try {
            OutputStream out = System.out;

            int l;
            byte[] buffer = new byte[1024];
            while ((l = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, l);
                out.flush();
            }
            out.write( '\n' );
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
