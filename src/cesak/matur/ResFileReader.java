package cesak.matur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResFileReader
{
    public List<String> getFile(String fileName)
    {
        InputStream ioStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (ioStream != null)
        {
            List<String> list = new ArrayList<>();

            try (InputStreamReader isr = new InputStreamReader(ioStream); BufferedReader br = new BufferedReader(isr);)
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    list.add(line);
                }

            } catch (IOException ignored)
            {
            }

            return list;
        }
        else
        {
            return null;
        }
    }
}
