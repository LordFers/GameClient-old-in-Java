package client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GameData
{
    public class GrhData
    {
        short sX;
        short sY;

        int fileNum;

        short pixelWidth;
        short pixelHeight;

        float tileWidth;
        float tileHeight;

        short numFrames;
        int frames[];

        float speed;
    }
    
    public class Grh
    {
        short grhIndex;
        float frameCounter;
        float speed;
        boolean started;
        int loops;
        float angle;
        
        public Grh()
        {
            started = false;
            grhIndex = 1;
            frameCounter = 1.0f;
            loops = 0;
            speed = 0.0f;
            angle = 0.0f;
        }
        
        public Grh(Grh other)
        {
            grhIndex = other.grhIndex;
            frameCounter = other.frameCounter;
            speed = other.speed;
            started = other.started;
            loops = other.loops;
            angle = other.angle;
        }
    }
    
    public class Obj
    {
        short objindex;
        short amount;
    }
    
    public class WorldPos
    {
        short map;
        short x;
        short y;
    }
    
    public class Position
    {
        int x;
        int y;
    }
    
    public class BodyData
    {
        Grh[] Walk = new Grh[4+1];
        Position HeadOffset;
        
        public BodyData()
        {
            Walk[1] = new Grh();
            Walk[2] = new Grh();
            Walk[3] = new Grh();
            Walk[4] = new Grh();
            
            HeadOffset = new Position();
        }
        
        public BodyData(BodyData other)
        {
            Walk[1] = new Grh(other.Walk[1]);
            Walk[2] = new Grh(other.Walk[2]);
            Walk[3] = new Grh(other.Walk[3]);
            Walk[4] = new Grh(other.Walk[4]);
            
            HeadOffset = other.HeadOffset;
        }
    }
    
    public class HeadData
    {
        Grh[] Head = new Grh[4+1];
        
        public HeadData()
        {
            Head[1] = new Grh();
            Head[2] = new Grh();
            Head[3] = new Grh();
            Head[4] = new Grh();
        }
        
        public HeadData(HeadData other)
        {
            Head[1] = new Grh(other.Head[1]);
            Head[2] = new Grh(other.Head[2]);
            Head[3] = new Grh(other.Head[3]);
            Head[4] = new Grh(other.Head[4]);
        }
    }
    
    public class WeaponData
    {
        Grh[] WeaponWalk = new Grh[4+1];
        
        public WeaponData()
        {
            WeaponWalk[1] = new Grh();
            WeaponWalk[2] = new Grh();
            WeaponWalk[3] = new Grh();
            WeaponWalk[4] = new Grh();
        }
        
        public WeaponData(WeaponData other)
        {
            WeaponWalk[1] = new Grh(other.WeaponWalk[1]);
            WeaponWalk[2] = new Grh(other.WeaponWalk[2]);
            WeaponWalk[3] = new Grh(other.WeaponWalk[3]);
            WeaponWalk[4] = new Grh(other.WeaponWalk[4]);
        }
    }
    
    public class ShieldData
    {
        Grh[] ShieldWalk = new Grh[4+1];
        
        public ShieldData()
        {
            ShieldWalk[1] = new Grh();
            ShieldWalk[2] = new Grh();
            ShieldWalk[3] = new Grh();
            ShieldWalk[4] = new Grh();
        }
        
        public ShieldData(ShieldData other)
        {
            ShieldWalk[1] = new Grh(other.ShieldWalk[1]);
            ShieldWalk[2] = new Grh(other.ShieldWalk[2]);
            ShieldWalk[3] = new Grh(other.ShieldWalk[3]);
            ShieldWalk[4] = new Grh(other.ShieldWalk[4]);
        }
    }
    
    public class tIndexBodys
    {
        short Body[] = new short [4+1];
        short HeadOffsetX;
        short HeadOffsetY;
    }
    
    public class tIndexHeads
    {
        short[] Head = new short[4+1];
    }
    
    public class MapData
    {
        Grh[] layer = new Grh[4+1];
        short charindex;
        Grh objgrh;
        short npcindex;
        Obj objinfo;
        WorldPos tileexit;
        byte blocked;
        short trigger;
        
        public MapData()
        {
            layer[1] = new Grh();
            layer[2] = new Grh();
            layer[3] = new Grh();
            layer[4] = new Grh();
            objgrh = new Grh();
        }
    }
    
    public class tFont
    {
        short font_size;
        int[] ascii_code = new int[256];
    }
    
    BodyData[] BodyData;
    HeadData[] HeadData;
    HeadData[] HelmetsData;
    WeaponData[] WeaponData;
    ShieldData[] ShieldData;
    
    GrhData[] GrhData;
    MapData[][] MapData;
    tFont[] font_types = new tFont[3];
    int LastChar = 0;
    
    public void initialize()
    {
        System.out.print("loading and processing the gamedata...");
        
        System.out.print("initializing the loadGrhData");
        loadGrhData();
        System.out.print("...loadGrhData OK!");
        
        System.out.print("initializing the loadHeads");
        loadHeads();
        System.out.print("...loadHeads OK!");
        
        System.out.print("initializing the loadHelmets");
        loadHelmets();
        System.out.print("...loadHelmets OK!");
        
        System.out.print("initializing the loadBodys");
        loadBodys();
        System.out.print("...loadBodys OK!");
        
        System.out.print("initializing the loadArms");
        loadArms();
        System.out.print("...loadArms OK!");
        
        System.out.print("initializing the loadShields");
        loadShields();
        System.out.print("...loadShields OK!");
        
        System.out.print("initializing the loadFonts");
        loadFonts();
        System.out.print("...loadFonts OK!");
    }
    
    private static int bigToLittle_Int(int bigendian)
    {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getInt(0);
    }

    private static float bigToLittle_Float(float bigendian)
    {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putFloat(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getFloat(0);
    }
    
    private static short bigToLittle_Short(short bigendian)
    {
        ByteBuffer buf = ByteBuffer.allocate(2);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getShort(0);
    }
    
    private static byte bigToLittle_Byte(byte bigendian)
    {
        ByteBuffer buf = ByteBuffer.allocate(1);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.get(0);
    }
    
    private void loadGrhData()
    {
        try
        {
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/graphics.ind", "rw")) //("C:\\Users\\Lord Fers\\Desktop\\AO Java\\client\\src\\inits\\graphics.ind", "rw");
            {
                f.seek(0);
                
                int fileversion = bigToLittle_Int(f.readInt());
                int grhCount = bigToLittle_Int(f.readInt());
                float tempfloat;
                
                GrhData = new GrhData[grhCount + 1];
                int Grh = 0;
                GrhData[0] = new GrhData();
                
                while (Grh < grhCount)
                {
                    Grh = bigToLittle_Int(f.readInt());
                    GrhData[Grh] = new GrhData();
                    GrhData[Grh].numFrames = bigToLittle_Short(f.readShort());
                    
                    if (GrhData[Grh].numFrames <= 0)
                    {
                        new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh);
                        return;
                    }
                    
                    GrhData[Grh].frames = new int[GrhData[Grh].numFrames + 1];
                    
                    if (GrhData[Grh].numFrames > 1)
                    {
                        for(short frame = 1; frame <= GrhData[Grh].numFrames; frame++)
                        {
                            GrhData[Grh].frames[frame] = bigToLittle_Int(f.readInt());
                            if (GrhData[Grh].frames[frame] <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh);
                            return;}
                        }

                        tempfloat = bigToLittle_Float(f.readFloat());
                        if (tempfloat <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                        GrhData[Grh].speed = tempfloat;

                        GrhData[Grh].pixelHeight = GrhData[GrhData[Grh].frames[1]].pixelHeight;
                        if (GrhData[Grh].pixelHeight <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                        GrhData[Grh].pixelWidth = GrhData[GrhData[Grh].frames[1]].pixelWidth;
                        if (GrhData[Grh].pixelWidth <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                        GrhData[Grh].tileWidth = GrhData[GrhData[Grh].frames[1]].tileWidth;
                        if (GrhData[Grh].tileWidth <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                        GrhData[Grh].tileHeight = GrhData[GrhData[Grh].frames[1]].tileHeight;
                        if (GrhData[Grh].tileHeight <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                    }
                    else
                    {
                        GrhData[Grh].fileNum = bigToLittle_Int(f.readInt());
                        if (GrhData[Grh].fileNum <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}
                        GrhData[Grh].sX = bigToLittle_Short(f.readShort());
                        if (GrhData[Grh].sX < 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}

                        GrhData[Grh].sY = bigToLittle_Short(f.readShort());
                        if (GrhData[Grh].sY < 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}

                        GrhData[Grh].pixelWidth = bigToLittle_Short(f.readShort());
                        if (GrhData[Grh].pixelWidth <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}

                        GrhData[Grh].pixelHeight = bigToLittle_Short(f.readShort());
                        if (GrhData[Grh].pixelHeight <= 0){new Client().MessageBox("ERROR IN THE GRHINDEX: " + Grh); return;}

                        GrhData[Grh].tileWidth = (float)GrhData[Grh].pixelWidth / 32;
                        GrhData[Grh].tileHeight = (float)GrhData[Grh].pixelHeight / 32;

                        GrhData[Grh].frames[1] = (short)Grh;
                    }
                }   
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadHeads()
    {
        try{
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/heads.ind", "rw")) {
                f.seek(0);
                
                short numHeads;
                tIndexHeads[] myHeads;
                byte[] cabecera = new byte[263];
                
                f.read(cabecera);
                numHeads = bigToLittle_Short(f.readShort());
                
                HeadData = new HeadData[numHeads + 1];
                myHeads = new tIndexHeads[numHeads + 1];
                
                for (int i = 1; i <= numHeads; i++)
                {
                    myHeads[i] = new tIndexHeads();
                    myHeads[i].Head[1] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[2] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[3] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[4] = bigToLittle_Short(f.readShort());
                    
                    if (myHeads[i].Head[1] != 0)
                    {
                        HeadData[i] = new HeadData();
                        HeadData[i].Head[1] = InitGrh(HeadData[i].Head[1], myHeads[i].Head[1], false);
                        HeadData[i].Head[2] = InitGrh(HeadData[i].Head[2], myHeads[i].Head[2], false);
                        HeadData[i].Head[3] = InitGrh(HeadData[i].Head[3], myHeads[i].Head[3], false);
                        HeadData[i].Head[4] = InitGrh(HeadData[i].Head[4], myHeads[i].Head[4], false);
                    }
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadHelmets()
    {
        try{
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/helmets.ind", "rw")) {
                f.seek(0);
                
                short numHeads;
                tIndexHeads[] myHeads;
                byte[] cabecera = new byte[263];
                
                f.read(cabecera);
                numHeads = bigToLittle_Short(f.readShort());
                
                HelmetsData = new HeadData[numHeads + 1];
                myHeads = new tIndexHeads[numHeads + 1];
                
                for (int i = 1; i <= numHeads; i++)
                {
                    myHeads[i] = new tIndexHeads();
                    myHeads[i].Head[1] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[2] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[3] = bigToLittle_Short(f.readShort());
                    myHeads[i].Head[4] = bigToLittle_Short(f.readShort());
                    
                    if (myHeads[i].Head[1] != 0)
                    {
                        HelmetsData[i] = new HeadData();
                        HelmetsData[i].Head[1] = InitGrh(HelmetsData[i].Head[1], myHeads[i].Head[1], false);
                        HelmetsData[i].Head[2] = InitGrh(HelmetsData[i].Head[2], myHeads[i].Head[2], false);
                        HelmetsData[i].Head[3] = InitGrh(HelmetsData[i].Head[3], myHeads[i].Head[3], false);
                        HelmetsData[i].Head[4] = InitGrh(HelmetsData[i].Head[4], myHeads[i].Head[4], false);
                    }
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadBodys()
    {
        try{
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/bodys.ind", "rw")) {
                f.seek(0);
                
                short numBodys;
                tIndexBodys[] myBodys;
                byte[] cabecera = new byte[263];
                
                f.read(cabecera);
                numBodys = bigToLittle_Short(f.readShort());
                
                BodyData = new BodyData[numBodys + 1];
                myBodys = new tIndexBodys[numBodys + 1];
                
                for (int i = 1; i <= numBodys; i++)
                {
                    myBodys[i] = new tIndexBodys();
                    myBodys[i].Body[1] = bigToLittle_Short(f.readShort());
                    myBodys[i].Body[2] = bigToLittle_Short(f.readShort());
                    myBodys[i].Body[3] = bigToLittle_Short(f.readShort());
                    myBodys[i].Body[4] = bigToLittle_Short(f.readShort());
                    
                    myBodys[i].HeadOffsetX = bigToLittle_Short(f.readShort());
                    myBodys[i].HeadOffsetY = bigToLittle_Short(f.readShort());
                    
                    if (myBodys[i].Body[1] != 0)
                    {
                        BodyData[i] = new BodyData();
                        BodyData[i].Walk[1] = InitGrh(BodyData[i].Walk[1], myBodys[i].Body[1], false);
                        BodyData[i].Walk[2] = InitGrh(BodyData[i].Walk[2], myBodys[i].Body[2], false);
                        BodyData[i].Walk[3] = InitGrh(BodyData[i].Walk[3], myBodys[i].Body[3], false);
                        BodyData[i].Walk[4] = InitGrh(BodyData[i].Walk[4], myBodys[i].Body[4], false);
                        
                        BodyData[i].HeadOffset.x = myBodys[i].HeadOffsetX;
                        BodyData[i].HeadOffset.y = myBodys[i].HeadOffsetY;
                    }
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadArms()
    {
        try{
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/arms.ind", "rw")) {
                int numArms = bigToLittle_Short(f.readShort());
                WeaponData = new WeaponData[numArms + 1];
                
                short tempshort;
                for(int loopc = 1; loopc <= numArms; loopc++)
                {
                    WeaponData[loopc] = new WeaponData();
                    tempshort = bigToLittle_Short(f.readShort());
                    //if (tempshort <= 0) tempshort++;
                    WeaponData[loopc].WeaponWalk[1] = InitGrh(WeaponData[loopc].WeaponWalk[1], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    //if (tempshort <= 0) tempshort++;
                    WeaponData[loopc].WeaponWalk[2] = InitGrh(WeaponData[loopc].WeaponWalk[2], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    //if (tempshort <= 0) tempshort++;
                    WeaponData[loopc].WeaponWalk[3] = InitGrh(WeaponData[loopc].WeaponWalk[3], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    //if (tempshort <= 0) tempshort++;
                    WeaponData[loopc].WeaponWalk[4] = InitGrh(WeaponData[loopc].WeaponWalk[4], tempshort, false);
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadShields()
    {
        try{
            try (RandomAccessFile f = new RandomAccessFile("resources/inits/shields.ind", "rw")) {
                int numShields = bigToLittle_Short(f.readShort());
                ShieldData = new ShieldData[numShields + 1];
                
                short tempshort;
                for(int loopc = 1; loopc <= numShields; loopc++)
                {
                    ShieldData[loopc] = new ShieldData();
                    
                    tempshort = bigToLittle_Short(f.readShort());
                    ShieldData[loopc].ShieldWalk[1] = InitGrh(ShieldData[loopc].ShieldWalk[1], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    ShieldData[loopc].ShieldWalk[2] = InitGrh(ShieldData[loopc].ShieldWalk[2], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    ShieldData[loopc].ShieldWalk[3] = InitGrh(ShieldData[loopc].ShieldWalk[3], tempshort, false);
                    tempshort = bigToLittle_Short(f.readShort());
                    ShieldData[loopc].ShieldWalk[4] = InitGrh(ShieldData[loopc].ShieldWalk[4], tempshort, false);
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void loadmap(int map)
    {
        try
        {System.out.print("initializing the loadMapData");
            try (RandomAccessFile f = new RandomAccessFile("resources/maps/mapa" + map + ".map", "rw")) {
                f.seek(0);
                
                MapData = new MapData[101][101];
                
                short mapversion = bigToLittle_Short(f.readShort());
                byte[] cabecera = new byte[263]; f.read(cabecera);
                
                byte byflags = 0; short tempint;
                
                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());
                
                byte bloq;
                
                for (int y = 1; y <= 100; y++)
                {
                    for (int x = 1; x <= 100; x++)
                    {
                        MapData[x][y] = new MapData();
                        
                        byflags = bigToLittle_Byte(f.readByte());
                        bloq = (byte)(byflags & 1);
                        MapData[x][y].blocked = bloq;
                        
                        MapData[x][y].layer[1].grhIndex = bigToLittle_Short(f.readShort());
                        MapData[x][y].layer[1] = InitGrh(MapData[x][y].layer[1],
                                MapData[x][y].layer[1].grhIndex, true);
                        
                        if ((byte)(byflags & 2) != 0)
                        {
                            MapData[x][y].layer[2].grhIndex = bigToLittle_Short(f.readShort());
                            MapData[x][y].layer[2] = InitGrh(MapData[x][y].layer[2],
                                    MapData[x][y].layer[2].grhIndex, true);
                        }
                        else
                        {
                            MapData[x][y].layer[2].grhIndex = 0;
                        }
                        
                        if ((byte)(byflags & 4) != 0)
                        {
                            
                            MapData[x][y].layer[3].grhIndex = bigToLittle_Short(f.readShort());
                            MapData[x][y].layer[3] = InitGrh(MapData[x][y].layer[3],
                                    MapData[x][y].layer[3].grhIndex, true);
                        }
                        else
                        {
                            MapData[x][y].layer[3].grhIndex = 0;
                        }
                        
                        if ((byte)(byflags & 8) != 0)
                        {
                            MapData[x][y].layer[4].grhIndex = bigToLittle_Short(f.readShort());
                            MapData[x][y].layer[4] = InitGrh(MapData[x][y].layer[4],
                                    MapData[x][y].layer[4].grhIndex, true);
                        }
                        else
                        {
                            MapData[x][y].layer[4].grhIndex = 0;
                        }
                        
                        if ((byte)(byflags & 16) != 0)
                        {
                            MapData[x][y].trigger = bigToLittle_Short(f.readShort());
                        }
                        else
                        {
                            MapData[x][y].trigger = 0;
                        }
                        
                        if (MapData[x][y].charindex > 0)
                        {
                            //EraseChar;
                        }
                        
                        MapData[x][y].objgrh.grhIndex = 0;
                    }
                }   }
        System.out.print("...loadMapData OK!");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void loadFonts()
    {
        int a;
        font_types[1] = new tFont();
        font_types[1].font_size = 9;
        font_types[1].ascii_code[48] = 23703;
        font_types[1].ascii_code[49] = 23704;
        font_types[1].ascii_code[50] = 23705;
        font_types[1].ascii_code[51] = 23706;
        font_types[1].ascii_code[52] = 23707;
        font_types[1].ascii_code[53] = 23708;
        font_types[1].ascii_code[54] = 23709;
        font_types[1].ascii_code[55] = 23710;
        font_types[1].ascii_code[56] = 23711;
        font_types[1].ascii_code[57] = 23712;

        for (a = 0; a <= 25; a++)
        {
            font_types[1].ascii_code[a + 97] = 23651 + a;
        }

        for (a = 0; a <= 25; a++)
        {
            font_types[1].ascii_code[a + 65] = 23677 + a;
        }

        font_types[1].ascii_code[33] = 23713;
        font_types[1].ascii_code[161] = 23714;
        font_types[1].ascii_code[34] = 23715;
        font_types[1].ascii_code[36] = 23716;
        font_types[1].ascii_code[191] = 23717;
        font_types[1].ascii_code[35] = 23718;
        font_types[1].ascii_code[36] = 23719;
        font_types[1].ascii_code[37] = 23720;
        font_types[1].ascii_code[38] = 23721;
        font_types[1].ascii_code[47] = 23722;
        font_types[1].ascii_code[92] = 23723;
        font_types[1].ascii_code[40] = 23724;
        font_types[1].ascii_code[41] = 23725;
        font_types[1].ascii_code[61] = 23726;
        font_types[1].ascii_code[39] = 23727;
        font_types[1].ascii_code[123] = 23728;
        font_types[1].ascii_code[125] = 23729;
        font_types[1].ascii_code[95] = 23730;
        font_types[1].ascii_code[45] = 23731;
        font_types[1].ascii_code[63] = 23716;
        font_types[1].ascii_code[64] = 23732;
        font_types[1].ascii_code[94] = 23733;
        font_types[1].ascii_code[91] = 23734;
        font_types[1].ascii_code[93] = 23735;
        font_types[1].ascii_code[60] = 23736;
        font_types[1].ascii_code[62] = 23737;
        font_types[1].ascii_code[42] = 23738;
        font_types[1].ascii_code[43] = 23739;
        font_types[1].ascii_code[46] = 23740;
        font_types[1].ascii_code[44] = 23741;
        font_types[1].ascii_code[58] = 23742;
        font_types[1].ascii_code[59] = 23743;
        font_types[1].ascii_code[124] = 23744;
        /////////////////
        font_types[1].ascii_code[252] = 23845;
        font_types[1].ascii_code[220] = 23846;
        font_types[1].ascii_code[225] = 23847;
        font_types[1].ascii_code[233] = 23848;
        font_types[1].ascii_code[237] = 23849;
        font_types[1].ascii_code[243] = 23850;
        font_types[1].ascii_code[250] = 23851;
        font_types[1].ascii_code[253] = 23852;
        font_types[1].ascii_code[193] = 23853;
        font_types[1].ascii_code[201] = 23854;
        font_types[1].ascii_code[205] = 23855;
        font_types[1].ascii_code[211] = 23856;
        font_types[1].ascii_code[218] = 23857;
        font_types[1].ascii_code[221] = 23858;
        font_types[1].ascii_code[224] = 23859;
        font_types[1].ascii_code[232] = 23860;
        font_types[1].ascii_code[236] = 23861;
        font_types[1].ascii_code[242] = 23862;
        font_types[1].ascii_code[249] = 23863;
        font_types[1].ascii_code[192] = 23864;
        font_types[1].ascii_code[200] = 23865;
        font_types[1].ascii_code[204] = 23866;
        font_types[1].ascii_code[210] = 23867;
        font_types[1].ascii_code[217] = 23868;
        font_types[1].ascii_code[241] = 23869;
        font_types[1].ascii_code[209] = 23870;
        
        font_types[1].ascii_code[196] = 23970;
        font_types[1].ascii_code[194] = 23971;
        font_types[1].ascii_code[203] = 23972;
        font_types[1].ascii_code[207] = 23973;
        font_types[1].ascii_code[214] = 23974;
        font_types[1].ascii_code[212] = 23975;
        
        font_types[1].ascii_code[172] = 23975;
        
        font_types[2] = new tFont();
        font_types[2].font_size = 9;
        font_types[2].ascii_code[97] = 24076;
        font_types[2].ascii_code[108] = 24077;
        font_types[2].ascii_code[115] = 24078;
        font_types[2].ascii_code[70] = 24079;
        font_types[2].ascii_code[48] = 24080;
        font_types[2].ascii_code[49] = 24081;
        font_types[2].ascii_code[50] = 24082;
        font_types[2].ascii_code[51] = 24083;
        font_types[2].ascii_code[52] = 24084;
        font_types[2].ascii_code[53] = 24085;
        font_types[2].ascii_code[54] = 24086;
        font_types[2].ascii_code[55] = 24087;
        font_types[2].ascii_code[56] = 24088;
        font_types[2].ascii_code[57] = 24089;
        font_types[2].ascii_code[33] = 24090;
        font_types[2].ascii_code[161] = 24091;
        font_types[2].ascii_code[42] = 24092;
    }
    
    public Grh InitGrh(Grh grh, short grhindex, boolean started)
    {
        grh.grhIndex = grhindex;
        
        if (started)
        {
            if (GrhData[grh.grhIndex].numFrames > 1)
            {
                grh.started = true;
            }
            else
            {
                grh.started = false;
            }
        }
        else
        {
            if (GrhData[grh.grhIndex].numFrames == 1) started = false;
            grh.started = started;
        }
        
        if (grh.started)
        {
            grh.loops = -1;
        }
        else
        {
            grh.loops = 0;
        }
        
        grh.frameCounter = 1;
        grh.speed = GrhData[grh.grhIndex].speed;
        
        return grh;
    }
}
