package client;

import client.GameData.*;

public class User
{   
    public enum E_Heading
    {
        NORTH(1), EAST(2), SOUTH(3), WEST(4);
        public int value;
        
        private E_Heading(int value)
        {
            this.value = value;
        }
    };
    
    public class Position
    {
        int x;
        int y;
    }
    
    public class Char
    {
        byte Active;
        E_Heading Heading;
        Position Pos = new Position();

        short iHead;
        short iBody;
        BodyData Body;
        HeadData Head;
        HeadData Helmet;
        WeaponData Weapon;
        ShieldData Shield;
        boolean UsingArm;

        Grh fX;
        short FxIndex;

        byte criminal;
        boolean attackable;

        String name;

        short scrollDirectionX;
        short scrollDirectionY;

        byte Moving;
        float MoveOffsetX;
        float MoveOffsetY;

        boolean pie;
        boolean dead;
        boolean invisible;
        byte priv;
    }
    
    GameData game = new GameData();
    Position UserPos = new Position();
    Position AddToUserPos = new Position();
    Char[] charlist = new Char[10000+1];
    
    public void initialize()
    {
        for (int i = 1; i <= 10000; i++)
        {
            charlist[i] = new Char();
            charlist[i].Active = 0;
        }
    }
    
    public int MakeChar(int charindex, int Body, E_Heading Heading, int x, int y)
    {
        if (charindex > game.LastChar) game.LastChar = charindex;
        if (charlist[charindex].Active > 0) return 0;
        charlist[charindex].name = "Lord Fers";
        charlist[charindex].iBody = (short)Body;
        charlist[charindex].iHead = 8;
        charlist[charindex].Body = game.new BodyData(game.BodyData[Body]);
        charlist[charindex].Head = game.new HeadData(game.HeadData[8]);
        charlist[charindex].Weapon = game.new WeaponData(game.WeaponData[10]);
        charlist[charindex].Shield = game.new ShieldData(game.ShieldData[2]);
        charlist[charindex].Helmet = game.new HeadData(game.HelmetsData[4]);
        charlist[charindex].Heading = Heading;
        charlist[charindex].Moving = 0;
        charlist[charindex].MoveOffsetX = 0;
        charlist[charindex].MoveOffsetY = 0;
        charlist[charindex].Pos.x = x;
        charlist[charindex].Pos.y = y;
        charlist[charindex].Active = 1;
        return charindex;
    }
    
    public Grh reference(Grh ref)
    {
        return ref;
    }
    
    public void RefreshAllChars()
    {
        for (short LoopC = 1; LoopC <= game.LastChar; LoopC++)
        {
            if (charlist[LoopC].Active == 1)
            {
                game.MapData[charlist[LoopC].Pos.x][charlist[LoopC].Pos.y].charindex = LoopC;
            }
        }
    }
}
