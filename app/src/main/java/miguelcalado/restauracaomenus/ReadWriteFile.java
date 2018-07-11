package miguelcalado.restauracaomenus;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Diogo on 09/03/2018.
 */

public class ReadWriteFile {
  String file;
  Context context;

  public ReadWriteFile(String filename, Context context){
    this.file=filename;
    this.context=context;
  }

  public void saveFile(String text) {
      if(text!=null) {
          try {
              FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
              fos.write(text.getBytes());
              fos.close();
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

  public String readFile() {
      String text = null;
      try {
          FileInputStream fis = context.openFileInput(file);
          int size = fis.available();
          byte[] buffer = new byte[size];
          fis.read(buffer);
          fis.close();
          text = new String(buffer);
      } catch (FileNotFoundException e) {
          e.printStackTrace();
          saveFile("");
      } catch (IOException e) {
          e.printStackTrace();
      }
      return text;
  }

}
