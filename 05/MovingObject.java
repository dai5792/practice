import java.awt.Graphics;
import java.awt.*;

abstract class MovingObject extends Frame{ //抽象クラス
    int x;   //中心座標
    int y;   //中心座標
    int dx;  //速度
    int dy;  //速度
    int w;   //横幅の半分
    int h;   //立幅の半分
    int hp;  //ヒットポイント(ゼロ以下で死亡)

    //コンストラクタ１　引数がない場合は初期設定しない(フィールド変数は初期値0を持つ)
    MovingObject(){}
    //コンストラクタ２　描画領域の沖さを引数に,出現の初期値をランダムに設定
    MovingObject(int width, int height){
        x  = (int) (Math.random()*width);  //画面の内部でランダム
        y  = (int) (Math.random()*height); //画面の内部でランダム
        dx = (int) (Math.random()*6-3);    //^3から+3の範囲でランダム
        dy = (int) (Math.random()*6-3);    //^3から+3の範囲でランダム
        w  = 2;
        h  = 2;
        hp = 10;
    }

    abstract void move(Graphics buf, int apWidth, int apHeight);
    //オブジェクトを動かし,位置を更新する抽象メソッド
    //moveでは1．まず描いてから　2．vx,vyだけ移動させる
    //moveを呼び出す前に衝突判定し,合格したものだけ描く

    abstract void revive(int w, int h);
    //オブジェクトを生き返らせる抽象メソッド
    //引数は,通常はアプレットの立幅の大きさ.　弾の場合は,オブジェクトの位置

    boolean collisionCheck(MovingObject obj){
        //引数は相手のオブジェクト
        //衝突判定の共通メソッド
        if (Math.abs(this.x-obj.x) <= (this.w+obj.w) && Math.abs(this.y-obj.y) <= (this.h+obj.h)) {
            this.hp--; //自分のhpを減らす
            obj.hp--;  //相手のhpを減らす
            return true;
        } else
            return false;
    }
}

class Fighter extends MovingObject {
    Image fighterImage;
    boolean lflag;
    boolean rflag;
    boolean uflag;
    boolean dflag;
    boolean sflag;
    int delaytime;

    //コンストラクタ
    Fighter(int apWidth, int apHeight){
        //super(); //省略可
        x  = (int)(apWidth/2);     //画面の真中
        y  = (int) (apHeight*0.9); //画面の下の方
        dx = 5;
        dy = 5;
        w  = 10;
        h  = 10;
        hp = 10;
        lflag = false;
        rflag = false;
        uflag = false;
        dflag = false;
        sflag = false;
        delaytime = 5; //弾の発射待ち時間. 毎回1ずつ減り, 0で発射可能になる
        fighterImage = this.getToolkit().getImage("img05/jiki.png");
    }


    void revive(int apWidth, int apHeight){
    }

    void move(Graphics buf, int apWidth, int apHeight){
        //buf.setColor(Color.blue);         //gcの色を青に
        //buf.fillRect(x-w, y-h, 2*w, 2*h); //gcを使って□を描く
        buf.drawImage(fighterImage, x-w, y-h, 2*w,2*h,null);
        if (lflag && !rflag && x>w)
            x = x - dx;
        if (rflag && !lflag && x<apWidth-w)
            x = x + dx;
        if (uflag && !dflag && y>h)
            y = y - dy;
        if (dflag && !uflag && y<apHeight-h)
            y = y + dy;
    }
}
    
class FighterBullet extends MovingObject{
    //コンストラクタ
    FighterBullet(){
        w = h = 3; //弾の半径
        dx = 0; dy = -6;
        hp = 0; //初期値は全てひアクティブ
    }

    void move(Graphics buf, int apWidth, int apHeight){
        if (hp>0){
            buf.setColor(Color.blue);        //gcの色を黒に
            buf.fillOval(x-w, y-h, 2*w, 2*h); //gcを使って・を描く
            if (y>0 && y<apHeight && x>0 && x<apWidth) //もし弾が画面内なら
                y = y + dy; //弾のいちを更新
            else
                hp = 0;     //画面の外に出たらhpを0に
        }
    }

    void revive(int x, int y){ //x,yはFighterの位置
        this.x = x;
        this.y = y;
        hp = 1; //発射したら弾をアクティブに
    }
}

class EnemyA extends MovingObject{
    
    Image enemyaImage;
    EnemyBullet enemyBullet; // 敵機が発射する弾

    //コンストラクタ
    EnemyA(int apWidth, int apHeight){
        super(apWidth, apHeight); //スーパークラスのコンストラクタの呼び出し
        w = 24;
        h = 24;
        hp = 0; //初期状態では全て死亡
        enemyaImage = this.getToolkit().getImage("img05/tekia2.png");
    }

    //〇を描き更新するメソッド
    void move(Graphics buf, int apWidth, int apHeight){
        buf.setColor(Color.blue); //gcの色を黒に
        if (hp>0){ //もし生きていれば
            //buf.fillOval(x-w, y-h, 2*w, 2*h); //gcを使って〇を描く
            buf.drawImage(enemyaImage,x-w, y-h, 2*w, 2*h,null);
            x = x + dx; //座標値の更新
            y = y + dy; //座標値の更新

            if (y>apHeight+h)
                hp = 0;
        }
    }

    void revive(int apWidth, int apHeight){ //敵を新たに生成
        x = (int)(Math.random()*(apWidth-2*w)+w);
        y = -h;
        dy = 1;
        if (x<apWidth/2)
            dx = (int)(Math.random()*2);
        else
            dx = -(int)(Math.random()*2);
        hp = 1;
    }
}

class EnemyB extends MovingObject{

    Image enemybImage;
    EnemyBullet enemyBullet; // 敵機が発射する弾

    //コンストラクタ
    EnemyB(int apWidth, int apHeight){
        super(apWidth, apHeight); //スーパークラスのコンストラクタの呼び出し
        w = 12;
        h = 12;
        hp = 0; //初期状態では全て死亡
        enemybImage = this.getToolkit().getImage("img05/tekib.png");
    }

    //〇を描き更新するメソッド
    void move(Graphics buf, int apWidth, int apHeight){
        buf.setColor(Color.red); //gcの色を赤に
        if (hp>0){ //もし生きていれば
            //buf.fillRect(x-w, y-h, 2*w, 2*h); //gcを使って□を描く
            buf.drawImage(enemybImage,x-w, y-h, 2*w, 2*h,null);
            x = x + dx; //座標値の更新
            y = y + dy; //座標値の更新

            if (y>apHeight+h)
                hp = 0;
        }
    }

    void revive(int apWidth, int apHeight){ //敵を新たに生成
        x = (int)(Math.random()*(apWidth-2*w)+w);
        y = -h;
        dy = 2;
        if (x<apWidth/2)
            dx = (int)(Math.random()*3);
        else
            dx = -(int)(Math.random()*3);
        hp = 3;
    }
}

class EnemyBullet extends MovingObject {
    // コンストラクタ
    EnemyBullet() {
        w = h = 3; // 弾の半径
        dx = 0;
        dy = 4; // 下方向に移動
        hp = 0; // 初期値は非アクティブ
    }

    void move(Graphics buf, int apWidth, int apHeight) {
        if (hp > 0) {
            buf.setColor(Color.red);           // gcの色を赤に
            buf.fillOval(x - w, y - h, 2 * w, 2 * h); // gcを使って●を描く
            if (y > 0 && y < apHeight && x>0 && x<apWidth) // もし弾が画面内なら
                y = y + dy; // 弾の位置を更新
            else
                hp = 0;     // 画面外に出たら非アクティブに
        }
    }

    void revive(int x, int y) {
        this.x = x;
        this.y = y;
        hp = 1; // 発射されたらアクティブに
    }
}