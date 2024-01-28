import java.awt.*;

abstract class Object extends Frame{
    int x;
    int y;
    int w;
    int h;

    Object(){}

    Object(int width, int height){
        x  = (int) (Math.random()*width);  //画面の内部でランダム
        y  = (int) (Math.random()*height); //画面の内部でランダム
        w = 2;
        h = 2;
    }

    abstract void move(Graphics buf, int apWidth, int apHeight);
    
}
class Player extends Object{
    double dx;
    double dy;
    double up;
    double down;
    Image playerImage;
    boolean lflag;
    boolean rflag;
    boolean sflag;
    boolean gflag;
    int score;

    Player(int width, int height){
        x = (int)(width/2);
        y = (int)(height*0.9);
        dx = 0;
        dy = 0;
        w = 10;
        h = 10;
        lflag = false;
        rflag = false;
        sflag = false;
        gflag = false;
        score = 0;
        playerImage = this.getToolkit().getImage("img00/player.png");
    }

    void collisionCheck(Object obj){
        //System.out.println(this.sflag);
        if(this.x >= (obj.x-this.w) && this.x<=(obj.x+obj.w) && this.y <= obj.y && (this.y+this.h) >= (obj.y-obj.h)){
            this.y = (obj.y - obj.h) - this.h - 1;
            this.dy = 0.0;
            this.dx = 0.0;
            this.down = 0.0;
            this.sflag = false;
            this.lflag = false;
            this.rflag = false;
            this.score += 10;
        }else if (this.x >= (obj.x - this.w) && this.x <= (obj.x + obj.w) && (this.y + this.h) >= obj.y && this.y <= obj.y) {
    // 横から衝突
            this.dx = -this.dx; // dxを反転
        } else if (this.y >= (obj.y - this.h) && this.y <= (obj.y + obj.h) && this.x >= (obj.x - this.w) && this.x <= (obj.x + obj.w)) {
    // 下から衝突
            this.dy = -this.dy; // dyを反転
        }
    }

    void collisionCheck(Object obj, int mode){
        if(this.x >= (obj.x-this.w) && this.x<=(obj.x+obj.w) && this.y <= obj.y && (this.y+this.h) >= (obj.y-obj.h)){
            this.y = (obj.y - obj.h) - this.h - 1;
            this.dy = 0.0;
            this.dx = 0.0;
            this.down = 0.0;
            this.sflag = false;
            this.lflag = false;
            this.rflag = false;

            if(obj instanceof Goal){
                //ゴールフラグをtrueに
                this.gflag = true;
                this.score += 100;
            }
        }
    }

    void move(Graphics buf, int apWidth, int apHeight){
        //buf.setColor(Color.blue);         //gcの色を青に
        //buf.fillRect(x-w, y-h, 2*w, 2*h); //gcを使って□を描く
        buf.drawImage(playerImage, x-w, y-h, 2*w,2*h,null);
        dy += (up - down);
        if(sflag){
            y -= (int)dy;
            x += (int)dx;
        }
    }

    int getScore(){
        return score;
    }
}

class Field extends Object{

    Image fieldImage;

    Field(int a, int b, int c, int d){
        x = a;
        y = b;
        w = c;
        h = d;
        fieldImage = this.getToolkit().getImage("img00/field.jpg");
    }

    Field(int width,int height){
        x  = (int) (Math.random()*width);  //画面の内部でランダム
        y  = (int) (Math.random()*420); //画面の内部でランダム
        w  = 7;
        h  = 7;
        fieldImage = this.getToolkit().getImage("img00/field.jpg");
    }

    void move(Graphics buf, int apWidth, int apHeight){
        //buf.setColor(Color.red);
        //buf.fillRect(x-w, y-h, 2*w, 2*h);
        buf.drawImage(fieldImage, x-w, y-h, 2*w,2*h,null);

    }
}

class Goal extends Object{

    Goal(int a, int b, int c, int d){
        x = a;
        y = b;
        w = c;
        h = d;
    }

    void move(Graphics buf, int apWidth, int apHeight){
        buf.setColor(Color.red);
        buf.fillRect(x-w, y-h, 2*w, 2*h);
    }
}