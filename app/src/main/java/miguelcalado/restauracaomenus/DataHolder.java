package miguelcalado.restauracaomenus;

/**
 * Created by snovaisg on 2/1/18.
 */

public class DataHolder {
    //file name para gravar o json que vem da base de dados para dentro do telemovel
    private String filename = "myfile";
    private String serverFilename = "maLucasNotification.json"; //enviarMiguel-1.json
    private String bucket = "myhappymealfctbucket";

    public String getDataFilename() {return filename;}
    public void setDatafilename(String data) {this.filename = data;}

    public String getDataServerFilename() {return serverFilename;}
    public void setDataServerFilename(String data) {this.serverFilename = data;}

    public String getDataBucket() {return bucket;}
    public void setDataBucket(String data) {this.bucket = data;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
