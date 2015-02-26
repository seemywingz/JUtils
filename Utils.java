package wrld;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import sun.audio.AudioPlayer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


interface Logic {
    public abstract void apply() throws Exception;
}// end interface Logic

public final class Utils {

    public static Clip mkClip(String soundFile){
        Clip clip = null;
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(Utils.class.getResource(soundFile));
            DataLine.Info lineInfo = new DataLine.Info(Clip.class, ais.getFormat());
            clip = (Clip) AudioSystem.getLine(lineInfo);
            clip.open(ais);
        }catch (Exception e){
            e.printStackTrace();
        }
        return clip;
        //clip.loop(2);
        //Clip theme = AudioSystem.getClip();
    }//..

    public static void setClipVolume(Clip clip,double gain){
        float db = (float) (Math.log(gain)/Math.log(10.0)*20.0);
        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(db); // Reduce by 'volume' decibels.
    }//..

    public static Texture loadTexture(String textureFileName){
        GL2 gl = Scene.gl;
        Texture texture;
        String delims = "[.]+";
        String file[] = textureFileName.split(delims);
        try {
            texture = TextureIO.newTexture(Utils.class.getResourceAsStream(textureFileName), true, file[1]);

            // Use linear filter for texture if image is larger than the original texture
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

            // Use linear filter for texture if image is smaller than the original texture
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL ,0);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL , 20 );

            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE, GL2.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

            // Texture image loads upside down ??.
        } catch (Exception e){
            texture = null;
        }

        return texture;
    }//..

    public static JLabel mkGraphic(Class c,String image,int x,int y,int w, int h){
        try {
            ImageIcon img;
            img = new ImageIcon(ImageIO.read(c.getResourceAsStream(image)));
            img = Utils.scaleImageIcon(img, w, h);
            JLabel graphic = new JLabel(img);
            graphic.setBounds(x, y, w, h);
            return graphic;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }//..

    public static ImageIcon scaleImageIcon(ImageIcon icon, int w, int h){
        Image img = icon.getImage() ;
        return new ImageIcon(  img.getScaledInstance( w, h,  Image.SCALE_SMOOTH )  );
    }//..


    protected static void startThread(final Logic logic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        logic.apply();
                    }catch (Exception e){e.printStackTrace();}
            }
        }).start();
    }//..

    protected static void startThreadLoop(final Logic logic, final int waitMills){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        logic.apply();
                        Thread.sleep(waitMills);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }).start();
    }//..

    protected static FloatBuffer mkFloatBuffer(float vertices[]){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        FloatBuffer fb = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        fb.put(vertices);    // add the coordinates to the FloatBuffer
        fb.position(0);      // set the buffer to read the first coordinate
        return fb;
    }//..

    public static void wait(int mils){
        try {
            Thread.sleep(mils);
        }catch (Exception e){}
    }//..

    public static void playSound(String path){
        AudioPlayer.player.start(Utils.class.getResourceAsStream(path));
    }//..

    public static float calcDistance(Point3d p1, Point3d p2){
        return (float) Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y-p2.y,2)+ Math.pow(p1.z-p2.z,2));
    }//..

    public static float calcDistance(Vector3f p1, Vector3f p2){
        return (float) Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y-p2.y,2)+ Math.pow(p1.z-p2.z,2));
    }//..

    protected static float random(int max){
        double rand =  Math.random()*max;
        if((int)(Math.random()*100) < 50)
            rand = -rand;
        return (float) rand;
    }//..

}// end Class wrld.Utils
