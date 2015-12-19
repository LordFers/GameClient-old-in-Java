package client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Surface
{
    static final int HASH_TABLE_SIZE = 541;
    
    public class SurfaceDB
    {
        public class SURFACEDATA
        {
            int filenum;
            long lastaccess;
            int texture;
            int tex_width;
            int tex_height;
        }

        public class HASHDATA
        {
            int surfaceCount = 0;
            SURFACEDATA[] surfaceData;
        }

        HASHDATA[] TexList = new HASHDATA[HASH_TABLE_SIZE];
    }
    
    public class TextureOGL
    {
        int id;
        int tex_width;
        int tex_height;
    }
    
    SurfaceDB surface = new SurfaceDB();
    private ByteBuffer pixels;
    private TextureOGL tx = new TextureOGL();
    int textureWidth, textureHeight;
    
    public void initialize()
    {
        for (int i = 0; i <= HASH_TABLE_SIZE - 1; i++)
        {
            surface.TexList[i] = surface.new HASHDATA();
        }
    }

    public TextureOGL getTexture(int fileNum)
    {
        int index = (fileNum % HASH_TABLE_SIZE);
        for (int i = 1; i <= surface.TexList[index].surfaceCount; i++)
        {
            if (surface.TexList[index].surfaceData[i].filenum == fileNum)
            {
                tx.id = surface.TexList[index].surfaceData[i].texture;
                tx.tex_width = surface.TexList[index].surfaceData[i].tex_width;
                tx.tex_height = surface.TexList[index].surfaceData[i].tex_height;
                return tx;
            }
        }
        return createTexture(fileNum);
    }
    
    private TextureOGL createTexture(int fileNum)
    {
        int index = (fileNum % HASH_TABLE_SIZE);
        if (surface.TexList[index].surfaceCount > 0)
        {
            SurfaceDB.SURFACEDATA[] surfaceData2 = new SurfaceDB.SURFACEDATA[surface.TexList[index].surfaceCount + 1];
            System.arraycopy(surface.TexList[index].surfaceData, 1, surfaceData2, 1, surface.TexList[index].surfaceCount);
            
            int count = surface.TexList[index].surfaceCount + 1;
            surface.TexList[index].surfaceData = new SurfaceDB.SURFACEDATA[count+1];
            surface.TexList[index].surfaceData[count] = surface.new SURFACEDATA();
            
            System.arraycopy(surfaceData2, 1, surface.TexList[index].surfaceData, 1, surface.TexList[index].surfaceCount);
        }
        else
        {
            surface.TexList[index].surfaceData = new SurfaceDB.SURFACEDATA[2];
            surface.TexList[index].surfaceData[1] = surface.new SURFACEDATA();
            surface.TexList[index].surfaceCount++;
        }
        
        surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].filenum = fileNum;
        surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].lastaccess = System.currentTimeMillis();
        surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].texture = loadTexture("resources/graphics/" + fileNum + ".bmp");//("C:\\Users\\Lord Fers\\Desktop\\AO Java\\client\\src\\graphics\\" + fileNum + ".bmp");
        surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].tex_width = textureWidth;
        surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].tex_height = textureHeight;
        
        tx.id = surface.TexList[index].surfaceData[surface.TexList[index].surfaceCount].texture;
        tx.tex_width = textureWidth;
        tx.tex_height = textureHeight;
        
        return tx;
    }
    
    int id;
    public int loadTexture(String file)
    {
        pixels = BufferUtils.createByteBuffer(1);
        BufferedImage bi;
        try
        {
            File fil = new File(file);
            BufferedImage image = ImageIO.read(fil);
            
            textureWidth = image.getWidth();
            textureHeight = image.getHeight();

            bi = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_4BYTE_ABGR);
            
            Graphics2D g = bi.createGraphics();
            g.scale(1, -1);
            g.drawImage(image, 0, 0, textureWidth, -textureHeight, null);
            
            byte[] data = new byte[4 * textureWidth * textureHeight];
            bi.getRaster().getDataElements(0, 0, textureWidth, textureHeight, data);

            for (int j = 0; j < textureWidth * textureHeight; j++) // <= tw*th -1
            {
                if (data[j*4] == 0 && data[j*4 + 1] == 0 && data[j*4 + 2] == 0)
                {
                    data[j*4] = -1;
                    data[j*4+1] = -1;
                    data[j*4+2] = -1;
                    data[j*4+3] = 0;
                }
                else
                {
                    data[j*4+3] = -1;
                }
            }
            IntBuffer i = BufferUtils.createIntBuffer(1);
            GL11.glGenTextures(i);
            
            id =  i.get(0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            
            pixels = BufferUtils.createByteBuffer(data.length);
            pixels.put(data);
            pixels.rewind();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                textureWidth, textureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
            
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        } 
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return id;
    }
}
