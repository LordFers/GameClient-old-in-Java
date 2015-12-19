package client;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;
import client.Surface.TextureOGL;
import client.GameData.Grh;
import client.User.*;

public class Engine
{
    public class fColor
    {
        float red;
        float green;
        float blue;
    }
    
    Surface surface;
    GameData game;
    User user;

    final int XMaxMapSize = 100;
    final int XMinMapSize = 1;
    final int YMaxMapSize = 100;
    final int YMinMapSize = 1;

    final float engineBaseSpeed = 0.018f;
    
    final int TileBufferSize = 9;
    
    final int HalfWindowTileWidth = 8;
    final int HalfWindowTileHeight = 6;
    
    final int TilePixelWidth = 32;
    final int TilePixelHeight = 32;
    
    final int ScrollPixelsPerFrameX = 8;
    final int ScrollPixelsPerFrameY = 8;
    
    private float OffsetCounterX = 0;
    private float OffsetCounterY = 0;
    private boolean UserMoving = false;
    
    private long lFrameTimer;
    private long FramesPerSecCounter;
    private long FPS;
    private long timerElapsedTime;
    private float timerTicksPerFrame;
    private long end_time;
    
    fColor ambientcolor;
    
    public void initialize()
    {
        try
        {
            Display.setDisplayMode(new DisplayMode(544, 416));
            Display.setTitle("Argentum Online in Java (OpenGL)");
            Display.setVSyncEnabled(true);
            Display.create();
            Keyboard.create();
            Keyboard.enableRepeatEvents(true);
        }
            catch (LWJGLException e)
        {
            e.printStackTrace();
        }
        
        surface = new Surface();
        game = new GameData();
        user = new User();
        
        surface.initialize();
        game.initialize();
        user.initialize();
        
        game.loadmap(1);
        user.game = (GameData) game;
        
        game.MapData[50][50].charindex = (short)user.MakeChar(1, 128, E_Heading.SOUTH, 50, 50);
        
        user.RefreshAllChars();
        
        user.UserPos.x = 50;
        user.UserPos.y = 50;
        
        ambientcolor = new fColor();
        ambientcolor.red = 1.0f; ambientcolor.green = 1.0f; ambientcolor.blue = 1.0f;
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);               
	GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);        
        
        GL11.glViewport(0, 0, 544, 416);
	GL11.glOrtho(0, 544, 416, 0, 1, -1);
        
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GL11.glEnable(GL11.GL_ALPHA);
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND); GL11.glDisable(GL11.GL_DEPTH_TEST);
        Display.sync(60);
        
        while (!Display.isCloseRequested())
        {
            keyevents();
            render();
        }
    }
    
    private void render()
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        ShowNextFrame();
        Display.update();
        
        if (GetTime() >= lFrameTimer + 1000)
        {
            lFrameTimer = GetTime();
            FPS = FramesPerSecCounter;
            FramesPerSecCounter = 0;
        }
        
        FramesPerSecCounter++;
        timerElapsedTime = GetElapsedTime();
        timerTicksPerFrame = (timerElapsedTime * engineBaseSpeed);
    }
    
    private long GetTime()
    {
        return System.nanoTime() / 1000000;
    }
    
    private long GetElapsedTime()
    {
        
        long start_time = GetTime();
        long ms = (start_time - end_time);
        end_time = GetTime();
        return ms;
    }
    
    private void keyevents()
    {
        while(Keyboard.next())
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                if (UserMoving) return;
                MoveScreen(E_Heading.EAST);
                MoveCharbyHead(1, E_Heading.EAST);
                user.RefreshAllChars();
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                if (UserMoving) return;
                MoveScreen(E_Heading.WEST);
                MoveCharbyHead(1, E_Heading.WEST);
                user.RefreshAllChars();
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                if (UserMoving) return;
                MoveScreen(E_Heading.NORTH);
                MoveCharbyHead(1, E_Heading.NORTH);
                user.RefreshAllChars();
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                if (UserMoving) return;
                MoveScreen(E_Heading.SOUTH);
                MoveCharbyHead(1, E_Heading.SOUTH);
                user.RefreshAllChars();
            }
        }
    }
    
    private void geometry_box_render(int grh_index, int x, int y, int src_width, int src_height, int sX, int sY, boolean blend
                                        , fColor color)
    {
        if (blend)
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
        
        TextureOGL texture = surface.getTexture(game.GrhData[grh_index].fileNum);
        
        int textureWidth = texture.tex_width;
        int textureHeight = texture.tex_height;
        
        float src_left = sX;
        float src_top = sY;
        float src_right = src_left + src_width;
        float src_bottom = src_top + (src_height);

        float dest_left = x;
        float dest_top = y;
        float dest_right = x + (src_right - src_left);
        float dest_bottom = y + (src_bottom - src_top);
        
        float x_cor; float y_cor;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id);
        GL11.glBegin(GL11.GL_QUADS);
        {
        //0 
            x_cor = dest_left;
            y_cor = dest_bottom;

            GL11.glColor4f(color.red, color.green, color.blue, 1.0f);
            GL11.glTexCoord2f (src_left / textureWidth, (src_bottom) / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);
            
        //1
            x_cor = dest_left;
            y_cor = dest_top;

            GL11.glColor4f(color.red, color.green, color.blue, 1.0f);
            GL11.glTexCoord2f(src_left / textureWidth, src_top / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);
  
        //3
            x_cor = dest_right;
            y_cor = dest_top;

            GL11.glColor4f(color.red, color.green, color.blue, 1.0f);
            GL11.glTexCoord2f((src_right) / textureWidth, src_top / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);
            
        //2
            x_cor = dest_right;
            y_cor = dest_bottom;

            GL11.glColor4f(color.red, color.green, color.blue, 1.0f);
            GL11.glTexCoord2f((src_right) / textureWidth, (src_bottom) / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);
        }
        GL11.glEnd();
        
        if (blend)
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
    
    private void ShowNextFrame()
    {
        if (UserMoving)
        {
            if (user.AddToUserPos.x != 0)
            {
                OffsetCounterX = OffsetCounterX - ScrollPixelsPerFrameX * user.AddToUserPos.x * timerTicksPerFrame;
                if (Math.abs(OffsetCounterX) >= Math.abs(TilePixelWidth * user.AddToUserPos.x))
                {
                    OffsetCounterX = 0;
                    user.AddToUserPos.x = 0;
                    UserMoving = false;
                }
            }

            if (user.AddToUserPos.y != 0)
            {
                OffsetCounterY = OffsetCounterY - ScrollPixelsPerFrameY * user.AddToUserPos.y * timerTicksPerFrame;
                if (Math.abs(OffsetCounterY) >= Math.abs(TilePixelHeight * user.AddToUserPos.y))
                {
                    OffsetCounterY = 0;
                    user.AddToUserPos.y = 0;
                    UserMoving = false;
                }
            }
        }
        
        //OffsetCounterY = OffsetCounterY - 2;
        
        RenderScreen(user.UserPos.x - user.AddToUserPos.x,
                     user.UserPos.y - user.AddToUserPos.y,
                     (int)(OffsetCounterX), (int)(OffsetCounterY)); 
    }
    
    private void MoveScreen(E_Heading nHeading)
    {
        int X = 0, Y = 0, tX = 0, tY = 0;
        switch (nHeading)
        {
            case NORTH:
                Y = -1;
                    break;
            case EAST:
                X = 1;
                    break;
            case SOUTH:
                Y = 1;
                    break;
            case WEST:
                X = -1;
                    break;
        }
        
        tX = user.UserPos.x + X;
        tY = user.UserPos.y + Y;
        
        if (tX < TileBufferSize || tX > XMaxMapSize - TileBufferSize || tY < TileBufferSize || tY > YMaxMapSize - TileBufferSize)
        {
            return;
        }
        else
        {
            user.AddToUserPos.x = X;
            user.UserPos.x = tX;
            user.AddToUserPos.y = Y;
            user.UserPos.y = tY;
            UserMoving = true;
        }
    }
    
    public void MoveCharbyHead(int CharIndex, E_Heading nHeading)
    {
        int addX = 0; int addY = 0;
        int X; int Y;
        int nX; int nY;
        
        X = user.charlist[CharIndex].Pos.x;
        Y = user.charlist[CharIndex].Pos.y;
        
        switch (nHeading)
        {
            case NORTH:
                addY = -1;
            break;
                
            case EAST:
                addX = 1;
            break;
                
            case SOUTH:
                addY = 1;
            break;
                
            case WEST:
                addX = -1;
            break;
        }
        
        nX = X + addX;
        nY = Y + addY;
        
        game.MapData[X][Y].charindex = 0;
        game.MapData[nX][nY].charindex = (short)CharIndex;
        user.charlist[CharIndex].Pos.x = nX;
        user.charlist[CharIndex].Pos.y = nY;
        
        user.charlist[CharIndex].MoveOffsetX = -1 * (TilePixelWidth * addX);
        user.charlist[CharIndex].MoveOffsetY = -1 * (TilePixelHeight * addY);
        
        user.charlist[CharIndex].Moving = 1;
        user.charlist[CharIndex].Heading = nHeading;
        
        user.charlist[CharIndex].scrollDirectionX = (short)addX;
        user.charlist[CharIndex].scrollDirectionY = (short)addY;
        
        /*If UserEstado = 0 Then Call DoPasosFx(CharIndex)
    
        'areas viejos
        If (nY < MinLimiteY) Or (nY > MaxLimiteY) Or (nX < MinLimiteX) Or (nX > MaxLimiteX) Then
            If CharIndex <> UserCharIndex Then
                Call EraseChar(CharIndex)
            End If
        End If*/
    }
    
    private Grh draw(Grh grh, int x, int y, boolean center, boolean animate, boolean alpha, fColor color)
    {
        if (grh.grhIndex == 0) return grh;
        if (game.GrhData[grh.grhIndex].numFrames == 0) return grh;
        if (animate)
        {
            if (grh.started)
            {
                grh.frameCounter = grh.frameCounter + (timerElapsedTime * game.GrhData[grh.grhIndex].numFrames / grh.speed);
                if (grh.frameCounter > game.GrhData[grh.grhIndex].numFrames)
                {
                    grh.frameCounter = (grh.frameCounter % game.GrhData[grh.grhIndex].numFrames) + 1;
                    if (grh.loops != -1)
                    {
                        if (grh.loops > 0)
                        {
                            grh.loops--;
                        }
                        else
                        {
                            grh.started = false;
                        }
                    }
                }
            }
        }
        
        int currentGrhIndex = game.GrhData[grh.grhIndex].frames[(int)(grh.frameCounter)];
        
        if (center)
        {
            if (game.GrhData[currentGrhIndex].tileWidth != 1)
            {
                x = x - (int)(game.GrhData[currentGrhIndex].tileWidth * TilePixelWidth / 2) + TilePixelWidth / 2;
            }
            
            if (game.GrhData[currentGrhIndex].tileHeight != 1)
            {
                y = y - (int)(game.GrhData[currentGrhIndex].tileHeight * TilePixelHeight) + TilePixelHeight;
            }
        }

        if (currentGrhIndex == 0) return grh;
        if (game.GrhData[currentGrhIndex].fileNum == 0) return grh;

        geometry_box_render (currentGrhIndex, x, y,
                game.GrhData[currentGrhIndex].pixelWidth,
                game.GrhData[currentGrhIndex].pixelHeight,
                game.GrhData[currentGrhIndex].sX,
                game.GrhData[currentGrhIndex].sY, false, color);
        return grh;
    }
    
    private void drawText(String text, int x, int y, fColor color, int font_index, boolean multi_line)
    {
        int a, b = 0, c, d, e, f;
        Grh graf = game.new Grh();
        if (text.length() == 0) return;
        
        d = 0;
        if (!multi_line)
        {
            for (a = 0; a < text.length(); a++)
            {
                b = text.charAt(a);
                if (b > 255) b = 0;
                graf.grhIndex = (short)game.font_types[font_index].ascii_code[b];
                if (b != 32)
                {
                    if (graf.grhIndex != 0)
                    {
                        //mega sombra O-matica
                        graf.grhIndex = (short)(game.font_types[font_index].ascii_code[b] + 100);
                        graf = game.InitGrh(graf, graf.grhIndex, false);
                        draw(graf, (x + d) + 1, y + 1, false, false, false, color);
                        graf = game.InitGrh(graf, graf.grhIndex, false);
                        graf.grhIndex = (short)(game.font_types[font_index].ascii_code[b]);
                        draw(graf, (x + d) + 1, y, false, false, false, color);
                        d = d + game.GrhData[game.GrhData[graf.grhIndex].frames[1]].pixelWidth;
                    }
                }
                else
                {
                    d = d + 4;
                }
            }
        }
        else
        {
            e = 0;
            f = 0;
            for (a = 1; a <= text.length(); a++)
            {
                b = text.charAt(a);
                if (b > 255) b = 0;
                
                graf.grhIndex = (short)game.font_types[1].ascii_code[b];
                if (b == 32 || b == 13)
                {
                    if (e >= 20)
                    {
                        f++;
                        e = 0;
                        d = 0;
                    }
                    else
                    {
                        if (b == 32) d = d + 4;
                        if (b > 255) b = 0;
                    }
                }
                else
                {
                    if (graf.grhIndex > 12)
                    {
                        d = d + game.GrhData[game.GrhData[graf.grhIndex].frames[1]].pixelWidth;
                        if (d > 0);
                    }
                }
            }
        }
    }
    
    private void RenderScreen(int tilex, int tiley, int PixelOffsetX, int PixelOffsetY)
    {
        int y; int x;
        int screenminY; int screenmaxY;
        int screenminX; int screenmaxX;
        int minY; int maxY;
        int minX; int maxX;
        int ScreenX = 0; int ScreenY = 0;
        int minXOffset = 0; int minYOffset = 0;
        
        screenminY = tiley - HalfWindowTileHeight;
        screenmaxY = tiley + HalfWindowTileHeight;
        screenminX = tilex - HalfWindowTileWidth;
        screenmaxX = tilex + HalfWindowTileWidth;
        
        minY = screenminY - TileBufferSize;
        maxY = screenmaxY + TileBufferSize;
        minX = screenminX - TileBufferSize;
        maxX = screenmaxX + TileBufferSize;
        
        if (minY < XMinMapSize)
        {
            minYOffset = YMinMapSize - minY;
            minY = YMinMapSize;
        }
        
        if (maxY > YMaxMapSize) maxY = YMaxMapSize;
        
        if (minX < XMinMapSize)
        {
            minXOffset = XMinMapSize - minX;
            minX = XMinMapSize;
        }
        
        if (maxX > XMaxMapSize) maxX = XMaxMapSize;
        
        if (screenminY > YMinMapSize)
        {
            screenminY = screenminY - 1;
        }
        else
        {
            screenminY = 1;
            ScreenY = 1;
        }
        
        if (screenmaxY < YMaxMapSize) screenmaxY = screenmaxY + 1;
        
        if (screenminX > XMinMapSize)
        {
            screenminX = screenminX - 1;
        }
        else
        {
            screenminX = 1;
            ScreenX = 1;
        }

        if (screenmaxX < XMaxMapSize) screenmaxX = screenmaxX + 1;
        
        for (y = screenminY; y <= screenmaxY; y++)
        {
            for(x = screenminX; x <= screenmaxX; x++)
            {
                draw(game.MapData[x][y].layer[1],
                        (ScreenX - 1) * TilePixelWidth + PixelOffsetX,
                        (ScreenY - 1) * TilePixelHeight + PixelOffsetY, true, true, false, ambientcolor);

                if (game.MapData[x][y].layer[2].grhIndex != 0)
                {
                    draw(game.MapData[x][y].layer[2],
                            (ScreenX - 1) * TilePixelWidth + PixelOffsetX,
                            (ScreenY - 1) * TilePixelHeight + PixelOffsetY, true, true, false, ambientcolor);
                }
                ScreenX++;
            }
            ScreenX = ScreenX - x + screenminX;
            ScreenY++;
        }
        
        ScreenY = minYOffset - TileBufferSize;
        for (y = minY; y <= maxY; y++)
        {
            ScreenX = minXOffset - TileBufferSize;
            for(x = minX; x <= maxX; x++)
            {
                if (game.MapData[x][y].objgrh.grhIndex != 0)
                {
                    draw(game.MapData[x][y].objgrh, ScreenX * 32 + PixelOffsetX, ScreenY * 32 + PixelOffsetY, true, true, false, ambientcolor);
                }
                
                if (game.MapData[x][y].charindex > 0)
                {
                    CharRender(game.MapData[x][y].charindex, ScreenX * 32 + PixelOffsetX, ScreenY * 32 + PixelOffsetY);
                }
              
                if (game.MapData[x][y].layer[3].grhIndex != 0)
                {
                    draw(game.MapData[x][y].layer[3], ScreenX * 32 + PixelOffsetX,
                            ScreenY * 32 + PixelOffsetY, true, true, false, ambientcolor);
                }
                ScreenX++;
            }
            ScreenY++;
        }
        
        ScreenY = minYOffset - TileBufferSize;
        for (y = minY; y <= maxY; y++)
        {
            ScreenX = minXOffset - TileBufferSize;
            for(x = minX; x <= maxX; x++)
            {
                if (game.MapData[x][y].layer[4].grhIndex > 0)
                {
                    draw(game.MapData[x][y].layer[4], ScreenX * 32 + PixelOffsetX,
                            ScreenY * 32 + PixelOffsetY, true, true, false, ambientcolor);
                }
                ScreenX++;
            }
            ScreenY++;
        }
        
        drawText(FPS + " FPS", 454, 8, ambientcolor, 1, false);
    }
    
    public void CharRender(int CharIndex, int PixelOffsetX, int PixelOffsetY)
    {
        boolean moved = false;
        int Pos;
        String line;
        fColor color = new fColor();
        
        if (user.charlist[CharIndex].Moving != 0)
        {
            if (user.charlist[CharIndex].scrollDirectionX != 0)
            {
                user.charlist[CharIndex].MoveOffsetX = user.charlist[CharIndex].MoveOffsetX
                        + ScrollPixelsPerFrameX * Sgn(user.charlist[CharIndex].scrollDirectionX) * timerTicksPerFrame;
                if (user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].speed > 0.0f)
                {user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].started = true;}
                user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value].started = true;
                user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value].started = true;
                
                moved = true;
                
                if ((Sgn(user.charlist[CharIndex].scrollDirectionX) == 1 && user.charlist[CharIndex].MoveOffsetX >= 0)
                 || (Sgn(user.charlist[CharIndex].scrollDirectionX) == -1 && user.charlist[CharIndex].MoveOffsetX <= 0))
                {
                    user.charlist[CharIndex].MoveOffsetX = 0;
                    user.charlist[CharIndex].scrollDirectionX = 0;
                }
            }
            
            if (user.charlist[CharIndex].scrollDirectionY != 0)
            {
                user.charlist[CharIndex].MoveOffsetY = user.charlist[CharIndex].MoveOffsetY
                        + ScrollPixelsPerFrameY * Sgn(user.charlist[CharIndex].scrollDirectionY) * timerTicksPerFrame;
                if (user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].speed > 0.0f)
                {user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].started = true;}
                user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value].started = true;
                user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value].started = true;
                
                moved = true;
                
                if ((Sgn(user.charlist[CharIndex].scrollDirectionY) == 1 && user.charlist[CharIndex].MoveOffsetY >= 0)
                 || (Sgn(user.charlist[CharIndex].scrollDirectionY) == -1 && user.charlist[CharIndex].MoveOffsetY <= 0))
                {
                    user.charlist[CharIndex].MoveOffsetY = 0;
                    user.charlist[CharIndex].scrollDirectionY = 0;
                }
            }
        }
        
        if (!moved)
        {
            user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].started = false;
            user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].frameCounter = 1;

            user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value].started = false;
            user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value].frameCounter = 1;

            user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value].started = false;
            user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value].frameCounter = 1;

            user.charlist[CharIndex].Moving = 0;
        }

        PixelOffsetX = PixelOffsetX + (int)user.charlist[CharIndex].MoveOffsetX;
        PixelOffsetY = PixelOffsetY + (int)user.charlist[CharIndex].MoveOffsetY;
            
        if (user.charlist[CharIndex].Head.Head[user.charlist[CharIndex].Heading.value].grhIndex != 0)
        {
            if (!user.charlist[CharIndex].invisible)
            {
                if (user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].grhIndex > 0)
                {draw(user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX, PixelOffsetY, true, true, false, ambientcolor);}
                
                if (user.charlist[CharIndex].Head.Head[user.charlist[CharIndex].Heading.value].grhIndex != 0)
                {
                    draw(user.charlist[CharIndex].Head.Head[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX + user.charlist[CharIndex].Body.HeadOffset.x, PixelOffsetY + user.charlist[CharIndex].Body.HeadOffset.y, true, false, false, ambientcolor);
                    
                    if (user.charlist[CharIndex].Helmet.Head[user.charlist[CharIndex].Heading.value].grhIndex != 0)
                    {draw(user.charlist[CharIndex].Helmet.Head[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX + user.charlist[CharIndex].Body.HeadOffset.x, PixelOffsetY + user.charlist[CharIndex].Body.HeadOffset.y -34, true, false, false, ambientcolor);}
                    
                    if (user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value].grhIndex != 0)
                    {draw(user.charlist[CharIndex].Weapon.WeaponWalk[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX, PixelOffsetY, true, true, false, ambientcolor);}
                    
                    if (user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value].grhIndex != 0)
                    {draw(user.charlist[CharIndex].Shield.ShieldWalk[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX, PixelOffsetY, true, true, false, ambientcolor);}
                    
                    if (user.charlist[CharIndex].name.length() > 0)
                    {
                        if (user.charlist[CharIndex].priv == 0)
                        {
                            color.red = 0.0f;
                            color.green = 0.5f;
                            color.blue = 1.0f;
                        }
                        
                        line = user.charlist[CharIndex].name;
                        //drawText(line, PixelOffsetX - (line.length() * 4) + 28, PixelOffsetY + 30, color, 1, false);
                    }
                }
            }
        }
        else
        {
            if (user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value].grhIndex > 0)
                {draw(user.charlist[CharIndex].Body.Walk[user.charlist[CharIndex].Heading.value],
                        PixelOffsetX, PixelOffsetY, true, true, false, ambientcolor);}
        }
    }
    
    private int Sgn(short number)
    {
        if (number == 0) return 0;
        return (number / Math.abs(number));
    }
}